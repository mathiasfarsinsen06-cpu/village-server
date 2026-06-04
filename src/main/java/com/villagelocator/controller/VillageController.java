package com.villagelocator.controller;

import com.villagelocator.service.VillageFinderService;
import com.villagelocator.service.VillageFinderService.VillageLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API for finding villages by seed.
 * Endpoint: GET /api/villages?seed=SEED&x=X&z=Z&radius=RADIUS
 */
@RestController
@RequestMapping("/api/villages")
@CrossOrigin(origins = "*")
public class VillageController {
    
    @Autowired
    private VillageFinderService villageFinderService;
    
    /**
     * Find villages near a coordinate for a given seed.
     * 
     * Query parameters:
     * - seed: long (required) - Minecraft world seed
     * - x: int (required) - center X coordinate in blocks
     * - z: int (required) - center Z coordinate in blocks
     * - radius: int (optional, default=1000) - search radius in chunks
     * 
     * Example: GET /api/villages?seed=5975010353295290926&x=-8729&z=-21647&radius=100
     */
    @GetMapping
    public Map<String, Object> findVillages(
            @RequestParam(value = "seed") long seed,
            @RequestParam(value = "x") int x,
            @RequestParam(value = "z") int z,
            @RequestParam(value = "radius", defaultValue = "1000") int radiusChunks) {
        
        System.out.println("[API] Finding villages - seed=" + seed + " x=" + x + " z=" + z + 
                         " radius=" + radiusChunks + " chunks");
        
        try {
            List<VillageLocation> villages = villageFinderService.findVillagesNear(seed, x, z, radiusChunks);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("seed", seed);
            response.put("centerX", x);
            response.put("centerZ", z);
            response.put("radiusChunks", radiusChunks);
            response.put("villageCount", villages.size());
            response.put("villages", villages);
            
            System.out.println("[API] SUCCESS - Found " + villages.size() + " villages");
            return response;
            
        } catch (Exception e) {
            System.err.println("[API] ERROR: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        }
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Village Locator");
        response.put("version", "1.0");
        return response;
    }
}
