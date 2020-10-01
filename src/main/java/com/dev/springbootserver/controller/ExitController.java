package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.ExitRequest;
import com.dev.springbootserver.dto.response.ExitResponse;
import com.dev.springbootserver.errors.ResourceNotFoundException;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.Exit;
import com.dev.springbootserver.model.School;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.ExitRepository;
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
@RequestMapping("/api/exit")
public class ExitController {

    @Autowired
    ExitRepository exitRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    MessagesComponent messages;

    @GetMapping("/allExitsBySchool")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listAllExitsBySchoolId(@RequestParam(value = "schoolId") Long schoolId) {
        School school = getSchoolById(schoolId);

        List<ExitResponse> exitResponses = new ArrayList<>();
        List<Exit> exits = exitRepository.findAllBySchool(school);

        exits.forEach(exit ->
                exitResponses.add(new ExitResponse(
                        exit.getId(),
                        exit.getName()
                ))
        );

        return ResponseEntity.ok(exitResponses);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addExit(@RequestBody ExitRequest exitRequest) {
        if (isInvalidExitName(exitRequest.getExitName())) {
            return badRequest(messages.get("EMPTY_EXIT_NAME"));
        }

        boolean doesExitExits = checkExitExists(exitRequest.getExitName(), exitRequest.getSchoolId());

        if (doesExitExits) {
            return badRequest(messages.get("EXIT_NAME_ALREADY_EXISTS_IN_SCHOOL"));
        }

        Exit exit = new Exit(exitRequest.getExitName(), getSchoolById(exitRequest.getSchoolId()));
        exitRepository.save(exit);

        return ResponseEntity.ok(
                new MessageResponse(MessageFormat.format(messages.get("ADD_EXIT_TO_SCHOOL"), exit.getName()))
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateExit(@RequestBody ExitRequest exitRequest) {
        if (isInvalidExitName(exitRequest.getExitName())) {
            return badRequest(messages.get("EMPTY_EXIT_NAME"));
        }

        Exit exit = getExitById(exitRequest.getExitId());
        School school = getSchoolById(exitRequest.getSchoolId());

        if (!exit.getName().equals(exitRequest.getExitName())) {
            if (checkExitExists(exitRequest.getExitName(), school.getId())) {
                return badRequest(messages.get("EXIT_NAME_ALREADY_EXISTS_IN_SCHOOL"));
            }
        }

        exit.setName(exitRequest.getExitName());
        exitRepository.save(exit);
        return ResponseEntity.ok(new MessageResponse(messages.get("EXIT_UPDATED_SUCCESS")));
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeExit(@RequestParam(value = "exitId") Long exitId) {
        Exit exit = getExitById(exitId);
        exitRepository.delete(exit);
        return ResponseEntity.ok(new MessageResponse(messages.get("EXIT_DELETED")));
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(message));
    }

    private boolean isInvalidExitName(String exitName) {
        return exitName == null || exitName.isEmpty();
    }

    private School getSchoolById(Long id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_SCHOOL_ID")));
    }

    private Exit getExitById(Long exitId) {
        return exitRepository.findById(exitId)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("INVALID_EXIT_ID")));
    }

    private boolean checkExitExists(String name, Long schoolId) {
        return exitRepository.existsByNameAndSchoolId(name, schoolId);
    }
}
