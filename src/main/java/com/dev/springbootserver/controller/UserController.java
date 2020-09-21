package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.UserSchoolRequest;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.ERole;
import com.dev.springbootserver.model.Role;
import com.dev.springbootserver.model.School;
import com.dev.springbootserver.model.User;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.SchoolRepository;
import com.dev.springbootserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    MessagesComponent messages;

    @PostMapping("/addSchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addTeacher(@RequestBody UserSchoolRequest userSchoolRequest) {

        Optional<School> schoolOptional = schoolRepository.findById(userSchoolRequest.getSchoolId());
        Optional<User> userOptional = userRepository.findByUsername(userSchoolRequest.getUsername());

        if (!schoolOptional.isPresent()) {
            return badRequest(messages.get("INVALID_SCHOOL_ID"));
        }

        if (!userOptional.isPresent()) {
            return badRequest(messages.get("USER_NOT_FOUND"));
        }

        School school = schoolOptional.get();
        User user = userOptional.get();

        for (Role role : user.getRoles()) {
            if (role.getName().equals(ERole.ROLE_ADMIN)) {
                return badRequest(messages.get("USER_PRINCIPAL_SCHOOL"));
            }
        }

        user.getSchoolsTeacher().add(school);
        userRepository.save(user);

        return ResponseEntity.ok(
                new MessageResponse(
                        MessageFormat.format(messages.get("ADD_USER_TO_SCHOOL"), user.getUsername())
                )
        );
    }

    @DeleteMapping("/removeSchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeTeacher(@RequestBody UserSchoolRequest userSchoolRequest) {

        Optional<User> userOptional = userRepository.findByUsername(userSchoolRequest.getUsername());
        Optional<School> schoolOptional = schoolRepository.findById(userSchoolRequest.getSchoolId());

        if (!schoolOptional.isPresent()) {
            return badRequest(messages.get("INVALID_SCHOOL_ID"));
        }

        if (!userOptional.isPresent()) {
            return badRequest(messages.get("USER_NOT_FOUND"));
        }

        School school = schoolOptional.get();
        User user = userOptional.get();

        String message = "SCHOOL_WITHOUT_USER";

        if (user.getSchoolsTeacher().contains(school)) {
            user.getSchoolsTeacher().remove(school);
            userRepository.save(user);

            message = "DELETE_USER_FROM_SCHOOL";
        }

        return ResponseEntity.ok(
                new MessageResponse(
                        MessageFormat.format(messages.get(message), user.getUsername())
                )
        );
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(message));
    }
}
