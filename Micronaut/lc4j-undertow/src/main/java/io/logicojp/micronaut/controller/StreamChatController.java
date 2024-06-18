package io.logicojp.micronaut.controller;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller("/chat")
public class StreamChatController {

    @Inject
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    public StreamChatController(StreamingChatLanguageModel streamingChatLanguageModel) {
        this.streamingChatLanguageModel = streamingChatLanguageModel;
    }

    @Get( "/stream")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getAnswerWithStreaming(
            @QueryValue(value = "u", defaultValue = "誰がモナリザの微笑を描きましたか？") String userPrompt) {
        CompletableFuture<Response<AiMessage>> responseCompletableFuture = new CompletableFuture<>();
        final String[] answer = new String[1];
        OpenAiTokenizer azureOpenAiTokenizer = new OpenAiTokenizer("gpt-4o");
        UserMessage userMessage = UserMessage.from(userPrompt);
        Map<String, Integer> tokenUsage = new HashMap<>();
        tokenUsage.put("input", azureOpenAiTokenizer.estimateTokenCountInMessage(userMessage));

        streamingChatLanguageModel.generate(userMessage, new StreamingResponseHandler<>() {
            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                answer[0] = response.content().text();
                responseCompletableFuture.complete(response);
            }

            @Override
            public void onError(Throwable throwable) {
                responseCompletableFuture.completeExceptionally(throwable);
            }
        });

        System.out.println("Waiting for response...");
        responseCompletableFuture.join();
        try {
            tokenUsage.put("output", azureOpenAiTokenizer.estimateTokenCountInMessage(responseCompletableFuture.get().content()));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return List.of(Map.of("question", userPrompt),
                Map.of("answer", answer[0]),
                Map.of("input", tokenUsage.get("input").toString()),
                Map.of("output", tokenUsage.get("output").toString()));
    }
}
