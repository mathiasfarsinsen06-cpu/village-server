package com.villagelocator.controller;

import com.villagelocator.service.VillageLocatorService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/villages")
@CrossOrigin(origins = "*")
public class VillageController {

    @Autowired
    private VillageLocatorService villageLocatorService;

    @GetMapping("/find")
    public String findVillages(
            @RequestParam long seed,
            @RequestParam(defaultValue = "5000") int radius,
            @RequestParam(defaultValue = "0") int centerX,
            @RequestParam(defaultValue = "0") int centerZ) {
        
        List<Map<String, Integer>> villages = villageLocatorService.findVillages(seed, centerX, centerZ, radius);
        
        JSONObject response = new JSONObject();
        JSONArray data = new JSONArray();
        
        for (Map<String, Integer> village : villages) {
            JSONObject villageJson = new JSONObject();
            villageJson.put("x", village.get("x"));
            villageJson.put("z", village.get("z"));
            data.put(villageJson);
        }
        
        response.put("data", data);
        response.put("count", villages.size());
        response.put("seed", seed);
        
        return response.toString();
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"ok\"}";
    }
}
