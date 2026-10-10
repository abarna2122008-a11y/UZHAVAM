# உழவம் (Uzhavam)

Uzhavam is a small farm-management project I built to bring a few everyday tools into one place. Farmers can save farm details, keep crop plans and reminders, record income and expenses, and ask agriculture questions. The site supports English, Tamil, Hindi, Malayalam, Kannada, Telugu, and Marathi.

This is a hackathon prototype, not a finished farming service. The weather shown is sample data, and the advice depends on having a Gemini API key configured.

## Run it with Docker

You will need Java 21, Maven, and Docker Desktop.

1. Start Docker Desktop.
2. Copy `.env.example` to `.env`.
3. Add your Google AI Studio key as `GOOGLE_API_KEY` in `.env`. Set the gateway username and password there too.
4. Open a terminal in this folder and run:

   ```powershell
   docker compose up --build
   ```

5. Go to [http://localhost:8080](http://localhost:8080) and sign in using the gateway credentials from `.env`.

To stop the services, press `Ctrl+C` in the terminal. You can also run `docker compose down`.

## What is in the project?

There are three Spring Boot services:

| Service | Port | What it does |
| --- | ---: | --- |
| API Gateway | 8080 | Serves the web page, handles sign-in, and forwards API requests |
| Portal | 8081 | Saves farm details, crop plans, reminders, transactions, and chat history |
| Advisory | 8082 | Prepares agriculture advice and calls Gemini |
| PostgreSQL | 5432 (internal) | Stores application data when running with Docker Compose |

The browser talks to the Gateway. The Gateway routes portal requests to the Portal service and advice requests to the Advisory service. The Portal uses Spring Data JPA to work with the database. In the Docker setup, the services and PostgreSQL run as separate containers on the same Compose network.

## Voice assistant

The microphone button in the lower-right corner opens the voice assistant. Pick farm details, a crop plan, a reminder, an income/expense entry, or a question. It asks for the information one field at a time, reads back what it heard, and gives you a chance to review before saving. You can skip optional fields.

Voice input needs browser speech-recognition support and microphone permission. Chrome or Edge should work best. If speech recognition is unavailable, use the regular forms. On the chat page, the microphone can also fill in a question; check the transcription before sending it.

## Market and scheme links

The market and scheme pages link to outside information, including e-NAM, Agmarknet, PM-KISAN, PMFBY, and Kisan Credit Card resources. They are there to help farmers find official or public information. Uzhavam does not provide live market prices or decide whether someone qualifies for a scheme, insurance, or loan.

## Advice and sample data

The Advisory service sends the question and available farm context to Gemini. It also uses crop and soil notes and clearly labels its district weather as a demo estimate. The weather is not live, and the prototype cannot verify local prices, diagnoses, product doses, or scheme eligibility. Check important decisions with a local agricultural officer or another reliable source.

Set `GOOGLE_API_KEY` before trying Gemini-backed responses. Do not put a real key in the source code or commit your `.env` file.

## Run locally without Docker

These commands use an in-memory H2 database for the Portal. Start each service in its own PowerShell terminal, from the project folder. H2 data is temporary and is lost when the Portal stops.

Portal:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:h2:mem:agridb;DB_CLOSE_DELAY=-1"
$env:SPRING_DATASOURCE_USERNAME = "sa"
$env:SPRING_DATASOURCE_PASSWORD = ""
mvn -pl agri-portal-service spring-boot:run
```

Advisory:

```powershell
$env:PORTAL_SERVICE_URL = "http://localhost:8081"
$env:GOOGLE_API_KEY = "your-google-ai-studio-key"
mvn -pl agri-advisory-service spring-boot:run
```

Gateway:

```powershell
$env:GATEWAY_USER = "farmer"
$env:GATEWAY_PASSWORD = "choose-a-local-demo-password"
$env:PORTAL_SERVICE_URL = "http://localhost:8081"
$env:ADVISORY_SERVICE_URL = "http://localhost:8082"
mvn -pl api-gateway-service spring-boot:run
```

For persistent data, use the PostgreSQL setup in Docker Compose instead of H2.

## Build checks

From the project folder:

```powershell
mvn clean package
docker compose config --quiet
```

Before sharing a deployment, replace the example gateway credentials and keep `.env` private.
