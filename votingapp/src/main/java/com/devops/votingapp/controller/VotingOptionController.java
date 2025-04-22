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
    public ResponseEntity<Void> init(@RequestBody List<VotingOption> options) {
        service.initializeOptions(options);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<VotingOption> getAll() {
        return service.getAllOptions();
    }
}
