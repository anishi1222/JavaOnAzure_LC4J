package io.logicojp.quarkus;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/chat")
public class ChatProvider {
    @Inject
    ChatLanguageModel chatLanguageModel;

    public ChatProvider(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @GET()
    @Path("/simple")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAnswer(
            @DefaultValue("誰がモナリザの微笑を描きましたか？") @QueryParam("u") String userPrompt) {
        UserMessage userMessage = UserMessage.from(userPrompt);
        return chatLanguageModel.generate(userMessage).content().text();
    }
}
