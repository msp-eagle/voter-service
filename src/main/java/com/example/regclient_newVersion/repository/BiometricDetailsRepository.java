package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.BiometricDetails;
import com.example.regclient_newVersion.dataMigration.entity.BiometricDetailsServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiometricDetailsRepository extends JpaRepository<BiometricDetails, String> {

    @Query("SELECT COUNT(b) FROM BiometricDetails b WHERE b.face IS NOT NULL")
    long countByFaceIsNotNull();

    @Query("SELECT COUNT(b) FROM BiometricDetails b WHERE b.leftIris IS NOT NULL OR b.rightIris IS NOT NULL")
    long countByIrisIsNotNull();

    @Query("SELECT COUNT(b) FROM BiometricDetails b WHERE b.leftIndexFinger IS NOT NULL OR b.rightIndexFinger IS NOT NULL OR b.leftThumb IS NOT NULL OR b.rightThumb IS NOT NULL")
    long countByFingerprintsIsNotNull();

    List<BiometricDetails> findAllByStatus(String aNew);
}
