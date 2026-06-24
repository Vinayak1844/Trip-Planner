package com.tripplanner.dto.request;

import com.tripplanner.util.BudgetPreference;
import com.tripplanner.util.PreferredTransport;
import com.tripplanner.util.TravelStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Profile creation and update request")
public class ProfileRequest {

    @Schema(description = "Current occupation", example = "Software Engineer")
    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @NotBlank(message = "Home city is required")
    @Size(min = 2, max = 100, message = "Home city must be between 2 and 100 characters")
    @Schema(description = "Home city", example = "Bangalore")
    private String homeCity;

    @NotNull(message = "Travel style is required")
    @Schema(description = "Travel style preference", example = "ADVENTURE")
    private TravelStyle travelStyle;

    @NotNull(message = "Budget preference is required")
    @Schema(description = "Budget preference", example = "MODERATE")
    private BudgetPreference budgetPreference;

    @NotNull(message = "Preferred transport is required")
    @Schema(description = "Preferred transport mode", example = "TRAIN")
    private PreferredTransport preferredTransport;

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
}
