````html
<!-- ========================================================= -->
<!--                    TRAVEL METRO README                    -->
<!-- ========================================================= -->

<div align="center">

<img src="./assets/banner.png" width="100%" alt="Travel Metro Banner"/>

<br><br>

<h1 style="
font-family: 'Space Grotesk', sans-serif;
font-size:64px;
font-weight:700;
letter-spacing:-2px;
color:#17045c;
margin-bottom:10px;
">

🚇 Travel Metro

</h1>

<p style="
font-family:'Inter', sans-serif;
font-size:20px;
max-width:900px;
line-height:1.8;
color:#444444;
">

Graph-based metro route optimization system built using Spring Boot, MongoDB and Leaflet.js.

</p>

<br>

<img src="https://img.shields.io/badge/Java-17045c?style=for-the-badge&logo=openjdk&logoColor=17045c&color=white"/>

<img src="https://img.shields.io/badge/Spring_Boot-21106e?style=for-the-badge&logo=springboot&logoColor=21106e&color=white"/>

<img src="https://img.shields.io/badge/MongoDB-3B1FA8?style=for-the-badge&logo=mongodb&logoColor=3B1FA8&color=white"/>

<img src="https://img.shields.io/badge/Leaflet.js-4A2CA5?style=for-the-badge&logo=leaflet&logoColor=4A2CA5&color=white"/>

<img src="https://img.shields.io/badge/Dijkstra_Algorithm-5A1E3F?style=for-the-badge&color=white"/>

<img src="https://img.shields.io/badge/Transit_Simulation-6A214B?style=for-the-badge&color=white"/>

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#17045c;
letter-spacing:-1px;
">

Overview

</h2>

</div>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

Travel Metro is a Spring Boot based metro route optimization and transit simulation system that models realistic Delhi Metro traversal using graph-based routing, dynamic edge weighting and runtime transit behavior.

The project allows users to select source and destination stations, compute optimal traversal routes across metro lines and visualize the computed route using an interactive Leaflet map interface.

Unlike traditional shortest-path implementations, the system introduces runtime transit modeling through contextual traversal weights such as waiting time, weekday congestion, peak-hour crowd behavior and interchange overhead. The result is a route engine that attempts to simulate realistic metro traversal conditions rather than only minimizing physical distance.

</p>

<br><br>

<div align="center">

<img src="./assets/overview-ui.png" width="92%" alt="Project Overview Screenshot"/>

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#5A1E3F;
letter-spacing:-1px;
">

System Architecture

</h2>

</div>

<br>

<div align="center">

<img src="./assets/architecture-diagram.png" width="92%" alt="Architecture Diagram"/>

</div>

<br>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

The backend architecture is centered around a dynamically constructed weighted graph representation of the metro network.

Station topology for Yellow and Blue metro lines is stored in MongoDB and loaded during server startup. The <strong>GraphService</strong> transforms this topology into an adjacency-list graph structure where:

</p>

<br>

<div align="center">

| Graph Component | Representation |
|---|---|
| Node | Metro Station |
| Edge | Station Connection |
| Weight | Dynamic Transit Cost |

</div>

<br>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

Static edge metadata such as inter-station distance and base traversal time is computed using the Haversine formula during graph initialization.

Since metro topology changes infrequently, the graph is cached entirely in memory after initialization, significantly reducing graph reconstruction overhead and improving traversal performance across requests.

</p>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#17045c;
letter-spacing:-1px;
">

Traversal Engine

</h2>

</div>

<br>

<div align="center">

<img src="./assets/traversal-flowchart.png" width="88%" alt="Traversal Flowchart"/>

</div>

<br>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

The routing engine is implemented using Dijkstra’s algorithm.

Each station acts as a graph node while metro connections behave as weighted edges. Instead of relying solely on geographical distance, traversal cost is dynamically computed using the <strong>TransitWeightService</strong>.

Runtime edge cost is calculated using:

</p>

<br>

<div align="center">

| Runtime Weight Component | Purpose |
|---|---|
| Base Travel Time | Core traversal duration |
| Waiting Time | Platform arrival behavior |
| Peak Hour Penalty | Rush-hour congestion simulation |
| Weekday Traffic Factor | Office-day crowd impact |
| Interchange Penalty | Platform transfer overhead |

</div>

<br>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

This transforms traversal computation from a static shortest-path problem into a dynamic transit optimization system capable of simulating contextual metro behavior.

