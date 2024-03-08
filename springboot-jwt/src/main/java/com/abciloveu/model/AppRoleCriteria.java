package com.abciloveu.model;

import java.util.Set;

/**
 * https://github.com/BigWattanachai/JpaDemoApplication
 */
public class AppRoleCriteria extends SearchCriteria {

    private Long id;

    private String roleName;

    private String description;

    private String privileges;

    private Set<String> priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public Set<String> getPriority() {
        return priority;
    }

    public void setPriority(Set<String> priority) {
        this.priority = priority;
    }

}
