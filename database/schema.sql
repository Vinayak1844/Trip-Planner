-- =============================================================================
-- AI Trip Planner — PostgreSQL Schema
-- Version: 1.0
-- =============================================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================================
-- ENUM Types
-- =============================================================================

CREATE TYPE user_role AS ENUM ('USER');

CREATE TYPE travel_style AS ENUM (
    'ADVENTURE',
    'NATURE',
    'BEACH',
    'MOUNTAINS',
    'HISTORICAL',
    'RELIGIOUS',
    'LUXURY',
    'BACKPACKING'
);

CREATE TYPE budget_preference AS ENUM (
    'BUDGET',
    'MODERATE',
    'PREMIUM',
    'LUXURY'
);

CREATE TYPE preferred_transport AS ENUM (
    'FLIGHT',
    'TRAIN',
    'BUS',
    'CAR',
    'ANY'
);

CREATE TYPE trip_status AS ENUM (
    'DRAFT',
    'GENERATED',
    'ACCEPTED',
    'REJECTED'
);

-- =============================================================================
-- Table: users
-- =============================================================================

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    role            user_role NOT NULL DEFAULT 'USER',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_name_length CHECK (char_length(name) >= 2),
    CONSTRAINT chk_users_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_created_at ON users (created_at DESC);

COMMENT ON TABLE users IS 'Application users with authentication credentials';
COMMENT ON COLUMN users.password IS 'BCrypt-hashed password (never store plain text)';

-- =============================================================================
-- Table: profiles
-- =============================================================================

CREATE TABLE profiles (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL,
    occupation          VARCHAR(100),
    home_city           VARCHAR(100) NOT NULL,
    travel_style        travel_style NOT NULL DEFAULT 'NATURE',
    budget_preference   budget_preference NOT NULL DEFAULT 'MODERATE',
    preferred_transport preferred_transport NOT NULL DEFAULT 'ANY',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_profiles_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_profiles_user_id UNIQUE (user_id),
    CONSTRAINT chk_profiles_home_city CHECK (char_length(home_city) >= 2)
);

CREATE INDEX idx_profiles_user_id ON profiles (user_id);
CREATE INDEX idx_profiles_travel_style ON profiles (travel_style);

COMMENT ON TABLE profiles IS 'User travel preferences — one profile per user';

-- =============================================================================
-- Table: trips
-- =============================================================================

CREATE TABLE trips (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL,
    source_city             VARCHAR(100) NOT NULL,
    start_date              DATE NOT NULL,
    end_date                DATE NOT NULL,
    budget                  NUMERIC(12, 2) NOT NULL,
    travellers              INTEGER NOT NULL DEFAULT 1,
    travel_style            travel_style NOT NULL,
    additional_preferences  TEXT,
    status                  trip_status NOT NULL DEFAULT 'DRAFT',
    selected_destination    VARCHAR(200),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_trips_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_trips_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_trips_budget CHECK (budget > 0),
    CONSTRAINT chk_trips_travellers CHECK (travellers >= 1 AND travellers <= 50),
    CONSTRAINT chk_trips_source_city CHECK (char_length(source_city) >= 2)
);

CREATE INDEX idx_trips_user_id ON trips (user_id);
CREATE INDEX idx_trips_status ON trips (status);
CREATE INDEX idx_trips_user_created ON trips (user_id, created_at DESC);
CREATE INDEX idx_trips_dates ON trips (start_date, end_date);

COMMENT ON TABLE trips IS 'Trip planning requests created by users';
COMMENT ON COLUMN trips.selected_destination IS 'AI-recommended destination chosen for itinerary generation';

-- =============================================================================
-- Table: itineraries
-- =============================================================================

CREATE TABLE itineraries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id         UUID NOT NULL,
    destination     VARCHAR(200) NOT NULL,
    itinerary_json  JSONB NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_itineraries_trip_id
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_itineraries_trip_id UNIQUE (trip_id),
    CONSTRAINT chk_itineraries_destination CHECK (char_length(destination) >= 2),
    CONSTRAINT chk_itineraries_json CHECK (jsonb_typeof(itinerary_json) = 'object')
);

