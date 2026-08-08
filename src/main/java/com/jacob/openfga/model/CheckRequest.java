package com.jacob.openfga.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for a permission check.
 *
 * <p>Answers the question: "Does {@code user} have {@code relation} on {@code object}?"
 * All three fields follow OpenFGA's typed-identifier convention, e.g.
 * {@code user:anne}, {@code reader}, {@code document:roadmap}.
 */
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

    public CheckRequest() {
    }

    public CheckRequest(String user, String relation, String object) {
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
        return "CheckRequest{" +
                "user='" + user + '\'' +
                ", relation='" + relation + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
