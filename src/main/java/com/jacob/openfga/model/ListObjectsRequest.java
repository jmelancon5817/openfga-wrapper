package com.jacob.openfga.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for listing the objects of a given type that a user can access.
 *
 * <p>Answers: "Which {@code type} objects does {@code user} have {@code relation} on?"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListObjectsRequest {

    /** The subject whose access is being enumerated, e.g. {@code user:anne}. */
    @NotBlank(message = "user must not be blank")
    private String user;

    /** The relation to enumerate, e.g. {@code reader}. */
    @NotBlank(message = "relation must not be blank")
    private String relation;

    /** The object type to enumerate, e.g. {@code document}. */
    @NotBlank(message = "type must not be blank")
    private String type;
}
