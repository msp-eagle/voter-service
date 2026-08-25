package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.DocumentTypeDTO;

import java.util.List;
import java.util.Optional;

public interface DocumentTypeService {
    List<DocumentTypeDTO> getAllDocumentTypes();
    List<DocumentTypeDTO> getActiveDocumentTypes();
    Optional<DocumentTypeDTO> getDocumentTypeByCode(String code);
}
