package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.dto.DocumentTypeDTO;
import com.example.regclient_newVersion.Model.DocumentType;
import com.example.regclient_newVersion.repository.DocumentTypeRepository;
import com.example.regclient_newVersion.Service.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public DocumentTypeServiceImpl(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Override
    public List<DocumentTypeDTO> getAllDocumentTypes() {
        return documentTypeRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentTypeDTO> getActiveDocumentTypes() {
        return documentTypeRepository.findByIsActiveTrueOrderByCodeAsc().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DocumentTypeDTO> getDocumentTypeByCode(String code) {
        return documentTypeRepository.findById(code)
                .map(this::mapEntityToDto);
    }

    private DocumentTypeDTO mapEntityToDto(DocumentType entity) {
        return new DocumentTypeDTO(
                entity.getCode(),
                entity.getName(),
                entity.getDescr(),
                entity.getLangCode(),
                entity.getIsActive()
        );
    }
}
