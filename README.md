# DEV OPS 2 Project

# Voting App

A scalable Spring Boot application for managing electronic voting processes, complete with REST APIs, a web UI, and Prometheus metrics. Designed for containerized deployment in Kubernetes environments.
The application is built with **Spring Boot 3.4.4** and targets **Java 17**. It is designed for containerization and deployment on **Kubernetes**, leveraging Spring Boot's production-ready capabilities .

## 📋 Features

- 📥 Initialize **voters** and **voting options**
- ✅ Track voter **status** (voted or not)
- 🗳 Cast **votes** securely
- 📊 Retrieve **results**
- 🌐 UI via `Thymeleaf`

| Layer | Technology |
| --- | --- |
| Backend | Spring Boot 3.4.4 |
| Language | Java 17 |
| ORM | Spring Data JPA |
| DB | PostgreSQL |
| UI Engine | Thymeleaf (HTML templates) |
| Observability | Spring Boot Actuator + Micrometer |
| Monitoring | Prometheus (metrics endpoint) |
| Deployment | Docker + Kubernetes ready |

## 📦 Maven Dependencies

Key dependencies from `pom.xml`:

- `spring-boot-starter-web`: REST API
- `spring-boot-starter-data-jpa`: ORM
- `spring-boot-starter-thymeleaf`: Web UI
- `spring-boot-starter-actuator`: Production endpoints
- `micrometer-registry-prometheus`: Metrics export
- `postgresql`: Runtime database
- `lombok`: Reduces boilerplate

## 🧩 Application Layers

Based on standard patterns, the following layers are expected:

1. **Model Layer**: Entities for Voter, Vote, and Option
2. **Repository Layer**: Interfaces extending `JpaRepository`
3. **Service Layer**: Business logic for validation, voting rules, etc.
4. **Controller Layer**: Exposes REST + Web endpoints
5. **Configuration Layer** (optional): For metrics, database, security

## Configuration

This configuration sets up a Spring Boot application with:

- **PostgreSQL database** connection using environment variables (`SPRING_DATASOURCE_*`) for flexibility and security.
- **Hibernate JPA** for ORM with automatic schema updates and PostgreSQL-optimized SQL.
- **Minimal logging** for SQL to reduce noise in logs.
- The **application runs on port 8080**, while **Actuator endpoints** (like health checks and Prometheus metrics) are exposed separately on **port 9321**.
- **Prometheus metrics** are enabled to support observability in cloud-native environments like Kubernetes.

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false

logging:
  level:
    org:
      hibernate:
        SQL: warn
        type:
          descriptor:
            sql:
              BasicBinder: warn

server:
  port: 8080

management:
  server:
    port: 9321
    base-path: /
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    prometheus:
      enabled: true

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false

logging:
  level:
    org:
      hibernate:
        SQL: warn
        type:
          descriptor:
            sql:
              BasicBinder: warn

server:
  port: 8080

management:
  server:
    port: 9321
    base-path: /
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    prometheus:
      enabled: true

```

## Docker File

- **Multi-stage build**: Reduces final image size by separating build and runtime stages.
- **Optimized for Java 17**: Uses Eclipse Temurin, a high-performance OpenJDK distribution.
- **Efficient**: Skips tests in build and pre-fetches dependencies.
- **Deployment-ready**: Suitable for use in Kubernetes or any container orchestration platform.

```docker
# Use a Maven image with Java 17 (Eclipse Temurin distribution) to build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container to /app
WORKDIR /app

# Copy the Maven configuration file (pom.xml) to the working directory
COPY pom.xml .

# Download all project dependencies for offline build (faster repeated builds)
RUN mvn dependency:go-offline

# Copy the application source code to the container
COPY src ./src

# Build the application and skip tests to speed up build time
RUN mvn clean package -DskipTests

# Use a lightweight JDK 17 runtime image for the final container
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the runtime container
WORKDIR /app

# Copy the built JAR file from the "build" stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 for access (must match server.port in config)
EXPOSE 8080

# Run the application using the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

