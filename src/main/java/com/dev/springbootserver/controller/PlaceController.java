package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.PlaceRequest;
import com.dev.springbootserver.dto.response.PlaceResponse;
import com.dev.springbootserver.errors.ResourceNotFoundException;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.*;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.PlaceRepository;
import com.dev.springbootserver.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/place")
public class PlaceController {

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    SchoolRepository schoolRepository;

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
                        place.getLimitTimeSeconds(),
                        place.getSchool().getId()
                ))
        );

        return ResponseEntity.ok(placeResponseList);
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
                new MessageResponse(MessageFormat.format(messages.get("ADD_PLACE_TO_SCHOOL"), place.getName()))
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
    public ResponseEntity<?> removePlace(@RequestBody PlaceRequest placeRequest) {

        Place place = getPlaceByNameAndSchoolId(placeRequest.getPlaceName(), placeRequest.getPlaceSchoolId());

        placeRepository.delete(place);

        return ResponseEntity.ok(new MessageResponse(messages.get("PLACE_DELETED")));
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

    private Place getPlaceByNameAndSchoolId(String name, Long schoolId) {
        return placeRepository.findByNameAndSchoolId(name, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_PLACE_NAME")));
    }

    private boolean checkPlaceExists(String name, Long schoolId) {
        return placeRepository.existsByNameAndSchoolId(name, schoolId);
    }
}
