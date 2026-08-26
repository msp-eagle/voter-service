package com.example.regclient_newVersion.Controller;


import com.example.regclient_newVersion.applicant.entity.DocTable;
import com.example.regclient_newVersion.dto.VoterSearchResponse;
import com.example.regclient_newVersion.dto.VoterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voters")
@CrossOrigin("*")
public class VoterController {

    private final VoterService voterService;

    public VoterController(VoterService voterService) {
        this.voterService = voterService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoterSearchResponse> searchVoter(
            @PathVariable String id) {

        VoterSearchResponse response =
                voterService.searchVoter(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<DocTable> getById(@PathVariable String id){
        return voterService.getById(id);
    }




}