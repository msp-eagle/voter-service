package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.dto.DocumentTypeDTO;
import com.example.regclient_newVersion.Service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-types")
@CrossOrigin(origins = "*")
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @Autowired
    public DocumentTypeController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentTypeDTO>> getActiveDocumentTypes() {
        List<DocumentTypeDTO> list = documentTypeService.getActiveDocumentTypes();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentTypeDTO>> getAllDocumentTypes() {
        List<DocumentTypeDTO> list = documentTypeService.getAllDocumentTypes();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<DocumentTypeDTO> getDocumentTypeByCode(@PathVariable String code) {
        return documentTypeService.getDocumentTypeByCode(code)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
