# Galactic Trade Simulator

A full-stack simulation of an autonomous interstellar economy built with Spring Boot and React.

The application simulates AI-controlled traders operating within a procedurally generated galaxy. Markets evolve over time as supply and demand fluctuate, traders evaluate profitable trade routes, and the simulation advances in discrete ticks.

The project is intended as a portfolio application demonstrating backend architecture, REST API design, simulation systems, frontend visualization, and AWS deployment.

## Current Features

- Procedurally generated galaxy
- Region-based economic specialization
- Commodity market simulation
- Autonomous trader AI
- Tick-based simulation engine
- Interactive React galaxy map
- Live trader visualization
- Trade route overlays
- REST API between frontend and backend

### Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database

## Frontend

- React
- TypeScript
- Axios
- SVG Rendering

## Backend Architecture

Each feature is organized into its own package using a layered architecture.
backend/
├── commodity/
├── market/
├── simulation/
├── starsystem/
└── trader/

### Star System

Responsible for procedural galaxy generation.

Contains:

- star system entity
- region classification
- system generation
- REST endpoints

### Commodity

Defines all commodities available within the economy.

Contains:

- commodity definitions
- base pricing
- commodity data loader

### Market

Represents the local economy for every star system.

Responsible for:

- market prices
- supply
- demand
- imports
- exports

### Trader

Represents autonomous trading agents.

Responsible for:

- trader state
- cargo
- credits
- decision making

### Simulation

Coordinates the entire simulation.

Responsible for:

- simulation ticks
- market updates
- trader updates
- travel
- trade opportunities

## Frontend (React)

frontend/
- api/
- components/
- types/
- utils/

components/
- GalaxyMap
  - GalaxySystems
  - GalaxyTraders
  - TraderPanel
  - SystemPanel
  - TradeRouteOverlay

## Current Simulation Flow

Tick → Update Markets → Generate Price Changes → Evaluate Traders → 
Assign Trades → Travel → Buy → Travel → Sell → Repeat

## Current Limitations

The current implementation intentionally simplifies several systems.

Current simplifications include:

- Static trader cargo capacity
- Static market supply values
- Simplified price model
- Single trade per trader
- Random market fluctuations
- In-memory simulation state
