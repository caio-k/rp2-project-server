package com.dev.springbootserver.controller;

import com.dev.springbootserver.dto.request.ExitRequest;
import com.dev.springbootserver.dto.response.ExitLogResponse;
import com.dev.springbootserver.dto.response.ExitResponse;
import com.dev.springbootserver.errors.ResourceNotFoundException;
import com.dev.springbootserver.messages.MessagesComponent;
import com.dev.springbootserver.model.*;
import com.dev.springbootserver.payload.response.MessageResponse;
import com.dev.springbootserver.repository.ExitRepository;
import com.dev.springbootserver.repository.SchoolRepository;
import com.dev.springbootserver.repository.UserRepository;
import com.dev.springbootserver.repository.ExitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/exit")
public class ExitController {

    @Autowired
    ExitRepository exitRepository;

    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExitLogRepository exitLogRepository;

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

        return ResponseEntity.ok(new ExitResponse(exit.getId(), exit.getName()));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateExit(@RequestBody ExitRequest exitRequest) {
        if (isInvalidExitName(exitRequest.getExitName())) {
            return badRequest(messages.get("EMPTY_EXIT_NAME"));
        }

        Exit exit = getExitById(exitRequest.getExitId());

        if (!exit.getName().equals(exitRequest.getExitName())) {
            if (checkExitExists(exitRequest.getExitName(), exit.getSchool().getId())) {
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

        return ResponseEntity.ok(new MessageResponse(
                MessageFormat.format(messages.get("EXIT_DELETED"), exit.getName()))
        );
    }

    @PostMapping("/addExitLog")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addExitLog(@RequestParam(value = "username") String username,
                                        @RequestParam(value = "exitId") Long exitId) {
        Exit exit = getExitById(exitId);
        Set<ExitLog> exitLogs = exit.getExitLogs();

        Optional<ExitLog> exitLogOptional = exitLogs.stream().filter(o -> o.getUser().getUsername().equals(username)).findFirst();

        ExitLog exitLog;
        if (exitLogOptional.isPresent()) {
            exitLog = exitLogOptional.get();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            exitLog.setTimestamp(timestamp.getTime());
        } else {
            String message = "The teacher " + username + " ended his class at ";

            User user = getUserByUsername(username);
            ExitLogKey exitLogKey = new ExitLogKey(user.getId(), exitId);
            exitLog = new ExitLog(user, exit, message, System.currentTimeMillis());
            exitLog.setId(exitLogKey);
        }

        exitLogRepository.save(exitLog);
        return ResponseEntity.ok(new ExitLogResponse(createFullMessage(exitLog.getMessage(), exitLog.getTimestamp())));
    }

    @GetMapping("/allValidTimestampsByExitId")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> allValidTimestampsByExitId(@RequestParam(value = "exitId") Long exitId) {
        List<ExitLogResponse> exitLogResponses = new ArrayList<>();

        Exit exit = getExitById(exitId);
        List<ExitLog> exitLogs = new ArrayList<>(exit.getExitLogs());

        exitLogs.sort(Comparator.comparingLong(ExitLog::getTimestamp));
        LocalDate today = new Timestamp(System.currentTimeMillis()).toLocalDateTime().toLocalDate();

        for (ExitLog exitLog : exitLogs) {
            LocalDate day = new Timestamp(exitLog.getTimestamp()).toLocalDateTime().toLocalDate();

            if (day.equals(today)) {
                exitLogResponses.add(new ExitLogResponse(createFullMessage(exitLog.getMessage(), exitLog.getTimestamp())));
            }
        }

        return ResponseEntity.ok(exitLogResponses);
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

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(messages.get("USER_NOT_FOUND")));
    }

    private String createFullMessage(String message, Long timestamp) {
        LocalTime localTime = new Timestamp(timestamp).toLocalDateTime().toLocalTime();

        return message + localTime.toString().substring(0, 5);
    }
}
