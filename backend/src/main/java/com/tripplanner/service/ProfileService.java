package com.tripplanner.service;

import com.tripplanner.dto.request.ProfileRequest;
import com.tripplanner.dto.response.ProfileResponse;
import com.tripplanner.entity.Profile;
import com.tripplanner.exception.ProfileAlreadyExistsException;
import com.tripplanner.exception.ResourceNotFoundException;
import com.tripplanner.mapper.ProfileMapper;
import com.tripplanner.repository.ProfileRepository;
import com.tripplanner.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
    }

    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();

        if (profileRepository.existsByUserId(userId)) {
            throw new ProfileAlreadyExistsException(userId);
        }

        Profile profile = profileMapper.toEntity(request);
        profile.setUserId(userId);

        return profileMapper.toResponse(profileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getCurrentProfile() {
        UUID userId = SecurityUtils.getCurrentUserId();
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user"));

        return profileMapper.toResponse(profile);
    }

    @Transactional
    public ProfileResponse updateCurrentProfile(ProfileRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for current user"));

        profile.setOccupation(request.getOccupation());
        profile.setHomeCity(request.getHomeCity());
        profile.setTravelStyle(request.getTravelStyle());
        profile.setBudgetPreference(request.getBudgetPreference());
        profile.setPreferredTransport(request.getPreferredTransport());

        return profileMapper.toResponse(profileRepository.save(profile));
    }
}
