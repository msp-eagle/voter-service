package com.example.regclient_newVersion.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class PrimaryClearService {

    @PersistenceContext(unitName = "primary")
    private EntityManager entityManager;

    @Transactional("primaryTransactionManager")
    public void clearData() {

        entityManager.createNativeQuery(
                "TRUNCATE TABLE voter_reg_details, biometric_details " +
                "RESTART IDENTITY CASCADE"
        ).executeUpdate();
    }
}