```

# Data Base

## 🧩 PostgreSQL

The app uses **PostgreSQL** to persist:

- Voter details (ID, name, national ID, voting status)
- Voting options
- Vote records (linked to voters and options)

Why PostgreSQL?

- High reliability and SQL compliance
- Great support for concurrent transactions (important for voting)
- Excellent integration with Spring Boot via JPA

## 🖥️ pgAdmin

**pgAdmin 4** is included for:

- Easy inspection and modification of database contents
- Viewing voter/vote tables visually
- Running manual SQL queries for debugging or reporting

## Docker Compose

- Spins up a **PostgreSQL database** for persistent storage.
- Deploys **pgAdmin**, a web-based UI for managing the database.
- All services are **containerized**, **interconnected**, and **self-contained** for local development or CI/CD pipelines.

```yaml
version: "3.8"  # Specifies the Docker Compose file version

services:
  # PostgreSQL Database Service
  db:
    image: postgres                  # Use the official PostgreSQL image
    container_name: local_pgdb       # Set a custom container name
    restart: always                  # Always restart if container stops
    ports:
      - "5432:5432"                  # Map local port 5432 to container's port 5432
    environment:
      POSTGRES_USER: user-name       # Set the PostgreSQL username
      POSTGRES_PASSWORD: strong-password  # Set the PostgreSQL password
    volumes:
      - local_pgdata:/var/lib/postgresql/data  # Persist database data between restarts

  # pgAdmin - Web UI for PostgreSQL
  pgadmin:
    image: dpage/pgadmin4            # Use the official pgAdmin4 image
    container_name: pgadmin4_container  # Name the container
    restart: always                  # Restart automatically on failure or reboot
    ports:
      - "8888:80"                    # Access pgAdmin at localhost:8888
    environment:
      PGADMIN_DEFAULT_EMAIL: user-name@domain-name.com  # Login email for pgAdmin
      PGADMIN_DEFAULT_PASSWORD: strong-password         # Login password for pgAdmin
    volumes:
      - pgadmin-data:/var/lib/pgadmin  # Persist pgAdmin user settings and configs

volumes:
  local_pgdata:   # Volume for persisting PostgreSQL data
  pgadmin-data:   # Volume for persisting pgAdmin configuration

```

# Full Docker Compose (APP & DB)

```yaml
version: "3.8"  # Specifies the Docker Compose file version

services:
  # PostgreSQL Database Service
  db:
    image: postgres                  # Use the official PostgreSQL image
    container_name: local_pgdb       # Set a custom container name
    restart: always                  # Always restart if container stops
    ports:
      - "5432:5432"                  # Map local port 5432 to container's port 5432
    environment:
      POSTGRES_USER: user-name       # Set the PostgreSQL username
      POSTGRES_PASSWORD: strong-password  # Set the PostgreSQL password
    volumes:
      - local_pgdata:/var/lib/postgresql/data  # Persist database data between restarts

  # pgAdmin - Web UI for PostgreSQL
  pgadmin:
    image: dpage/pgadmin4            # Use the official pgAdmin4 image
    container_name: pgadmin4_container  # Name the container
    restart: always                  # Restart automatically on failure or reboot
    ports:
      - "8888:80"                    # Access pgAdmin at localhost:8888
    environment:
      PGADMIN_DEFAULT_EMAIL: user-name@domain-name.com  # Login email for pgAdmin
      PGADMIN_DEFAULT_PASSWORD: strong-password         # Login password for pgAdmin
    volumes:
      - pgadmin-data:/var/lib/pgadmin  # Persist pgAdmin user settings and configs

  # Spring Boot Voting App Service
  voting-app:
    build: .                         # Build from the Dockerfile in the current directory
    container_name: voting_app       # Name of the app container
    depends_on:
      - db                           # Ensure the database starts before the app
    ports:
      - "8080:8080"                  # Expose the app on port 8080
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/postgres
      SPRING_DATASOURCE_USERNAME: user-name
      SPRING_DATASOURCE_PASSWORD: strong-password
      SPRING_JPA_HIBERNATE_DDL_AUTO: update  # Enable schema auto-update on boot

volumes:
  local_pgdata:   # Volume for persisting PostgreSQL data
  pgadmin-data:   # Volume for persisting pgAdmin configuration

