package info.jab.churrera;

import info.jab.churrera.agent.BaseAgent;
import info.jab.churrera.util.CursorApiKeyResolver;
import static info.jab.churrera.util.PmlConverter.toMarkdown;

public class PiAgent extends BaseAgent {

    public static void main(String[] args) throws Exception {
        PiAgent agent = new PiAgent();
        agent.executeWorkflow();
    }

    public void executeWorkflow() {

        // Launch initial agent
        var agent = launchAgent(toMarkdown("examples/pi/prompt1.xml"));

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("examples/pi/prompt2.xml"), agent.getId());

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("examples/pi/prompt3.xml"), agent.getId());

        // Add new prompt using the agent ID from the previous operation
        agent = updateAgent(toMarkdown("examples/pi/prompt4.xml"), agent.getId());

        // Show review message
        showCompletionMessage(agent);
    }
}
