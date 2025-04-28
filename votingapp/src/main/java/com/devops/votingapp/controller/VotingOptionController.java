package com.devops.votingapp.controller;

import com.devops.votingapp.model.VotingOption;
import com.devops.votingapp.service.VotingOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class VotingOptionController {

    private final VotingOptionService service;

    @PostMapping("/init")
    public ResponseEntity<List<VotingOption>> init(@RequestBody List<VotingOption> options) {
        List<VotingOption> savedOptions = service.initializeOptions(options);
        return ResponseEntity.ok(savedOptions);
    }

    @GetMapping
    public List<VotingOption> getAll() {
        return service.getAllOptions();
    }
}
