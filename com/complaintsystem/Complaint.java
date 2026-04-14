package com.complaintsystem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Category {
        INFRASTRUCTURE, SANITATION, WATER_SUPPLY, ELECTRICITY,
        PUBLIC_SAFETY, HEALTH, EDUCATION, TRANSPORT, NOISE, OTHER
    }

    public static class AuditEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String actor;
        private final String action;
        private final long   timestamp;

        public AuditEntry(String actor, String action) {
            this.actor     = actor;
            this.action    = action;
            this.timestamp = System.currentTimeMillis();
        }

        public String getActor()     { return actor; }
        public String getAction()    { return action; }
        public long   getTimestamp() { return timestamp; }

        public String getFormattedTimestamp() {
            return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date(timestamp));
        }

        @Override
        public String toString() {
            return "[" + getFormattedTimestamp() + "] " + actor + ": " + action;
        }
    }

    private final String          id;
    private final String          trackingNumber;
    private       String          title;
    private       String          description;
    private       Status          status;
    private       Priority        priority;
    private       Category        category;
    private final String          submittedByUserId;
    private       String          assignedToUserId;
    private       String          location;
    private       String          contactPhone;
    private final long            createdAt;
    private       long            updatedAt;
    private       long            resolvedAt;
    private       String          resolutionNote;
    private final List<AuditEntry> auditTrail;
    private       int             satisfactionRating;
    private       String          feedback;

    public Complaint(String title, String description, Status status, Priority priority,
                     Category category, String submittedByUserId, String location, String contactPhone) {
        this.id                 = UUID.randomUUID().toString();
        this.trackingNumber     = generateTrackingNumber();
        this.title              = title;
        this.description        = description;
        this.status             = status;
        this.priority           = priority;
        this.category           = category;
        this.submittedByUserId  = submittedByUserId;
        this.assignedToUserId   = null;
        this.location           = location;
        this.contactPhone       = contactPhone;
        this.createdAt          = System.currentTimeMillis();
        this.updatedAt          = this.createdAt;
        this.resolvedAt         = 0L;
        this.resolutionNote     = "";
        this.auditTrail         = new ArrayList<>();
        this.satisfactionRating = 0;
        this.feedback           = "";
        this.auditTrail.add(new AuditEntry(submittedByUserId, "Complaint registered with status OPEN"));
    }

    private static String generateTrackingNumber() {
        long ts  = System.currentTimeMillis();
        int  rnd = (int)(Math.random() * 9000) + 1000;
        return "CMP-" + Long.toHexString(ts).toUpperCase().substring(4) + "-" + rnd;
    }

    public String   getId()                 { return id; }
    public String   getTrackingNumber()     { return trackingNumber; }
    public String   getTitle()              { return title; }
    public String   getDescription()        { return description; }
    public Status   getStatus()             { return status; }
    public Priority getPriority()           { return priority; }
    public Category getCategory()           { return category; }
    public String   getSubmittedByUserId()  { return submittedByUserId; }
    public String   getAssignedToUserId()   { return assignedToUserId; }
    public String   getLocation()           { return location; }
    public String   getContactPhone()       { return contactPhone; }
    public long     getCreatedAt()          { return createdAt; }
    public long     getUpdatedAt()          { return updatedAt; }
    public long     getResolvedAt()         { return resolvedAt; }
    public String   getResolutionNote()     { return resolutionNote; }
    public List<AuditEntry> getAuditTrail() { return new ArrayList<>(auditTrail); }
    public int      getSatisfactionRating() { return satisfactionRating; }
    public String   getFeedback()           { return feedback; }

    public void setTitle(String title)                         { this.title = title;           touch(); }
    public void setDescription(String description)             { this.description = description; touch(); }
    public void setPriority(Priority priority)                 { this.priority = priority;     touch(); }
    public void setCategory(Category category)                 { this.category = category;     touch(); }
    public void setAssignedToUserId(String assignedToUserId)   { this.assignedToUserId = assignedToUserId; touch(); }
    public void setLocation(String location)                   { this.location = location;     touch(); }
    public void setContactPhone(String contactPhone)           { this.contactPhone = contactPhone; touch(); }
    public void setResolutionNote(String resolutionNote)       { this.resolutionNote = resolutionNote; touch(); }
    public void setSatisfactionRating(int rating)              { this.satisfactionRating = rating; touch(); }
    public void setFeedback(String feedback)                   { this.feedback = feedback;     touch(); }

    public void updateStatus(Status newStatus, String actor, String note) {
        Status old = this.status;
        this.status = newStatus;
        touch();
        if (newStatus == Status.RESOLVED || newStatus == Status.CLOSED) {
            this.resolvedAt = System.currentTimeMillis();
        }
        String entry = "Status changed from " + old + " to " + newStatus;
        if (note != null && !note.trim().isEmpty()) {
            entry += ". Note: " + note;
        }
        auditTrail.add(new AuditEntry(actor, entry));
    }

    public void addAuditEntry(String actor, String action) {
        auditTrail.add(new AuditEntry(actor, action));
        touch();
    }

    private void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    public String getFormattedCreatedAt() {
        return new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date(createdAt));
    }

    public String getFormattedUpdatedAt() {
        return new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date(updatedAt));
    }

    public String getFormattedResolvedAt() {
        if (resolvedAt == 0L) return "N/A";
        return new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date(resolvedAt));
    }

    public long getAgeInHours() {
        return (System.currentTimeMillis() - createdAt) / (1000 * 60 * 60);
    }

    public String getCategoryDisplayName() {
        if (category == null) return "Other";
        return category.name().replace("_", " ");
    }

    @Override
    public String toString() {
        return "[" + trackingNumber + "] " + title + " | " + status + " | " + priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Complaint)) return false;
        return this.id.equals(((Complaint) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
