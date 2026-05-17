package com.Ifrah.javaproject;

import java.util.*;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Controller
public class yellowcontroller {

    @Autowired
    private yellowrepo repo;

    @Autowired
    private RouteService routeService;

    @Autowired
    private bluerepo brepo;
    @Autowired
    private GraphService graphService;

    // Create graph only once and store it for later use
    @GetMapping("/y")
    public String getAllYellowStations(Model model) {
        System.out.println("Starting a new phase");

        // Fetch yellow stations and sort them by code so graph order matches Bellman-Ford
        List<yellow> y = repo.findAll();
        y.sort(Comparator.comparingInt(station -> GraphService.extractCodeNumber(station.getCode())));

        // Fetch blue stations and sort them by code
        Sort sort = Sort.by(Sort.Direction.ASC, "code");
        List<blue> b = brepo.findAll(sort);
        graphService.buildGraph(y, b);
        System.out.println("call returned to yellowcontroller after blueservice call");
        // Add stations to model for rendering
        model.addAttribute("stations", y);
        model.addAttribute("bstations", b);
        System.out.println("attributes added");

        return "index3";  // Render the page
    }

        // Calculate shortest path using pre-created graph
    @PostMapping("/cal")
    public String calculateShortestPath(@RequestParam("source") String source,
                                        @RequestParam("destination") String destination,
                                        Model model) {
        List<yellow> y = repo.findAll();
        y.sort(Comparator.comparingInt(station -> GraphService.extractCodeNumber(station.getCode())));

        // Fetch blue stations and sort them by code
        Sort sort = Sort.by(Sort.Direction.ASC, "code");
        List<blue> b = brepo.findAll(sort);

        System.out.println("inside cal, below is bellman");

        // Calculate the shortest path using Bellman-Ford
        ArrayList<Edge>[] graph = graphService.buildGraph(y, b);
        RouteResult shortestPathResult = routeService.bellman2(graph, b, y, graph.length, source, destination);

        // Add the shortest path result and other data to model
        model.addAttribute("shortestPath", shortestPathResult.shortestP);
        model.addAttribute("shortestDist", shortestPathResult.shortestD);
        model.addAttribute("shortestDistText", String.format("%.2f", shortestPathResult.shortestD));
        model.addAttribute("estimatedTravelTimeText", formatDuration(shortestPathResult.shortestD));
        model.addAttribute("totalTimeTakenText", formatDuration(shortestPathResult.shortestD));
        model.addAttribute("routeGeneratedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("shortestP in controller: "+shortestPathResult.shortestP);
        model.addAttribute("src", source);
        model.addAttribute("dest", destination);
        model.addAttribute("stations", y);
        model.addAttribute("bstations", b);
        // Return to the page with the result
        return "index3";
    }

    private String formatDuration(double totalMinutes) {
        long minutes = Math.max(0L, Math.round(totalMinutes));
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if (hours > 0) {
            return hours + " hr " + remainingMinutes + " min";
        }
        return remainingMinutes + " min";
    }

    // Simple endpoint to display stations on a leaflet map
    @GetMapping("/leaf")
    public String leaflet(Model model) {
        List<yellow> y  = repo.findAll();
        y.sort(Comparator.comparingInt(station -> GraphService.extractCodeNumber(station.getCode())));
        model.addAttribute("stations", y );
        return "leaf";
    }
}
