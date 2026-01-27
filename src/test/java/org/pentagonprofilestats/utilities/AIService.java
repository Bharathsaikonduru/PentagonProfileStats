package org.pentagonprofilestats.utilities;

import okhttp3.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.util.concurrent.TimeUnit;

public class AIService {
    // Read Azure details from System Properties (passed via Maven/Jenkins)
    private static final String API_KEY = System.getProperty("azure.api.key");
    private static final String RESOURCE_NAME = System.getProperty("azure.resource.name");
    private static final String DEPLOYMENT_NAME = System.getProperty("azure.deployment.name");
    private static final String API_VERSION = "2025-01-01-preview"; // Common stable version

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * Constructs the specific Azure Endpoint URL
     */
    private static String getAzureEndpoint() {
        if (RESOURCE_NAME == null || DEPLOYMENT_NAME == null) return null;
        return String.format("https://%s.openai.azure.com/openai/deployments/%s/chat/completions?api-version=%s",
                RESOURCE_NAME, DEPLOYMENT_NAME, API_VERSION);
    }

    public static String analyzeFailure(String testName, Throwable error, String pageSource) {
        if (API_KEY == null) return "AI Analysis Skipped: No API Key found.";

        String prompt = String.format(
                "You are a QA Automation Expert. A Selenium test '%s' failed.\n" +
                        "Error Message: %s\n" +
                        "Stack Trace (First 3 lines): %s\n" +
                        "Analyze this error. If it mentions a missing element, suggest why it might be missing based on common Selenium issues. Keep it concise.",
                testName, error.getMessage(), getStackTraceHead(error));

        return callAzureAI(prompt);
    }

    public static String selfHealLocator(String oldLocator, String pageSource) {
        if (API_KEY == null) return null;

        // Truncate to avoid token limits
        String snippet = pageSource.length() > 5000 ? pageSource.substring(0, 5000) : pageSource;

        String prompt = String.format(
                "I have a broken Selenium locator: '%s'.\n" +
                        "Here is a snippet of the HTML page source:\n```html\n%s\n```\n" +
                        "Find the most likely new valid CSS Selector or XPath for this element. " +
                        "Return ONLY the locator string, nothing else. If you cannot find it, return 'NOT_FOUND'.",
                oldLocator, snippet);

        String result = callAzureAI(prompt);
        return (result == null || result.contains("NOT_FOUND")) ? null : result.trim();
    }

    private static String callAzureAI(String content) {
        String endpoint = getAzureEndpoint();
        if (endpoint == null) return "Configuration Error: Missing Azure Resource Name or Deployment Name.";

        try {
            // Prepare Request Body
            JsonObject jsonBody = new JsonObject();
            // Note: In Azure, 'model' parameter is usually ignored as the deployment defines it,
            // but we focus on 'messages'.

            JsonArray messages = new JsonArray();
            JsonObject msg = new JsonObject();
            msg.addProperty("role", "user");
            msg.addProperty("content", content);
            messages.add(msg);

            jsonBody.add("messages", messages);
            jsonBody.addProperty("temperature", 0.0); // Keep it deterministic for code fixes

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json"));

            // Azure-specific headers
            Request request = new Request.Builder()
                    .url(endpoint)
                    .addHeader("api-key", API_KEY) // Different from OpenAI!
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return "Azure API Call Failed: " + response.code() + " - " + response.message();
                }
                String responseBody = response.body().string();

                // Parse Response
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                    return jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();
                } else {
                    return "Empty response from AI";
                }
            }
        } catch (Exception e) {
            return "AI Error: " + e.getMessage();
        }
    }

    private static String getStackTraceHead(Throwable t) {
        StackTraceElement[] stack = t.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, stack.length); i++) {
            sb.append(stack[i].toString()).append("\n");
        }
        return sb.toString();
    }
}