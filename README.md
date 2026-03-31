# ICT Technologies for the Web — Dynamic Version (CSR/SPA)

> **Grade: 28/30** — Final project for the [ICT Technologies for the Web](https://onlineservices.polimi.it/manifesti/manifesti/controller/ManifestoPublic.do?EVN_DETTAGLIO_RIGA_MANIFESTO=evento&aa=2025&k_cf=225&k_corso_la=531&k_indir=I3I&codDescr=085879&lang=IT&semestre=2&idGruppo=5457&idRiga=335673) course at **Politecnico di Milano**.

This repository contains the **dynamic (CSR + SPA)** implementation of a hierarchical category management web application. The counterpart static (SSR) version is maintained in a separate repository.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
  - [Three-Tier Design](#three-tier-design)
  - [Category Encoding Scheme](#category-encoding-scheme)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [REST API Reference](#rest-api-reference)
- [Database Schema](#database-schema)
- [Frontend](#frontend)
- [Backend](#backend)
  - [Servlets (Controllers)](#servlets-controllers)
  - [Filters](#filters)
  - [DAO Layer](#dao-layer)
  - [Beans](#beans)
  - [Utilities](#utilities)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Option A — Docker Compose (recommended)](#option-a--docker-compose-recommended)
  - [Option B — Manual Setup](#option-b--manual-setup)
- [Configuration](#configuration)
- [Documentation & Diagrams](#documentation--diagrams)
- [License](#license)

---

## Project Overview

The application is a **hierarchical category manager** that allows authenticated users to:

- View the full category tree rendered dynamically in the browser
- Create new categories under any parentable node
- Rename existing categories in-place via click-to-edit
- Copy (clone) an entire subtree to another location using drag-and-drop

The application is implemented as a **Single Page Application (SPA)** with **Client-Side Rendering (CSR)**: the server only serves static files and exposes a JSON REST API. All HTML rendering and DOM manipulation is performed client-side with vanilla JavaScript — no frontend framework is used.

---

## Architecture

### Three-Tier Design

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                     │
│                                                             │
│   index.html / home.html  ←→  login.js / home.js            │
│                                   ↕  fetch() (JSON/HTTP)    │
│                           categoryTree.js                   │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP (port 8080)
┌──────────────────────────────▼──────────────────────────────┐
│                  APPLICATION SERVER (Tomcat 9)              │
│                                                             │
│  Static files served from /webapp                           │
│                                                             │
│  Filters:  NoCacher  →  LoginChecker                        │
│                                                             │
│  Servlets: /login  /logout  /categories                     │
│            /createCategory  /renameCategory  /doCopy        │
│                                                             │
│  DAO Layer:  UserDAO  ←→  CategoryDAO                       │
└──────────────────────────────┬──────────────────────────────┘
                               │ JDBC (port 3306)
┌──────────────────────────────▼──────────────────────────────┐
│                    DATABASE (MySQL)                         │
│                                                             │
│  Schema: tiw                                                │
│  Tables: user,  category                                    │
└─────────────────────────────────────────────────────────────┘
```

The same Tomcat instance serves both static assets (HTML, CSS, JS) and the dynamic REST API. Request routing is handled via `web.xml` servlet mappings and two servlet filters.

### Category Encoding Scheme

The hierarchy is encoded using a **prefix-based numeric code** — no adjacency list or closure table is required:

| Code   | Name              | Depth |
|--------|-------------------|-------|
| `1`    | Materiali solidi  | 1     |
| `11`   | Materiali inerti  | 2     |
| `111`  | Inerti da edilizia| 3     |
| `1111` | Amianto           | 4     |
| `11111`| Amianto in lastre | 5     |

Key properties of this scheme:
- **Parent lookup**: remove the last character from the code.
- **Children lookup**: `WHERE code LIKE CONCAT(parentCode, '_')` (exactly one extra digit).
- **Subtree lookup**: `WHERE code LIKE CONCAT(parentCode, '%')`.
- **Subtree copy**: a single `INSERT … SELECT` with string manipulation — no recursion required (see `CategoryDAO.copySubTree`).
- **Max branching factor**: 9 children per node (digits 1–9).

---

## Technology Stack

| Layer      | Technology                          | Version   |
|------------|--------------------------------------|-----------|
| Runtime    | Java (OpenJDK / Eclipse Temurin)     | 19        |
| App server | Apache Tomcat                        | 9.0.74    |
| Build tool | Apache Maven                         | 3.8.7     |
| Packaging  | WAR                                  | —         |
| Database   | MySQL                                | 8.x       |
| JDBC driver| mysql-connector-java                 | 8.0.33    |
| JSON       | Gson (Google)                        | 2.10.1    |
| Sanitising | Apache Commons Text                  | 1.10.0    |
| Templating | Thymeleaf *(declared, not used in dynamic version)* | 3.0.15 |
| Frontend   | Vanilla JS (ES modules), HTML5, CSS3 | —         |
| Container  | Docker / Docker Compose              | —         |

---

## Project Structure

```
ICT-Technologies-for-the-Web-Dynamic/
│
├── src/main/
│   ├── java/it/group117/
│   │   ├── beans/
│   │   │   ├── Category.java          # Category POJO (code, name, parentable)
│   │   │   └── User.java              # User POJO (username only)
│   │   ├── controllers/
│   │   │   ├── CheckLogin.java        # POST /login
│   │   │   ├── Logout.java            # GET  /logout
│   │   │   ├── GetCategories.java     # GET  /categories
│   │   │   ├── CreateCategory.java    # POST /createCategory
│   │   │   ├── RenameCategory.java    # POST /renameCategory
│   │   │   └── DoCopy.java            # POST /doCopy
│   │   ├── dao/
│   │   │   ├── CategoryDAO.java       # All category DB operations
│   │   │   └── UserDAO.java           # Credential verification
│   │   ├── filters/
│   │   │   ├── LoginChecker.java      # Session guard for protected routes
│   │   │   └── NoCacher.java          # Cache-Control headers for home.html
│   │   └── utils/
│   │       ├── ConnectionHandler.java # JDBC connection factory
│   │       └── JsonResponse.java      # Utility to write JSON HTTP responses
│   └── webapp/
│       ├── WEB-INF/web.xml            # Servlet / filter / DB config
│       ├── META-INF/MANIFEST.MF
│       ├── index.html                 # Login page (SPA entry point)
│       ├── home.html                  # Main application page
│       ├── login.js                   # Login form handler
│       ├── home.js                    # Home page bootstrap + create/logout
│       ├── categoryTree.js            # Tree rendering + D&D + rename logic
│       ├── style.css
│       └── types.d.ts                 # JSDoc type declarations
│
├── SQL/
│   ├── CreateDB.sql                   # Schema and table definitions
│   ├── InsertSampleData.sql           # Sample categories and users
│   └── ExampleQueries.sql            # Reference queries (tree ops, subtree copy)
│
├── docs/
│   ├── Documentazione - RIA.pdf       # Full technical documentation (Italian)
│   └── raw/images/                    # IFML and sequence diagrams (draw.io + PNG)
│       ├── Home.IFML.drawio.png
│       ├── Login.IFML.drawio.png
│       ├── ERD.drawio.png
│       ├── CategoryCreation.SD.drawio.png
│       ├── CheckCredentials.SD.drawio.png
│       ├── EventLogin.SD.drawio.png
│       ├── EventLogout.SD.drawio.png
│       ├── EventHomeOnload.SD.drawio.png
│       ├── EventRenameCategory.SD.drawio.png
│       ├── EventSubtreeCopy.SD.drawio.png
│       ├── GetAllCategories.SD.drawio.png
│       └── NoCacher.SD.drawio.png
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── LICENSE
```

---

## REST API Reference

All endpoints return `Content-Type: application/json; charset=UTF-8`. Protected endpoints require an active HTTP session (set by `/login`); unauthenticated requests are redirected to the login page by `LoginChecker`.

### `POST /login`

Authenticates a user and creates an HTTP session.

**Request body** (`application/x-www-form-urlencoded`):

| Parameter  | Type   | Required | Description        |
|------------|--------|----------|--------------------|
| `username` | string | ✓        | Account username   |
| `password` | string | ✓        | Account password   |

**Response:**

```json
{ "success": true,  "message": "",                    "username": "admin" }
{ "success": false, "message": "Wrong username or password", "username": "" }
```

---

### `GET /logout` 🔒

Invalidates the current session and redirects to `/`.

---

### `GET /categories` 🔒

Returns the full category tree as a flat, code-ordered array. The virtual root node (`code: "/"`) is always the first element.

**Response:**

```json
[
  { "code": "/",    "name": "Root",              "parentable": true  },
  { "code": "1",    "name": "Materiali solidi",  "parentable": true  },
  { "code": "11",   "name": "Materiali inerti",  "parentable": true  },
  { "code": "111",  "name": "Inerti da edilizia","parentable": true  },
  { "code": "1111", "name": "Amianto",           "parentable": false }
]
```

The `parentable` flag is `true` when the node has fewer than 9 direct children.

---

### `POST /createCategory` 🔒

Creates a new leaf node under the specified parent.

**Request body** (`application/x-www-form-urlencoded`):

| Parameter | Type   | Required | Description                              |
|-----------|--------|----------|------------------------------------------|
| `parent`  | string | ✓        | Code of the parent node (`"/"` for root) |
| `name`    | string | ✓        | Display name of the new category         |

**Response:**

```json
{ "success": true,  "message": "",                              "code": "112" }
{ "success": false, "message": "Chosen node is not parent-able","code": ""    }
```

---

### `POST /renameCategory` 🔒

Updates the display name of an existing category.

**Request body** (`application/x-www-form-urlencoded`):

| Parameter | Type   | Required | Description               |
|-----------|--------|----------|---------------------------|
| `code`    | string | ✓        | Code of the category      |
| `newName` | string | ✓        | New display name          |

**Response:**

```json
{ "success": true,  "message": "" }
{ "success": false, "message": "Category not found" }
```

---

### `POST /doCopy` 🔒

Clones an entire subtree under a new parent node. The copy is performed atomically via a single `INSERT … SELECT` SQL statement.

**Request body** (`application/x-www-form-urlencoded`):

| Parameter | Type   | Required | Description                                 |
|-----------|--------|----------|---------------------------------------------|
| `src`     | string | ✓        | Root code of the subtree to copy            |
| `dest`    | string | ✓        | Code of the destination parent (`"/"` for root) |

**Response:**

```json
{ "success": true,  "message": "" }
{ "success": false, "message": "Chosen node is not parent-able" }
{ "success": false, "message": "Can't clone root of tree" }
```

---

## Database Schema

```sql
CREATE SCHEMA IF NOT EXISTS tiw;

CREATE TABLE IF NOT EXISTS tiw.`user` (
    username  VARCHAR(64)  PRIMARY KEY,
    `password` VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS tiw.category (
    code  VARCHAR(64)  PRIMARY KEY,   -- Prefix-encoded hierarchy key
    name  VARCHAR(64)  NOT NULL
);
```

### Notable SQL patterns

The `ExampleQueries.sql` file documents several non-trivial patterns enabled by the prefix encoding:

**Find all descendants (inclusive):**
```sql
SELECT * FROM tiw.category WHERE code LIKE CONCAT(:parentCode, '%') ORDER BY code;
```

**Count direct children:**
```sql
SELECT COUNT(*) FROM tiw.category WHERE code LIKE CONCAT(:parentCode, '_');
```

**Atomic subtree copy:**
```sql
INSERT INTO tiw.category (code, name)
SELECT CONCAT(:newRoot, SUBSTRING(c.code, INSTR(c.code, :srcRoot) + LENGTH(:srcRoot))), name
FROM tiw.category c
WHERE c.code LIKE CONCAT(:srcRoot, '%');
```

**Full tree with parentability flag:**
```sql
SELECT code, name,
  (SELECT COUNT(*) FROM tiw.category AS i WHERE i.code LIKE CONCAT(o.code, '_')) < 9 AS parentable
FROM tiw.category AS o ORDER BY code;
```

---

## Frontend

The frontend is entirely vanilla JavaScript using **ES modules** — no build step, no bundler, no framework.

### Pages

| File         | Route         | Description                             |
|--------------|---------------|-----------------------------------------|
| `index.html` | `/`           | Login page; redirects to `home.html` on success |
| `home.html`  | `/home.html`  | Main SPA; protected by `LoginChecker`  |

### Scripts

**`login.js`** — Handles the login form submission via `fetch()`. On success, stores the username in `localStorage` and redirects to `home.html`.

**`home.js`** — Bootstraps the home page: displays the username from `localStorage`, wires up the category creation form, and handles logout. Imports `fetchCategories` from `categoryTree.js`.

**`categoryTree.js`** — Core SPA module. Responsibilities:
- `fetchCategories()` — fetches `/categories`, rebuilds the tree DOM, and repopulates the parent `<select>`.
- `createCategoryTree(data)` — clears and re-renders the entire `#category-tree` container from the flat ordered array.
- `createDivFromSubtree(node, tree)` — recursively constructs a `<div>` for each node, wiring up:
  - **Rename**: click-to-edit on the name `<span>`, blur/submit fires `POST /renameCategory`.
  - **Drag-and-drop copy**: `dragstart` / `dragover` / `dragleave` / `drop` events. A visual preview clone is appended immediately; the actual `POST /doCopy` is sent only when the user confirms via the Save button.
- Copy state is tracked with module-level flags (`isCloningCategory`, `copyInfo`) to prevent concurrent operations.

### Client-Side State Management

```
localStorage.username   ← set on login, cleared on logout
isCloningCategory       ← prevents concurrent D&D copy operations
copyInfo.{src, dest}    ← staged copy parameters before server confirmation
```

---

## Backend

### Servlets (Controllers)

All servlets follow the same lifecycle pattern:

1. `init()` — acquires a JDBC `Connection` via `ConnectionHandler`.
2. `doPost()` / `doGet()` — validates input (using `StringEscapeUtils.escapeJava` for XSS prevention), delegates to the appropriate DAO, and writes a JSON response via `JsonResponse`.
3. `destroy()` — closes the database connection.

| Servlet           | Mapping             | Key logic |
|-------------------|---------------------|-----------|
| `CheckLogin`      | `/login`            | Verifies credentials via `UserDAO`, creates `HttpSession` with `user` attribute |
| `Logout`          | `/logout`           | Invalidates session, redirects to context root |
| `GetCategories`   | `/categories`       | Calls `CategoryDAO.getFullCategoryTree()`, serialises with Gson |
| `CreateCategory`  | `/createCategory`   | Calls `CategoryDAO.createNewCategory(parent, name)` |
| `RenameCategory`  | `/renameCategory`   | Calls `CategoryDAO.renameCategory(code, newName)` |
| `DoCopy`          | `/doCopy`           | Calls `CategoryDAO.copySubTree(src, dest)`, treats `"/"` as empty string for root |

### Filters

**`NoCacher`** — Applied to `/home.html`. Sets `Cache-Control: no-cache, no-store, must-revalidate` so that browsers always request a fresh copy of the page, preventing stale session state from being rendered.

**`LoginChecker`** — Applied to `/home.html`, `/categories`, `/createCategory`, `/renameCategory`, `/doCopy`. Checks for a valid `user` attribute in the `HttpSession`; redirects unauthenticated requests to the context root.

### DAO Layer

**`CategoryDAO`** — All operations use `PreparedStatement` to prevent SQL injection.

| Method                          | Description                                                    |
|---------------------------------|----------------------------------------------------------------|
| `getFullCategoryTree()`         | Returns a `LinkedList<Category>` ordered by code; prepends virtual root `"/"` |
| `createNewCategory(parent, name)` | Verifies parent exists and has capacity, builds new code, inserts |
| `renameCategory(code, newName)` | Updates `name` for the given code                             |
| `copySubTree(src, dest)`        | Atomic `INSERT … SELECT` subtree clone                        |
| `getNumberOfDirectChildren(code)` | Helper: counts `LIKE CONCAT(code, '_')` matches             |
| `doesCategoryExist(code)`       | Returns `true` for empty string (root) or an existing code    |

**`UserDAO`** — `checkCredentials(username, password)` returns a `User` bean on match, `null` otherwise.

### Beans

- **`Category`** — `String code`, `String name`, `boolean parentable` (Serializable)
- **`User`** — `String username` only; password is never stored in the bean

### Utilities

- **`ConnectionHandler`** — Reads `dbDriver`, `dbUrl`, `dbUser`, `dbPassword` from `ServletContext` init parameters and returns a `DriverManager` connection. Throws `UnavailableException` on failure to make Tomcat mark the servlet as unavailable.
- **`JsonResponse`** — Two overloaded `sendJsonResponse` static methods: one accepts a `JsonObject`, the other a pre-serialised `String`. Both set `Content-Type: application/json; charset=UTF-8`.

---

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/)  
  **or**
- JDK 19, Maven 3.8+, Tomcat 9.0.74, MySQL 8.x

### Option A — Docker Compose (recommended)

```bash
git clone <repo-url>
cd ICT-Technologies-for-the-Web-Dynamic

# 1. Build the WAR
mvn clean package

# 2. Start MySQL + Tomcat
docker compose up --build
```

Tomcat will be available at **http://localhost:8080/TIWProjec**.  
MySQL is exposed on port **3306**.

> **Note:** The `docker-compose.yml` mounts `./target` into Tomcat's `webapps/` directory. The WAR built by Maven must exist before starting containers.

**Seed the database:**

```bash
# Connect to the running MySQL container
docker exec -it <mysql-container-name> mysql -u root -p tiw

# Then run the SQL scripts in order:
source /path/to/SQL/CreateDB.sql
source /path/to/SQL/InsertSampleData.sql
```

Or copy the scripts into the container first:

```bash
docker cp SQL/CreateDB.sql <mysql-container>:/tmp/
docker cp SQL/InsertSampleData.sql <mysql-container>:/tmp/
docker exec -it <mysql-container> mysql -u root -p -e "source /tmp/CreateDB.sql; source /tmp/InsertSampleData.sql;"
```

### Option B — Manual Setup

```bash
# 1. Create the database
mysql -u root -p < SQL/CreateDB.sql
mysql -u root -p tiw < SQL/InsertSampleData.sql

# 2. Update DB credentials in src/main/webapp/WEB-INF/web.xml
#    (see Configuration section below)

# 3. Build
mvn clean package

# 4. Deploy the WAR to your local Tomcat
cp target/TIWProjec.war $CATALINA_HOME/webapps/

# 5. Start Tomcat
$CATALINA_HOME/bin/startup.sh
```

### Default credentials

| Username | Password  |
|----------|-----------|
| `admin`  | `abc123`  |
| `user`   | `password`|

---

## Configuration

Database connection parameters are declared as `<context-param>` entries in `src/main/webapp/WEB-INF/web.xml`:

```xml
<context-param>
    <param-name>dbUrl</param-name>
    <param-value>jdbc:mysql://localhost:3306/tiw</param-value>
</context-param>
<context-param>
    <param-name>dbUser</param-name>
    <param-value>root</param-value>
</context-param>
<context-param>
    <param-name>dbPassword</param-name>
    <param-value>your-password-here</param-value>
</context-param>
<context-param>
    <param-name>dbDriver</param-name>
    <param-value>com.mysql.cj.jdbc.Driver</param-value>
</context-param>
```

> ⚠️ **Security note:** The `docker-compose.yml` and `web.xml` currently contain plaintext credentials. For any non-local deployment, replace these with environment variables or a secrets manager.

---

## Documentation & Diagrams

Full technical documentation (in Italian) is available under `docs/`:

- `docs/Documentazione - RIA.pdf` — Architecture, design decisions, IFML models, sequence diagrams
- `docs/raw/images/` — Individual diagram exports (PNG) and editable draw.io sources:

| Diagram | Description |
|---------|-------------|
| `ERD.drawio.png` | Entity-Relationship Diagram |
| `Home.IFML.drawio.png` | IFML model — home page interactions |
| `Login.IFML.drawio.png` | IFML model — login flow |
| `CategoryCreation.SD.drawio.png` | Sequence diagram — category creation |
| `CheckCredentials.SD.drawio.png` | Sequence diagram — credential check |
| `EventLogin.SD.drawio.png` | Sequence diagram — login event |
| `EventLogout.SD.drawio.png` | Sequence diagram — logout event |
| `EventHomeOnload.SD.drawio.png` | Sequence diagram — home page onload |
| `EventRenameCategory.SD.drawio.png` | Sequence diagram — rename category |
| `EventSubtreeCopy.SD.drawio.png` | Sequence diagram — subtree copy |
| `GetAllCategories.SD.drawio.png` | Sequence diagram — fetch category tree |
| `NoCacher.SD.drawio.png` | Sequence diagram — NoCacher filter |

---

## License

This project is distributed under the terms of the license found in the [`LICENSE`](LICENSE) file.
