package com.jacob.openfga.model;

/**
 * Response payload for a permission check.
 */
public class CheckResponse {

    /** {@code true} when the user is granted the requested relation on the object. */
    private boolean allowed;

    /** Echoes back the subject that was evaluated. */
    private String user;

    /** Echoes back the relation that was evaluated. */
    private String relation;

    /** Echoes back the object that was evaluated. */
    private String object;

    public CheckResponse() {
    }

    public CheckResponse(boolean allowed, String user, String relation, String object) {
        this.allowed = allowed;
        this.user = user;
        this.relation = relation;
        this.object = object;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
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
        return "CheckResponse{" +
                "allowed=" + allowed +
                ", user='" + user + '\'' +
                ", relation='" + relation + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
