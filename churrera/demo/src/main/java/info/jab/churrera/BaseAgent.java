package info.jab.churrera;

import info.jab.cursor.CursorAgent;
import info.jab.cursor.client.model.Agent;
import java.time.LocalTime;
import static info.jab.churrera.AgentOperation.LAUNCH;
import static info.jab.churrera.AgentOperation.UPDATE;
import info.jab.churrera.utils.PromptResolver;
import java.time.format.DateTimeFormatter;

public abstract class BaseAgent {

    protected long startTime;

    protected String apiKey;
    protected String model;
    protected String repository;
    protected int delaySeconds;

    protected CursorAgent cursorAgent;

    protected BaseAgent(String apiKey, String model, String repository, int delaySeconds) {
        this.apiKey = apiKey;
        this.model = model;
        this.repository = repository;
        this.delaySeconds = delaySeconds;

        cursorAgent = new CursorAgent(apiKey);
    }

    protected Agent executeAgentOperation(AgentOperation operation, String promptFile) throws Exception {
        String prompt = PromptResolver.retrieve(promptFile);
        Agent resultAgent;

        switch (operation) {
            case LAUNCH:
                System.out.println("🚀 Launching cursor background agent...");
                resultAgent = cursorAgent.launch(prompt, model, repository);
                System.out.println("✅ Cursor background agent launched successfully!");
                break;

            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }

        monitorAgentWithCustomLogic(resultAgent, cursorAgent, delaySeconds);
        return resultAgent;
    }

    private void monitorAgentWithCustomLogic(Agent agent, CursorAgent cursorAgent, int delaySeconds) throws Exception {
        System.out.println("🔄 Starting to monitor agent status...");
        System.out.println("📊 Checking status every " + delaySeconds + " seconds");
        System.out.println("⏹️  Press Ctrl+C to stop monitoring\n");

        int checkCount = 0;

        while (true) {
            try {
                checkCount++;

                // Get current agent status
                AgentState currentAgentState = getCurrentAgent(cursorAgent, agent.getId());

                // Always show status updates
                String currentTime = getCurrentTime();
                System.out.printf("📊 %s - Status Update #%d: %s%n", currentTime, checkCount, currentAgentState);

                // Check if agent has completed or failed
                if (currentAgentState.isTerminal()) {
                    System.out.println("\n✅ Agent monitoring completed!");
                    System.out.println("🏁 Final Status: " + currentAgentState);

                    if (currentAgentState.isSuccessful()) {
                        System.out.println("🎉 Agent completed successfully!");
                    } else if (currentAgentState.isFailed()) {
                        System.out.println("❌ Agent failed or was terminated!");
                    }

                    // Show elapsed time
                    String totalTime = getFormattedElapsedTime();
                    System.out.println("⏱️  Total time: " + totalTime);
                    System.out.println();
                    break;
                }

                // Wait for the next check
                Thread.sleep(delaySeconds * 1000L);

            } catch (InterruptedException e) {
                System.out.println("\n⏹️  Monitoring interrupted by user");
                Thread.currentThread().interrupt();
                throw e;
            } catch (Exception e) {
                System.err.println("⚠️  Error during status check: " + e.getMessage());
                // Continue monitoring despite errors
                Thread.sleep(delaySeconds * 1000L);
            }
        }
    }

    private AgentState getCurrentAgent(CursorAgent cursorAgent, String agentId) {
        try {
            Agent agent = cursorAgent.getStatus(agentId);
            return AgentState.of(agent);
        } catch (Exception e) {
            System.err.println("⚠️  Error retrieving agent: " + e.getMessage());
            return AgentState.UNKNOWN;
        }
    }

    /**
     * Gets the current time formatted as HH:MM:SS.
     *
     * @return formatted current time string (e.g., "14:30:45")
     */
    private String getCurrentTime() {
        LocalTime now = LocalTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Calculates and formats the elapsed time since execution started.
     *
     * @return formatted time string (e.g., "2m 30s")
     */
    private String getFormattedElapsedTime() {
        if (startTime == 0) {
            return "unknown";
        }

        long elapsedMs = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsedMs / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

}
