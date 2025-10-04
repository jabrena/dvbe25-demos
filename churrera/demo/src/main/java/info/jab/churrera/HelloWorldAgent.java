package info.jab.churrera;

import info.jab.cursor.CursorAgent;
import static info.jab.churrera.AgentOperation.LAUNCH;
import info.jab.churrera.utils.ApiKeyResolver;

public class HelloWorldAgent extends BaseAgent {

    public static void main(String[] args) {

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

    public void executeWorkflow() {
        startTime = System.currentTimeMillis();

        try {
            // Launch initial agent
            var agent = executeAgentOperation(LAUNCH, "hello-world/prompt1.md");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}