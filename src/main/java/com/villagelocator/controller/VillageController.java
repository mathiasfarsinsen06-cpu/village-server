package com.villagelocator.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.villagelocator.service.VillageLocatorService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villages")
@CrossOrigin(origins = "*")
public class VillageController {
    
    @Autowired
    private VillageLocatorService villageLocatorService;
    
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping
    public ResponseEntity<?> getVillagesBySeed(@RequestParam long seed) {
        try {
            // Use the actual VillageLocatorService to find villages
            List<Map<String, Integer>> villages = villageLocatorService.findVillages(seed, 0, 0, 5000);
            
            JsonArray jsonArray = new JsonArray();
            
            for (Map<String, Integer> village : villages) {
                JsonObject jsonVillage = new JsonObject();
                jsonVillage.addProperty("x", village.get("x"));
                jsonVillage.addProperty("z", village.get("z"));
                jsonArray.add(jsonVillage);
            }
            
            return ResponseEntity.ok(jsonArray.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
