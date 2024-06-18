package io.logicojp.micronaut.factory;

import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import static io.logicojp.micronaut.LC4JConstant.AZURE_OPENAI_ENDPOINT;
import static io.logicojp.micronaut.LC4JConstant.AZURE_OPENAI_KEY;

@Factory
public class StreamChatModelFactory {

    @Singleton
    public StreamingChatLanguageModel getStreamingChatLanguageModel() {
        return AzureOpenAiStreamingChatModel.builder()
                .endpoint(AZURE_OPENAI_ENDPOINT)
                .apiKey(AZURE_OPENAI_KEY)
                .deploymentName("gpt-4o")
                .temperature(0.0)
                .logRequestsAndResponses(true)
                .maxRetries(3)
                .build();
    }
}
