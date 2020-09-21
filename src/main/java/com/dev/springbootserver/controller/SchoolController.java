package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.SchoolRequest;
import com.dev.springbootserver.dto.response.SchoolResponse;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.School;
import com.dev.springbootserver.model.User;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.SchoolRepository;
import com.dev.springbootserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/school")
public class SchoolController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    MessagesComponent messages;

    @GetMapping("/allSchoolsByUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> listAllSchoolsByUsername(@RequestBody String username) {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            return badRequest(messages.get("USER_NOT_FOUND"));
        }

        User user = optionalUser.get();

        List<SchoolResponse> schoolResponses = new ArrayList<>();

        user.getSchoolsTeacher().forEach(school -> schoolResponses.add(
                new SchoolResponse(
                        school.getId(),
                        school.getName(),
                        school.getRepresentativeUser().getUsername()
                )));

        return ResponseEntity.ok(schoolResponses);
    }

    @GetMapping("/adminSchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAdminSchoolByUsername(@RequestBody String username) {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            return badRequest(messages.get("USER_NOT_FOUND"));
        }

        User user = optionalUser.get();

        Optional<School> school = schoolRepository.findSchoolByRepresentativeUser(user);

        if (school.isPresent()) {
            return ResponseEntity.ok(
                    new SchoolResponse(
                            school.get().getId(),
                            school.get().getName(),
                            school.get().getRepresentativeUser().getUsername()
                    )
            );
        } else {
            return badRequest(messages.get("ADMIN_WITHOUT_SCHOOL"));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSchool(@RequestBody SchoolRequest schoolRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(schoolRequest.getSchoolPrincipalUsername());

        if (!optionalUser.isPresent()) {
            return badRequest(messages.get("USER_NOT_FOUND"));
        }

        User user = optionalUser.get();

        if (schoolRepository.existsByRepresentativeUser(user)) {
            return badRequest(messages.get("ADMIN_WITH_SCHOOL"));
        }

        if (isInvalidSchoolName(schoolRequest.getSchoolName())) {
            return badRequest(messages.get("EMPTY_SCHOOL_NAME"));
        }

        if (schoolRepository.existsByName(schoolRequest.getSchoolName())) {
            return badRequest(messages.get("SCHOOL_NAME_ALREADY_TAKEN"));
        }

        School school = new School(schoolRequest.getSchoolName(), user);
        schoolRepository.save(school);

        return ResponseEntity.ok(new MessageResponse(messages.get("SCHOOL_REGISTERED_SUCCESS")));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSchool(@RequestBody SchoolRequest schoolRequest) {

        Optional<School> schoolOptional = schoolRepository.findById(schoolRequest.getSchoolId());

        if (!schoolOptional.isPresent()) {
            return badRequest(messages.get("INVALID_SCHOOL_ID"));
        }

        School school = schoolOptional.get();

        if (school.getName().equals(schoolRequest.getSchoolName())) {
            return ResponseEntity.ok(new MessageResponse(messages.get("SCHOOL_UPDATED_SUCCESS_WITHOUT_CHANGES")));
        } else {
            if (isInvalidSchoolName(schoolRequest.getSchoolName())) {
                return badRequest(messages.get("EMPTY_SCHOOL_NAME"));
            }

            if (schoolRepository.existsByName(schoolRequest.getSchoolName())) {
                return badRequest(messages.get("SCHOOL_NAME_ALREADY_TAKEN"));
            }

            school.setName(schoolRequest.getSchoolName());
            schoolRepository.save(school);
            return ResponseEntity.ok(new MessageResponse(messages.get("SCHOOL_UPDATED_SUCCESS")));
        }
    }

    private boolean isInvalidSchoolName(String schoolName) {
        return schoolName == null || schoolName.isEmpty();
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(message));
    }
}
