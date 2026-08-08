package com.jacob.openfga.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for a permission check.
 *
 * <p>Answers the question: "Does {@code user} have {@code relation} on {@code object}?"
 * All three fields follow OpenFGA's typed-identifier convention, e.g.
 * {@code user:anne}, {@code reader}, {@code document:roadmap}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequest {

    /** The subject being checked, e.g. {@code user:anne}. */
    @NotBlank(message = "user must not be blank")
    private String user;

    /** The relation/permission being checked, e.g. {@code reader}. */
    @NotBlank(message = "relation must not be blank")
    private String relation;

    /** The object the permission applies to, e.g. {@code document:roadmap}. */
    @NotBlank(message = "object must not be blank")
    private String object;
}
