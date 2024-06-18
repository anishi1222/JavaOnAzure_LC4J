package io.logicojp.quarkus;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.azure.search.AzureAiSearchEmbeddingStore;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class EmbeddingStoreFactory {

    @ConfigProperty(name = "azure.search.endpoint")
    String azureSearchEndpoint;
    @ConfigProperty(name = "azure.search.key")
    String azureSearchKey;

    @Singleton
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        return AzureAiSearchEmbeddingStore.builder()
                .endpoint(azureSearchEndpoint)
                .apiKey(azureSearchKey)
                .createOrUpdateIndex(true)
                .dimensions(1536)
                .indexName("rag-demo")
                .build();
    }
}
