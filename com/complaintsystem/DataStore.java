package com.complaintsystem;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataStore {

    private static final String DATA_DIR         = System.getProperty("user.home") + File.separator + ".complaintsystem";
    private static final String COMPLAINTS_FILE  = DATA_DIR + File.separator + "complaints.dat";
    private static final String USERS_FILE       = DATA_DIR + File.separator + "users.dat";
    private static final String SETTINGS_FILE    = DATA_DIR + File.separator + "settings.dat";

    private static DataStore instance;

    private List<Complaint>         complaints;
    private List<User>              users;
    private Map<String, String>     settings;

    private DataStore() {
        complaints = new ArrayList<>();
        users      = new ArrayList<>();
        settings   = new HashMap<>();
        ensureDataDirectory();
        load();
        if (users.isEmpty()) {
            seedDefaultData();
        }
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void load() {
        complaints = loadList(COMPLAINTS_FILE);
        users      = loadList(USERS_FILE);
        settings   = loadMap(SETTINGS_FILE);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> loadMap(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, String>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private <T> void saveList(List<T> list, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMap(Map<String, String> map, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void persistComplaints() {
        saveList(complaints, COMPLAINTS_FILE);
    }

    public synchronized void persistUsers() {
        saveList(users, USERS_FILE);
    }

    public synchronized void persistSettings() {
        saveMap(settings, SETTINGS_FILE);
    }

    public synchronized void persistAll() {
        persistComplaints();
        persistUsers();
        persistSettings();
    }

    private void seedDefaultData() {
        User admin = new User("admin", User.hashPassword("admin123"), "System Administrator",
                "admin@complaintsystem.gov", "IT Department", User.Role.ADMIN);
        User agent1 = new User("agent1", User.hashPassword("agent123"), "Ramesh Kumar",
                "ramesh@complaintsystem.gov", "Public Works", User.Role.AGENT);
        User agent2 = new User("agent2", User.hashPassword("agent123"), "Priya Sharma",
                "priya@complaintsystem.gov", "Sanitation", User.Role.AGENT);
        User citizen1 = new User("citizen1", User.hashPassword("pass123"), "Arun Singh",
                "arun@email.com", "N/A", User.Role.CITIZEN);
        User citizen2 = new User("citizen2", User.hashPassword("pass123"), "Meena Patel",
                "meena@email.com", "N/A", User.Role.CITIZEN);

        users.add(admin);
        users.add(agent1);
        users.add(agent2);
        users.add(citizen1);
        users.add(citizen2);

        Complaint c1 = new Complaint("Pothole on MG Road", "Large pothole causing accidents near MG Road junction.",
                Complaint.Status.OPEN, Complaint.Priority.HIGH, Complaint.Category.INFRASTRUCTURE,
                citizen1.getId(), "MG Road Junction, Delhi", "9876543210");
        Complaint c2 = new Complaint("Garbage not collected for 5 days", "Residential area waste accumulation.",
                Complaint.Status.IN_PROGRESS, Complaint.Priority.MEDIUM, Complaint.Category.SANITATION,
                citizen2.getId(), "Sector 14, Dwarka", "9876543211");
        Complaint c3 = new Complaint("Broken street light", "Street light on Park Avenue not working for 2 weeks.",
                Complaint.Status.OPEN, Complaint.Priority.LOW, Complaint.Category.ELECTRICITY,
                citizen1.getId(), "Park Avenue, Connaught Place", "9876543212");
        Complaint c4 = new Complaint("Water supply disrupted", "No water supply for 3 consecutive days.",
                Complaint.Status.RESOLVED, Complaint.Priority.CRITICAL, Complaint.Category.WATER_SUPPLY,
                citizen2.getId(), "Block C, Rohini", "9876543213");
        Complaint c5 = new Complaint("Encroachment on footpath", "Shopkeeper illegally occupying footpath.",
                Complaint.Status.CLOSED, Complaint.Priority.MEDIUM, Complaint.Category.PUBLIC_SAFETY,
                citizen1.getId(), "Chandni Chowk, Old Delhi", "9876543214");
        Complaint c6 = new Complaint("Sewage overflow", "Sewage overflowing onto main road causing health hazard.",
                Complaint.Status.OPEN, Complaint.Priority.CRITICAL, Complaint.Category.SANITATION,
                citizen2.getId(), "Lajpat Nagar Market", "9876543215");
        Complaint c7 = new Complaint("Noise pollution from construction", "Illegal night-time construction.",
                Complaint.Status.IN_PROGRESS, Complaint.Priority.HIGH, Complaint.Category.NOISE,
                citizen1.getId(), "Model Town, Phase II", "9876543216");
        Complaint c8 = new Complaint("Bus route discontinued", "Route 507 discontinued without notice.",
                Complaint.Status.OPEN, Complaint.Priority.MEDIUM, Complaint.Category.TRANSPORT,
                citizen2.getId(), "Nehru Place Bus Terminus", "9876543217");

        c2.setAssignedToUserId(agent2.getId());
        c2.addAuditEntry(agent2.getUsername(), "Assigned to sanitation team. Scheduled pickup for tomorrow.");
        c4.updateStatus(Complaint.Status.RESOLVED, admin.getUsername(), "Water supply restored after pipeline repair.");
        c4.setResolutionNote("Emergency repair team deployed. Issue resolved within 6 hours.");
        c4.setAssignedToUserId(agent1.getId());
        c5.updateStatus(Complaint.Status.CLOSED, admin.getUsername(), "Encroachment removed by enforcement team.");
        c5.setAssignedToUserId(agent1.getId());
        c7.setAssignedToUserId(agent1.getId());
        c7.addAuditEntry(agent1.getUsername(), "Notice issued to construction company. Night ban enforced.");

        complaints.add(c1);
        complaints.add(c2);
        complaints.add(c3);
        complaints.add(c4);
        complaints.add(c5);
        complaints.add(c6);
        complaints.add(c7);
        complaints.add(c8);

        settings.put("app.version", "1.0.0");
        settings.put("app.name", "Centralized Complaint Registration & Tracking System");
        settings.put("app.org", "Municipal Corporation of Delhi");

        persistAll();
    }

    public synchronized List<Complaint> getAllComplaints() {
        return new ArrayList<>(complaints);
    }

    public synchronized Optional<Complaint> getComplaintById(String id) {
        return complaints.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public synchronized Optional<Complaint> getComplaintByTrackingNumber(String tn) {
        return complaints.stream().filter(c -> c.getTrackingNumber().equalsIgnoreCase(tn)).findFirst();
    }

    public synchronized void addComplaint(Complaint complaint) {
        complaints.add(complaint);
        persistComplaints();
    }

    public synchronized boolean updateComplaint(Complaint complaint) {
        for (int i = 0; i < complaints.size(); i++) {
            if (complaints.get(i).getId().equals(complaint.getId())) {
                complaints.set(i, complaint);
                persistComplaints();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteComplaint(String id) {
        boolean removed = complaints.removeIf(c -> c.getId().equals(id));
        if (removed) persistComplaints();
        return removed;
    }

    public synchronized List<Complaint> getComplaintsByStatus(Complaint.Status status) {
        return complaints.stream().filter(c -> c.getStatus() == status).collect(Collectors.toList());
    }

    public synchronized List<Complaint> getComplaintsByPriority(Complaint.Priority priority) {
        return complaints.stream().filter(c -> c.getPriority() == priority).collect(Collectors.toList());
    }

    public synchronized List<Complaint> getComplaintsByUser(String userId) {
        return complaints.stream().filter(c -> c.getSubmittedByUserId().equals(userId)).collect(Collectors.toList());
    }

    public synchronized List<Complaint> getComplaintsByAgent(String agentId) {
        return complaints.stream().filter(c -> agentId.equals(c.getAssignedToUserId())).collect(Collectors.toList());
    }

    public synchronized List<Complaint> searchComplaints(String keyword) {
        String kw = keyword.toLowerCase();
        return complaints.stream().filter(c ->
            c.getTitle().toLowerCase().contains(kw) ||
            c.getDescription().toLowerCase().contains(kw) ||
            c.getTrackingNumber().toLowerCase().contains(kw) ||
            c.getLocation().toLowerCase().contains(kw) ||
            c.getCategoryDisplayName().toLowerCase().contains(kw)
        ).collect(Collectors.toList());
    }

    public synchronized Map<Complaint.Status, Long> getStatusSummary() {
        Map<Complaint.Status, Long> map = new LinkedHashMap<>();
        for (Complaint.Status s : Complaint.Status.values()) {
            map.put(s, complaints.stream().filter(c -> c.getStatus() == s).count());
        }
        return map;
    }

    public synchronized Map<Complaint.Priority, Long> getPrioritySummary() {
        Map<Complaint.Priority, Long> map = new LinkedHashMap<>();
        for (Complaint.Priority p : Complaint.Priority.values()) {
            map.put(p, complaints.stream().filter(c -> c.getPriority() == p).count());
        }
        return map;
    }

    public synchronized Map<Complaint.Category, Long> getCategorySummary() {
        Map<Complaint.Category, Long> map = new LinkedHashMap<>();
        for (Complaint.Category cat : Complaint.Category.values()) {
            long count = complaints.stream().filter(c -> c.getCategory() == cat).count();
            if (count > 0) map.put(cat, count);
        }
        return map;
    }

    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public synchronized Optional<User> getUserById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public synchronized Optional<User> getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public synchronized Optional<User> authenticate(String username, String password) {
        return users.stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username) && u.authenticate(password) && u.isActive())
            .findFirst();
    }

    public synchronized void addUser(User user) {
        users.add(user);
        persistUsers();
    }

    public synchronized boolean updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                persistUsers();
                return true;
            }
        }
        return false;
    }

    public synchronized List<User> getAgents() {
        return users.stream().filter(u -> u.getRole() == User.Role.AGENT && u.isActive()).collect(Collectors.toList());
    }

    public synchronized String getSetting(String key) {
        return settings.getOrDefault(key, "");
    }

    public synchronized void setSetting(String key, String value) {
        settings.put(key, value);
        persistSettings();
    }

    public synchronized long getTotalComplaints() {
        return complaints.size();
    }

    public synchronized long getOpenCount() {
        return complaints.stream().filter(c -> c.getStatus() == Complaint.Status.OPEN).count();
    }

    public synchronized long getResolvedCount() {
        return complaints.stream().filter(c -> c.getStatus() == Complaint.Status.RESOLVED ||
                                               c.getStatus() == Complaint.Status.CLOSED).count();
    }

    public synchronized long getCriticalCount() {
        return complaints.stream().filter(c -> c.getPriority() == Complaint.Priority.CRITICAL &&
                                               c.getStatus() == Complaint.Status.OPEN).count();
    }
}
