package io.logicojp.quarkus;

import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;


public class EmbeddingModelFactory {
    @ConfigProperty(name = "azure.openai.endpoint")
    String azureOpenAiEndpoint;
    @ConfigProperty(name = "azure.openai.key")
    String azureOpenAiKey;
    @Singleton
    public EmbeddingModel getEmbeddingModel() {
        return AzureOpenAiEmbeddingModel.builder()
                .endpoint(azureOpenAiEndpoint)
                .apiKey(azureOpenAiKey)
                .deploymentName("text-embedding-3-small")
                .tokenizer(new OpenAiTokenizer(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL))
                .logRequestsAndResponses(true)
                .build();
    }
}
