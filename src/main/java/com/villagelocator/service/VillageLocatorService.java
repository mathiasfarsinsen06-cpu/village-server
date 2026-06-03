package com.villagelocator.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VillageLocatorService {

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int searchRadius) {
        List<Map<String, Integer>> villages = new ArrayList<>();
        
        int spacing = 34;
        int regionSize = spacing * 16;
        
        int regionStartX = (centerX - searchRadius) / regionSize;
        int regionStartZ = (centerZ - searchRadius) / regionSize;
        int regionEndX = (centerX + searchRadius) / regionSize;
        int regionEndZ = (centerZ + searchRadius) / regionSize;
        
        for (int rx = regionStartX; rx <= regionEndX; rx++) {
            for (int rz = regionStartZ; rz <= regionEndZ; rz++) {
                long regionHash = seed;
                regionHash ^= (long) rx * 341873128712L;
                regionHash ^= (long) rz * 132897987541L;
                
                long a = regionHash;
                a ^= (a << 13);
                a ^= (a >> 7);
                a ^= (a << 17);
                
                if ((a & 0xFFFFFFFFL) % 10 < 6) {
                    int baseX = rx * regionSize;
                    int baseZ = rz * regionSize;
                    
                    long offsetHash = a;
                    offsetHash ^= (offsetHash << 13);
                    offsetHash ^= (offsetHash >> 7);
                    offsetHash ^= (offsetHash << 17);
                    
                    int offsetX = Math.abs((int) offsetHash % regionSize);
                    int offsetZ = Math.abs((int) (offsetHash >> 32) % regionSize);
                    
                    int villageX = baseX + offsetX;
                    int villageZ = baseZ + offsetZ;
                    
                    int distSq = (villageX - centerX) * (villageX - centerX) + 
                                 (villageZ - centerZ) * (villageZ - centerZ);
                    
                    if (distSq < searchRadius * searchRadius) {
                        Map<String, Integer> village = new HashMap<>();
                        village.put("x", villageX);
                        village.put("z", villageZ);
                        villages.add(village);
                    }
                }
            }
        }
        
        villages.sort((a, b) -> {
            int distA = (a.get("x") - centerX) * (a.get("x") - centerX) + 
                       (a.get("z") - centerZ) * (a.get("z") - centerZ);
            int distB = (b.get("x") - centerX) * (b.get("x") - centerX) + 
                       (b.get("z") - centerZ) * (b.get("z") - centerZ);
            return Integer.compare(distA, distB);
        });
        
        return villages;
    }
}
