package com.jacob.openfga.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.openfga.model.CheckRequest;
import com.jacob.openfga.model.CheckResponse;
import com.jacob.openfga.model.ListObjectsRequest;
import com.jacob.openfga.model.ListObjectsResponse;
import com.jacob.openfga.model.TupleRequest;
import com.jacob.openfga.model.TupleResponse;
import com.jacob.openfga.model.UserPermissionsResponse;
import com.jacob.openfga.service.OpenFGAService;

import jakarta.validation.Valid;

/**
 * REST facade for OpenFGA fine-grained authorization operations.
 *
 * <p>
 * Exposes permission checks, tuple management, and object listing under
 * {@code /api/authorization}. Request bodies are validated before reaching the
 * service layer; failures are handled centrally by
 * {@code GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/authorization")
public class AuthorizationController {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationController.class);

    private final OpenFGAService openFGAService;

    public AuthorizationController(OpenFGAService openFGAService) {
        this.openFGAService = openFGAService;
    }

    /**
     * Checks whether a user has a given relation on an object.
     *
     * @param request the check to evaluate (validated)
     * @return {@code 200 OK} with the allow/deny result
     */
    @PostMapping("/check")
    public ResponseEntity<CheckResponse> check(@Valid @RequestBody CheckRequest request) {
        log.info("POST /check user={} relation={} object={}",
                request.getUser(), request.getRelation(), request.getObject());
        return ResponseEntity.ok(openFGAService.check(request));
    }

    /**
     * Writes a relationship tuple.
     *
     * @param request the tuple to write (validated)
     * @return {@code 201 Created} with a confirmation payload
     */
    @PostMapping("/tuples")
    public ResponseEntity<TupleResponse> writeTuple(@Valid @RequestBody TupleRequest request) {
        log.info("POST /tuples user={} relation={} object={}",
                request.getUser(), request.getRelation(), request.getObject());
        return ResponseEntity.status(HttpStatus.CREATED).body(openFGAService.writeTuple(request));
    }

    /**
     * Deletes a relationship tuple.
     *
     * @param request the tuple to delete (validated)
     * @return {@code 200 OK} with a confirmation payload
     */
    @DeleteMapping("/tuples")
    public ResponseEntity<TupleResponse> deleteTuple(@Valid @RequestBody TupleRequest request) {
        log.info("DELETE /tuples user={} relation={} object={}",
                request.getUser(), request.getRelation(), request.getObject());
        return ResponseEntity.ok(openFGAService.deleteTuple(request));
    }

    /**
     * Lists the objects of a given type that a user can access via a relation.
     *
     * <p>
     * Parameters are supplied as query string values, e.g.
     * {@code /api/authorization/objects?user=user:anne&relation=reader&type=document}.
     *
     * @param user the subject, e.g. {@code user:anne}
     * @param relation the relation, e.g. {@code reader}
     * @param type the object type, e.g. {@code document}
     * @return {@code 200 OK} with the list of accessible objects
     */
    @GetMapping("/objects")
    public ResponseEntity<ListObjectsResponse> listObjects(
            @RequestParam String user,
            @RequestParam String relation,
            @RequestParam String type) {
        log.info("GET /objects user={} relation={} type={}", user, relation, type);
        ListObjectsRequest request = new ListObjectsRequest(user, relation, type);
        return ResponseEntity.ok(openFGAService.listObjects(request));
    }

    /**
     * Lightweight liveness check for this wrapper service.
     *
     * <p>
     * Deep health (including OpenFGA connectivity) is available via the
     * Actuator endpoint at {@code /actuator/health}.
     *
     * @return {@code 200 OK} with a simple status payload
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "openfga-wrapper"));
    }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<UserPermissionsResponse> getUserPermissions(
            @PathVariable String userId) {

        UserPermissionsResponse response
                = openFGAService.getUserPermissions(userId);
        return ResponseEntity.ok(response);
    }

}
