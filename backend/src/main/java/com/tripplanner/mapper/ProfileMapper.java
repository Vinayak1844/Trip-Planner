package com.tripplanner.mapper;

import com.tripplanner.dto.request.ProfileRequest;
import com.tripplanner.dto.response.ProfileResponse;
import com.tripplanner.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getOccupation(),
                profile.getHomeCity(),
                profile.getTravelStyle(),
                profile.getBudgetPreference(),
                profile.getPreferredTransport(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    public Profile toEntity(ProfileRequest request) {
        Profile profile = new Profile();
        profile.setOccupation(request.getOccupation());
        profile.setHomeCity(request.getHomeCity());
        profile.setTravelStyle(request.getTravelStyle());
        profile.setBudgetPreference(request.getBudgetPreference());
        profile.setPreferredTransport(request.getPreferredTransport());
        return profile;
    }
}
