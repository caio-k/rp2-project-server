package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.SchoolRequest;
import com.dev.springbootserver.dto.response.SchoolResponse;
import com.dev.springbootserver.dto.response.UserResponse;
import com.dev.springbootserver.errors.ResourceNotFoundException;
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
    public ResponseEntity<?> listAllSchoolsByUsername(@RequestParam(value = "username") String username) {

        User user = getUserByUsername(username);

        List<SchoolResponse> schoolResponses = new ArrayList<>();

        user.getSchoolsTeacher().forEach(school -> schoolResponses.add(
                new SchoolResponse(
                        school.getId(),
                        school.getName(),
                        school.getRepresentativeUser().getUsername()
                )));

        return ResponseEntity.ok(schoolResponses);
    }

    @GetMapping("/allUsersBySchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAllUsersBySchool(@RequestParam(value = "schoolId") Long schoolId) {
        School school = getSchoolById(schoolId);
        List<UserResponse> userResponses = new ArrayList<>();

        school.getSchoolTeachers().forEach(user ->
                userResponses.add(new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
        );

        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/adminSchool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAdminSchoolByUsername(@RequestParam(value = "username") String username) {

        User user = getUserByUsername(username);

        School school = schoolRepository.findSchoolByRepresentativeUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("ADMIN_WITHOUT_SCHOOL")));

        return ResponseEntity.ok(
                new SchoolResponse(
                        school.getId(),
                        school.getName(),
                        school.getRepresentativeUser().getUsername()
                )
        );
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSchool(@RequestBody SchoolRequest schoolRequest) {

        User user = getUserByUsername(schoolRequest.getSchoolPrincipalUsername());

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

        return ResponseEntity.ok(
                new SchoolResponse(
                        school.getId(),
                        school.getName(),
                        school.getRepresentativeUser().getUsername()
                )
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSchool(@RequestBody SchoolRequest schoolRequest) {

        School school = getSchoolById(schoolRequest.getSchoolId());

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

    private School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_SCHOOL_ID")));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("USER_NOT_FOUND")));
    }
}
