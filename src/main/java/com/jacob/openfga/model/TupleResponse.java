package com.jacob.openfga.model;

/**
 * Response payload confirming a tuple write or delete operation.
 */
public class TupleResponse {

    /** Human-readable outcome, e.g. "Tuple written successfully". */
    private String message;

    /** The subject side of the affected tuple. */
    private String user;

    /** The relation of the affected tuple. */
    private String relation;

    /** The object side of the affected tuple. */
    private String object;

    public TupleResponse() {
    }

    public TupleResponse(String message, String user, String relation, String object) {
        this.message = message;
        this.user = user;
        this.relation = relation;
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        return "TupleResponse{" +
                "message='" + message + '\'' +
                ", user='" + user + '\'' +
                ", relation='" + relation + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
