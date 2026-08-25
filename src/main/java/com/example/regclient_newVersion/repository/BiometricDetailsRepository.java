package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.BiometricDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiometricDetailsRepository extends JpaRepository<BiometricDetails, String> {
}
