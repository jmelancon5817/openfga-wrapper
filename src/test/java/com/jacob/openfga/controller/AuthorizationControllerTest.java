package com.jacob.openfga.controller;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacob.openfga.exception.OpenFGAException;
import com.jacob.openfga.model.CheckRequest;
import com.jacob.openfga.model.CheckResponse;
import com.jacob.openfga.model.ListObjectsResponse;
import com.jacob.openfga.model.PermissionItem;
import com.jacob.openfga.model.TupleRequest;
import com.jacob.openfga.model.TupleResponse;
import com.jacob.openfga.model.UserPermissionsResponse;
import com.jacob.openfga.service.OpenFGAService;

/**
 * Web-layer tests for {@link AuthorizationController} using MockMvc with a
 * mocked service. Verifies HTTP status codes, JSON serialisation, request
 * validation, and error translation without starting a full server.
 */
@WebMvcTest(AuthorizationController.class)
class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpenFGAService openFGAService;

    @Test
    @DisplayName("POST /check returns 200 with the allow result")
    void check_returns200() throws Exception {
        CheckRequest request = new CheckRequest("user:anne", "reader", "document:roadmap");
        when(openFGAService.check(any(CheckRequest.class)))
                .thenReturn(new CheckResponse(true, "user:anne", "reader", "document:roadmap"));

        mockMvc.perform(post("/api/authorization/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.user").value("user:anne"));
    }

    @Test
    @DisplayName("POST /check returns 400 when a required field is blank")
    void check_returns400_onValidationError() throws Exception {
        CheckRequest invalid = new CheckRequest("", "reader", "document:roadmap");

        mockMvc.perform(post("/api/authorization/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.user").exists());
    }

    @Test
    @DisplayName("POST /check returns 502 when OpenFGA is unavailable")
    void check_returns502_onUpstreamError() throws Exception {
        CheckRequest request = new CheckRequest("user:anne", "reader", "document:roadmap");
        when(openFGAService.check(any(CheckRequest.class)))
                .thenThrow(new OpenFGAException("Failed to check in OpenFGA: connection refused"));

        mockMvc.perform(post("/api/authorization/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }

    @Test
    @DisplayName("POST /tuples returns 201 on a successful write")
    void writeTuple_returns201() throws Exception {
        TupleRequest request = new TupleRequest("user:anne", "reader", "document:roadmap");
        when(openFGAService.writeTuple(any(TupleRequest.class)))
                .thenReturn(new TupleResponse(
                        "Tuple written successfully", "user:anne", "reader", "document:roadmap"));

        mockMvc.perform(post("/api/authorization/tuples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Tuple written successfully"));
    }

    @Test
    @DisplayName("DELETE /tuples returns 200 on a successful delete")
    void deleteTuple_returns200() throws Exception {
        TupleRequest request = new TupleRequest("user:anne", "reader", "document:roadmap");
        when(openFGAService.deleteTuple(any(TupleRequest.class)))
                .thenReturn(new TupleResponse(
                        "Tuple deleted successfully", "user:anne", "reader", "document:roadmap"));

        mockMvc.perform(delete("/api/authorization/tuples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tuple deleted successfully"));
    }

    @Test
    @DisplayName("GET /objects returns 200 with the accessible object list")
    void listObjects_returns200() throws Exception {
        when(openFGAService.listObjects(any()))
                .thenReturn(new ListObjectsResponse(
                        "user:anne", "reader", "document",
                        List.of("document:roadmap", "document:budget")));

        mockMvc.perform(get("/api/authorization/objects")
                .param("user", "user:anne")
                .param("relation", "reader")
                .param("type", "document"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objects.length()").value(2))
                .andExpect(jsonPath("$.objects[0]").value("document:roadmap"));
    }

    @Test
    @DisplayName("GET /objects returns 400 when a required parameter is missing")
    void listObjects_returns400_whenParamMissing() throws Exception {
        mockMvc.perform(get("/api/authorization/objects")
                .param("user", "user:anne")
                .param("relation", "reader"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /health returns 200 with an UP status")
    void health_returns200() throws Exception {
        mockMvc.perform(get("/api/authorization/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("openfga-wrapper"));
    }

    @Test
    void getUserPermissions_shouldReturn200_withPermissions() throws Exception {

        // ARRANGE
        List<PermissionItem> items = List.of(
                new PermissionItem("reader", "document:report"),
                new PermissionItem("writer", "document:budget")
        );
        UserPermissionsResponse fakeResponse
                = new UserPermissionsResponse("user:jacob", items, 2);

        when(openFGAService.getUserPermissions("user:jacob"))
                .thenReturn(fakeResponse);

        // ACT + ASSERT
        mockMvc.perform(get("/api/authorization/users/user:jacob/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("user:jacob"))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.permissions[0].relation").value("reader"))
                .andExpect(jsonPath("$.permissions[0].object").value("document:report"));
    }

    @Test
    void getUserPermissions_shouldReturn502_whenOpenFGAFails() throws Exception {

        // ARRANGE
        when(openFGAService.getUserPermissions("user:jacob"))
                .thenThrow(new OpenFGAException("Connection failed", new RuntimeException()));

        // ACT + ASSERT
        mockMvc.perform(get("/api/authorization/users/user:jacob/permissions"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }
}
