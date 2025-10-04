package info.jab.churrera;

import info.jab.churrera.agent.BaseAgent;
import info.jab.churrera.util.ApiKeyResolver;
import static info.jab.churrera.util.PmlConverter.toMarkdown;

public class PiAgent extends BaseAgent {

    public static void main(String[] args) throws Exception {

        //Agent configuration
        String model =  "claude-4-sonnet";
        String repository = "https://github.com/jabrena/dvbe25-demos";
        Integer delaySeconds = 30;
        String apiKey = ApiKeyResolver.resolveApiKey();

        //Instantiate the Agent
        PiAgent agent = new PiAgent(apiKey, model, repository, delaySeconds);
        agent.executeWorkflow();
    }

    public PiAgent(String apiKey, String model, String repository, int delaySeconds) {
        super(apiKey, model, repository, delaySeconds);
    }

    public void executeWorkflow() throws Exception {
        startTime = System.currentTimeMillis();

        // Launch initial agent
        var agent = launchAgent(toMarkdown("pi/prompt1.xml"));

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("pi/prompt2.xml"), agent.getId());

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("pi/prompt3.xml"), agent.getId());

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("pi/prompt4.xml"), agent.getId());

        // Show review message
        showCompletionMessage(agent);
    }
}
