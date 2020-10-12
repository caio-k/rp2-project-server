package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.UserSchoolRequest;
import com.dev.springbootserver.dto.response.PlaceWithFavoriteResponse;
import com.dev.springbootserver.dto.response.UserResponse;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    MessagesComponent messages;

    @PostMapping("/addSchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSchool(@RequestBody UserSchoolRequest userSchoolRequest) {

        School school = getSchoolById(userSchoolRequest.getSchoolId());
        User user = getUserByUsername(userSchoolRequest.getUsername());

        for (Role role : user.getRoles()) {
            if (role.getName().equals(ERole.ROLE_ADMIN)) {
                return badRequest(messages.get("USER_PRINCIPAL_SCHOOL"));
            }
        }

        if (user.getSchoolsTeacher().contains(school)) {
            return badRequest(messages.get("USER_ALREADY_REGISTERED_IN_SCHOOL"));
        }

        user.getSchoolsTeacher().add(school);
        userRepository.save(user);

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                )
        );
    }

    @DeleteMapping("/removeSchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeSchool(@RequestBody UserSchoolRequest userSchoolRequest) {

        School school = getSchoolById(userSchoolRequest.getSchoolId());
        User user = getUserByUsername(userSchoolRequest.getUsername());

        String message = "SCHOOL_WITHOUT_USER";

        if (user.getSchoolsTeacher().contains(school)) {
            user.getSchoolsTeacher().remove(school);
            userRepository.save(user);

            message = "DELETE_USER_FROM_SCHOOL";
        }

        return ResponseEntity.ok(
                new MessageResponse(MessageFormat.format(messages.get(message), user.getUsername()))
        );
    }

    @PostMapping("/addFavoritePlace")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addFavoritePlace(@RequestParam(value = "username") String username,
                                              @RequestParam(value = "placeId") Long placeId) {
        Place place = getPlaceById(placeId);
        User user = getUserByUsername(username);

        if (user.getFavoritePlaces().contains(place)) {
            return badRequest(messages.get("PLACE_ALREADY_FAVORITE"));
        }

        user.getFavoritePlaces().add(place);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(messages.get("PLACE_ADDED_FAVORITES")));
    }

    @DeleteMapping("/removeFavoritePlace")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeFavoritePlace(@RequestParam(value = "username") String username,
                                                 @RequestParam(value = "placeId") Long placeId) {
        Place place = getPlaceById(placeId);
        User user = getUserByUsername(username);

        if (user.getFavoritePlaces().contains(place)) {
            user.getFavoritePlaces().remove(place);
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse(messages.get("PLACE_REMOVED_FAVORITES")));
        }

        return badRequest(messages.get("PLACE_IS_NOT_FAVORITE"));
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
}
