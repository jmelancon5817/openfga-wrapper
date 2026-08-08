package com.jacob.openfga.model;

import java.util.List;

/**
 * Response payload containing the objects a user can access for a given relation.
 */
public class ListObjectsResponse {

    /** The subject the results are for. */
    private String user;

    /** The relation the results are for. */
    private String relation;

    /** The object type the results are for. */
    private String type;

    /** Fully-qualified object identifiers the user can access, e.g. {@code document:roadmap}. */
    private List<String> objects;

    public ListObjectsResponse() {
    }

    public ListObjectsResponse(String user, String relation, String type, List<String> objects) {
        this.user = user;
        this.relation = relation;
        this.type = type;
        this.objects = objects;
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

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }

    @Override
    public String toString() {
        return "ListObjectsResponse{" +
                "user='" + user + '\'' +
                ", relation='" + relation + '\'' +
                ", type='" + type + '\'' +
                ", objects=" + objects +
                '}';
    }
}
