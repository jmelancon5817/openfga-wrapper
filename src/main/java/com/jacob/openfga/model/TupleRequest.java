package com.jacob.openfga.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for writing or deleting a relationship tuple.
 *
 * <p>A relationship tuple is the fundamental unit stored in OpenFGA and is made
 * up of a {@code user}, a {@code relation}, and an {@code object}, e.g.
 * "{@code user:anne} is a {@code reader} of {@code document:roadmap}".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleRequest {

    /** The subject side of the tuple, e.g. {@code user:anne}. */
    @NotBlank(message = "user must not be blank")
    private String user;

    /** The relation connecting user and object, e.g. {@code reader}. */
    @NotBlank(message = "relation must not be blank")
    private String relation;

    /** The object side of the tuple, e.g. {@code document:roadmap}. */
    @NotBlank(message = "object must not be blank")
    private String object;
}
