# Fieldwise Enterprise Backend

Java 21, Spring Boot 3.3.13, Spring Cloud Gateway, Spring Security, PostgreSQL, and Spring AI in a four-service REST architecture:

| Service | Container port | Responsibility |
| --- | ---: | --- |
| API Gateway | 8080 | HTTP Basic authentication and routing |
| Agri Portal | 8081 | Persist interaction logs in PostgreSQL |
| Agri Advisory | 8082 | Language-directed grounded response generation and portal logging |
| Hardware & Vision | 8083 | ESP32 telemetry and YOLO/OpenCV detection metadata |
| PostgreSQL | 5432 | Persistent interaction history |

Only the gateway is published to the host. Services and PostgreSQL are isolated on the Compose bridge network.

## Start with Docker Compose

1. Copy `.env.example` to `.env` and set strong passwords and a Google AI Studio API key.
2. Run `docker compose up --build` from this directory.
3. Wait for the health checks, then open the API demo console at `http://localhost:8080` or call the APIs directly through the same gateway.

The demo console lets you send ESP32 telemetry, read the current hardware context, request grounded advice, and inspect saved interaction history. Enter the configured gateway username/password in the console; these credentials are sent using HTTP Basic to the gateway and are not saved by the page.

For example, with PowerShell:

```powershell
$pair = "farmer:your-gateway-password"
$basic = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($pair))
$headers = @{ Authorization = "Basic $basic" }

Invoke-RestMethod -Uri http://localhost:8080/api/hardware/telemetry `
  -Method Post -Headers $headers -ContentType application/json `
  -Body '{"sensorId":"ESP32-Node1","soilMoisture":42,"nitrogen":"low","location":"Thanjavur"}'

Invoke-RestMethod -Uri http://localhost:8080/api/advisory/process `
  -Method Post -Headers $headers -ContentType application/json `
  -Body '{"query":"How should I manage rice pests?","language":"Tamil","location":"Thanjavur"}'
```

The advisory service retrieves a small, clearly labeled crop-note dataset, district forecast fixtures, and latest in-memory hardware/vision context, then passes that evidence to Spring AI `ChatClient`. Gemini is accessed through its OpenAI-compatible API using Spring AI's OpenAI starter, which keeps this project on the requested Spring Boot 3.3.x line. Set `GOOGLE_API_KEY` for generated responses. Compose starts the API even without a key, but Gemini-backed requests require a valid key.

`POST /api/portal/log` persists `farmerQuery`, `detectedLanguage`, `aiResponse`, UTC `timestamp`, and `sourceReferences`. `GET /api/portal/history` returns saved interactions. The advisory service logs successful generated answers before returning them.

Hardware endpoints:

- `POST /api/hardware/telemetry` accepts sensor ID, soil moisture percent, nitrogen level, and location.
- `POST /api/hardware/vision` accepts image ID, crop, location, and detection bounding boxes.
- `GET /api/hardware/context?location=Thanjavur` returns the latest matching metadata for advisory grounding.

Hardware readings and vision metadata are in-memory for this PoC and reset when that container is recreated. Replace them with a durable telemetry store/message broker for production. Forecast and crop notes are demo fixtures, not live official data; the generated response is instructed to cite evidence IDs and disclose missing evidence.

## Local build

From this directory, run `mvn test` or `mvn package`. Open the root `pom.xml` in IntelliJ IDEA to import the multi-module project.

To preview the console without Docker, start `HardwareVisionApplication` on port 8083 and `ApiGatewayApplication` on port 8080. For local overrides, set the gateway environment variables `GATEWAY_USER`, `GATEWAY_PASSWORD`, and `HARDWARE_SERVICE_URL=http://localhost:8083`. The console and hardware demo can then be opened at `http://localhost:8080`; portal history and AI generation additionally need PostgreSQL and the advisory service running.

## Security and deployment notes

- Set unique strong credentials in `.env`; never commit `.env`.
- The default Compose credentials are for local development only.
- The gateway protects all proxied routes with HTTP Basic. Use TLS at the ingress/reverse proxy before exposing it beyond a trusted development network.
- Backend containers have no published host ports. Keep them on a private network; add service-to-service identity and authorization before production.
