package com.jacob.openfga.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jacob.openfga.exception.OpenFGAException;
import com.jacob.openfga.model.CheckRequest;
import com.jacob.openfga.model.CheckResponse;
import com.jacob.openfga.model.ListObjectsRequest;
import com.jacob.openfga.model.ListObjectsResponse;
import com.jacob.openfga.model.TupleRequest;
import com.jacob.openfga.model.TupleResponse;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientCheckRequest;
import dev.openfga.sdk.api.client.model.ClientCheckResponse;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientListObjectsResponse;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import dev.openfga.sdk.api.client.model.ClientWriteResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link OpenFGAService}, mocking the OpenFGA SDK client so the
 * service logic (request translation, response mapping, error handling) can be
 * verified in isolation.
 */
@ExtendWith(MockitoExtension.class)
class OpenFGAServiceTest {

    @Mock
    private OpenFgaClient fgaClient;

    @InjectMocks
    private OpenFGAService service;

    @Test
    @DisplayName("check() returns allowed=true when OpenFGA grants access")
    void check_returnsAllowed_whenGranted() throws Exception {
        ClientCheckResponse sdkResponse = mock(ClientCheckResponse.class);
        when(sdkResponse.getAllowed()).thenReturn(true);
        when(fgaClient.check(any(ClientCheckRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(sdkResponse));

        CheckResponse result = service.check(
                new CheckRequest("user:anne", "reader", "document:roadmap"));

        assertThat(result.isAllowed()).isTrue();
        assertThat(result.getUser()).isEqualTo("user:anne");
        assertThat(result.getRelation()).isEqualTo("reader");
        assertThat(result.getObject()).isEqualTo("document:roadmap");
    }

    @Test
    @DisplayName("check() returns allowed=false when OpenFGA denies access")
    void check_returnsDenied_whenNotGranted() throws Exception {
        ClientCheckResponse sdkResponse = mock(ClientCheckResponse.class);
        when(sdkResponse.getAllowed()).thenReturn(false);
        when(fgaClient.check(any(ClientCheckRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(sdkResponse));

        CheckResponse result = service.check(
                new CheckRequest("user:bob", "reader", "document:roadmap"));

        assertThat(result.isAllowed()).isFalse();
    }

    @Test
    @DisplayName("check() wraps SDK failures in OpenFGAException")
    void check_wrapsFailures() throws Exception {
        when(fgaClient.check(any(ClientCheckRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("connection refused")));

        assertThatThrownBy(() -> service.check(
                new CheckRequest("user:anne", "reader", "document:roadmap")))
                .isInstanceOf(OpenFGAException.class)
                .hasMessageContaining("check");
    }

    @Test
    @DisplayName("writeTuple() confirms a successful write and forwards the tuple to the SDK")
    void writeTuple_succeeds() throws Exception {
        when(fgaClient.write(any(ClientWriteRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(ClientWriteResponse.class)));

        TupleResponse result = service.writeTuple(
                new TupleRequest("user:anne", "reader", "document:roadmap"));

        assertThat(result.getMessage()).contains("written");
        assertThat(result.getObject()).isEqualTo("document:roadmap");
        verify(fgaClient).write(any(ClientWriteRequest.class));
    }

    @Test
    @DisplayName("deleteTuple() confirms a successful delete")
    void deleteTuple_succeeds() throws Exception {
        when(fgaClient.write(any(ClientWriteRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(ClientWriteResponse.class)));

        TupleResponse result = service.deleteTuple(
                new TupleRequest("user:anne", "reader", "document:roadmap"));

        assertThat(result.getMessage()).contains("deleted");
        verify(fgaClient).write(any(ClientWriteRequest.class));
    }

    @Test
    @DisplayName("writeTuple() wraps SDK failures in OpenFGAException")
    void writeTuple_wrapsFailures() throws Exception {
        when(fgaClient.write(any(ClientWriteRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("boom")));

        assertThatThrownBy(() -> service.writeTuple(
                new TupleRequest("user:anne", "reader", "document:roadmap")))
                .isInstanceOf(OpenFGAException.class)
                .hasMessageContaining("write tuple");
    }

    @Test
    @DisplayName("listObjects() maps the SDK object list into the response")
    void listObjects_returnsObjects() throws Exception {
        ClientListObjectsResponse sdkResponse = mock(ClientListObjectsResponse.class);
        when(sdkResponse.getObjects()).thenReturn(List.of("document:roadmap", "document:budget"));
        when(fgaClient.listObjects(any(ClientListObjectsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(sdkResponse));

        ListObjectsResponse result = service.listObjects(
                new ListObjectsRequest("user:anne", "reader", "document"));

        assertThat(result.getObjects()).containsExactly("document:roadmap", "document:budget");
        assertThat(result.getType()).isEqualTo("document");
    }

    @Test
    @DisplayName("listObjects() wraps SDK failures in OpenFGAException")
    void listObjects_wrapsFailures() throws Exception {
        when(fgaClient.listObjects(any(ClientListObjectsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("timeout")));

        assertThatThrownBy(() -> service.listObjects(
                new ListObjectsRequest("user:anne", "reader", "document")))
                .isInstanceOf(OpenFGAException.class)
                .hasMessageContaining("list objects");
    }
}
