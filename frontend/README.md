# Frontend — React SPA

React 18 · TypeScript · Vite · Tailwind CSS · React Query · React Router

## Folder Structure

```
src/
├── api/                 # Axios instance + API service functions
│   ├── axios.ts         # Configured Axios with JWT interceptor
│   ├── auth.api.ts
│   ├── profile.api.ts
│   └── trip.api.ts
├── components/
│   ├── layout/          # Navbar, Sidebar, ProtectedRoute, Layout
│   ├── ui/              # Button, Input, Card, Badge, Modal
│   ├── trip/            # TripCard, ItineraryView, BudgetBreakdown
│   └── profile/         # ProfileForm
├── contexts/
│   └── AuthContext.tsx  # JWT token + user state
├── hooks/
│   ├── useAuth.ts
│   ├── useProfile.ts
│   └── useTrips.ts
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── DashboardPage.tsx
│   ├── CreateTripPage.tsx
│   ├── TripDetailsPage.tsx
│   └── ProfilePage.tsx
├── types/
│   ├── auth.types.ts
│   ├── profile.types.ts
│   └── trip.types.ts
├── utils/
│   ├── formatters.ts    # Date, currency formatting
│   └── validators.ts
├── App.tsx
├── main.tsx
└── index.css            # Tailwind directives
```

## Pages

| Route          | Component        | Auth Required |
|----------------|------------------|---------------|
| `/login`       | LoginPage        | No            |
| `/register`    | RegisterPage     | No            |
| `/dashboard`   | DashboardPage    | Yes           |
| `/trips/new`   | CreateTripPage   | Yes           |
| `/trips/:id`   | TripDetailsPage  | Yes           |
| `/profile`     | ProfilePage      | Yes           |

## Run Locally

```bash
npm install
npm run dev
```

## Environment

```env
VITE_API_BASE_URL=http://localhost:8080/api
```
