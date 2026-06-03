package com.mathiasfar.villagelocator.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RestController
@RequestMapping("/api/villages")
@CrossOrigin(origins = "*")
public class VillageController {
    
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping
    public ResponseEntity<?> getVillagesBySeed(@RequestParam long seed) {
        try {
            // Her skal du implementere village-finding algoritmen
            // For nu returnerer vi et simpelt svar
            
            JsonArray villages = new JsonArray();
            
            // Eksempel villages
            JsonObject village1 = new JsonObject();
            village1.addProperty("x", 100);
            village1.addProperty("z", 200);
            villages.add(village1);
            
            JsonObject village2 = new JsonObject();
            village2.addProperty("x", 300);
            village2.addProperty("z", 400);
            villages.add(village2);
            
            return ResponseEntity.ok(villages.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
