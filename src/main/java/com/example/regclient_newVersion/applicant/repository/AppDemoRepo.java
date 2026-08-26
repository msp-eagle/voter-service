package com.example.regclient_newVersion.applicant.repository;

import com.example.regclient_newVersion.applicant.entity.AppDemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDemoRepo extends JpaRepository<AppDemo, String> {
}
