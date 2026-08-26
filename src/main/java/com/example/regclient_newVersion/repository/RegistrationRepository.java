package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.Registration;
import com.example.regclient_newVersion.dataMigration.entity.VoterRegDetailsServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, String> {
    long countByCreatedAtAfter(LocalDateTime dateTime);
    List<Registration> findTop10ByOrderByCreatedAtDesc();

    List<Registration> findAllByStatus(String aNew);
}
