package info.jab.churrera;

import info.jab.cursor.CursorAgent;
import info.jab.churrera.util.CursorApiKeyResolver;
import info.jab.churrera.agent.BaseAgent;
import static info.jab.churrera.util.PmlConverter.toMarkdown;
import static info.jab.churrera.util.ClasspathResolver.retrieve;

public class HelloWorldAgent extends BaseAgent {

    public static void main(String[] args) throws Exception {
        HelloWorldAgent agent = new HelloWorldAgent();
        agent.executeWorkflow();
    }

    public void executeWorkflow() {

        // Launch initial agent
        var agent = launchAgent(toMarkdown("examples/hello-world/prompt1.xml"));

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("examples/hello-world/prompt2.xml"), agent.getId());

        // Show review message
        showCompletionMessage(agent);
    }
}
