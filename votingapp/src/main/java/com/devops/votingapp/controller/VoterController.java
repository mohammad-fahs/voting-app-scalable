package com.devops.votingapp.controller;

import com.devops.votingapp.model.Voter;
import com.devops.votingapp.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voters")
@RequiredArgsConstructor
public class VoterController {

    private final VoterService service;


    @PostMapping("/import")
    public ResponseEntity<Void> importVoters(@RequestBody List<Voter> voters) {
        service.importVoters(voters);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Voter> getAll() {
        return service.getAllVoters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voter> getVoter(@PathVariable String id) {
        return service.getVoter(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
