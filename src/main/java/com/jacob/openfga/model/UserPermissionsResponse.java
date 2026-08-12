package com.jacob.openfga.model;

import java.util.List;

public class UserPermissionsResponse {

    private String user;
    private List<PermissionItem> permissions;
    private int total;

    // No-arg constructor 
    public UserPermissionsResponse() {
    }

    // All-args constuctor 
    public UserPermissionsResponse(String user, List<PermissionItem> permissions, int total) {
        this.user = user;
        this.permissions = permissions;
        this.total = total;
    }

    // Getters 
    public String getUser() {
        return user;
    }

    public List<PermissionItem> getPermissions() {
        return permissions;
    }

    public int getTotal() {
        return total;
    }

    // Setters 
    public void setUser(String user) {
        this.user = user;
    }

    public void setPermissions(List<PermissionItem> permissions) {
        this.permissions = permissions;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
