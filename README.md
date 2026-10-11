# உழவம் (Uzhavam)

Uzhavam is a small farm-management project I built to bring a few everyday tools into one place. Farmers can save farm details, keep crop plans and reminders, record income and expenses, and ask agriculture questions. The site supports English, Tamil, Hindi, Malayalam, Kannada, Telugu, and Marathi.

This is a hackathon prototype, not a finished farming service. The dashboard weather comes from Open-Meteo; the separate weather context used by crop advice is still sample data. Gemini-backed advice and photo checks need an API key.

## Run it with Docker

You will need Java 21, Maven, and Docker Desktop.

1. Start Docker Desktop.
2. Copy `.env.example` to `.env`.
3. Add your Google AI Studio key as `GOOGLE_API_KEY` in the root `.env` file (not `.env.example`). Set the gateway username and password there too. Keep `.env` private; it is ignored by Git.
4. Open a terminal in this folder and run:

   ```powershell
   docker compose up --build
   ```

5. Go to [http://localhost:8080](http://localhost:8080) and sign in using the gateway credentials from `.env`.

To stop the services, press `Ctrl+C` in the terminal. You can also run `docker compose down`.

If chat shows **“Local guide · Gemini not set”**, check that the key is in the root `.env` and restart the Advisory service. Docker Compose reads this file automatically; local Spring Boot runs also import the root `.env` when launched from the project root or service folder. The default Gemini model is `gemini-3.8-flash`; override `GEMINI_MODEL` only if your Google AI Studio account supports another model. Never place a real key in `.env.example`, commit it, or paste it into a chat or screenshot.

## What is in the project?

There are three Spring Boot services:

| Service | Port | What it does |
| --- | ---: | --- |
| API Gateway | 8080 | Serves the web page, handles sign-in, and forwards API requests |
| Portal | 8081 | Saves farm details, crop plans, reminders, transactions, and chat history |
| Advisory | 8082 | Prepares agriculture advice and calls Gemini |
| PostgreSQL | 5432 (internal) | Stores application data when running with Docker Compose |

The browser talks to the Gateway. The Gateway routes portal requests to the Portal service and advice requests to the Advisory service. The Portal uses Spring Data JPA to work with the database. In the Docker setup, the services and PostgreSQL run as separate containers on the same Compose network.

The Compose setup also runs a single-node Kafka broker for weather forecast-cue events. It is a demo broker, not a production cluster. The Gateway user can queue threshold-based forecast events; the Portal consumes them asynchronously and stores the received events. This is not an IMD/NDMA official-warning feed.

## Voice assistant

The microphone button in the lower-right corner opens the voice assistant. Pick farm details, a crop plan, a reminder, an income/expense entry, or a question. It asks for the information one field at a time, reads back what it heard, and gives you a chance to review before saving. You can skip optional fields.

Voice input needs browser speech-recognition support and microphone permission. Chrome or Edge should work best. If speech recognition is unavailable, use the regular forms. On the chat page, the microphone can also fill in a question; check the transcription before sending it.

## Camera plant check

Open **Camera disease check** from the sidebar or dashboard shortcut. On `localhost` or HTTPS, allow camera access to capture a leaf/stem image; choosing a JPEG/PNG photo is also supported. The Advisory service sends the image to Gemini and saves the assessment, not the original photo. A valid `GOOGLE_API_KEY` must be configured for that service before AI analysis can run; `/api/advisory/vision/status` reports whether it is configured without exposing the key. The result is an estimate, not a confirmed diagnosis; confirm uncertain symptoms locally before treatment.

## Market and scheme links

The market and scheme pages link to outside information, including e-NAM, Agmarknet, PM-KISAN, PMFBY, and Kisan Credit Card resources. They are there to help farmers find official or public information. Uzhavam does not provide live market prices or decide whether someone qualifies for a scheme, insurance, or loan.

## Advice and sample data

When `GOOGLE_API_KEY` is configured, the Advisory service sends the question and available farm context to Gemini. Without a key, chat still returns a clearly labelled, limited local checklist in the selected language; coconut questions get a basic care checklist. This fallback is not generative AI and is not a substitute for locally verified crop advice. If Portal history storage is unavailable, the answer is returned with a source note and a server log entry rather than discarded. The service also uses crop and soil notes; the weather context included with crop advice is still sample data. The dashboard fetches current conditions and a short forecast from Open-Meteo using the saved district. This is a forecast, not an official severe-weather warning. Check local alerts and confirm important crop decisions with an agricultural officer or another reliable source.

The live weather lookup geocodes the saved district within India and requests current conditions plus today's high, low, and rain chance. The dashboard refreshes it when opened, on request, and every 15 minutes while the page is active. An internet connection is required; if the provider is unavailable or the district cannot be matched, the dashboard reports that instead of displaying a made-up forecast.

The safety panel uses a few transparent forecast thresholds to show a heads-up for thunderstorms, high rain probability, strong wind, or high temperature. Those cues are not official warnings and cannot confirm that an area is safe. The page links to IMD and NDMA Sachet information and India emergency number 112. This prototype does not identify environmental hazards from images, calculate shelter or evacuation routes, or send SMS/email/Kafka alerts.

## Harvest sales and settlement

The Income & expenses page includes a Harvest sales & settlement section for recording buyer agreements, agreed quantity and price, transport, deductions, and receipt notes. When the produce is weighed and payment is received, save the actual weighed quantity, final deductions, and amount paid. Uzhavam calculates expected and actual net proceeds and any unpaid balance from those entered values; it does not fetch or verify market prices, buyer identity, contracts, weighment, or payments.

The Portal persists these records in PostgreSQL under Docker Compose (or the local H2 database) and exposes them through `GET/POST /api/portal/harvest-sales` and `PATCH /api/portal/harvest-sales/{id}/settlement`. The Gateway serves the form on the existing workspace page and forwards these requests to the Portal.

The same page has a HarvestShield calculator for comparing at least two user-entered offers after transport and deductions, a Spoilage Clock that compares a farmer-entered quality window with travel and handling estimates, and a simple estimated-versus-actual outcome delta after settlement. These are planning calculations, not live market, route, or shelf-life data. The separate Voice assistant already supports income/expense entries with speech capture and a review-before-save step. Market comparison values and spoken drafts are not independently verified.

## ReLoop, field evidence, and treatment tracking

Uzhavam ReLoop provides a district/search-filtered agricultural-material board, listing creation (including future availability for planned surplus), reservation, pickup recording with collected quantity and seller/buyer confirmation names, a reported collected-quantity summary, and a small reviewed catalogue of possible second-life uses. Confirmation names and quantities are self-reported in the shared demo workspace, not independently authenticated or verified. Listings are user-entered and not verified; feed, soil, food-contact, and contamination safety must be confirmed locally. Listing contact information is shown to signed-in workspace users if the seller provides it.

FieldProof saves a crop-loss incident date, location, description, and a JPEG/PNG evidence photo (up to 5 MB) in the Portal database. A photo can be retrieved or deleted with its record. The saved media is not independently time- or location-verified and does not establish an insurance claim. SafeSpray Clock stores the product and farmer-entered label pre-harvest interval (PHI), calculates the earliest date from the application date, and flags a planned harvest that is earlier. It does not recommend a product or replace the label, re-entry interval, local rules, or expert advice.

## FarmShield map and notifications

FarmShield now also displays a map marker for the district used by the Open-Meteo forecast, a seven-day rainfall-probability/temperature chart, and crop-plan-aware preparation reminders. The map is a location reference, not a hazard, shelter, or evacuation map. Optional notifications use browser permission and local settings for rain-probability and temperature thresholds; they only run while the site is open and are not SMS, email, or official warnings. Forecast cues can be queued to Kafka and viewed after the Portal consumer stores them.

## MannArai soil assistant and SoilChange

MannArai provides a safe home-screening catalogue, an indicative shopping list, official programme/directory links, manual entry of soil lab/home-screening values, deterministic pH/EC range flags, and saved history by test type. N, P, and K are accepted only as laboratory-report entries; supported report units are retained with each value. The app deliberately does not generate fertilizer rates. Soil Health Card/lab data should be entered with its reported units; home methods are screening only and should not drive major input decisions. Shop links are not a live geolocated list.

## Farm memory, local knowledge, and coordination

MyField Memory and Season Rewind save actual user-entered cost, yield, and sale results and compare those outcomes with user-entered alternative prices. Profit Before Planting calculates break-even price and a net scenario from entered costs, selling expenses, yield, and price. CropCrowd counts crop plans in the shared demo workspace and flags repeated crop names for review; it is not an acreage-weighted local supply forecast or a glut prediction. The Farm Credit Passport downloads a JSON bundle of the entered farm profile, outcomes, transactions, and harvest deals; it is not a credit score, verified identity, or lender approval, so review private details before sharing. BeforeYouBorrow performs transparent arithmetic on opening cash, expected inflows, costs, and a chosen time window; it is not a lending recommendation. Elder Knowledge saves tagged text or reviewed speech transcription as explicitly unverified notes, not audio recordings. PestWatch records symptoms and actions; PestSignal only counts recent reports in this shared workspace and does not infer a regional outbreak. Community offers coordinate user-entered transport, water turns, labour exchanges, and buyer/supplier interest. No offer or participant is independently verified.

## Offline transaction drafts and speech parsing

The records page can keep transaction entries in browser local storage while offline and submits them to the Portal only when the user syncs or returns online. Confirm that each draft is correct; clearing browser storage removes unsynced entries. The transaction text helper uses transparent local keyword/amount rules to prefill a draft, not an AI diagnosis or financial calculation. Voice capture uses browser speech recognition; review the transcription before saving.

These features are implemented for the prototype's shared demo workspace. The configured Gateway has a single basic-auth identity, not real multi-farmer accounts or per-farmer marketplace access control. Do not use it to publish personal contact details or real evidence until proper user identity, authorization, retention, and deployment security are configured.

Set `GOOGLE_API_KEY` before trying Gemini-backed responses. Do not put a real key in the source code or commit your `.env` file.

## Plant photo check

The Plant Health page lets a farmer take or choose a JPEG or PNG photo of an affected plant and request a Gemini visual assessment. The service checks the uploaded file and image dimensions before sending it for analysis. It saves the assessment and its source notes in the Portal database, but does not save the original photo. The photo is sent to Google Gemini, so this feature needs a working API key and internet access. Results are estimates, not a confirmed diagnosis; ask a local agricultural officer before treating a crop.

The photo-check interface is a small React component built into the Gateway's static files. The rest of the existing website remains regular HTML, CSS, and JavaScript.

## Run locally without Docker

These commands use an in-memory H2 database for the Portal. Build the React photo-check screen once before starting the Gateway; this needs Node.js and npm. Then start each service in its own PowerShell terminal, from the project folder. H2 data is temporary and is lost when the Portal stops.

```powershell
Push-Location .\api-gateway-service\frontend
npm ci
npm run build
Pop-Location
mvn -pl api-gateway-service -am package
```

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
