package info.jab.churrera;

import info.jab.cursor.CursorAgent;
import static info.jab.churrera.AgentOperation.LAUNCH;
import static info.jab.churrera.AgentOperation.UPDATE;
import info.jab.churrera.utils.ApiKeyResolver;

public class HelloWorldAgent extends BaseAgent {

    public static void main(String[] args) throws Exception {

        //Define the parameters
        String model =  "claude-4-sonnet";
        String repository = "https://github.com/jabrena/dvbe25-demos";
        Integer delaySeconds = 30; // Parse delay from command-line args or use default
        String apiKey = ApiKeyResolver.resolveApiKey();

        //Instantiate the Agent
        HelloWorldAgent agent = new HelloWorldAgent(apiKey, model, repository, delaySeconds);
        agent.executeWorkflow();
    }

    public HelloWorldAgent(String apiKey, String model, String repository, int delaySeconds) {
        super(apiKey, model, repository, delaySeconds);
    }

    public void executeWorkflow() throws Exception {
        startTime = System.currentTimeMillis();

        // Launch initial agent
        var agent = executeAgentOperation(LAUNCH, "hello-world/prompt1.md", null);

        // Add new prompt using the agent ID from the previous operation
        agent = executeAgentOperation(UPDATE, "hello-world/prompt2.md", agent.getId());


        // Add new prompt using the agent ID from the previous operation
        agent = executeAgentOperation(UPDATE, "hello-world/prompt3.md", agent.getId());

        // Show review message
        showCompletionMessage(agent);
    }
}
