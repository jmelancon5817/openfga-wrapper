package com.jacob.openfga.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for writing or deleting a relationship tuple.
 *
 * <p>A relationship tuple is the fundamental unit stored in OpenFGA and is made
 * up of a {@code user}, a {@code relation}, and an {@code object}, e.g.
 * "{@code user:anne} is a {@code reader} of {@code document:roadmap}".
 */
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

    public TupleRequest() {
    }

    public TupleRequest(String user, String relation, String object) {
        this.user = user;
        this.relation = relation;
        this.object = object;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "TupleRequest{" +
                "user='" + user + '\'' +
                ", relation='" + relation + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
