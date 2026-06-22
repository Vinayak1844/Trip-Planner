# AI Service — FastAPI Microservice

Python 3.11+ · FastAPI · OpenAI API · Pydantic

## Folder Structure

```
app/
├── main.py                  # FastAPI app entry, CORS, router mounting
├── config.py                # Settings via pydantic-settings
├── models/
│   ├── destination.py       # RecommendDestinationRequest/Response
│   ├── itinerary.py         # GenerateItineraryRequest/Response
│   ├── budget.py            # EstimateBudgetRequest/Response
│   └── regenerate.py        # RegenerateItineraryRequest/Response
├── routers/
│   ├── destination.py       # POST /recommend-destination
│   ├── itinerary.py         # POST /generate-itinerary
│   ├── budget.py            # POST /estimate-budget
│   └── regenerate.py        # POST /regenerate-itinerary
├── services/
│   ├── openai_client.py     # OpenAI SDK wrapper with retry
│   ├── destination_service.py
│   ├── itinerary_service.py
│   ├── budget_service.py
│   └── regenerate_service.py
└── prompts/
    ├── destination.txt
    ├── itinerary.txt
    ├── budget.txt
    └── regenerate.txt

tests/
├── test_destination.py
├── test_itinerary.py
└── conftest.py
```

## Endpoints

| Method | Path                     | Description              |
|--------|--------------------------|--------------------------|
| POST   | `/recommend-destination` | Suggest destinations     |
| POST   | `/generate-itinerary`    | Day-wise itinerary       |
| POST   | `/estimate-budget`       | Budget allocation        |
| POST   | `/regenerate-itinerary`  | Modified plan            |
| GET    | `/health`                | Health check             |

## Run Locally

```bash
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

## API Docs

Swagger UI: `http://localhost:8000/docs`

## Environment

```env
OPENAI_API_KEY=sk-...
OPENAI_MODEL=gpt-4o
LOG_LEVEL=INFO
```
