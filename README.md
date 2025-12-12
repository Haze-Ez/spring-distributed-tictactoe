\# Spring Distributed Tic-Tac-Toe System



\## Overview

A secure, real-time multiplayer application architecture built using \*\*Spring Boot\*\*, \*\*Thymeleaf\*\*, and \*\*PostgreSQL\*\*. This project implements a distributed MVC pattern to handle game state synchronization, user session management, and persistent data storage for multiple concurrent users.



\## Key Features

\* \*\*Distributed Architecture:\*\* Supports multiple concurrent game sessions via stateless RESTful communication.

\* \*\*Security:\*\* Implements Spring Security for authentication and role-based access control.

\* \*\*Persistence:\*\* Uses JPA/Hibernate with PostgreSQL for relational data integrity (User Profiles, Game History).

\* \*\*Game Modes:\*\* Supports PvP (Local Network) and Single Player (vs CPU).



\## Technology Stack

\* \*\*Backend:\*\* Java 17, Spring Boot 3 (Web, Security, Data JPA)

\* \*\*Frontend:\*\* Thymeleaf, JavaScript (Fetch API), CSS

\* \*\*Database:\*\* PostgreSQL

\* \*\*Build Tool:\*\* Maven



\## Documentation

Full system documentation, including the thesis and architectural UML diagrams, can be found in the `documentation/` directory.



\## Getting Started

1\.  Configure `application.properties` with your local PostgreSQL credentials.

2\.  Run the application: `mvn spring-boot:run`

3\.  Access via `http://localhost:8080`



\## How to Test Multiplayer (PvP)

To verify the real-time multiplayer functionality on a single machine:



1\.  \*\*Open Browser A (Player X):\*\*

&nbsp;   \* Go to `http://localhost:8080/login`.

&nbsp;   \* Register a user (e.g., `user1`) and log in.

&nbsp;   \* Click "New Game" -> "Player vs Player".

&nbsp;   \* Copy the Game ID from the URL (or wait in the lobby).



2\.  \*\*Open Browser B in \*Incognito/Private Mode\* (Player O):\*\*

&nbsp;   \* \*Note: You must use Incognito mode or a different browser to create a separate session.\*

&nbsp;   \* Go to `http://localhost:8080/login`.

&nbsp;   \* Register a second user (e.g., `user2`) and log in.

&nbsp;   \* Go to "My Games" and look for the "Open Games to Join" list.

&nbsp;   \* Click "Join as O" on the game created by `user1`.



3\.  \*\*Verify Synchronization:\*\*

&nbsp;   \* Place a move in Browser A.

&nbsp;   \* Observe the board update automatically in Browser B (within 1 second).

&nbsp;   \* Play until a win/draw to see the game-over overlay on both screens.



\### Alternative: Testing with a Mobile Device (LAN)

You can also test the multiplayer feature by using your computer as the host and a mobile phone as the second player.



1\.  \*\*Connect to the same Network:\*\* Ensure both your computer and phone are connected to the same WiFi.

2\.  \*\*Find your Computer's Local IP:\*\*

&nbsp;   \* \*\*Windows:\*\* Open Command Prompt and type `ipconfig`. Look for "IPv4 Address" (e.g., `192.168.1.15`).

&nbsp;   \* \*\*Mac/Linux:\*\* Open Terminal and type `ifconfig | grep "inet "`.

3\.  \*\*Open Browser on Phone:\*\*

&nbsp;   \* Navigate to `http://YOUR\_COMPUTER\_IP:8080/login` (replace `YOUR\_COMPUTER\_IP` with the numbers found above).

4\.  \*\*Play:\*\*

&nbsp;   \* \*\*Computer:\*\* Log in as `user1` and create a game.

&nbsp;   \* \*\*Phone:\*\* Log in as `user2` and join the game.

&nbsp;   \* \*Note: If the phone cannot connect, ensure your computer's Firewall allows connections on port 8080.\*

