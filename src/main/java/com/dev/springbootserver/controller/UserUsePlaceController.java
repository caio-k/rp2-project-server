package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.UserUsePlaceRequest;
import com.dev.springbootserver.dto.response.UserUsePlaceResponse;
import com.dev.springbootserver.errors.ResourceNotFoundException;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.*;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.PlaceRepository;
import com.dev.springbootserver.repository.SchoolRepository;
import com.dev.springbootserver.repository.UserRepository;
import com.dev.springbootserver.repository.UserUsePlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/usePlace")
public class UserUsePlaceController {

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserUsePlaceRepository userUsePlaceRepository;

    @Autowired
    MessagesComponent messages;

    @GetMapping("/allUses")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listAllUsesByCategoryAndSchoolId(@RequestParam(value = "category") String category,
                                                              @RequestParam(value = "schoolId") Long schoolId) {
        School school = getSchoolById(schoolId);
        List<Place> places = placeRepository.findAllBySchoolAndType(school, EPlace.valueOf(category));
        return getUserUsePlaceByPlaces(places);
    }

    @GetMapping("/allUsesFavoritePlaces")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> allUsesOfFavoritePlacesByUsernameAndSchoolId(@RequestParam(value = "username") String username,
                                                                          @RequestParam(value = "schoolId") Long schoolId) {
        List<Place> favoritePlaces = getFavoritePLacesBySchoolIdAndUsername(schoolId, username);
        return getUserUsePlaceByPlaces(favoritePlaces);
    }

    @PutMapping("/increase")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> increase(@RequestBody UserUsePlaceRequest userUsePlaceRequest) {
        return updateCounter(userUsePlaceRequest, true);
    }

    @PutMapping("/decrease")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> decrease(@RequestBody UserUsePlaceRequest userUsePlaceRequest) {
        return updateCounter(userUsePlaceRequest, false);
    }

    private ResponseEntity<?> updateCounter(UserUsePlaceRequest userUsePlaceRequest, boolean isIncrease) {
        if (userUsePlaceRequest.getNumberOfPeople() <= 0)
            return badRequest(messages.get("NEGATIVE_NUMBER"));

        Place place = getPlaceById(userUsePlaceRequest.getPlaceId());
        User user = getUserByUsername(userUsePlaceRequest.getUsername());

        int totalCounter = userUsePlaceRepository.findAllByPlaceAndCounterGreaterThan(place, 0)
                .stream()
                .mapToInt(UserUsePlace::getCounter)
                .sum();

        Optional<UserUsePlace> optionalUserUsePlace = userUsePlaceRepository.findByPlaceAndUser(place, user);
        UserUsePlace userUsePlace;

        if (optionalUserUsePlace.isPresent()) {
            userUsePlace = optionalUserUsePlace.get();
            int newCounter;

            if (isIncrease) {
                newCounter = userUsePlace.getCounter() + userUsePlaceRequest.getNumberOfPeople();

                if (totalCounter + userUsePlaceRequest.getNumberOfPeople() > place.getMaxPeople())
                    return badRequest(messages.get("SURPLUS_NUMBER"));
            } else {
                newCounter = userUsePlace.getCounter() - userUsePlaceRequest.getNumberOfPeople();

                if (newCounter < 0)
                    return badRequest(messages.get("SURPLUS_NUMBER"));
            }

            userUsePlace.setCounter(newCounter);
            userUsePlace.setLastUpdate(System.currentTimeMillis());
        } else {
            int numberOfPeople = userUsePlaceRequest.getNumberOfPeople();

            if (!isIncrease || totalCounter + numberOfPeople > place.getMaxPeople())
                return badRequest(messages.get("SURPLUS_NUMBER"));

            UserUsePlaceKey userUsePlaceKey = new UserUsePlaceKey(user.getId(), place.getId());
            userUsePlace = new UserUsePlace(user, place, numberOfPeople, System.currentTimeMillis());
            userUsePlace.setId(userUsePlaceKey);
        }

        userUsePlaceRepository.save(userUsePlace);
        return ResponseEntity.ok(
                new UserUsePlaceResponse(
                        userUsePlace.getPlace().getId(),
                        userUsePlace.getUser().getId(),
                        userUsePlace.getCounter(),
                        userUsePlace.getLastUpdate()
                )
        );
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(message));
    }

    private School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_SCHOOL_ID")));
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_PLACE_ID")));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("USER_NOT_FOUND")));
    }

    private List<Place> getFavoritePLacesBySchoolIdAndUsername(Long schoolId, String username) {
        User user = getUserByUsername(username);

        return new ArrayList<>(user.getFavoritePlaces())
                .stream()
                .filter(place -> place.getSchool().getId().equals(schoolId))
                .collect(Collectors.toList());
    }

    private ResponseEntity<?> getUserUsePlaceByPlaces(List<Place> places) {
        List<UserUsePlace> userUsePlaces = new ArrayList<>();
        List<UserUsePlaceResponse> userUsePlaceResponses = new ArrayList<>();

        for (Place place : places) {
            userUsePlaces.addAll(userUsePlaceRepository.findAllByPlaceAndCounterGreaterThan(place, 0));
        }

        for (UserUsePlace userUsePlace : userUsePlaces) {
            userUsePlaceResponses.add(
                    new UserUsePlaceResponse(
                            userUsePlace.getPlace().getId(),
                            userUsePlace.getUser().getId(),
                            userUsePlace.getCounter(),
                            userUsePlace.getLastUpdate()
                    )
            );
        }

        return ResponseEntity.ok(userUsePlaceResponses);
    }
}
