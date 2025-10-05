package info.jab.churrera;

import info.jab.churrera.agent.BaseAgent;
import static info.jab.churrera.util.PmlConverter.toMarkdown;
import static info.jab.churrera.util.XmlResultDeserializer.extractResultAs;

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

        // Add new prompt using the agent ID from the previous operation
        //agent = updateAgent(toMarkdown("examples/pi/prompt5.xml"), agent.getId());

        // Get agent conversation to extract the result
        //String agentConversation = getAgentConversation(agent.getId());
        //System.out.println("\n🔍 Result: " + extractResultAs(agentConversation, String.class).get());

        // Show review message
        showCompletionMessage(agent);
    }
}
