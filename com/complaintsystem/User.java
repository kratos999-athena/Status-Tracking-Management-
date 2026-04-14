package com.complaintsystem;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Role {
        ADMIN, AGENT, CITIZEN
    }

    private final String id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String department;
    private Role role;
    private boolean active;
    private final long createdAt;
    private long lastLogin;

    public User(String username, String passwordHash, String fullName, String email, String department, Role role) {
        this.id           = UUID.randomUUID().toString();
        this.username     = username;
        this.passwordHash = passwordHash;
        this.fullName     = fullName;
        this.email        = email;
        this.department   = department;
        this.role         = role;
        this.active       = true;
        this.createdAt    = System.currentTimeMillis();
        this.lastLogin    = 0L;
    }

    public String getId()           { return id; }
    public String getUsername()     { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName()     { return fullName; }
    public String getEmail()        { return email; }
    public String getDepartment()   { return department; }
    public Role   getRole()         { return role; }
    public boolean isActive()       { return active; }
    public long   getCreatedAt()    { return createdAt; }
    public long   getLastLogin()    { return lastLogin; }

    public void setUsername(String username)         { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setFullName(String fullName)         { this.fullName = fullName; }
    public void setEmail(String email)               { this.email = email; }
    public void setDepartment(String department)     { this.department = department; }
    public void setRole(Role role)                   { this.role = role; }
    public void setActive(boolean active)            { this.active = active; }
    public void setLastLogin(long lastLogin)         { this.lastLogin = lastLogin; }

    public boolean authenticate(String rawPassword) {
        return this.passwordHash.equals(hashPassword(rawPassword));
    }

    public static String hashPassword(String raw) {
        int hash = 31;
        for (char c : raw.toCharArray()) {
            hash = hash * 31 + c;
        }
        return Integer.toHexString(hash);
    }

    public String getRoleDisplayName() {
        switch (role) {
            case ADMIN:   return "Administrator";
            case AGENT:   return "Support Agent";
            case CITIZEN: return "Citizen";
            default:      return "Unknown";
        }
    }

    @Override
    public String toString() {
        return fullName + " (" + username + ") [" + role + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        return this.id.equals(((User) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
