package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.PlaceRequest;
import com.dev.springbootserver.dto.response.PlaceResponse;
import com.dev.springbootserver.dto.response.PlaceWithFavoriteResponse;
import com.dev.springbootserver.errors.ResourceNotFoundException;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.*;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.PlaceRepository;
import com.dev.springbootserver.repository.SchoolRepository;
import com.dev.springbootserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessagesComponent messages;

    @GetMapping("/allPlacesBySchool")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listAllPlacesBySchoolId(@RequestParam(value = "schoolId") Long schoolId) {
        School school = getSchoolById(schoolId);

        List<PlaceResponse> placeResponseList = new ArrayList<>();
        List<Place> places = placeRepository.findAllBySchool(school);

        places.forEach(place ->
                placeResponseList.add(new PlaceResponse(
                        place.getId(),
                        place.getName(),
                        place.getType().name(),
                        place.getMaxPeople(),
                        place.getLimitTimeSeconds()
                ))
        );

        return ResponseEntity.ok(placeResponseList);
    }

    @GetMapping("/allPlacesWithFavoriteBySchool")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listAllPlacesBySchoolId(@RequestParam(value = "schoolId") Long schoolId,
                                                     @RequestParam(value = "username") String username) {
        School school = getSchoolById(schoolId);
        List<PlaceWithFavoriteResponse> placeWithFavoriteResponses = new ArrayList<>();
        List<Place> favoritePlaces = getFavoritePLacesBySchoolIdAndUsername(schoolId, username);
        List<Place> places = placeRepository.findAllBySchool(school);
        places.removeAll(favoritePlaces);

        places.forEach(place ->
                placeWithFavoriteResponses.add(new PlaceWithFavoriteResponse(
                        place.getId(),
                        place.getName(),
                        place.getType().name(),
                        place.getMaxPeople(),
                        place.getLimitTimeSeconds(),
                        false
                ))
        );

        favoritePlaces.forEach(place ->
                placeWithFavoriteResponses.add(new PlaceWithFavoriteResponse(
                        place.getId(),
                        place.getName(),
                        place.getType().name(),
                        place.getMaxPeople(),
                        place.getLimitTimeSeconds(),
                        true
                ))
        );

        return ResponseEntity.ok(placeWithFavoriteResponses);
    }

    @GetMapping("/allFavoritePlacesBySchool")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listAllFavoritePlacesBySchoolId(@RequestParam(value = "schoolId") Long schoolId,
                                                             @RequestParam(value = "username") String username) {
        List<PlaceWithFavoriteResponse> placeWithFavoriteResponses = new ArrayList<>();
        List<Place> favoritePlaces = getFavoritePLacesBySchoolIdAndUsername(schoolId, username);

        favoritePlaces.forEach(place ->
                placeWithFavoriteResponses.add(new PlaceWithFavoriteResponse(
                        place.getId(),
                        place.getName(),
                        place.getType().name(),
                        place.getMaxPeople(),
                        place.getLimitTimeSeconds(),
                        true
                ))
        );

        return ResponseEntity.ok(placeWithFavoriteResponses);
    }

    @PostMapping("/addPlace")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPlace(@RequestBody PlaceRequest placeRequest) {

        if (isInvalidPlaceName(placeRequest.getPlaceName())) {
            return badRequest(messages.get("EMPTY_PLACE_NAME"));
        }

        boolean doesPlaceExist = checkPlaceExists(placeRequest.getPlaceName(), placeRequest.getPlaceSchoolId());

        if (doesPlaceExist) {
            return badRequest(messages.get("PlACE_NAME_ALREADY_EXISTS_IN_SCHOOL"));
        }

        int minimumTime = 60, minimumOccupation = 1;

        if (placeRequest.getPlaceLimitTimeSeconds() < minimumTime) {
            return badRequest(messages.get("TIME_LIMIT_T00_LITTLE"));
        }
        if (placeRequest.getPlaceMaxPeople() < minimumOccupation) {
            return badRequest(messages.get("MAX_PEOPLE_LESS_THAN_1"));
        }

        Place place = new Place(placeRequest.getPlaceName(), EPlace.valueOf(placeRequest.getPlaceType()), 0,
                placeRequest.getPlaceMaxPeople(), placeRequest.getPlaceLimitTimeSeconds(), getSchoolById(placeRequest.getPlaceSchoolId()));

        placeRepository.save(place);

        return ResponseEntity.ok(
                new PlaceResponse(
                        place.getId(),
                        place.getName(),
                        place.getType().name(),
                        place.getMaxPeople(),
                        place.getLimitTimeSeconds()
                )
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePlace(@RequestBody PlaceRequest placeRequest) {
        Place place = getPlaceById(placeRequest.getPlaceId());

        int minimumTime = 60, minimumOccupation = 1;

        if (isInvalidPlaceName(placeRequest.getPlaceName())) {
            return badRequest(messages.get("EMPTY_PLACE_NAME"));
        }

        if (!place.getName().equals(placeRequest.getPlaceName())) {
            if (checkPlaceExists(placeRequest.getPlaceName(), place.getSchool().getId())) {
                return badRequest(messages.get("PlACE_NAME_ALREADY_EXISTS_IN_SCHOOL"));
            }
        }

        if (placeRequest.getPlaceLimitTimeSeconds() < minimumTime) {
            return badRequest(messages.get("TIME_LIMIT_TOO_LITTLE"));
        }

        if (placeRequest.getPlaceMaxPeople() < minimumOccupation) {
            return badRequest(messages.get("MAX_PEOPLE_LESS_THAN_1"));
        }

        place.setName(placeRequest.getPlaceName());
        place.setLimitTimeSeconds(placeRequest.getPlaceLimitTimeSeconds());
        place.setMaxPeople(placeRequest.getPlaceMaxPeople());
        place.setType(EPlace.valueOf(placeRequest.getPlaceType()));

        placeRepository.save(place);
        return ResponseEntity.ok(new MessageResponse(messages.get("PLACE_UPDATED_SUCCESS")));
    }

    @DeleteMapping("/removePlace")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removePlace(@RequestParam(value = "placeId") Long placeId) {
        Place place = getPlaceById(placeId);
        Set<User> users = place.getFavoriteUsers();

        for (User user : users) {
            user.getFavoritePlaces().remove(place);
            userRepository.save(user);
        }

        placeRepository.delete(place);

        return ResponseEntity.ok(new MessageResponse(
                MessageFormat.format(messages.get("PLACE_DELETED"), place.getName()))
        );
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(message));
    }

    private boolean isInvalidPlaceName(String placeName) {
        return placeName == null || placeName.isEmpty();
    }

    private School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_SCHOOL_ID")));
    }

    private Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_PLACE_ID")));
    }

    private boolean checkPlaceExists(String name, Long schoolId) {
        return placeRepository.existsByNameAndSchoolId(name, schoolId);
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
}
