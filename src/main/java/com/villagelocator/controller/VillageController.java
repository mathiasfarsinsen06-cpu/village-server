package com.villagelocator.controller;

import com.villagelocator.service.VillageFinderService;
import com.villagelocator.service.VillageFinderService.VillageLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API for village finding with AMIDST v4.7 validation.
 */
@RestController
@RequestMapping("/api/villages")
public class VillageController {
    
    @Autowired
    private VillageFinderService villageFinderService;
    
    /**
     * Find villages in a region.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchVillages(
            @RequestParam long seed,
            @RequestParam int minChunkX,
            @RequestParam int maxChunkX,
            @RequestParam int minChunkZ,
            @RequestParam int maxChunkZ) {
        
        List<VillageLocation> villages = villageFinderService.findVillages(seed, minChunkX, maxChunkX, minChunkZ, maxChunkZ);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seed", seed);
        response.put("region", Map.of(
            "minChunkX", minChunkX,
            "maxChunkX", maxChunkX,
            "minChunkZ", minChunkZ,
            "maxChunkZ", maxChunkZ
        ));
        response.put("villageCount", villages.size());
        response.put("villages", villages);
        response.put("algorithm", "AMIDST v4.7 adapted for Minecraft 1.21.4");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Find villages near a specific coordinate.
     */
    @GetMapping("/near")
    public ResponseEntity<?> findNear(
            @RequestParam long seed,
            @RequestParam int centerX,
            @RequestParam int centerZ,
            @RequestParam(defaultValue = "100") int radiusChunks) {
        
        List<VillageLocation> villages = villageFinderService.findVillagesNear(seed, centerX, centerZ, radiusChunks);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seed", seed);
        response.put("center", Map.of("x", centerX, "z", centerZ));
        response.put("radiusChunks", radiusChunks);
        response.put("villageCount", villages.size());
        response.put("villages", villages);
        response.put("algorithm", "AMIDST v4.7 adapted for Minecraft 1.21.4");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("version", "1.0.0");
        response.put("algorithm", "AMIDST v4.7 + Minecraft 1.21.4");
        return ResponseEntity.ok(response);
    }
}
