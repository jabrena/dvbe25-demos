package info.jab.churrera.util;

import info.jab.churrera.CIS194Agent.Cis194Homework;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for ConversationJsonDeserializer.
 */
class ConversationJsonDeserializerTest {

    @Test
    void deserialize_WithValidResultTag_ShouldReturnDeserializedObject() {
        // Given
        String conversationContent = """
            Some conversation content here.
            <result>
            {
              "number": 1,
              "title": "Homework 1",
              "pdfUrl": "https://example.com/hw1.pdf",
              "additionalFiles": ["file1.txt", "file2.txt"]
            }
            </result>
            More content after.
            """;

        // When
        Optional<Cis194Homework> result = ConversationJsonDeserializer.deserialize(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isPresent();
        Cis194Homework homework = result.get();
        assertThat(homework.number()).isEqualTo(1);
        assertThat(homework.title()).isEqualTo("Homework 1");
        assertThat(homework.pdfUrl()).isEqualTo("https://example.com/hw1.pdf");
        assertThat(homework.additionalFiles()).containsExactly("file1.txt", "file2.txt");
    }

    @Test
    void deserializeList_WithValidResultTag_ShouldReturnDeserializedList() {
        // Given
        String conversationContent = """
            Some conversation content here.
            <result>
            [
              {
                "number": 1,
                "title": "Homework 1",
                "pdfUrl": "https://example.com/hw1.pdf",
                "additionalFiles": ["file1.txt"]
              },
              {
                "number": 2,
                "title": "Homework 2",
                "pdfUrl": "https://example.com/hw2.pdf"
              }
            ]
            </result>
            More content after.
            """;

        // When
        Optional<List<Cis194Homework>> result = ConversationJsonDeserializer.deserializeList(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isPresent();
        List<Cis194Homework> homeworks = result.get();
        assertThat(homeworks).hasSize(2);

        Cis194Homework homework1 = homeworks.get(0);
        assertThat(homework1.number()).isEqualTo(1);
        assertThat(homework1.title()).isEqualTo("Homework 1");
        assertThat(homework1.pdfUrl()).isEqualTo("https://example.com/hw1.pdf");
        assertThat(homework1.additionalFiles()).containsExactly("file1.txt");

        Cis194Homework homework2 = homeworks.get(1);
        assertThat(homework2.number()).isEqualTo(2);
        assertThat(homework2.title()).isEqualTo("Homework 2");
        assertThat(homework2.pdfUrl()).isEqualTo("https://example.com/hw2.pdf");
        assertThat(homework2.additionalFiles()).isEmpty();
    }

    @Test
    void deserialize_WithTestFile1_ShouldReturnValidList() {
        // Given
        String conversationContent = ClasspathResolver.retrieve("bc-2a175308-84d3-471d-89a8-f10939adacd9-sample.txt");

        // When
        Optional<List<Cis194Homework>> result = ConversationJsonDeserializer.deserializeList(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isPresent();
        List<Cis194Homework> homeworks = result.get();
        assertThat(homeworks).hasSize(12);

        // Verify first homework
        Cis194Homework homework1 = homeworks.get(0);
        assertThat(homework1.number()).isEqualTo(1);
        assertThat(homework1.title()).isEqualTo("Homework 1");
        assertThat(homework1.pdfUrl()).isEqualTo("https://www.cis.upenn.edu/~cis1940/spring13/hw/01-intro.pdf");
        assertThat(homework1.additionalFiles()).isEmpty();

        // Verify second homework (has additional files)
        Cis194Homework homework2 = homeworks.get(1);
        assertThat(homework2.number()).isEqualTo(2);
        assertThat(homework2.title()).isEqualTo("Homework 2");
        assertThat(homework2.pdfUrl()).isEqualTo("https://www.cis.upenn.edu/~cis1940/spring13/hw/02-ADTs.pdf");
        assertThat(homework2.additionalFiles()).containsExactly("error.log", "sample.log", "Log.hs");

        // Verify last homework
        Cis194Homework homework12 = homeworks.get(11);
        assertThat(homework12.number()).isEqualTo(12);
        assertThat(homework12.title()).isEqualTo("Homework 12");
        assertThat(homework12.pdfUrl()).isEqualTo("https://www.cis.upenn.edu/~cis1940/spring13/hw/12-monads.pdf");
        assertThat(homework12.additionalFiles()).containsExactly("Risk.hs");
    }

    @Test
    void deserialize_WithTestFile2_ShouldReturnValidList() {
        // Given
        String conversationContent = ClasspathResolver.retrieve("bc-dfc0cae7-ef6e-4e1d-bcc2-b7652117168b-sample.txt");

        // When
        Optional<List<Cis194Homework>> result = ConversationJsonDeserializer.deserializeList(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isPresent();
        List<Cis194Homework> homeworks = result.get();
        assertThat(homeworks).hasSize(12);

        // Verify first homework
        Cis194Homework homework1 = homeworks.get(0);
        assertThat(homework1.number()).isEqualTo(1);
        assertThat(homework1.title()).isEqualTo("Homework 1");
        assertThat(homework1.pdfUrl()).isEqualTo("https://www.cis.upenn.edu/~cis1940/spring13/hw/01-intro.pdf");
        assertThat(homework1.additionalFiles()).isEmpty();

        // Verify homework with additional files
        Cis194Homework homework5 = homeworks.get(4);
        assertThat(homework5.number()).isEqualTo(5);
        assertThat(homework5.title()).isEqualTo("Homework 5");
        assertThat(homework5.pdfUrl()).isEqualTo("https://www.cis.upenn.edu/~cis1940/spring13/hw/05-type-classes.pdf");
        assertThat(homework5.additionalFiles()).containsExactly("ExprT.hs", "Parser.hs", "StackVM.hs");
    }

    @Test
    void deserialize_WithNoResultTag_ShouldReturnEmpty() {
        // Given
        String conversationContent = "Some content without result tags.";

        // When
        Optional<Cis194Homework> result = ConversationJsonDeserializer.deserialize(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void deserialize_WithInvalidJson_ShouldReturnEmpty() {
        // Given
        String conversationContent = """
            <result>
            { invalid json content
            </result>
            """;

        // When
        Optional<Cis194Homework> result = ConversationJsonDeserializer.deserialize(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void deserialize_WithNullContent_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> ConversationJsonDeserializer.deserialize(null, Cis194Homework.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Conversation content cannot be null");
    }

    @Test
    void deserialize_WithNullTargetType_ShouldThrowException() {
        // Given
        String conversationContent = "<result>{}</result>";

        // When & Then
        assertThatThrownBy(() -> ConversationJsonDeserializer.deserialize(conversationContent, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Target type cannot be null");
    }

    @Test
    void deserializeList_WithNullContent_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> ConversationJsonDeserializer.deserializeList(null, Cis194Homework.class))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Conversation content cannot be null");
    }

    @Test
    void deserializeList_WithNullTargetType_ShouldThrowException() {
        // Given
        String conversationContent = "<result>[]</result>";

        // When & Then
        assertThatThrownBy(() -> ConversationJsonDeserializer.deserializeList(conversationContent, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Target type cannot be null");
    }

    @Test
    void deserialize_WithCaseInsensitiveResultTag_ShouldWork() {
        // Given
        String conversationContent = """
            <RESULT>
            {
              "number": 1,
              "title": "Homework 1",
              "pdfUrl": "https://example.com/hw1.pdf"
            }
            </RESULT>
            """;

        // When
        Optional<Cis194Homework> result = ConversationJsonDeserializer.deserialize(conversationContent, Cis194Homework.class);

        // Then
        assertThat(result).isPresent();
        Cis194Homework homework = result.get();
        assertThat(homework.number()).isEqualTo(1);
        assertThat(homework.title()).isEqualTo("Homework 1");
    }
}