```

# Voting App and Data Baser Manifests

## Voting App

### Deployment

A **Deployment** in Kubernetes manages the lifecycle of application **Pods**. It defines:

- **What** to run (e.g., Docker image)
- **How many replicas** to run
- **How to update** the application safely
- **How to restart** Pods if they fail

Deployments provide **declarative updates**, **scaling**, **rollback**, and **self-healing**.

### Service

A **Service** exposes a set of Pods as a **network endpoint** so they can be accessed:

- Internally (by other apps inside the cluster)
- Externally (with `NodePort`, `LoadBalancer`, or `Ingress`)

It **load balances** traffic across Pods matching a **selector** (like `app: voting-app`).

### Why Use a Deployment for the Voting App?

The voting app needs to:

- Run **multiple replicas** (3 in this case) for load balancing and high availability
- Restart automatically if a Pod crashes
- Be **updated safely** without downtime
- Expose **metrics** for Prometheus scraping (for monitoring)

A Deployment ensures all this is handled automatically.

```yaml
# SERVICE: Exposes the voting app to other services in the cluster
apiVersion: v1
kind: Service
metadata:
  name: voting-app             # Service name
  namespace: voting-app        # Scoped to the voting-app namespace
spec:
  selector:
    app: voting-app            # Targets Pods with this label
  ports:
    - name: http
      port: 8080               # Service port (what clients use)
      targetPort: 8080         # Container port (inside Pod)
    - name: metrics
      port: 9321               # Expose Prometheus metrics on this port
      targetPort: 9321

# DEPLOYMENT: Defines how to run the voting app pods
apiVersion: apps/v1
kind: Deployment
metadata:
  name: voting-app
  namespace: voting-app
spec:
  replicas: 3                  # Run 3 identical Pods for load balancing
  selector:
    matchLabels:
      app: voting-app          # Matches Pods with this label
  template:
    metadata:
      labels:
        app: voting-app        # This label connects Pods to Service and Deployment
      annotations:
        prometheus.io/scrape: "true"                # Tell Prometheus to scrape this Pod
        prometheus.io/port: "9321"                  # Metrics exposed on this port
        prometheus.io/path: "/actuator/prometheus"  # Path to scrape
    spec:
      containers:
        - name: voting-app
          image: voting-app                        # Use the image called 'voting-app'
          imagePullPolicy: IfNotPresent            # Use local image if available
          ports:
            - containerPort: 8080                  # Application traffic port
            - containerPort: 9321                  # Metrics port
          env:                                     # Set environment variables for DB config
            - name: SPRING_DATASOURCE_URL
              value: jdbc:postgresql://db:5432/postgres
            - name: SPRING_DATASOURCE_USERNAME
              value: user-name
            - name: SPRING_DATASOURCE_PASSWORD
              value: strong-password
            - name: SPRING_JPA_HIBERNATE_DDL_AUTO
              value: update                        # Automatically update the DB schema

```

## Data Base (PostgreSQL)

### Stateful set

A **StatefulSet** is a Kubernetes workload object used to manage **stateful applications**. Unlike Deployments (which are good for stateless apps), StatefulSets are ideal when:

- You need **stable, unique network identifiers** for each Pod (e.g., `pod-0`, `pod-1`).
- Each Pod needs **persistent storage** that **doesn't get lost** when the Pod is deleted or restarted.
- Pods must be started/stopped in **order** (e.g., primary/replica databases).

### Why Use a StatefulSet for PostgreSQL?

PostgreSQL is a **stateful database**. It needs:

- Persistent storage to retain data across restarts.
- A **stable identity** (hostname, storage volume) to avoid data loss or conflicts.
- One replica (here) — but if expanded, **each replica must be uniquely addressable**.

So, using a **StatefulSet** ensures:
✅ Persistent data

✅ Stable hostnames (`postgres-0.db.voting-app.svc.cluster.local`)

✅ Proper lifecycle management

```yaml
# SERVICE: Headless service used by StatefulSet for stable DNS and pod discovery
apiVersion: v1
kind: Service
metadata:
  name: db                    # This is the service that Pods will use to resolve DNS
  namespace: voting-app
spec:
  ports:
    - port: 5432              # PostgreSQL default port
      targetPort: 5432
  clusterIP: None             # Headless service (no load balancing), allows direct Pod addressing
  selector:
    app: postgres             # Targets Pods labeled with app=postgres

# STATEFULSET: Used to run and manage a PostgreSQL instance with persistent storage
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
  namespace: voting-app
spec:
  selector:
    matchLabels:
      app: postgres           # Matches Pods with this label
  serviceName: "db"           # Tied to the headless service for stable network identity
  replicas: 1                 # Only one PostgreSQL instance for now
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres     # Uses the official PostgreSQL Docker image
          ports:
            - containerPort: 5432
          env:                # Set credentials and other config
            - name: POSTGRES_USER
              value: "user-name"
            - name: POSTGRES_PASSWORD
              value: "strong-password"
          volumeMounts:
            - name: pgdata
              mountPath: /var/lib/postgresql/data  # Data will be stored here
  volumeClaimTemplates:
    - metadata:
        name: pgdata
      spec:
        accessModes: ["ReadWriteOnce"]             # One node can read/write
        resources:
          requests:
            storage: 1Gi                           # Requests 1Gi of persistent storage

