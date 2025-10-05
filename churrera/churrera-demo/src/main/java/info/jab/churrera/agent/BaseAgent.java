package info.jab.churrera.agent;

import info.jab.cursor.CursorAgent;
import info.jab.cursor.client.model.Agent;
import info.jab.cursor.client.model.FollowUpResponse;
import info.jab.churrera.util.ClasspathResolver;
import info.jab.churrera.util.TimeAccumulated;
import info.jab.churrera.util.CursorApiKeyResolver;
import info.jab.churrera.util.PropertyResolver;
import info.jab.cursor.client.model.ConversationMessage;
import java.util.stream.Collectors;
import java.util.List;

public abstract class BaseAgent {

    protected long startTime;

    protected String apiKey;
    protected String model;
    protected String repository;
    protected int delaySeconds;

    protected CursorAgent cursorAgent;

    protected BaseAgent() {
        String apiKey = CursorApiKeyResolver.resolveApiKey();
        this.cursorAgent = new CursorAgent(apiKey);

        this.model = PropertyResolver.getPropertyAs("application.properties", "model", String.class).get();
        this.repository = PropertyResolver.getPropertyAs("application.properties", "repository", String.class).get();
        this.delaySeconds = PropertyResolver.getPropertyAs("application.properties", "delay", Integer.class).get();

        // Verify if the model is valid with the cursorAgent.getModels()
        try {
            var availableModels = cursorAgent.getModels();
            if (availableModels == null || !availableModels.contains(model)) {
                throw new RuntimeException("Model '" + model + "' is not available. Available models: " + availableModels);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify model '" + model + "': " + e.getMessage(), e);
        }

        // Verify if the repository is valid with the cursorAgent.getRepositories()
        // Note: This validation is optional due to API rate limits (1 request per minute)
        // Skipped due to rate limits

        startTime = System.currentTimeMillis();
    }

    protected Agent launchAgent(String prompt) {
        System.out.println("🚀 Launching cursor background agent...");
        Agent resultAgent = cursorAgent.launch(prompt, model, repository);
        System.out.println("✅ Cursor background agent launched successfully!");
        System.out.println("🔗 Agent Details: " + resultAgent.getTarget().getUrl());

        return monitorAgent(resultAgent);
    }

    protected Agent updateAgent(String prompt, String agentId) {
        System.out.println("🚀 Adding new prompt...");
        FollowUpResponse followUpResponse = cursorAgent.followUp(agentId, prompt);
        Agent resultAgent = cursorAgent.getStatus(followUpResponse.getId());
        System.out.println("✅ Cursor background agent updated successfully!");

        return monitorAgent(resultAgent);
    }

    protected String getAgentConversation(String agentId) {
        try {
            return cursorAgent.getAgentConversation(agentId).getMessages().stream()
                .map(ConversationMessage::getText)
                .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get agent conversation", e);
        }
    }

    /**
     * Monitors the status of the agent and prints the status updates.
     * Blocking method that will only return when the agent is completed or failed.
     * @param agent
     * @return
     * @throws Exception
     */
    private Agent monitorAgent(Agent agent) {
        System.out.println();
        System.out.println("🔄 Starting to monitor agent status...");
        System.out.println("📊 Checking status every " + delaySeconds + " seconds");
        System.out.println("⏹️  Press Ctrl+C to stop monitoring\n");

        int checkCount = 0;
        Agent currentAgent = agent;

        while (true) {
            try {
                checkCount++;

                // Get current agent status
                currentAgent = cursorAgent.getStatus(currentAgent.getId());
                AgentState agentState = AgentState.of(currentAgent);

                // Always show status updates
                String currentTime = TimeAccumulated.getCurrentTime();
                System.out.printf("📊 %s - Status Update #%d: %s%n", currentTime, checkCount, agentState);

                // Check if agent has completed or failed
                if (agentState.isTerminal()) {
                    System.out.println("\n✅ Agent monitoring completed!");
                    System.out.println("🏁 Final Status: " + agentState);

                    if (agentState.isSuccessful()) {
                        System.out.println("🎉 Agent completed successfully!");
                    } else if (agentState.isFailed()) {
                        System.out.println("❌ Agent failed or was terminated!");
                    }

                    // Show accumulated time
                    String totalTime = TimeAccumulated.getTimeFormatted(startTime);
                    System.out.println("⏱️  Total accumulated time: " + totalTime);
                    System.out.println();
                    break;
                }

                // Wait for the next check
                Thread.sleep(delaySeconds * 1000L);

            } catch (InterruptedException e) {
                System.out.println("\n⏹️  Monitoring interrupted by user");
                Thread.currentThread().interrupt();
                //Convert to silent exception
                throw new RuntimeException(e);
            } catch (Exception e) {
                System.err.println("⚠️  Error during status check: " + e.getMessage());
                // Continue monitoring despite errors
                try {
                    Thread.sleep(delaySeconds * 1000L);
                } catch (InterruptedException ie) {
                    System.err.println("⚠️  Sleep interrupted, continuing...");
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
            }
        }

        return currentAgent;
    }


    protected void showCompletionMessage(Agent finalAgent) {
        System.out.println("\n🎉 Cursor background agent execution completed!");

        Agent agentToUse = finalAgent;

        if (agentToUse != null && agentToUse.getSource() != null && agentToUse.getSource().getRepository() != null) {
            String repositoryUrl = agentToUse.getSource().getRepository().toString();
            String prReviewUrl = generatePrReviewUrl(repositoryUrl);

            System.out.println("🔍 Review the changes:");
            System.out.println("   📋 Pull Requests: " + prReviewUrl);

            if (agentToUse.getTarget() != null && agentToUse.getTarget().getUrl() != null) {
                System.out.println("   🔗 Agent Details: " + agentToUse.getTarget().getUrl());
            }
        }

        System.out.println("\n✨ Thank you for using Churrera!");
    }

    protected String generatePrReviewUrl(String repositoryUrl) {
        if (repositoryUrl == null || repositoryUrl.trim().isEmpty()) {
            return "Unable to generate PR URL";
        }

        // Remove trailing slashes
        String cleanUrl = repositoryUrl.replaceAll("/+$", "");

        // Add https:// if not already present
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "https://" + cleanUrl;
        }

        return cleanUrl + "/pulls";
    }

}
