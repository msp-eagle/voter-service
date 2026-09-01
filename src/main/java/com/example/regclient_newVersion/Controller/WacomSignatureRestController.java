package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.Service.WacomSignatureService;
import com.example.regclient_newVersion.dto.SignatureResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signature")
@CrossOrigin
public class WacomSignatureRestController {

    private final WacomSignatureService signatureService;

    public WacomSignatureRestController(WacomSignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/capture")
    public ResponseEntity<SignatureResponse> captureSignature() {
        

        try {
            SignatureResponse response =
                    signatureService.captureSignature();

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            SignatureResponse response = new SignatureResponse(
                    false,
                    "Signature capture failed: " + e.getMessage(),
                    null
            );

            return ResponseEntity.internalServerError().body(response);
        }
    }
}

