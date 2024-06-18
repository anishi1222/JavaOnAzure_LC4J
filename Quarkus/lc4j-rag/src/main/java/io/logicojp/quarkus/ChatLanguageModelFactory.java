package io.logicojp.quarkus;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class ChatLanguageModelFactory {

    @ConfigProperty(name = "azure.openai.endpoint")
    String azureOpenAiEndpoint;
    @ConfigProperty(name = "azure.openai.key")
    String azureOpenAiKey;

    @Singleton
    public ChatLanguageModel getChatLanguageModel() {

        return AzureOpenAiChatModel.builder()
                .endpoint(azureOpenAiEndpoint)
                .apiKey(azureOpenAiKey)
                .deploymentName("gpt-4o")
                .temperature(0.0)
                .logRequestsAndResponses(true)
                .maxRetries(3)
                .build();
    }
}