</p>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#5A1E3F;
letter-spacing:-1px;
">

Temporal Transit Simulation

</h2>

</div>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

To model realistic travel conditions, the project introduces temporal traversal simulation using the <strong>TraversalTimeManager</strong>.

As graph traversal progresses, accumulated traversal time is tracked continuously. This allows the system to estimate the real-world arrival time at each station during traversal execution.

The runtime clock simulation enables contextual transit penalties based on weekday office timings and rush-hour conditions. Peak-hour penalties are primarily applied during office traffic windows, while weekends remain comparatively relaxed to better mimic real metro crowd behavior.

</p>

<br>

<div align="center">

<img src="./assets/time-simulation.png" width="88%" alt="Transit Simulation"/>

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#17045c;
letter-spacing:-1px;
">

Interchange-Aware Routing

</h2>

</div>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

The routing engine includes interchange-aware traversal logic.

Interchange penalties are activated only when the traversal route actually switches metro lines instead of merely passing through an interchange station.

This prevents unrealistic traversal inflation while accurately modeling platform transfer delays and walking overhead inside large interchange stations such as Rajiv Chowk and Kashmere Gate.

The system therefore distinguishes:

</p>

<br>

<div align="center">

| Traversal Behavior | Penalty Applied |
|---|---|
| Same-Line Continuation | No |
| Metro Line Switch | Yes |

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#5A1E3F;
letter-spacing:-1px;
">

Branch-Aware Metro Modeling

</h2>

</div>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

The Blue Line architecture supports branch-aware graph traversal.

Since the Blue Line splits into multiple branches, graph formation logic handles branch topology independently during graph initialization. This prevents invalid cross-branch traversal and ensures route computation respects actual metro connectivity constraints.

The graph structure therefore models not only sequential station traversal but also realistic metro branching behavior.

</p>

<br>

<div align="center">

<img src="./assets/branch-routing.png" width="90%" alt="Branch Aware Routing"/>

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#17045c;
letter-spacing:-1px;
">

Frontend & Visualization

</h2>

</div>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

The frontend layer uses Thymeleaf templates together with Leaflet.js for interactive metro visualization.

Computed traversal routes are rendered dynamically as highlighted map polylines accompanied by station markers and compact route summaries.

The interface additionally supports:

</p>

<br>

<div align="center">

| Frontend Capability |
|---|
| Interactive Leaflet Rendering |
| Station Marker Visualization |
| Route Highlighting |
| Autocomplete Station Selection |
| Compact Route Summaries |

</div>

<br>

<div align="center">

<img src="./assets/map-ui.png" width="92%" alt="Map UI"/>

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#5A1E3F;
letter-spacing:-1px;
">

Core Engineering Concepts

</h2>

</div>

<br>

<div align="center">

| Concept | Usage |
|---|---|
| Graph Construction | Metro topology modeling |
| Dijkstra Traversal | Route optimization |
| Dynamic Edge Weighting | Transit behavior simulation |
| Haversine Formula | Geospatial distance calculation |
| In-Memory Graph Caching | Performance optimization |
| Runtime Time Simulation | Context-aware traversal |
| Interchange Modeling | Realistic transfer handling |

</div>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#17045c;
letter-spacing:-1px;
">

Project Structure

</h2>

</div>

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

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#5A1E3F;
letter-spacing:-1px;
">

Future Improvements

</h2>

</div>

<p style="
font-family:'Inter', sans-serif;
font-size:17px;
line-height:2;
color:#444444;
">

* Full Delhi Metro network support
* Live metro API integration
* Fare estimation system
* Predictive crowd analytics
* Multi-objective route optimization
* Real-time delay simulation
* Deployment with scalable distributed caching

</p>

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#17045c;
letter-spacing:-1px;
">

Run The Project

</h2>

</div>

```bash
git clone https://github.com/YOUR_USERNAME/travel-metro.git
```

```bash
cd travel-metro
```

```bash
./gradlew bootRun
```

```txt
http://localhost:8080
```

<br><br>

---

<div align="center">

<h2 style="
font-family:'Space Grotesk', sans-serif;
font-size:42px;
font-weight:650;
color:#5A1E3F;
letter-spacing:-1px;
">

Author

</h2>

<p style="
font-family:'Inter', sans-serif;
font-size:18px;
line-height:2;
color:#444444;
">

Ifrah Rauf

<br>

Systems engineering • Backend development • Product building

</p>

</div>
```
