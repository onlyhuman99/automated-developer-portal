# Automated Developer Portal

A centralized developer portal that discovers GitHub repositories, reads service metadata from `catalog-info.yaml`, and synchronizes registered services into a searchable service catalogue.

The portal provides a dashboard for viewing service information, lifecycle distribution, and engineering ownership.
## Features

- **GitHub integration:** Discover repositories from an authenticated GitHub account.
- **Metadata discovery:** Retrieve and parse root-level `catalog-info.yaml` files.
- **Metadata validation:** Validate required service metadata, including name, description, owner, and lifecycle.
- **Service catalogue:** Create, read, update, and delete registered services.
- **Manual synchronization:** Synchronize repository metadata with the service catalogue.
- **Dashboard:** View service counts, lifecycle distribution, and services grouped by owner.
- **Persistent storage:** Store catalogue records in PostgreSQL.
- **Containerized deployment:** Run the frontend, backend, and database using Docker Compose.

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | React, TypeScript, Vite |
| Styling | Tailwind CSS v4 |
| Backend | Java 21, Spring Boot |
| API | Spring Web MVC |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL 17 |
| GitHub integration | GitHub REST API |
| Metadata parsing | SnakeYAML |
| Containerization | Docker, Docker Compose |
| Web server | Nginx |

## Architecture
```text
                 GitHub REST API
                       |
                       v
              GitHub Integration
                       |
                       v
                Spring Boot API
                 /           \
                v             v
        Metadata Parser    Service Catalogue
                               |
                               v
                          PostgreSQL
                               |
                               v
                         REST Endpoints
                               |
                               v
                     React + TypeScript UI
                               |
                               v
                             Nginx
```

The frontend communicates with the Spring Boot backend through REST APIs. The backend retrieves repository information from GitHub, processes service metadata, and persists catalogue records in PostgreSQL.

## Project Structure

```text
automated-developer-portal/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── Dockerfile
│   ├── pom.xml
│   └── mvnw
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── infrastructure/
│   └── docker/
│       └── docker-compose.yml
│
├── catalog-info.yaml
└── .gitignore
```

## Prerequisites

Install the following:

- Docker
- Docker Compose
- A GitHub account
- A GitHub Personal Access Token with permission to read the repositories you want to discover

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/onlyhuman99/automated-developer-portal.git

cd automated-developer-portal
```

### 2. Configure GitHub authentication

Create a GitHub Personal Access Token with the required repository read permissions.

Export it in your terminal:

```bash
export GITHUB_TOKEN="your_github_token"
```

Do not commit your token or expose it in source code.

### 3. Start the application

Build and start all services:

```bash
docker compose -f infrastructure/docker/docker-compose.yml up --build -d
```

Check container status:

```bash
docker compose -f infrastructure/docker/docker-compose.yml ps
```

### 4. Access the application

| Component | URL |
|---|---|
| Frontend | http://localhost:3001 |
| Backend API | http://localhost:8080 |
| Backend health | http://localhost:8080/actuator/health |
| PostgreSQL | localhost:5433 |

The PostgreSQL database is persisted using a Docker named volume.

### 5. Stop the application

```bash
docker compose -f infrastructure/docker/docker-compose.yml down
```

This stops and removes the containers and network without deleting the persistent database volume.

## API Endpoints

### Service Catalogue

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/services` | Register a service |
| GET | `/api/v1/services` | Retrieve all services |
| GET | `/api/v1/services/{id}` | Retrieve a service by ID |
| PUT | `/api/v1/services/{id}` | Update a service |
| DELETE | `/api/v1/services/{id}` | Delete a service |

### GitHub Integration

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/github/repositories` | Discover GitHub repositories |
| POST | `/api/v1/github/sync` | Synchronize repository metadata |

### Dashboard

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/catalog/statistics` | Retrieve catalogue statistics |

## Service Metadata

Repositories can provide a `catalog-info.yaml` file in their root directory.

Example:

```yaml
apiVersion: backstage.io/v1alpha1
kind: Component

metadata:
  name: payment-service
  description: Handles payment processing and transactions

spec:
  owner: payments-team
  lifecycle: production
```

The application reads the metadata and validates the required fields before synchronizing a service into the catalogue.

## Testing

### Backend tests

Run from the backend directory:

```bash
cd backend
./mvnw clean test
```

### Frontend production build

Run from the frontend directory:

```bash
cd frontend
npm install
npm run build
```

## Current Scope

This MVP focuses on GitHub repository discovery, service metadata processing, catalogue management, manual synchronization, and dashboard visualization.

Advanced ownership validation, scheduled synchronization, multi-version-control support, Kubernetes deployment, and advanced analytics are outside the current MVP scope.

## Author

**Parth**

GitHub: [@onlyhuman99](https://github.com/onlyhuman99)

## License

No license has been specified yet.
