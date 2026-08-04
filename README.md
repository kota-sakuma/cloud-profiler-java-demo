# cloud-profiler-java-demo

A small [Spring Boot](https://spring.io/projects/spring-boot) application that generates predictable CPU, heap, wall-time, and mixed workloads. Use it to exercise [Google Cloud Profiler](https://cloud.google.com/profiler/docs) for Java and verify that profiles appear in the Google Cloud console.

## Requirements

- **Java 25** (see [`.tool-versions`](.tool-versions) if you use [asdf](https://asdf-vm.com/))
- **Gradle** (wrapper included: `./gradlew`)
- **Docker** (optional, for container images with the Cloud Profiler agent preinstalled)

## Quick start (local)

```bash
./gradlew bootRun
```

The app listens on **8080**.

| URL | Description |
|-----|-------------|
| http://localhost:8080/ | Service info and endpoint list |
| http://localhost:8080/swagger-ui.html | OpenAPI UI |
| http://localhost:8080/actuator/health | Health check |

Background load runs on a schedule (CPU every 5s, heap every 10s) so profiles have steady signal even without calling the APIs.

## HTTP API

All load endpoints are `GET` under `/api/*`. Query parameters are clamped to safe ranges server-side.

| Endpoint | Purpose | Main parameters (defaults) |
|----------|---------|----------------------------|
| `/api/cpu` | CPU-bound work (primes, hashing, sorting) | `iterations` (1–20, default `5`) |
| `/api/heap` | Heap allocation and caching | `entries` (1_000–100_000, default `50000`) |
| `/api/wall-time` | Sleep + lock contention | `delayMillis` (100–5000, default `500`), `threads` (1–16, default `4`) |
| `/api/mixed` | Combined CPU, heap, and wall-time | `cpuIterations`, `heapEntries`, `delayMillis` |

Example:

```bash
curl "http://localhost:8080/api/cpu?iterations=10"
curl "http://localhost:8080/api/mixed?cpuIterations=5&heapEntries=20000&delayMillis=500"
```

## Docker

The [`Dockerfile`](Dockerfile) builds a fat JAR in a JDK stage and runs it on **Eclipse Temurin 25 JRE** with the official Cloud Profiler Java agent downloaded at build time.

### Build

```bash
docker build -t cloud-profiler-java-demo .
```

Optional build arguments (also exposed as environment variables in the runtime image):

| Build arg | Default | Used for |
|-----------|---------|----------|
| `CPROF_SERVICE` | `cloud-profiler-java-demo` | Profiler service name |
| `CPROF_SERVICE_VERSION` | `1.0.0` | Profiler service version |

```bash
docker build \
  --build-arg CPROF_SERVICE=my-service \
  --build-arg CPROF_SERVICE_VERSION=1.2.3 \
  -t cloud-profiler-java-demo .
```

### Run

```bash
docker run --rm -p 8080:8080 cloud-profiler-java-demo
```

Port **8080** is exposed. Override profiler labels at runtime if needed:

```bash
docker run --rm -p 8080:8080 \
  -e CPROF_SERVICE=my-service \
  -e CPROF_SERVICE_VERSION=1.2.3 \
  cloud-profiler-java-demo
```

Note: `JAVA_TOOL_OPTIONS` is set in the image to load `profiler_java_agent.so` with heap sampling enabled and logs sent to stderr. Changing `CPROF_*` env vars after the image is built does not rewrite `JAVA_TOOL_OPTIONS`; rebuild with `--build-arg` or set `JAVA_TOOL_OPTIONS` explicitly when you need different agent flags.

### `.dockerignore`

Build context excludes `.git`, `.gradle`, `build`, and `tmp-init` to keep images smaller and builds faster.

## Google Cloud Profiler

### What the container does

On startup, the JVM loads the agent from `/opt/cprof/profiler_java_agent.so` via `JAVA_TOOL_OPTIONS`, with:

- `-cprof_service` / `-cprof_service_version` from `CPROF_SERVICE` and `CPROF_SERVICE_VERSION`
- `-cprof_enable_heap_sampling=true`
- `-logtostderr`

Agent binaries are fetched from `https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz` during the Docker build.

### Running on Google Cloud

For profiles to show up in Cloud Profiler:

1. Run the container on a GCP environment where the process can authenticate (for example **Cloud Run**, **GKE**, **Compute Engine**, or **App Engine** with an appropriate service account).
2. Enable the [Cloud Profiler API](https://cloud.google.com/profiler/docs/setting-up) for your project.
3. Grant the runtime service account permission to write profiles (for example the **Cloud Profiler Agent** role, or a custom role with `cloudprofiler.profiles.create`).
4. Deploy the image, generate load (API calls and/or background scheduler), then open **Profiler** in the Google Cloud console and select the service name matching `CPROF_SERVICE`.

Local `./gradlew bootRun` does **not** attach the profiler agent unless you configure `JAVA_TOOL_OPTIONS` yourself and provide Application Default Credentials.

## Development

```bash
./gradlew test
./gradlew bootJar
```

Stack: Spring Boot 4.x, Spring Web MVC, Actuator (health), springdoc OpenAPI 3.
