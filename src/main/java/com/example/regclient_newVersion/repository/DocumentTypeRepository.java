package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, String> {
    List<DocumentType> findByIsActiveTrue();
    List<DocumentType> findByIsActiveTrueOrderByCodeAsc();
    List<DocumentType> findByLangCodeAndIsActiveTrue(String langCode);
}
