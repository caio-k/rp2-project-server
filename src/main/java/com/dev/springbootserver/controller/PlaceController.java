package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.PlaceRequest;
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

    @PostMapping("/addPlace")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addPlace(@RequestBody PlaceRequest placeRequest) {

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

    private School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_SCHOOL_ID")));
    }

    private Place getPlaceByNameAndSchoolId(String name, Long schoolId) {
        return placeRepository.findByNameAndSchoolId(name, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_PLACE_NAME")));
    }

    private boolean checkPlaceExists(String name, Long schoolId) {
        return placeRepository.existsByNameAndSchoolId(name, schoolId);
    }
}