```

## PG Admin

This configuration defines a **pgAdmin** web UI deployment in Kubernetes to manage your PostgreSQL database visually. It consists of:

- A **Deployment** to run the `pgAdmin` container
- A **Service** to expose the pgAdmin interface inside the cluster
- A **PersistentVolumeClaim (PVC)** to persist pgAdmin settings and session data

```yaml
# SERVICE: Exposes pgAdmin on port 80 inside the cluster
apiVersion: v1
kind: Service
metadata:
  name: pgadmin                   # Service name
  namespace: voting-app
spec:
  selector:
    app: pgadmin                 # Targets Pods labeled with app=pgadmin
  ports:
    - port: 80                   # Exposes port 80 to other services
      targetPort: 80             # Forwards to container's port 80

# DEPLOYMENT: Runs a pgAdmin container with one replica
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pgadmin
  namespace: voting-app
spec:
  replicas: 1                    # One instance is enough for a UI tool
  selector:
    matchLabels:
      app: pgadmin               # Matches Pods with this label
  template:
    metadata:
      labels:
        app: pgadmin
    spec:
      containers:
        - name: pgadmin
          image: dpage/pgadmin4  # Official pgAdmin Docker image
          ports:
            - containerPort: 80  # pgAdmin listens on port 80
          env:                   # Required environment variables
            - name: PGADMIN_DEFAULT_EMAIL
              value: user-name@domain-name.com
            - name: PGADMIN_DEFAULT_PASSWORD
              value: strong-password
          volumeMounts:
            - name: pgadmin-data
              mountPath: /var/lib/pgadmin  # Persistent data directory
      volumes:
        - name: pgadmin-data
          persistentVolumeClaim:
            claimName: pgadmin-pvc         # Mount the PVC into the container

# PVC: Claims persistent storage for pgAdmin settings and session data
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pgadmin-pvc
  namespace: voting-app
spec:
  accessModes: ["ReadWriteOnce"]  # Single-node read-write access
  resources:
    requests:
      storage: 512Mi              # Requests 512Mi of persistent storage

```

# Ingress

**Ingress** is a Kubernetes API object that manages **external access to services** within a cluster, typically over HTTP/HTTPS. It acts as a **smart router** that forwards traffic from the outside world to the correct service inside your Kubernetes cluster.

### 💡 Why Use Ingress?

- **Centralized routing**: It lets you define traffic rules in a single place.
- **Path/Host-based routing**: You can route requests to different services based on the URL or hostname.
- **SSL termination**: Offload HTTPS from your app.
- **Authentication** and **rate limiting** via Ingress Controller support.

To make Ingress work, you need an **Ingress Controller** like **NGINX**, **Traefik**, or **HAProxy** that actually implements the routing logic.

```yaml
# Ingress object - defines HTTP routing to services inside the cluster
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: voting-app-voting-app-chart-ingress  # Unique name for the Ingress
  namespace: voting-app                      # Namespace in which it is deployed
  labels:
    helm.sh/chart: voting-app-chart-0.1.0    # Helm chart metadata
    app.kubernetes.io/name: voting-app-chart
    app.kubernetes.io/instance: voting-app
    app.kubernetes.io/version: "1.16.0"
    app.kubernetes.io/managed-by: Helm
  annotations:
    nginx.ingress.kubernetes.io/proxy-body-size: 8m         # Max body size allowed
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "500"  # Connection timeout
    nginx.ingress.kubernetes.io/proxy-read-timeout: "600"     # Read timeout
    nginx.ingress.kubernetes.io/proxy-send-timeout: "600"     # Send timeout
    nginx.ingress.kubernetes.io/rewrite-target: /             # Rewrite incoming URL to root (e.g., /foo -> /)

spec:
  ingressClassName: nginx  # Use the NGINX ingress controller
  rules:
    - host: "voting.local"   # Requests for voting.local
      http:
        paths:
          - path: /          # Any path under root
            pathType: Prefix
            backend:
              service:
                name: haproxy-service  # Forward to this internal service
                port:
                  number: 80
    - host: "pgadmin.local"  # Requests for pgadmin.local
      http:
        paths:
          - path: /          
            pathType: Prefix
            backend:
              service:
                name: pgadmin  # Forward to pgadmin service
                port:
                  number: 80