CREATE INDEX idx_itineraries_trip_id ON itineraries (trip_id);
CREATE INDEX idx_itineraries_destination ON itineraries (destination);
CREATE INDEX idx_itineraries_json_gin ON itineraries USING GIN (itinerary_json);

COMMENT ON TABLE itineraries IS 'AI-generated day-wise trip itineraries stored as JSONB';
COMMENT ON COLUMN itineraries.itinerary_json IS 'Structure: { "days": [{ "day": 1, "title": "...", "activities": [...] }] }';

-- =============================================================================
-- Table: budget_breakdowns
-- =============================================================================

CREATE TABLE budget_breakdowns (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id         UUID NOT NULL,
    transport_cost  NUMERIC(12, 2) NOT NULL DEFAULT 0,
    hotel_cost      NUMERIC(12, 2) NOT NULL DEFAULT 0,
    food_cost       NUMERIC(12, 2) NOT NULL DEFAULT 0,
    activity_cost   NUMERIC(12, 2) NOT NULL DEFAULT 0,
    buffer_cost     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_cost      NUMERIC(12, 2) GENERATED ALWAYS AS (
        transport_cost + hotel_cost + food_cost + activity_cost + buffer_cost
    ) STORED,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_budget_breakdowns_trip_id
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON DELETE CASCADE,
    CONSTRAINT uq_budget_breakdowns_trip_id UNIQUE (trip_id),
    CONSTRAINT chk_budget_transport CHECK (transport_cost >= 0),
    CONSTRAINT chk_budget_hotel CHECK (hotel_cost >= 0),
    CONSTRAINT chk_budget_food CHECK (food_cost >= 0),
    CONSTRAINT chk_budget_activity CHECK (activity_cost >= 0),
    CONSTRAINT chk_budget_buffer CHECK (buffer_cost >= 0)
);

CREATE INDEX idx_budget_breakdowns_trip_id ON budget_breakdowns (trip_id);

COMMENT ON TABLE budget_breakdowns IS 'AI-estimated budget allocation per trip category';

-- =============================================================================
-- Table: trip_regeneration_history (audit trail for regenerations)
-- =============================================================================

CREATE TABLE trip_regeneration_history (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id             UUID NOT NULL,
    modification_prompt TEXT NOT NULL,
    previous_destination VARCHAR(200),
    new_destination     VARCHAR(200),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_regen_history_trip_id
        FOREIGN KEY (trip_id) REFERENCES trips (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_regen_prompt CHECK (char_length(modification_prompt) >= 3)
);

CREATE INDEX idx_regen_history_trip_id ON trip_regeneration_history (trip_id, created_at DESC);

COMMENT ON TABLE trip_regeneration_history IS 'Audit log of user-requested trip regenerations';

-- =============================================================================
-- Trigger: auto-update updated_at
-- =============================================================================

CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

CREATE TRIGGER trg_profiles_updated_at
    BEFORE UPDATE ON profiles
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

CREATE TRIGGER trg_trips_updated_at
    BEFORE UPDATE ON trips
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

CREATE TRIGGER trg_itineraries_updated_at
    BEFORE UPDATE ON itineraries
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

CREATE TRIGGER trg_budget_breakdowns_updated_at
    BEFORE UPDATE ON budget_breakdowns
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- =============================================================================
-- Views (convenience for dashboard queries)
-- =============================================================================

CREATE OR REPLACE VIEW v_trip_summary AS
SELECT
    t.id,
    t.user_id,
    t.source_city,
    t.start_date,
    t.end_date,
    t.budget,
    t.travellers,
    t.travel_style,
    t.status,
    t.selected_destination,
    t.created_at,
    i.destination,
    i.itinerary_json,
    b.transport_cost,
    b.hotel_cost,
    b.food_cost,
    b.activity_cost,
    b.buffer_cost,
    b.total_cost,
    (t.end_date - t.start_date + 1) AS total_days
FROM trips t
LEFT JOIN itineraries i ON i.trip_id = t.id
LEFT JOIN budget_breakdowns b ON b.trip_id = t.id;

COMMENT ON VIEW v_trip_summary IS 'Denormalized trip view for dashboard and detail pages';
