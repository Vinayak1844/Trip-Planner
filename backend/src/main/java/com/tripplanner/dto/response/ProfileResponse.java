package com.tripplanner.dto.response;

import com.tripplanner.util.BudgetPreference;
import com.tripplanner.util.PreferredTransport;
import com.tripplanner.util.TravelStyle;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "User profile response")
public class ProfileResponse {

    private UUID id;
    private UUID userId;
    private String occupation;
    private String homeCity;
    private TravelStyle travelStyle;
    private BudgetPreference budgetPreference;
    private PreferredTransport preferredTransport;
    private Instant createdAt;
    private Instant updatedAt;

    public ProfileResponse() {
    }

    public ProfileResponse(UUID id, UUID userId, String occupation, String homeCity,
                           TravelStyle travelStyle, BudgetPreference budgetPreference,
                           PreferredTransport preferredTransport, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.occupation = occupation;
        this.homeCity = homeCity;
        this.travelStyle = travelStyle;
        this.budgetPreference = budgetPreference;
        this.preferredTransport = preferredTransport;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public TravelStyle getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(TravelStyle travelStyle) {
        this.travelStyle = travelStyle;
    }

    public BudgetPreference getBudgetPreference() {
        return budgetPreference;
    }

    public void setBudgetPreference(BudgetPreference budgetPreference) {
        this.budgetPreference = budgetPreference;
    }

    public PreferredTransport getPreferredTransport() {
        return preferredTransport;
    }

    public void setPreferredTransport(PreferredTransport preferredTransport) {
        this.preferredTransport = preferredTransport;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
