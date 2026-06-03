package com.villagelocator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

@Service
public class VillageLocatorService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OLELA_API_URL = "https://api.olela.me/v1/structures";

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int searchRadius) {
        List<Map<String, Integer>> villages = new ArrayList<>();
        
        try {
            // Call OlelaFinder API to find villages
            String url = String.format("%s?seed=%d&x=%d&z=%d&type=village&radius=%d", 
                OLELA_API_URL, seed, centerX, centerZ, searchRadius / 16);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && !response.isEmpty()) {
                JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                
                // Check if the response contains a structures array
                if (jsonResponse.has("structures")) {
                    JsonArray structuresArray = jsonResponse.getAsJsonArray("structures");
                    
                    for (int i = 0; i < structuresArray.size(); i++) {
                        JsonObject structure = structuresArray.get(i).getAsJsonObject();
                        
                        // OlelaFinder returns x, z coordinates
                        if (structure.has("x") && structure.has("z")) {
                            int villageX = structure.get("x").getAsInt();
                            int villageZ = structure.get("z").getAsInt();
                            
                            // Check distance
                            int dx = villageX - centerX;
                            int dz = villageZ - centerZ;
                            long distSq = (long)dx * dx + (long)dz * dz;
                            
                            if (distSq < (long)searchRadius * searchRadius) {
                                Map<String, Integer> village = new HashMap<>();
                                village.put("x", villageX);
                                village.put("z", villageZ);
                                villages.add(village);
                            }
                        }
                    }
                }
            }
            
            // Sort by distance from center
            final int centerXFinal = centerX;
            final int centerZFinal = centerZ;
            villages.sort((a, b) -> {
                int dx1 = a.get("x") - centerXFinal;
                int dz1 = a.get("z") - centerZFinal;
                int dx2 = b.get("x") - centerXFinal;
                int dz2 = b.get("z") - centerZFinal;
                
                long dist1 = (long)dx1 * dx1 + (long)dz1 * dz1;
                long dist2 = (long)dx2 * dx2 + (long)dz2 * dz2;
                
                return Long.compare(dist1, dist2);
            });
            
        } catch (Exception e) {
            System.err.println("Error calling OlelaFinder API: " + e.getMessage());
            e.printStackTrace();
        }
        
        return villages;
    }
}
