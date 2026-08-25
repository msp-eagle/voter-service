package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.LocationHierarchyList;
import com.example.regclient_newVersion.Model.LocationHierarchyListID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationHierarchyListRepository extends JpaRepository<LocationHierarchyList, LocationHierarchyListID> {
    List<LocationHierarchyList> findByIsActiveTrueOrderByHierarchyLevelAsc();
}
