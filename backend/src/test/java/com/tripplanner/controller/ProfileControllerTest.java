package com.tripplanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripplanner.dto.request.ProfileRequest;
import com.tripplanner.dto.response.ProfileResponse;
import com.tripplanner.exception.GlobalExceptionHandler;
import com.tripplanner.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createProfile_shouldReturn201WhenValidRequest() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setOccupation("Designer");
        request.setHomeCity("Mumbai");
        request.setTravelStyle(com.tripplanner.util.TravelStyle.BEACH);
        request.setBudgetPreference(com.tripplanner.util.BudgetPreference.MODERATE);
        request.setPreferredTransport(com.tripplanner.util.PreferredTransport.TRAIN);

        ProfileResponse response = new ProfileResponse();
        response.setId(UUID.randomUUID());
        response.setUserId(UUID.randomUUID());
        response.setOccupation("Designer");
        response.setHomeCity("Mumbai");
        response.setTravelStyle(com.tripplanner.util.TravelStyle.BEACH);
        response.setBudgetPreference(com.tripplanner.util.BudgetPreference.MODERATE);
        response.setPreferredTransport(com.tripplanner.util.PreferredTransport.TRAIN);

        when(profileService.createProfile(any(ProfileRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.homeCity").value("Mumbai"))
                .andExpect(jsonPath("$.travelStyle").value("BEACH"));
    }

    @Test
    void getMyProfile_shouldReturn200() throws Exception {
        ProfileResponse response = new ProfileResponse();
        response.setId(UUID.randomUUID());
        response.setUserId(UUID.randomUUID());
        response.setOccupation("Engineer");
        response.setHomeCity("Delhi");
        response.setTravelStyle(com.tripplanner.util.TravelStyle.ADVENTURE);
        response.setBudgetPreference(com.tripplanner.util.BudgetPreference.BUDGET);
        response.setPreferredTransport(com.tripplanner.util.PreferredTransport.ANY);

        when(profileService.getCurrentProfile()).thenReturn(response);

        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeCity").value("Delhi"))
                .andExpect(jsonPath("$.travelStyle").value("ADVENTURE"));
    }

    @Test
    void updateMyProfile_shouldReturn200() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setOccupation("Architect");
        request.setHomeCity("Pune");
        request.setTravelStyle(com.tripplanner.util.TravelStyle.NATURE);
        request.setBudgetPreference(com.tripplanner.util.BudgetPreference.PREMIUM);
        request.setPreferredTransport(com.tripplanner.util.PreferredTransport.FLIGHT);

        ProfileResponse response = new ProfileResponse();
        response.setId(UUID.randomUUID());
        response.setUserId(UUID.randomUUID());
        response.setOccupation("Architect");
        response.setHomeCity("Pune");
        response.setTravelStyle(com.tripplanner.util.TravelStyle.NATURE);
        response.setBudgetPreference(com.tripplanner.util.BudgetPreference.PREMIUM);
        response.setPreferredTransport(com.tripplanner.util.PreferredTransport.FLIGHT);

        when(profileService.updateCurrentProfile(any(ProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeCity").value("Pune"))
                .andExpect(jsonPath("$.preferredTransport").value("FLIGHT"));
    }
}