```

### Run Ingress:

1. **Ensure you have a Kubernetes cluster running**
2. **Install NGINX Ingress Controller**
    
    ```yaml
    helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
    helm install nginx-ingress ingress-nginx/ingress-nginx
    ```
    
3. Apply ingress resources Save your YAML as `ingress.yaml` and run
    
    ```yaml
    kubectl apply -f ingress.yaml
    ```
    
4. Update the Hosts file: 
    
    ```yaml
    127.0.0.1 voting.local
    127.0.0.1 pgadmin.local
    ```
    

### Validation

![image.png](image.png)

![image.png](image%201.png)

![image.png](image%202.png)

# HA Proxy

**HAProxy** (High Availability Proxy) is a fast and reliable open-source solution offering high availability, load balancing, and proxying for TCP and HTTP-based applications. In Kubernetes, HAProxy can be deployed as a pod (typically via a Deployment) to manage incoming traffic and route it to services or pods in the cluster.

![image.png](image%203.png)

### Why Is HAProxy Used in Kubernetes?

HAProxy is used in Kubernetes for:

1. **Load Balancing** – It can balance traffic across multiple pods or services to ensure even distribution and high availability.
2. **Traffic Routing** – It allows fine-grained control over routing logic, including sticky sessions, path-based routing, etc.
3. **Failover** – HAProxy can detect failed pods or endpoints and reroute traffic to healthy instances.
4. **Custom Proxying Needs** – When built-in Kubernetes Ingress controllers or services don't meet the application’s needs, HAProxy offers customizable behavior.

### Relationship Between HAProxy and Ingress

- **Ingress Controller**: An Ingress controller in Kubernetes typically uses a proxy/load balancer to handle inbound HTTP/HTTPS traffic. Popular ones include **NGINX**, **Traefik**, and **HAProxy** itself.
- HAProxy **can serve as** an Ingress Controller when configured with Ingress resources. It interprets Ingress rules and routes external traffic accordingly.
- In your case, **HAProxy is used as a standalone load balancer/proxy**, not necessarily an Ingress controller (since no `Ingress` resource is shown in the YAML). It may be managing traffic between internal services or acting as a gateway proxy.

```yaml
# Source: voting-app-chart/templates/haproxy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: haproxy                       # Deployment name
  namespace: voting-app              # Namespace where it's deployed
  labels:                            # Helm and app-related metadata
    helm.sh/chart: voting-app-chart-0.1.0
    app.kubernetes.io/name: voting-app-chart
    app.kubernetes.io/instance: voting-app
    app.kubernetes.io/version: "1.16.0"
    app.kubernetes.io/managed-by: Helm
    app.kubernetes.io/component: proxy
spec:
  replicas: 1                         # Only one HAProxy pod (no redundancy)
  selector:
    matchLabels:
      app: haproxy                   # Used to match pods controlled by this Deployment
  template:
    metadata:
      labels:
        app: haproxy
        helm.sh/chart: voting-app-chart-0.1.0
        app.kubernetes.io/name: voting-app-chart
        app.kubernetes.io/instance: voting-app
        app.kubernetes.io/version: "1.16.0"
        app.kubernetes.io/managed-by: Helm
        app.kubernetes.io/component: proxy
    spec:
      containers:
        - name: haproxy
          image: "haproxy:2.4"       # HAProxy container image
          imagePullPolicy: IfNotPresent
          volumeMounts:
            - name: haproxy-config-volume
              mountPath: /usr/local/etc/haproxy # Mounts config from a ConfigMap
          ports:
            - containerPort: 80      # Exposes port 80 (HTTP)
      volumes:
        - name: haproxy-config-volume
          configMap:
            name: haproxy-config     # References a ConfigMap (not shown) for HAProxy config

