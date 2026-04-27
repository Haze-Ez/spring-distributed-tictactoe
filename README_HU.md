# Spring Elosztott Amőba Rendszer

> 🇬🇧 For the English version, see: [README.md](README.md)

---

## Áttekintés

Egy biztonságos, valós idejű többjátékos alkalmazás, amelyet **Spring Boot**, **Thymeleaf** és **PostgreSQL** technológiákra építettünk. A projekt elosztott MVC mintát valósít meg a játékállapot szinkronizálásához, a felhasználói munkamenet-kezeléshez és a perzisztens adattároláshoz, több egyidejű felhasználó kiszolgálása mellett.

---

## Főbb Funkciók

- **Elosztott Architektúra:** Több egyidejű játéksession kezelése állapotmentes RESTful kommunikáción keresztül.
- **Biztonság:** Spring Security alapú form-hitelesítés és szerepkör-alapú hozzáférés-vezérlés.
- **Perzisztencia:** JPA/Hibernate és PostgreSQL segítségével relációs adatintegritás (Felhasználói profilok, Játékelőzmények).
- **Játékmódok:** PvP (Helyi hálózat) és Egyjátékos mód (CPU ellen) egyaránt támogatott.

---

## Technológiai Stack

| Réteg         | Technológia                                       |
|---------------|---------------------------------------------------|
| **Backend**   | Java 17, Spring Boot 3.5.5 (Web, Security, JPA)  |
| **Frontend**  | Thymeleaf, JavaScript (Fetch API), CSS            |
| **Adatbázis** | PostgreSQL                                        |
| **Build**     | Maven (Maven Wrapper `mvnw` segítségével)         |

---

## Előfeltételek

Az alkalmazás futtatása előtt győződj meg arról, hogy a következők telepítve vannak:

- **Java 17+** — [Letöltés](https://adoptium.net/)
- **Maven 3.8+** (vagy használd a mellékelt `mvnw` wrappert — nem szükséges telepítés)
- **PostgreSQL 14+** — [Letöltés](https://www.postgresql.org/download/)

---

## Első Lépések

### 1. Adatbázis Beállítása

Nyisd meg a PostgreSQL kliensedet (`psql`), és futtasd az alábbi parancsokat az adatbázis és a felhasználó létrehozásához:

```sql
CREATE DATABASE tttdb;
CREATE USER tttuser WITH PASSWORD 'tttpass';
GRANT ALL PRIVILEGES ON DATABASE tttdb TO tttuser;
```

> **Megjegyzés:** Az alkalmazás `spring.jpa.hibernate.ddl-auto=update` beállítást használ, így a Hibernate az első indításkor automatikusan létrehozza az összes szükséges táblát. SQL sémaszkriptre nincs szükség.

### 2. Alkalmazás Konfigurálása

Az alapértelmezett adatbázis-adatok az `src/main/resources/application.properties` fájlban:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tttdb
spring.datasource.username=tttuser
spring.datasource.password=tttpass
```

Ha eltérő adatokat használsz, módosítsd ezt a fájlt az indítás előtt.

### 3. Az Alkalmazás Indítása

A Maven Wrapper segítségével (ajánlott — nem szükséges telepített Maven):

```bash
# Windows esetén
mvnw.cmd spring-boot:run

# Linux / macOS esetén
./mvnw spring-boot:run
```

Vagy, ha a Maven globálisan telepítve van:

```bash
mvn spring-boot:run
```

### 4. Az Alkalmazás Elérése

Nyisd meg a böngésződet, és navigálj a következő címre:

```
http://localhost:8080
```

A rendszer automatikusan átirányít a bejelentkezési oldalra. Hozz létre egy új fiókot a kezdéshez.

---

## Többjátékos Mód Tesztelése (PvP)

A valós idejű multiplayer funkció egyetlen gépen történő teszteléséhez **két különálló böngésző-munkamenetre** van szükség.

### 1. lépés — A böngésző megnyitása (X játékos)

1. Menj a `http://localhost:8080/login` címre.
2. Regisztrálj egy felhasználót (pl. `user1`), majd lépj be.
3. Kattints a **„Új Játék"** → **„Játékos vs Játékos"** menüpontra.
4. Várakozz a lobbyban — a Játék azonosítója megjelenik az URL-ben.

### 2. lépés — A böngésző megnyitása *Inkognitó módban* (O játékos)

> **Fontos:** **Kötelező** inkognitó módot vagy egy teljesen más böngészőt használni egy különálló munkamenet létrehozásához. Ugyanazon böngészőben egy második normál lap ugyanazt a bejelentkezési munkamenetet fogja megosztani.

1. Menj a `http://localhost:8080/login` címre.
2. Regisztrálj egy második felhasználót (pl. `user2`), majd lépj be.
3. Menj a **„Játékaim"** menüpontra, és keresd a **„Csatlakozható játékok"** listát.
4. Kattints a **„Csatlakozás O-ként"** gombra az `user1` által létrehozott játékhoz.

### 3. lépés — Szinkronizáció Ellenőrzése

- Tedd le az első lépést az A böngészőben.
- Figyeld meg, ahogy a tábla automatikusan frissül a B böngészőben (kb. 1 másodpercen belül, szerveroldali lekérdezés révén).
- Játssz győzelemig vagy döntetlen állásig, és ellenőrizd, hogy a játék vége felirat mindkét képernyőn egyidejűleg jelenik meg.

---

## Alternatíva: Tesztelés Mobil Eszközzel (LAN)

A számítógépedet szerverként, a telefonodat pedig második játékosként is használhatod, amennyiben mindkét eszköz ugyanahhoz a WiFi hálózathoz csatlakozik.

### 1. lépés — A Számítógép Helyi IP-Címének Megkeresése

- **Windows:** Nyisd meg a Parancssort, és futtasd az `ipconfig` parancsot. Keresd az **IPv4-cím** értéket (pl. `192.168.1.15`).
- **macOS / Linux:** Nyisd meg a Terminált, és futtasd az `ifconfig | grep "inet "` parancsot.

### 2. lépés — Csatlakozás a Telefonról

A telefon böngészőjében navigálj a következő címre:

```
http://<A_SZAMITOGEP_IP_CIME>:8080/login
```

Cseréld ki `<A_SZAMITOGEP_IP_CIME>` értékét a fent megtalált IP-címre (pl. `http://192.168.1.15:8080/login`).

### 3. lépés — Játék

- **Számítógép:** Lépj be `user1` fiókkal, és hozz létre egy játékot.
- **Telefon:** Lépj be `user2` fiókkal, és csatlakozz a játékhoz a „Játékaim" menüből.

> **Tűzfal megjegyzés:** Ha a telefon nem tud csatlakozni, győződj meg arról, hogy a számítógép tűzfala engedélyezi a **beérkező TCP kapcsolatokat a 8080-as porton**.

---

## Projektstruktúra

```
spring-distributed-tictactoe/
├── src/
│   ├── main/
│   │   ├── java/          # Spring Boot forráskód
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/ # Thymeleaf HTML sablonok
│   └── test/              # Egység- és integrációs tesztek
├── documentation/         # Szakdolgozat, UML diagramok, use-case dokumentumok
├── JavaDoc/               # Generált API dokumentáció
├── pom.xml
└── README.md
```

---

## Dokumentáció

A teljes rendszerdokumentáció — beleértve a szakdolgozatot és az architekturális UML-diagramokat — a [`documentation/`](documentation/) könyvtárban található.

---

## Licenc

Ez a projekt akadémiai szakdolgozatként készült. Minden jog fenntartva.
