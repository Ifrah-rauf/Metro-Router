<div align="center">

# 🚇 Travel Metro

### Graph-based metro route optimization and transit simulation system

<br>

<img src="./assets/banner.png" width="100%" alt="Travel Metro Banner"/>

<br><br>

<img src="https://img.shields.io/badge/Java-17045c?style=for-the-badge&logo=openjdk&logoColor=white"/>

<img src="https://img.shields.io/badge/Spring_Boot-21106e?style=for-the-badge&logo=springboot&logoColor=white"/>

<img src="https://img.shields.io/badge/MongoDB-3B1FA8?style=for-the-badge&logo=mongodb&logoColor=white"/>

<img src="https://img.shields.io/badge/Leaflet.js-4A2CA5?style=for-the-badge&logo=leaflet&logoColor=white"/>

<img src="https://img.shields.io/badge/Dijkstra_Algorithm-5A1E3F?style=for-the-badge&logoColor=white"/>

</div>

---

## Overview

Travel Metro is a Spring Boot based metro route optimization system that models realistic Delhi Metro traversal using graph-based routing and dynamic transit simulation.

The project allows users to:

- Select source and destination stations
- Compute optimized metro routes
- Simulate realistic traversal conditions
- Visualize routes using interactive Leaflet maps

Unlike traditional shortest-path implementations, the routing engine incorporates dynamic runtime edge weighting based on waiting time, traffic behavior, rush-hour congestion and interchange overhead.

<br>

<div align="center">

<img src="./assets/overview.png" width="92%" alt="Overview Screenshot"/>

</div>

---

## System Architecture

The backend architecture is centered around a dynamically constructed weighted graph representation of the metro network.

- Stations are modeled as graph nodes
- Metro connections are modeled as weighted edges
- Graph topology is loaded from MongoDB
- Graphs are cached in memory for fast traversal

<br>

<div align="center">

<img src="./assets/architecture.png" width="92%" alt="Architecture Diagram"/>

</div>

<br>

| Component | Responsibility |
|---|---|
| MongoDB | Station topology storage |
| GraphService | Graph construction |
| TransitWeightService | Dynamic edge weighting |
| TraversalTimeManager | Runtime time simulation |
| Dijkstra Engine | Route optimization |
| Leaflet.js | Route visualization |

---

## Routing Engine

The routing engine uses Dijkstra’s algorithm to compute optimized traversal paths across metro stations.

Instead of using only geographical distance, traversal weights are dynamically calculated using contextual transit behavior.

### Runtime Weight Components

| Weight Component | Purpose |
|---|---|
| Base Travel Time | Core station traversal |
| Waiting Time | Platform delay simulation |
| Peak Hour Penalty | Office rush-hour impact |
| Weekday Traffic Factor | Realistic weekday behavior |
| Interchange Penalty | Platform transfer overhead |

<br>

<div align="center">

<img src="./assets/routing-flowchart.png" width="88%" alt="Routing Flowchart"/>

</div>

---

## Temporal Transit Simulation

The project introduces runtime traversal simulation using the `TraversalTimeManager`.

As traversal progresses through the graph, accumulated travel time is tracked continuously. This enables contextual edge weighting based on the estimated arrival time at each station.

The system therefore simulates:

- Office rush-hour congestion
- Weekday traffic behavior
- Dynamic waiting penalties
- Realistic transit conditions

<br>

<div align="center">

<img src="./assets/time-simulation.png" width="88%" alt="Time Simulation"/>

</div>

---

## Interchange-Aware Routing

Interchange penalties are applied only when traversal switches metro lines.

This prevents unrealistic traversal inflation while accurately modeling platform transfer delays inside major interchange stations such as:

- Rajiv Chowk
- Kashmere Gate

<br>

| Traversal Behavior | Penalty Applied |
|---|---|
| Same-Line Continuation | No |
| Metro Line Switch | Yes |

---

## Branch-Aware Blue Line Modeling

The Blue Line contains multiple branches.

The graph initialization layer models branch topology independently to prevent invalid cross-branch traversal and preserve realistic metro connectivity constraints.

<br>

<div align="center">

<img src="./assets/branch-routing.png" width="90%" alt="Branch Routing"/>

</div>

---

## Frontend Visualization

The frontend layer uses Thymeleaf together with Leaflet.js for interactive metro visualization.

### Features

- Interactive metro rendering
- Dynamic route highlighting
- Station marker visualization
- Autocomplete station selection
- Compact route summaries

<br>

<div align="center">

<img src="./assets/map-ui.png" width="92%" alt="Frontend UI"/>

</div>

---

## Core Engineering Concepts

| Concept | Usage |
|---|---|
| Adjacency List Graph | Metro topology modeling |
| Dijkstra Traversal | Route optimization |
| Dynamic Edge Weighting | Transit simulation |
| Haversine Formula | Distance calculation |
| In-Memory Graph Cache | Traversal optimization |
| Runtime Time Simulation | Context-aware routing |

---

## Project Structure

```txt
travel-metro/
│
├── backend/
│   ├── services/
│   ├── controllers/
│   ├── graph/
│   └── traversal/
│
├── frontend/
│   ├── templates/
│   ├── static/
│   └── leaflet/
│
├── assets/
│
└── README.md
````

---

## Future Improvements

* Full Delhi Metro network support
* Fare estimation system
* Live metro timing integration
* Predictive crowd analytics
* Multi-objective route optimization
* Distributed graph caching

---

## Running The Project

### Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/travel-metro.git
```

### Navigate To Project

```bash
cd travel-metro
```

### Run Spring Boot Application

```bash
./gradlew bootRun
```

### Open Browser

```txt
http://localhost:8080
```

---

<div align="center">

## Author

### Ifrah Rauf

Systems engineering • Backend development • Product building

</div>
```
