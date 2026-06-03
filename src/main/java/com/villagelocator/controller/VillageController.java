package com.villagelocator.controller;

import com.villagelocator.service.VillageLocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/villages")
public class VillageController {

    @Autowired
    private VillageLocatorService villageLocatorService;

    /**
     * Find villages from seed and location
     * @param seed World seed
     * @param x Center X coordinate
     * @param z Center Z coordinate
     * @param radius Search radius in blocks
     * @return List of village coordinates
     */
    @GetMapping("/find")
    public Map<String, Object> findVillages(
            @RequestParam long seed,
            @RequestParam int x,
            @RequestParam int z,
            @RequestParam(defaultValue = "10000") int radius) {
        
        List<Map<String, Integer>> villages = villageLocatorService.findVillages(seed, x, z, radius);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seed", seed);
        response.put("centerX", x);
        response.put("centerZ", z);
        response.put("radius", radius);
        response.put("count", villages.size());
        response.put("villages", villages);
        
        return response;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
