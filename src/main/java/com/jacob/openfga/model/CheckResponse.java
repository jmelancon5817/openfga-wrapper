package com.jacob.openfga.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for a permission check.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckResponse {

    /** {@code true} when the user is granted the requested relation on the object. */
    private boolean allowed;

    /** Echoes back the subject that was evaluated. */
    private String user;

    /** Echoes back the relation that was evaluated. */
    private String relation;

    /** Echoes back the object that was evaluated. */
    private String object;
}
