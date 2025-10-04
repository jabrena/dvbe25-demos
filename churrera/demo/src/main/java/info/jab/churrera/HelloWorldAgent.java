package info.jab.churrera;

import info.jab.cursor.CursorAgent;
import info.jab.churrera.util.ApiKeyResolver;
import info.jab.churrera.agent.BaseAgent;
import static info.jab.churrera.util.PmlConverter.toMarkdown;
import static info.jab.churrera.util.ClasspathResolver.retrieve;

public class HelloWorldAgent extends BaseAgent {

    public static void main(String[] args) throws Exception {

        //Agent configuration
        String model =  "claude-4-sonnet";
        String repository = "https://github.com/jabrena/dvbe25-demos";
        Integer delaySeconds = 30;
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
        var agent = launchAgent(toMarkdown("hello-world/prompt1.xml"));

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("hello-world/prompt2.xml"), agent.getId());

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(retrieve("hello-world/prompt3.md"), agent.getId());

        // Show review message
        showCompletionMessage(agent);
    }
}
