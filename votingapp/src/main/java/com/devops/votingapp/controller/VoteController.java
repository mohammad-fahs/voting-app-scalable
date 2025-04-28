package com.devops.votingapp.controller;

import com.devops.votingapp.service.VotingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VotingService service;


    @PostMapping
    public ResponseEntity<String> vote(@RequestParam String optionId, @RequestParam String nationalId) {
        try{
            log.info("Vote Requested for {}",nationalId);
            service.vote(optionId, nationalId);
            return ResponseEntity.ok("Vote cast successfully");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body("Error Occurred");
        }

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
