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
    private static final String MC_SEED_LOCATOR_URL = "http://localhost:3000/api/structures";

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int searchRadius) {
        List<Map<String, Integer>> villages = new ArrayList<>();
        
        try {
            // Call MC-SeedLocator API to find villages
            String url = String.format("%s?seed=%d&x=%d&z=%d&radius=%d&type=village", 
                MC_SEED_LOCATOR_URL, seed, centerX, centerZ, searchRadius);
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null) {
                JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
                
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject village = jsonArray.get(i).getAsJsonObject();
                    Map<String, Integer> villageMap = new HashMap<>();
                    villageMap.put("x", village.get("x").getAsInt());
                    villageMap.put("z", village.get("z").getAsInt());
                    villages.add(villageMap);
                }
            }
            
            // Sort by distance
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
            System.err.println("Error calling MC-SeedLocator: " + e.getMessage());
            e.printStackTrace();
        }
        
        return villages;
    }
}
