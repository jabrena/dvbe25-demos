package info.jab.churrera;

import info.jab.churrera.agent.BaseAgent;
import static info.jab.churrera.util.ClasspathResolver.retrieve;
import static info.jab.churrera.util.FileStorage.storeToFile;
import static info.jab.churrera.util.ConversationJsonDeserializer.deserializeList;

import java.util.List;
import java.util.Optional;

/**
 * https://github.com/jabrena/cis-194
 * https://www.cis.upenn.edu/~cis1940/spring13/lectures.html
 */
public class CIS194Agent extends BaseAgent {

    public static void main(String[] args) {

        CIS194Agent agent = new CIS194Agent();
        agent.executeWorkflow();
    }

    public void executeWorkflow() {

        // Launch initial agent
        var agent = launchAgent(retrieve("examples/cis194/prompt2-new.md"));

        String agentConversation = getAgentConversation(agent.getId());

        // Show review message
        showCompletionMessage(agent);

        // Store agent conversation to resources for testing
        //storeToFile(agentConversation, agent.getId() + "-sample.txt", "churrera/churrera-demo/src/test/resources");

        // Try to deserialize as JSON list of Cis194Homework records
        Optional<List<Cis194Homework>> homeworks = deserializeList(agentConversation, Cis194Homework.class);

        homeworks.ifPresentOrElse(
            this::displayHomeworkAssignments,
            () -> System.out.println("\n❌ Failed to deserialize CIS-194 homework assignments.")
        );
    }

    /**
     * Displays the homework assignments in a formatted way.
     *
     * @param homeworks the list of homework assignments to display
     */
    private void displayHomeworkAssignments(List<Cis194Homework> homeworks) {
        System.out.println("\n🔍 Successfully deserialized " + homeworks.size() + " CIS-194 homework assignments:");
        System.out.println("=" + "=".repeat(80));

        homeworks.forEach(homework -> {
            System.out.println("\n📚 " + homework.title());
            System.out.println("   Number: " + homework.number());
            System.out.println("   PDF URL: " + homework.pdfUrl());

            if (homework.additionalFiles() != null && !homework.additionalFiles().isEmpty()) {
                System.out.println("   Additional Files:");
                homework.additionalFiles().forEach(file ->
                    System.out.println("     • " + file)
                );
            } else {
                System.out.println("   Additional Files: None");
            }

            System.out.println("   " + "-".repeat(60));
        });

        System.out.println("\n✅ Total: " + homeworks.size() + " homework assignments processed successfully!");
    }

    /**
     * Record representing a CIS-194 homework assignment.
     *
     * @param number the homework number
     * @param title the homework title
     * @param pdfUrl the URL to the PDF file
     * @param additionalFiles optional list of additional files for the homework
     */
    public record Cis194Homework(
        int number,
        String title,
        String pdfUrl,
        List<String> additionalFiles
    ) {

        /**
         * Constructor with default empty list for additionalFiles when not provided.
         */
        public Cis194Homework(int number, String title, String pdfUrl) {
            this(number, title, pdfUrl, List.of());
        }

        /**
         * Compact constructor to ensure additionalFiles is never null.
         */
        public Cis194Homework {
            if (additionalFiles == null) {
                additionalFiles = List.of();
            }
        }
    }
}
