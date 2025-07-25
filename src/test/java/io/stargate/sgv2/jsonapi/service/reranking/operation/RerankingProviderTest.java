package io.stargate.sgv2.jsonapi.service.reranking.operation;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import io.stargate.sgv2.jsonapi.api.request.RerankingCredentials;
import io.stargate.sgv2.jsonapi.testresource.NoGlobalResourcesTestProfile;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(NoGlobalResourcesTestProfile.Impl.class)
public class RerankingProviderTest {

  private static final RerankingCredentials RERANK_CREDENTIALS =
      new RerankingCredentials("test-tenant", Optional.of("mocked reranking api key"));

  @Test
  @SuppressWarnings("unchecked")
  void createPassageBatchesTest() {
    // mock a test reranking provider with maxBatchSize configured to 3
    TestRerankingProvider mockRerankingProvider = new TestRerankingProvider(3);
    // mock 11 passages
    List<String> passages =
        List.of(
            "orange",
            "apple",
            "banana",
            "grape",
            "kiwi",
            "mango",
            "pear",
            "peach",
            "plum",
            "pineapple",
            "strawberry");

    // invoke the private method createPassageBatches
    try {
      java.lang.reflect.Method method =
          RerankingProvider.class.getDeclaredMethod("createPassageBatches", List.class);
      method.setAccessible(true);
      List<List<String>> batches =
          (List<List<String>>) method.invoke(mockRerankingProvider, passages);
      assertThat(batches).hasSize(4);
      assertThat(batches.get(0)).hasSize(3);
      assertThat(batches.get(1)).hasSize(3);
      assertThat(batches.get(2)).hasSize(3);
      assertThat(batches.get(3)).hasSize(2);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void microBatchingTest() {
    // mock a test reranking provider with maxBatchSize configured to 10
    TestRerankingProvider mockRerankingProvider = new TestRerankingProvider(10);

    // mock query string
    String query = "apple";
    // mock 15 passages, which will be split into 2 micro batches
    List<String> passages =
        List.of(
            "orange",
            "apple",
            "banana",
            "grape",
            "kiwi",
            "mango",
            "pear",
            "peach",
            "plum",
            "pineapple",
            "strawberry",
            "blueberry",
            "raspberry",
            "watermelon",
            "cherry");

    final RerankingProvider.RerankingResponse finalResult =
        mockRerankingProvider
            .rerank(query, passages, RERANK_CREDENTIALS)
            .subscribe()
            .withSubscriber(UniAssertSubscriber.create())
            .awaitItem()
            .getItem();

    // check if the final result contains all 15 passages
    assertThat(finalResult.ranks().size()).isEqualTo(passages.size());
    // assert the order of the passages in the final result should be the same as the original
    // passage order
    IntStream.range(0, 15)
        .forEach(i -> assertThat(finalResult.ranks().get(i).index()).isEqualTo(i));
  }
}
