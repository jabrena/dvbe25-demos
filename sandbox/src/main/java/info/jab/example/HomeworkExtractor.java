package info.jab.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts homework assignments from CIS-194 webpage and converts to JSON format.
 */
public class HomeworkExtractor {

    private static final String CIS_194_URL = "https://www.cis.upenn.edu/~cis1940/spring13/lectures.html";

    public static class Homework {
        public int number;
        public String title;
        public String pdfUrl;
        public String dueDate;
        public List<String> additionalFiles = new ArrayList<>();

        public Homework(int number, String title, String pdfUrl, String dueDate) {
            this.number = number;
            this.title = title;
            this.pdfUrl = pdfUrl;
            this.dueDate = dueDate;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("    {\n");
            sb.append("      \"number\": ").append(number).append(",\n");
            sb.append("      \"title\": \"").append(title).append("\",\n");
            sb.append("      \"pdfUrl\": \"").append(pdfUrl).append("\",\n");
            sb.append("      \"dueDate\": \"").append(dueDate).append("\"");
            if (!additionalFiles.isEmpty()) {
                sb.append(",\n      \"additionalFiles\": [\n");
                for (int i = 0; i < additionalFiles.size(); i++) {
                    sb.append("        \"").append(additionalFiles.get(i)).append("\"");
                    if (i < additionalFiles.size() - 1) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }
                sb.append("      ]");
            }
            sb.append("\n    }");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        try {
            String htmlContent = fetchHtmlContent();
            List<Homework> homeworks = extractHomeworks(htmlContent);
            String json = convertToJson(homeworks);
            
            System.out.println("<result>" + json + "</result>");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String fetchHtmlContent() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CIS_194_URL))
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static List<Homework> extractHomeworks(String htmlContent) {
        List<Homework> homeworks = new ArrayList<>();
        
        // Pattern to match homework entries
        Pattern homeworkPattern = Pattern.compile(
            "<li><a href=\"hw/(\\d+)-.*?\\.pdf\">Homework (\\d+)</a>: due (.*?)\\.(.*?)</li>",
            Pattern.DOTALL
        );
        
        Matcher matcher = homeworkPattern.matcher(htmlContent);
        
        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            String title = "Homework " + matcher.group(2);
            String pdfUrl = "https://www.cis.upenn.edu/~cis1940/spring13/hw/" + matcher.group(1) + "-" + getHomeworkFilename(number) + ".pdf";
            String dueDate = matcher.group(3).trim();
            
            Homework hw = new Homework(number, title, pdfUrl, dueDate);
            
            // Extract additional files if present
            String additionalFilesSection = matcher.group(4);
            if (additionalFilesSection != null && additionalFilesSection.contains("[")) {
                Pattern filePattern = Pattern.compile("href=\"extras/\\d+-.*?/(.*?)\"");
                Matcher fileMatcher = filePattern.matcher(additionalFilesSection);
                while (fileMatcher.find()) {
                    hw.additionalFiles.add(fileMatcher.group(1));
                }
            }
            
            homeworks.add(hw);
        }
        
        return homeworks;
    }
    
    private static String getHomeworkFilename(int number) {
        String[] filenames = {
            "", "intro", "ADTs", "rec-poly", "higher-order", "type-classes", 
            "laziness", "folds-monoids", "IO", "functors", "applicative", 
            "applicative2", "monads"
        };
        return number < filenames.length ? filenames[number] : "unknown";
    }

    private static String convertToJson(List<Homework> homeworks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < homeworks.size(); i++) {
            sb.append(homeworks.get(i).toString());
            if (i < homeworks.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]");
        return sb.toString();
    }
}