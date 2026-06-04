package com.villagelocator.controller;

import com.villagelocator.VillageLocatorApplication;
import com.villagelocator.service.VillageFinderService;
import com.villagelocator.service.VillageFinderService.VillageLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VillageController.class)
@ContextConfiguration(classes = VillageLocatorApplication.class)
class VillageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VillageFinderService villageFinderService;

    @Test
    void findVillagesReturnsWrappedVillageOnlyResponse() throws Exception {
        List<VillageLocation> villages = List.of(
                new VillageLocation(512, 1024, 32, 64)
        );

        when(villageFinderService.findVillagesNear(5975010353295290926L, 0, 0, 50)).thenReturn(villages);

        mockMvc.perform(get("/api/villages")
                        .param("seed", "5975010353295290926")
                        .param("x", "0")
                        .param("z", "0")
                        .param("radius", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.seed").value("5975010353295290926"))
                .andExpect(jsonPath("$.centerX").value(0))
                .andExpect(jsonPath("$.centerZ").value(0))
                .andExpect(jsonPath("$.radiusChunks").value(50))
                .andExpect(jsonPath("$.villageCount").value(1))
                .andExpect(jsonPath("$.villages[0].blockX").value(512))
                .andExpect(jsonPath("$.villages[0].blockZ").value(1024))
                .andExpect(jsonPath("$.villages[0].chunkX").value(32))
                .andExpect(jsonPath("$.villages[0].chunkZ").value(64))
                .andExpect(jsonPath("$.villages[0].villagers").doesNotExist());
    }
}
