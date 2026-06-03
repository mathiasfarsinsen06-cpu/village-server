package com.villagelocator.controller;

import com.villagelocator.service.VillageFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.math.BigInteger;

@RestController
@RequestMapping("/api")
public class VillageController {
    
    @Autowired
    private VillageFinderService villageFinderService;
    
    @GetMapping("/villages")
    public Map<String, Object> findVillages(
            @RequestParam String seed,
            @RequestParam(defaultValue = "0") int x,
            @RequestParam(defaultValue = "0") int z,
            @RequestParam(defaultValue = "10000") int radius) {
        
        // Convert seed string to long (handle large numbers by taking modulo)
        long seedLong;
        try {
            // If it's a huge number, convert via BigInteger then to long
            BigInteger bi = new BigInteger(seed);
            seedLong = bi.longValue();
        } catch (NumberFormatException e) {
            return Map.of("error", "Invalid seed format: " + seed);
        }
        
        List<Map<String, Integer>> villages = villageFinderService.findVillages(seedLong, x, z, radius);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seed", seed);
        response.put("seedLong", seedLong);
        response.put("center", Map.of("x", x, "z", z));
        response.put("radius", radius);
        response.put("villages", villages);
        response.put("count", villages.size());
        
        return response;
    }
}
