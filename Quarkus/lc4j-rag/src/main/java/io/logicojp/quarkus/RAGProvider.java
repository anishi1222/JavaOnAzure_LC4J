package io.logicojp.quarkus;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/embedding")
public class RAGProvider {
    @Inject
    ChatLanguageModel chatLanguageModel;
    @Inject
    EmbeddingStore<TextSegment> embeddingStore;
    @Inject
    EmbeddingModel embeddingModel;

    public RAGProvider(ChatLanguageModel chatLanguageModel, EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.chatLanguageModel = chatLanguageModel;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

interface Assistant {
    @SystemMessage("""
    あなたはJava言語のエキスパートです。
    Instructions:
    - Java SE 22言語仕様に基づいて回答してください。
    - 参考文献は英語ですが、回答は日本語でお願いします。
    - 回答がわからない場合は、"わからない" と答えてください。
    - 参考文献のURLを提示してください。
    """)
    Result<String> answer(String query);
}

Assistant createAssistant() {
    ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .maxResults(3)
            .minScore(0.6)
            .build();

    ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
    return AiServices.builder(Assistant.class)
            .chatLanguageModel(chatLanguageModel)
            .contentRetriever(contentRetriever)
            .chatMemory(chatMemory)
            .build();
}

    @GET()
    @Path("/answer")
    @Produces(MediaType.TEXT_PLAIN)
    public String answer(
            @DefaultValue("リフレクションとは何ですか？")
            @QueryParam("q")
            String query) {
        // プロンプトの受け渡しと回答の取得
        Assistant assistant = createAssistant();
        return assistant.answer(query).content();
    }

    @GET()
    @Path("/load")
    @Produces(MediaType.TEXT_PLAIN)
    public Response load(@QueryParam("url") Optional<String> url) {
        if(url.isEmpty()) {
            try(Response response = Response.status(Response.Status.BAD_REQUEST).entity("URL is required!").build()) {
                return response;
            }
        }
        // HTMLからテキストを抽出し、分割してEmbeddingを生成し、Embedding Storeに格納
        DocumentParser documentParser = new TextDocumentParser();
        HtmlTextExtractor htmlTextExtractor = new HtmlTextExtractor();
        Document document = htmlTextExtractor.transform(UrlDocumentLoader.load(url.get(), documentParser));
        DocumentSplitter splitter = DocumentSplitters.recursive(600,30);
        List<TextSegment> segments = splitter.split(document);
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        this.embeddingStore.addAll(embeddings, segments);
        System.out.println("Loaded " + url);
        return Response.ok("Loaded!").build();
    }
}


