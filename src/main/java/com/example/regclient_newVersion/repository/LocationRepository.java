package com.example.regclient_newVersion.repository;

import com.example.regclient_newVersion.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    List<Location> findByParentLocCode(String parentLocCode);

    List<Location> findByParentLocCodeAndIsActiveTrue(String parentLocCode);

    List<Location> findByParentLocCodeIsNullAndIsActiveTrue();

    @Query("SELECT l FROM Location l WHERE (l.parentLocCode IS NULL OR l.parentLocCode = '' OR l.parentLocCode = 'null') AND (l.isActive IS NULL OR l.isActive = true)")
    List<Location> findRootLocations();

    List<Location> findByHierarchyLevelAndIsActiveTrue(Integer hierarchyLevel);

    List<Location> findByHierarchyNameIgnoreCaseAndIsActiveTrue(String hierarchyName);

    List<Location> findByIsActiveTrue();
}
