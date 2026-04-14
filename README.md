# Centralized Complaint Registration & Status Tracking System

**Notice:** This repository contains a comprehensive College Java Project. It is designed to demonstrate proficiency in Object-Oriented Programming, GUI development with Java Swing, local data persistence, and system design patterns. 

## Overview

The Centralized Complaint Registration & Status Tracking System is a robust, desktop-based Java application engineered to streamline the lifecycle of civic or organizational complaints. It provides a structured workflow for citizens to log issues, agents to resolve them, and administrators to oversee system operations and manage personnel. The application operates entirely offline, utilizing Java Serialization for local data persistence, ensuring a lightweight footprint without the need for an external database setup.

## Key Features

* **Role-Based Access Control (RBAC):** Secure login system supporting three distinct user roles:
  * **Admin:** Full system access, including User Management, system settings, and complete oversight of all complaints.
  * **Agent:** Access to assigned complaints, status modification, and resolution tracking.
  * **Citizen:** Ability to register new complaints and track personal submission statuses.
* **Comprehensive Complaint Tracking:**
  * Automated generation of unique tracking numbers.
  * Categorization (Infrastructure, Sanitation, Water Supply, etc.) and Priority levels (Low, Medium, High, Critical).
  * Status lifecycle management (Open, In Progress, Resolved, Closed).
* **Immutable Audit Trails:**
  Every state change or note addition to a complaint is logged with a timestamp and the acting user's ID, ensuring total accountability and transparency.
* **Advanced Analytics Dashboard:**
  Custom-built chart panels providing real-time data visualization of complaint distributions by Status, Priority, and Category.
* **Custom User Interface:**
  A highly polished, dark-mode inspired custom theme built entirely on top of standard Java Swing components. Features include custom rendering for tables, combo boxes, and scroll panes to provide a modern, industry-standard aesthetic.
* **Local Data Persistence:**
  Singleton-based DataStore pattern utilizing Java Object Serialization to securely read and write `.dat` files directly to the local filesystem (`~/.complaintsystem/`).

## Architecture & Technology Stack

* **Language:** Java (JDK 8 or higher)
* **User Interface:** Java Swing / AWT
* **Data Storage:** Java Serialization (`java.io.Serializable`)
* **Design Patterns Implemented:**
  * Singleton Pattern (DataStore)
  * Model-View-Controller (MVC) architectural principles
  * Component Customization (Custom UI Renderers in ThemeManager)

## System Requirements

* Java Development Kit (JDK) 8 or higher installed and configured in the system PATH.
* Minimum screen resolution of 1280x820 recommended for optimal UI rendering.

## Getting Started

### Installation and Setup

1. **Clone the Repository**
   Download or clone the source code into your preferred IDE (e.g., IntelliJ IDEA, Eclipse, or NetBeans).

2. **Compile the Source Code**
   Ensure all `.java` files in the `com.complaintsystem` package are compiled successfully. 

3. **Run the Application**
   Execute the `Main.java` class. The application will initialize the data store, display a custom splash screen, and proceed to the login view. 
   *Note: Upon initial execution, the application will automatically seed the data directory with default users and sample complaints.*

## Default Credentials

The system automatically provisions the following default accounts upon first launch:

* **Administrator Account**
  * Username: `admin`
  * Password: `admin123`

* **Agent Account**
  * Username: `agent1`
  * Password: `agent123`

* **Citizen Account**
  * Username: `citizen1`
  * Password: `pass123`

## Directory Structure

* `Main.java` - Application entry point and splash screen logic.
* `LoginView.java` - Authentication interface.
* `DashboardView.java` - Primary UI controller handling the tracker, forms, and analytics.
* `DataStore.java` - Centralized singleton managing file I/O operations and memory state.
* `ThemeManager.java` - Global style configurations and custom Swing component builders.
* `User.java` - User entity model and authentication logic.
* `Complaint.java` - Core complaint entity model and audit logging logic.

## Disclaimer

As explicitly stated, this system was developed as an academic college project. While it implements industry-standard design patterns and custom UI rendering, it relies on local file serialization rather than a relational database. It is intended for educational purposes, portfolio demonstration, and as a foundation for further enterprise-level development.