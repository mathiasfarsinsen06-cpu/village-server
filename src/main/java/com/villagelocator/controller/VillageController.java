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

    private static final int DEFAULT_RADIUS_CHUNKS = 50;
    private static final int MAX_RADIUS_CHUNKS = 512;
    
    @Autowired
    private VillageFinderService villageFinderService;
    
    /**
     * Find villages near a coordinate for a given seed.
     * 
     * Query parameters:
     * - seed: long (required) - Minecraft world seed
     * - x: int (required) - center X coordinate in blocks
     * - z: int (required) - center Z coordinate in blocks
     * - radius: int (optional, default=50, max=512) - search radius in chunks
     * 
     * Example: GET /api/villages?seed=5975010353295290926&x=-8729&z=-21647&radius=100
     */
    @GetMapping
    public Map<String, Object> findVillages(
            @RequestParam(value = "seed") long seed,
            @RequestParam(value = "x") int x,
            @RequestParam(value = "z") int z,
            @RequestParam(value = "radius", defaultValue = "50") int radiusChunks) {
        int effectiveRadiusChunks = Math.max(0, Math.min(radiusChunks, MAX_RADIUS_CHUNKS));
        if (effectiveRadiusChunks == 0) {
            effectiveRadiusChunks = DEFAULT_RADIUS_CHUNKS;
        }
        
        System.out.println("[API] Finding villages - seed=" + seed + " x=" + x + " z=" + z + 
                         " radius=" + effectiveRadiusChunks + " chunks");
        
        try {
            List<VillageLocation> villages = villageFinderService.findVillagesNear(seed, x, z, effectiveRadiusChunks);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("seed", seed);
            response.put("centerX", x);
            response.put("centerZ", z);
            response.put("radiusChunks", effectiveRadiusChunks);
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
