package com.jacob.openfga.model;

public class PermissionItem {

    private String relation;
    private String object;

    // No-arg constructor 
    public PermissionItem() {
    }

    // All-args constructor 
    public PermissionItem(String relation, String object) {
        this.relation = relation;
        this.object = object;
    }

    // Getters 
    public String getRelation() {
        return relation;
    }

    public String getObject() {
        return object;
    }

    // Setters 
    public void setRelation(String relation) {
        this.relation = relation;
    }

    public void setOject(String object) {
        this.object = object;
    }

}
