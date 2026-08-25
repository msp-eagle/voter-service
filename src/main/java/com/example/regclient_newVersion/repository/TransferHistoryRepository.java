package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.TransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {

    List<TransferHistory> findAllByOrderByCreatedAtDesc();

    List<TransferHistory> findByWorkstationIpOrderByCreatedAtDesc(String workstationIp);
}
