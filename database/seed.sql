-- =============================================================================
-- AI Trip Planner — Sample Seed Data
-- Run AFTER schema.sql
-- Note: Passwords are BCrypt hash of "Password123!" (for demo only)
-- =============================================================================

-- Demo password hash (BCrypt, strength 12): Password123!
-- Generated for seed/demo purposes — change in production

INSERT INTO users (id, name, email, password, role, created_at)
VALUES
    (
        'a1000000-0000-4000-8000-000000000001',
        'Priya Sharma',
        'priya@example.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.G2oQ.LxKqZ5KHy',
        'USER',
        NOW() - INTERVAL '30 days'
    ),
    (
        'a1000000-0000-4000-8000-000000000002',
        'Rahul Verma',
        'rahul@example.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/X4.G2oQ.LxKqZ5KHy',
        'USER',
        NOW() - INTERVAL '15 days'
    );

INSERT INTO profiles (id, user_id, occupation, home_city, travel_style, budget_preference, preferred_transport)
VALUES
    (
        'b2000000-0000-4000-8000-000000000001',
        'a1000000-0000-4000-8000-000000000001',
        'Software Engineer',
        'Bangalore',
        'NATURE',
        'MODERATE',
        'CAR'
    ),
    (
        'b2000000-0000-4000-8000-000000000002',
        'a1000000-0000-4000-8000-000000000002',
        'Student',
        'Mumbai',
        'BEACH',
        'BUDGET',
        'TRAIN'
    );

INSERT INTO trips (id, user_id, source_city, start_date, end_date, budget, travellers, travel_style, additional_preferences, status, selected_destination, created_at)
VALUES
    (
        'c3000000-0000-4000-8000-000000000001',
        'a1000000-0000-4000-8000-000000000001',
        'Bangalore',
        '2026-07-10',
        '2026-07-12',
        15000.00,
        2,
        'NATURE',
        'Prefer less crowded places, vegetarian food',
        'GENERATED',
        'Coorg',
        NOW() - INTERVAL '5 days'
    ),
    (
        'c3000000-0000-4000-8000-000000000002',
        'a1000000-0000-4000-8000-000000000001',
        'Bangalore',
        '2026-08-14',
        '2026-08-17',
        25000.00,
        4,
        'BEACH',
        'Family trip with kids',
        'ACCEPTED',
        'Gokarna',
        NOW() - INTERVAL '2 days'
    );

INSERT INTO itineraries (id, trip_id, destination, itinerary_json)
VALUES
    (
        'd4000000-0000-4000-8000-000000000001',
        'c3000000-0000-4000-8000-000000000001',
        'Coorg',
        '{
            "destination": "Coorg",
            "totalDays": 3,
            "days": [
                {
                    "day": 1,
                    "title": "Arrival & Abbey Falls",
                    "activities": [
                        "Early morning drive from Bangalore (5-6 hours)",
                        "Hotel check-in at Madikeri",
                        "Visit Abbey Falls",
                        "Sunset at Raja Seat viewpoint"
                    ]
                },
                {
                    "day": 2,
                    "title": "Nature & Wildlife",
                    "activities": [
                        "Dubare Elephant Camp visit",
                        "Coffee plantation tour",
                        "Optional river rafting at Barapole"
                    ]
                },
                {
                    "day": 3,
                    "title": "Departure",
                    "activities": [
                        "Local spice market shopping",
                        "Breakfast at local cafe",
                        "Return drive to Bangalore"
                    ]
                }
            ]
        }'::jsonb
    ),
    (
        'd4000000-0000-4000-8000-000000000002',
        'c3000000-0000-4000-8000-000000000002',
        'Gokarna',
        '{
            "destination": "Gokarna",
            "totalDays": 4,
            "days": [
                {
                    "day": 1,
                    "title": "Travel & Beach Sunset",
                    "activities": [
                        "Train/flight to Hubli, cab to Gokarna",
                        "Check-in near Kudle Beach",
                        "Evening at Kudle Beach"
                    ]
                },
                {
                    "day": 2,
                    "title": "Beach Hopping",
                    "activities": [
                        "Om Beach water activities",
                        "Half Moon Beach trek",
                        "Seafood dinner (optional)"
                    ]
                },
                {
                    "day": 3,
                    "title": "Temple & Relaxation",
                    "activities": [
                        "Mahabaleshwar Temple visit",
                        "Paradise Beach",
                        "Beach bonfire (if permitted)"
                    ]
                },
                {
                    "day": 4,
                    "title": "Return",
                    "activities": [
                        "Morning beach walk",
                        "Checkout and return journey"
                    ]
                }
            ]
        }'::jsonb
    );

INSERT INTO budget_breakdowns (id, trip_id, transport_cost, hotel_cost, food_cost, activity_cost, buffer_cost)
VALUES
    (
        'e5000000-0000-4000-8000-000000000001',
        'c3000000-0000-4000-8000-000000000001',
        4000.00,
        5000.00,
        3000.00,
        2000.00,
        1000.00
    ),
    (
        'e5000000-0000-4000-8000-000000000002',
        'c3000000-0000-4000-8000-000000000002',
        8000.00,
        9000.00,
        4000.00,
        3000.00,
        1000.00
    );
