package io.logicojp.micronaut.factory;

import com.azure.ai.openai.models.ImageGenerationResponseFormat;
import dev.langchain4j.model.azure.AzureOpenAiImageModel;
import dev.langchain4j.model.image.ImageModel;
import io.micronaut.context.annotation.*;
import jakarta.inject.Singleton;

import static io.logicojp.micronaut.LC4JConstant.AZURE_OPENAI_ENDPOINT;
import static io.logicojp.micronaut.LC4JConstant.AZURE_OPENAI_KEY;

@Factory
public class ImageModelFactory {

    @Singleton
    public ImageModel getImageModel() {
        return AzureOpenAiImageModel.builder()
                .endpoint(AZURE_OPENAI_ENDPOINT)
                .apiKey(AZURE_OPENAI_KEY)
                .deploymentName("dall-e-3")
                .logRequestsAndResponses(true)
                .responseFormat(ImageGenerationResponseFormat.URL)
                .build();
    }
}
