package com.devops.votingapp.controller;

import com.devops.votingapp.service.VotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VotingService service;


    @PostMapping
    public ResponseEntity<String> vote(@RequestParam String optionId, @RequestParam String nationalId) {
        service.vote(optionId, nationalId);
        return ResponseEntity.ok("Vote cast successfully");
    }

    @GetMapping("/results")
    public Map<String, Long> results() {
        return service.getResults();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearVotesAndVoters() {
        service.clearVotesAndVoters();
        return ResponseEntity.ok("All votes and voters have been cleared.");
    }
}