```

# Auto Scaling

**Autoscaling** in Kubernetes is the process of automatically adjusting the number of pod replicas in a deployment (or other controller) based on observed resource usage (e.g., CPU or memory). This helps ensure that applications remain responsive under load and cost-efficient under low demand.

### ⚙️ **What is HPA (Horizontal Pod Autoscaler)?**

**Horizontal Pod Autoscaler (HPA)** is a Kubernetes resource that automatically scales the number of pods in a deployment, replica set, or stateful set based on specified metrics.

HPA:

- **Monitors resource metrics** (e.g., CPU, memory).
- **Increases or decreases pods** depending on the defined thresholds.
- Works with the **Metrics Server**, which collects resource usage data.

```yaml
# Defines the API version of the HPA resource (v2 supports multiple metrics types)
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler

metadata:
  name: voting-app                 # Name of the HPA object
  namespace: voting-app           # Namespace where the target deployment lives
  labels:                         # Helm-related and Kubernetes app labels
    helm.sh/chart: voting-app-chart-0.1.0
    app.kubernetes.io/name: voting-app-chart
    app.kubernetes.io/instance: voting-app
    app.kubernetes.io/version: "1.16.0"
    app.kubernetes.io/managed-by: Helm

spec:
  scaleTargetRef:
    apiVersion: apps/v1           # The API version of the target resource
    kind: Deployment              # We're targeting a Deployment
    name: voting-app              # Name of the Deployment to be scaled

  minReplicas: 1                  # Minimum number of pods allowed
  maxReplicas: 3                  # Maximum number of pods allowed

  metrics:                        # Define the metrics for autoscaling
    - type: Resource
      resource:
        name: cpu                 # Monitor CPU usage
        target:
          type: Utilization       # Based on utilization percentage
          averageUtilization: 70 # Target average CPU utilization across pods is 70%
    - type: Resource
      resource:
        name: memory              # Monitor memory usage
        target:
          type: Utilization
          averageUtilization: 75 # Target average memory utilization is 75%

  behavior:
    scaleUp:
      stabilizationWindowSeconds: 0 # No delay before scaling up

```

- Kubernetes watches the **CPU and memory usage** of all pods in the `voting-app` deployment.
- If **average CPU exceeds 70%** or **memory exceeds 75%**, it triggers a **scale-up** (adds pods), up to `maxReplicas: 3`.
- If usage drops, it scales back down to `minReplicas: 1`.
- `behavior.scaleUp.stabilizationWindowSeconds: 0` means it **reacts instantly** to increased demand (no waiting window).

### Apply

1. Check if `metrics-server` is installed:
    
    ```yaml
    kubectl get deployment metrics-server -n kube-system
    ```
    
2. **If it's not installed**, you can **install it** easily:
    
    ```yaml
    kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
    ```
    
3. **after installing**, wait a minute, then **verify metrics**:

    
    ```yaml
    kubectl top pods -n voting-app
    ```
    

As you can se below the helm chart started with one pod for voting-app and since the load started increasing automatically a new pod was created then after while a third pod was created 

![Screenshot 2025-04-28 112504.png](Screenshot_2025-04-28_112504.png)

# Helm Chart

Converting Kubernetes manifests to a Helm chart is relatively easy because Helm is designed to **template and manage Kubernetes YAML files**, not replace them. It essentially adds a layer of reusability, parameterization, and packaging on top of your existing manifests.

1. **Same YAML Format**: Helm charts use the same YAML manifests you're already writing for Kubernetes.
2. **Templating Engine**: Helm adds variables (using Go templating syntax like `{{ .Values.image }}`) which makes your manifests dynamic.
3. **Directory Structure**: Helm uses a standard directory structure, which keeps your configuration organized.
4. **Packaging & Reuse**: You can package your app as a chart and reuse it across environments (dev, staging, prod) with different values.

## Create the Helm Chart

### Create a new Helm chart

```yaml
helm create voting-app-chart 
cd voting-app-chart 
```

this will generate 

```yaml
voting-app-chart /
  charts/
  templates/
  values.yaml
  Chart.yaml

```

⇒ Put your existing manifest YAMLs into the `templates/` folder and start replacing hardcoded values with templated variables.

### `Chart.yaml`

```yaml
apiVersion: v2
name: voting-app-chart
description: A Helm chart for deploying voting app, Postgres, PgAdmin, HAProxy, and Ingress.
type: application
version: 0.1.0
appVersion: "1.16.0"

```

### `Values.yaml`

```yaml
# Global settings
nameOverride: ""
fullnameOverride: ""

# Namespace configuration
namespace:
  create: true
  name: voting-app

# PostgreSQL configuration
postgres:
  enabled: true
  image:
    repository: postgres
    tag: latest
    pullPolicy: IfNotPresent
  service:
    name: db
    port: 5432
  credentials:
    username: "user-name"
    password: "strong-password"
  persistence:
    enabled: true
    storageClass: ""
    size: 1Gi

