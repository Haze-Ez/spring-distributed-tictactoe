# Spring Distributed Tic-Tac-Toe System

> 🇭🇺 A magyar nyelvű leírásért lásd: [README_HU.md](README_HU.md)

---

## Overview

A secure, real-time multiplayer application built with **Spring Boot**, **Thymeleaf**, and **PostgreSQL**. The project implements a distributed MVC pattern to handle game state synchronization, user session management, and persistent data storage across multiple concurrent users.

---

## Key Features

- **Distributed Architecture:** Supports multiple concurrent game sessions via stateless RESTful communication.
- **Security:** Implements Spring Security for form-based authentication and role-based access control.
- **Persistence:** Uses JPA/Hibernate with PostgreSQL for relational data integrity (User Profiles, Game History).
- **Game Modes:** Supports PvP (Local Network) and Single Player (vs CPU).

---

## Technology Stack

| Layer        | Technology                                      |
|--------------|-------------------------------------------------|
| **Backend**  | Java 17, Spring Boot 3.5.5 (Web, Security, JPA) |
| **Frontend** | Thymeleaf, JavaScript (Fetch API), CSS          |
| **Database** | PostgreSQL                                      |
| **Build**    | Maven (with Maven Wrapper `mvnw`)               |

---

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** (or use the included `mvnw` wrapper — no installation needed)
- **PostgreSQL 14+** — [Download](https://www.postgresql.org/download/)

---

## Getting Started

### 1. Set Up the Database

Open your PostgreSQL client (`psql`) and run the following commands to create the database and user:

```sql
CREATE DATABASE tttdb;
CREATE USER tttuser WITH PASSWORD 'tttpass';
GRANT ALL PRIVILEGES ON DATABASE tttdb TO tttuser;
```

> **Note:** The application uses `spring.jpa.hibernate.ddl-auto=update`, so Hibernate will automatically create all required tables on first startup. No SQL schema script is needed.

### 2. Configure Application Properties

The default credentials in `src/main/resources/application.properties` are:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tttdb
spring.datasource.username=tttuser
spring.datasource.password=tttpass
```

If you use different credentials, update this file before running.

### 3. Run the Application

Using the Maven Wrapper (recommended — no Maven installation required):

```bash
# On Windows
mvnw.cmd spring-boot:run

# On Linux / macOS
./mvnw spring-boot:run
```

Or, if Maven is installed globally:

```bash
mvn spring-boot:run
```

### 4. Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

You will be redirected to the login page. Register a new account to get started.

---

## How to Test Multiplayer (PvP)

To verify real-time multiplayer functionality on a single machine, you need **two separate browser sessions**.

### Step 1 — Open Browser A (Player X)

1. Go to `http://localhost:8080/login`.
2. Register a user (e.g., `user1`) and log in.
3. Click **"New Game"** → **"Player vs Player"**.
4. Wait in the lobby — the Game ID will appear in the URL.

### Step 2 — Open Browser B in *Incognito / Private Mode* (Player O)

> **Important:** You **must** use Incognito mode or a completely different browser to establish a separate session. A second regular tab in the same browser will share the same login session.

1. Go to `http://localhost:8080/login`.
2. Register a second user (e.g., `user2`) and log in.
3. Go to **"My Games"** and find the **"Open Games to Join"** list.
4. Click **"Join as O"** on the game created by `user1`.

### Step 3 — Verify Synchronization

- Place a move in Browser A.
- Observe the board update automatically in Browser B (within ~1 second via server polling).
- Play until a win or draw to see the game-over overlay appear on both screens simultaneously.

---

## Alternative: Testing with a Mobile Device (LAN)

You can use your computer as the server and a phone as the second player, as long as both are on the same WiFi network.

### Step 1 — Find Your Computer's Local IP Address

- **Windows:** Open Command Prompt and run `ipconfig`. Look for the **IPv4 Address** (e.g., `192.168.1.15`).
- **macOS / Linux:** Open Terminal and run `ifconfig | grep "inet "`.

### Step 2 — Connect from Your Phone

On your phone's browser, navigate to:

```
http://<YOUR_COMPUTER_IP>:8080/login
```

Replace `<YOUR_COMPUTER_IP>` with the IP found above (e.g., `http://192.168.1.15:8080/login`).

### Step 3 — Play

- **Computer:** Log in as `user1` and create a game.
- **Phone:** Log in as `user2` and join the game from "My Games".

> **Firewall Note:** If the phone cannot connect, ensure your computer's firewall allows **inbound TCP connections on port 8080**.

---

## Project Structure

```
spring-distributed-tictactoe/
├── src/
│   ├── main/
│   │   ├── java/          # Spring Boot application source code
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/ # Thymeleaf HTML templates
│   └── test/              # Unit and integration tests
├── documentation/         # Thesis, UML diagrams, and use-case documents
├── JavaDoc/               # Generated API documentation
├── pom.xml
└── README.md
```

---

## Documentation

Full system documentation — including the thesis and architectural UML diagrams — can be found in the [`documentation/`](documentation/) directory.

---

## License

This project was developed as an academic thesis project. All rights reserved.
