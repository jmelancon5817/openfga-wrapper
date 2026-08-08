package com.jacob.openfga.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload containing the objects a user can access for a given relation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListObjectsResponse {

    /** The subject the results are for. */
    private String user;

    /** The relation the results are for. */
    private String relation;

    /** The object type the results are for. */
    private String type;

    /** Fully-qualified object identifiers the user can access, e.g. {@code document:roadmap}. */
    private List<String> objects;
}