# pgAdmin configuration
pgadmin:
  enabled: true
  image:
    repository: dpage/pgadmin4
    tag: latest
    pullPolicy: IfNotPresent
  service:
    port: 80
  credentials:
    email: "user-name@domain-name.com"
    password: "strong-password"
  persistence:
    enabled: true
    storageClass: ""
    size: 512Mi

# Voting application configuration
votingApp:
  enabled: true
  image:
    repository: voting-app
    tag: latest
    pullPolicy: IfNotPresent
  replicas: 1
  service:
    httpPort: 8080
    metricsPort: 9321
  prometheus:
    scrape: true
    path: "/actuator/prometheus"
    port: 9321
  env:
    datasourceUrl: "jdbc:postgresql://db:5432/postgres"
    datasourceUsername: "user-name"
    datasourcePassword: "strong-password"
    hibernateDdlAuto: "update"
  hpa:
    enabled: true
  resources:
    requests:
      cpu: "800m"
      memory: "512Mi"
    limits:
      cpu: "1000m"
      memory: "800Mi"
# HAProxy configuration
haproxy:
  enabled: true
  image:
    repository: haproxy
    tag: "2.4"
    pullPolicy: IfNotPresent
  service:
    port: 80
  config:
    maxconn: 256
    timeoutConnect: "50000s"
    timeoutClient: "500000s"
    timeoutServer: "500000s"

# Ingress configuration
ingress:
  enabled: true
  className: "nginx"
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "500"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-body-size: "8m"
  hosts:
    - host: voting.local
      paths:
        - path: /
          pathType: Prefix
          serviceName: haproxy-service
          servicePort: 80
    - host: pgadmin.local
      paths:
        - path: /
          pathType: Prefix
          serviceName: pgadmin
          servicePort: 80
```

## Commands

### Lint Chart

```yaml
# Lint the chart 
helm lint .

# Expected Output
# ==> Linting .
# [INFO] Chart.yaml: icon is recommended

#1 chart(s) linted, 0 chart(s) failed
```

### Package chart

```yaml

# Build the chart 
helm package .

# Expected Output
# Successfully packaged chart and saved it to: C:\voting-app-chart\voting-app-chart-0.1.0.tgz
```

### Install Chart

```yaml
# Install Chart 
helm install voting-app .\voting-app-chart-0.1.0.tgz --namespace voting --create-namespace

# Expected output 
# NAME: voting-app
# LAST DEPLOYED: Tue Apr 29 13:15:26 2025
# NAMESPACE: voting
# STATUS: deployed
# REVISION: 1
# TEST SUITE: None
```

### Validate Installation

```yaml
# Check Installation 
helm list -a -A   

#Expected ouput 
# NAME            NAMESPACE       REVISION        UPDATED                                 STATUS          CHART                           APP VERSION
# my-grafana      default         1               2025-04-20 01:57:21.075202243 -0700 PDT deployed        grafana-8.12.1                  11.6.0
# vmagent         default         1               2025-04-20 01:56:04.873451036 -0700 PDT deployed        victoria-metrics-agent-0.18.2   v1.115.0
# vmcluster       default         1               2025-04-20 01:54:25.911401682 -0700 PDT deployed        victoria-metrics-cluster-0.20.1 v1.115.0
# voting-app      voting          1               2025-04-29 13:15:26.5867617 -0700 PDT   deployed        voting-app-chart-0.1.0          1.16.0
```

### Check pods

```yaml
# Check Pods 
kubectl get pods -n voting-app

# Expected output 
# NAME                          READY   STATUS    RESTARTS   AGE
# haproxy-57df55b488-dwm99      1/1     Running   0          32s
# pgadmin-657976d4b4-728lk      1/1     Running   0          32s
# postgres-0                    1/1     Running   0          32s
# voting-app-7dc9b5466f-9zqfl   1/1     Running   0          32s

```

### Uninstall

```yaml
# Uninstall 
helm uninstall voting-app -n voting
```

# Pods and Voting App Metrics

## Voting App Metrics

Spring Boot application exposes a range of **custom and built-in metrics** primarily using **Micrometer** with Prometheus integration enabled. Here's a detailed breakdown of what metrics are exposed and what each one signifies:

 **Built-In Micrometer + Spring Boot Metrics** With this configuration:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    prometheus:
      enabled: true

```

