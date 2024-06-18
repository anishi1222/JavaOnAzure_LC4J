package io.logicojp.micronaut.controller;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@Controller("/image")
public class ImageController {

    @Inject
    private final ImageModel imageModel;

    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @Get("/dall-e")
    @Produces(MediaType.APPLICATION_JSON)
    List<Map<String, String>> createImage(
            @QueryValue(value = "u",
                        defaultValue = "夏の終わりの夕暮れ時に、海辺をドライブする様子をアメリカンポップアート風に描いてください。")
            String question) {
        Response<Image> image = imageModel.generate(question);
        return List.of(Map.of("question", question),
                Map.of("image", image.content().url().toString()));
    }
}
