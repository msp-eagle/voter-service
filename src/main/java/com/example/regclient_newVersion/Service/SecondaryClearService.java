package com.example.regclient_newVersion.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class SecondaryClearService {

    @PersistenceContext(unitName = "secondary")
    private EntityManager entityManager;

    @Transactional("secondaryTransactionManager")
    public void clearData() {

        entityManager.createNativeQuery(
                "TRUNCATE TABLE app_demo, " +
                "app_fp_left, " +
                "app_fp_right, " +
                "app_photo, " +
                "app_sign " +
                "RESTART IDENTITY CASCADE"
        ).executeUpdate();
    }
}