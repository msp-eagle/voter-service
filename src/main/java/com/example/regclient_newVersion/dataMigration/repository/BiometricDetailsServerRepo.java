package com.example.regclient_newVersion.dataMigration.repository;

import com.example.regclient_newVersion.dataMigration.entity.BiometricDetailsServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiometricDetailsServerRepo extends JpaRepository<BiometricDetailsServer,String> {

}
