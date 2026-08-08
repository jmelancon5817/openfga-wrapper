package com.jacob.openfga.service;

import com.jacob.openfga.exception.OpenFGAException;
import com.jacob.openfga.model.CheckRequest;
import com.jacob.openfga.model.CheckResponse;
import com.jacob.openfga.model.ListObjectsRequest;
import com.jacob.openfga.model.ListObjectsResponse;
import com.jacob.openfga.model.TupleRequest;
import com.jacob.openfga.model.TupleResponse;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.client.model.ClientCheckRequest;
import dev.openfga.sdk.api.client.model.ClientListObjectsRequest;
import dev.openfga.sdk.api.client.model.ClientTupleKey;
import dev.openfga.sdk.api.client.model.ClientTupleKeyWithoutCondition;
import dev.openfga.sdk.api.client.model.ClientWriteRequest;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service layer that translates the wrapper's simple DTOs into OpenFGA SDK
 * calls and back again.
 *
 * <p>All SDK interactions are asynchronous ({@link java.util.concurrent.CompletableFuture});
 * this service intentionally blocks on the result to present a synchronous API
 * to the controller. Any failure is normalised into an {@link OpenFGAException}
 * so the web layer can map it to a single, consistent HTTP status.
 */
@Service
public class OpenFGAService {

    private static final Logger log = LoggerFactory.getLogger(OpenFGAService.class);

    private final OpenFgaClient fgaClient;

    public OpenFGAService(OpenFgaClient fgaClient) {
        this.fgaClient = fgaClient;
    }

    /**
     * Checks whether a user has a relation on an object.
     *
     * @param request the check to evaluate
     * @return the evaluation result, echoing the request for convenience
     */
    public CheckResponse check(CheckRequest request) {
        log.debug("Checking: {} # {} @ {}", request.getUser(), request.getRelation(), request.getObject());

        ClientCheckRequest checkRequest = new ClientCheckRequest()
                .user(request.getUser())
                .relation(request.getRelation())
                ._object(request.getObject());

        try {
            boolean allowed = Boolean.TRUE.equals(fgaClient.check(checkRequest).get().getAllowed());
            log.debug("Check result: allowed={}", allowed);
            return new CheckResponse(
                    allowed,
                    request.getUser(),
                    request.getRelation(),
                    request.getObject());
        } catch (Exception e) {
            throw handle("check", e);
        }
    }

    /**
     * Writes a single relationship tuple.
     *
     * @param request the tuple to persist
     * @return confirmation of the write
     */
    public TupleResponse writeTuple(TupleRequest request) {
        log.debug("Writing tuple: {} # {} @ {}", request.getUser(), request.getRelation(), request.getObject());

        ClientWriteRequest writeRequest = new ClientWriteRequest()
                .writes(List.of(new ClientTupleKey()
                        .user(request.getUser())
                        .relation(request.getRelation())
                        ._object(request.getObject())));

        try {
            fgaClient.write(writeRequest).get();
            return new TupleResponse(
                    "Tuple written successfully",
                    request.getUser(),
                    request.getRelation(),
                    request.getObject());
        } catch (Exception e) {
            throw handle("write tuple", e);
        }
    }

    /**
     * Deletes a single relationship tuple.
     *
     * @param request the tuple to remove
     * @return confirmation of the delete
     */
    public TupleResponse deleteTuple(TupleRequest request) {
        log.debug("Deleting tuple: {} # {} @ {}", request.getUser(), request.getRelation(), request.getObject());

        ClientWriteRequest deleteRequest = new ClientWriteRequest()
                .deletes(List.of(new ClientTupleKeyWithoutCondition()
                        .user(request.getUser())
                        .relation(request.getRelation())
                        ._object(request.getObject())));

        try {
            fgaClient.write(deleteRequest).get();
            return new TupleResponse(
                    "Tuple deleted successfully",
                    request.getUser(),
                    request.getRelation(),
                    request.getObject());
        } catch (Exception e) {
            throw handle("delete tuple", e);
        }
    }

    /**
     * Lists the objects of a given type that a user can access via a relation.
     *
     * @param request the enumeration parameters
     * @return the matching object identifiers
     */
    public ListObjectsResponse listObjects(ListObjectsRequest request) {
        log.debug("Listing objects: {} # {} of type {}", request.getUser(), request.getRelation(), request.getType());

        ClientListObjectsRequest listRequest = new ClientListObjectsRequest()
                .user(request.getUser())
                .relation(request.getRelation())
                .type(request.getType());

        try {
            List<String> objects = fgaClient.listObjects(listRequest).get().getObjects();
            return new ListObjectsResponse(
                    request.getUser(),
                    request.getRelation(),
                    request.getType(),
                    objects);
        } catch (Exception e) {
            throw handle("list objects", e);
        }
    }

    /**
     * Normalises any exception thrown while talking to OpenFGA into a single
     * {@link OpenFGAException}, preserving the interrupt status where relevant.
     */
    private OpenFGAException handle(String operation, Exception e) {
        // Restore the interrupt flag so callers up the stack can react to it.
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        // Unwrap CompletableFuture's ExecutionException to surface the real cause.
        Throwable cause = (e instanceof ExecutionException && e.getCause() != null) ? e.getCause() : e;
        log.error("OpenFGA '{}' operation failed: {}", operation, cause.getMessage(), cause);
        return new OpenFGAException("Failed to " + operation + " in OpenFGA: " + cause.getMessage(), cause);
    }
}
