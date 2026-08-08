package com.jacob.openfga.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for listing the objects of a given type that a user can access.
 *
 * <p>Answers: "Which {@code type} objects does {@code user} have {@code relation} on?"
 */
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

    public ListObjectsRequest() {
    }

    public ListObjectsRequest(String user, String relation, String type) {
        this.user = user;
        this.relation = relation;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ListObjectsRequest{" +
                "user='" + user + '\'' +
                ", relation='" + relation + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
