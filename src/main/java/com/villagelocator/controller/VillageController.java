package com.villagelocator.controller;

import com.villagelocator.service.VillageFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class VillageController {
    
    @Autowired
    private VillageFinderService villageFinderService;
    
    @GetMapping("/villages")
    public Map<String, Object> findVillages(
            @RequestParam long seed,
            @RequestParam(defaultValue = "0") int x,
            @RequestParam(defaultValue = "0") int z,
            @RequestParam(defaultValue = "10000") int radius) {
        
        List<Map<String, Integer>> villages = villageFinderService.findVillages(seed, x, z, radius);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seed", seed);
        response.put("center", Map.of("x", x, "z", z));
        response.put("radius", radius);
        response.put("villages", villages);
        response.put("count", villages.size());
        
        return response;
    }
}