**Custom Application Metrics from `MetricsService`**

Your `MetricsService` class defines and exposes **custom domain-specific metrics** using `registry.gauge()` and `registry.counter()`. Here's a breakdown:

### Gauges (real-time snapshot values):

| Metric Name | Description |
| --- | --- |
| `voters` | Total number of voters (`voterRepository.count()`). |
| `voters_voted` | Number of voters who have voted (`countByHasVotedTrue()`). |
| `voters_not_voted` | Number of voters who have *not* voted (`countByHasVotedFalse()`). |
| `voting_options` | Total number of voting options (`votingOptionRepository.count()`). |
| `votes_per_option` | Number of votes per voting option (`countByVotingOption()`). Each option has its own tag value. |

## Kubernetes Pods Metrics

 **Kubernetes monitoring with `VictoriaMetrics` Cluster use the below resource** 
[https://docs.victoriametrics.com/guides/k8s-monitoring-via-vm-cluster/](https://docs.victoriametrics.com/guides/k8s-monitoring-via-vm-cluster/)

![image.png](image%204.png)

# Load Test

## Import Voting options

![image.png](image%205.png)

## Generate Voters

using the below scripts we generate 30000 voters and they are imported using the `/import`   rest API using postman 

```python
import json
import random

# Settings
num_files = 6
records_per_file = 5000

# Create 6 JSON files in the same folder as the script
for file_num in range(1, num_files + 1):
    records = []
    
    # Add 30,000 generated records for each file
    for i in range(records_per_file):
        generated_name = f"GeneratedName{file_num}_{i+1}"
        generated_nationalId = f"B{file_num}{str(i+1).zfill(8)}"  # Like B100000001
        records.append({
            "name": generated_name,
            "nationalId": generated_nationalId
        })
    
    # Shuffle the records (optional)
    random.shuffle(records)

    # Save to JSON file in the same folder as the script
    file_path = f"voters_{file_num}.json"
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(records, f, ensure_ascii=False, indent=4)

print("✅ JSON files created successfully in the current directory!")

```

![image.png](image%206.png)

![image.png](image%207.png)

### Run Load Test

**Locust** is an open-source load testing tool used to assess the performance and scalability of web applications and APIs. It allows you to simulate concurrent users interacting with your system to see how it behaves under load.

This script defines a **Locust load test** for a voting API. It simulates multiple users casting votes with different voter identities and randomly selected voting options. 

This test script:

- Loads voters from multiple files.
- Fetches vote options once before testing begins.
- Simulates users casting votes using different identities and random options.
- Avoids double-voting and handles failures gracefully.

```python
from locust import HttpUser, TaskSet, task, between, events
import random
import json
import requests

# Global variables
voters = []
option_ids = []

voter_index = 0

# Load voters once before starting
for i in range(1, 7):  # you have voters_1.json to voters_6.json
    with open(f"voters_{i}.json", "r", encoding="utf-8") as f:
        voters.extend(json.load(f))

@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """
    Fetch option IDs from the /options endpoint when the test starts.
    """
    global option_ids
    options_url = "http://voting.local/options"
    try:
        response = requests.get(options_url)
        response.raise_for_status()
        options = response.json()
        option_ids = [option['id'] for option in options]
        print(f"✅ Retrieved {len(option_ids)} options.")
    except Exception as e:
        print(f"❌ Failed to fetch options from {options_url}: {e}")
        option_ids = []  # fallback to empty, so that test fails clearly if necessary

class VotingTasks(TaskSet):
    @task
    def vote(self):
        global voter_index

        if not option_ids:
            print("❗ No options available, skipping vote.")
            return
        
        if voter_index >= len(voters):
            print("✅ All voters have voted. Skipping further votes.")
            return

        voter = voters[voter_index]
        voter_index += 1

        option_id = random.choice(option_ids)  # Still picking a random option
        national_id = voter["nationalId"]

        self.client.post(
            f"/vote?optionId={option_id}&nationalId={national_id}"
        )

class VotingUser(HttpUser):
    tasks = [VotingTasks]
    wait_time = between(0.1, 1)  # Random wait between tasks
    host = "http://voting.local"
    
```

→ run the script 

```python
locust -f locustfile.py
```

![image.png](image%208.png)

![response_times_(ms)_1745962733.096.png](response_times_(ms)_1745962733.096.png)