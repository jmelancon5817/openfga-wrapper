package com.jacob.openfga.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload confirming a tuple write or delete operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleResponse {

    /** Human-readable outcome, e.g. "Tuple written successfully". */
    private String message;

    /** The subject side of the affected tuple. */
    private String user;

    /** The relation of the affected tuple. */
    private String relation;

    /** The object side of the affected tuple. */
    private String object;
}
