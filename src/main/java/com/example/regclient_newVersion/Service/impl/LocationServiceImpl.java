package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.dto.LocationDTO;
import com.example.regclient_newVersion.dto.LocationHierarchyDTO;
import com.example.regclient_newVersion.dto.LocationHierarchyListDTO;
import com.example.regclient_newVersion.Model.Location;
import com.example.regclient_newVersion.Model.LocationHierarchyList;
import com.example.regclient_newVersion.repository.LocationHierarchyListRepository;
import com.example.regclient_newVersion.repository.LocationRepository;
import com.example.regclient_newVersion.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationHierarchyListRepository hierarchyListRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository,
                               LocationHierarchyListRepository hierarchyListRepository) {
        this.locationRepository = locationRepository;
        this.hierarchyListRepository = hierarchyListRepository;
    }

    @Override
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getRootLocations() {
        return locationRepository.findRootLocations().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getLocationsByParent(String parentLocCode) {
        if (parentLocCode == null || parentLocCode.trim().isEmpty() ||
                "root".equalsIgnoreCase(parentLocCode.trim()) ||
                "null".equalsIgnoreCase(parentLocCode.trim())) {
            return getRootLocations();
        }
        return locationRepository.findByParentLocCodeAndIsActiveTrue(parentLocCode).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getLocationsByLevel(Integer level) {
        return locationRepository.findByHierarchyLevelAndIsActiveTrue(level).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationDTO> getLocationsByLevelName(String levelName) {
        return locationRepository.findByHierarchyNameIgnoreCaseAndIsActiveTrue(levelName).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationHierarchyListDTO> getHierarchyLevels() {
        return hierarchyListRepository.findByIsActiveTrueOrderByHierarchyLevelAsc().stream()
                .map(this::mapHierarchyListEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Map<Integer, String>> getAddressOrderMapByGroup() {
        List<LocationHierarchyList> hierarchyLevels = hierarchyListRepository.findByIsActiveTrueOrderByHierarchyLevelAsc();
        Map<String, Map<Integer, String>> addressGroupMap = new LinkedHashMap<>();

        Map<Integer, String> permMap = new TreeMap<>();
        Map<Integer, String> presMap = new TreeMap<>();
        Map<Integer, String> pobMap = new TreeMap<>();

        for (LocationHierarchyList level : hierarchyLevels) {
            int levelNum = level.getHierarchyLevel();
            String name = level.getHierarchyName() != null ? level.getHierarchyName().trim() : "";

            if (levelNum == 1 || "Region".equalsIgnoreCase(name) || "Country".equalsIgnoreCase(name)) {
                permMap.put(levelNum, "permanentCountry");
                presMap.put(levelNum, "presentCountry");
                pobMap.put(levelNum, "pobCountry");
            } else if (levelNum == 2 || "Province".equalsIgnoreCase(name)) {
                permMap.put(levelNum, "permanentProvince");
                presMap.put(levelNum, "presentProvince");
                pobMap.put(levelNum, "pobProvince");
            } else if (levelNum == 3 || "City".equalsIgnoreCase(name) || "Municipality".equalsIgnoreCase(name)) {
                permMap.put(levelNum, "permanentCity");
                presMap.put(levelNum, "presentCity");
                pobMap.put(levelNum, "pobCity");
            } else if (levelNum == 4 || "Barangay".equalsIgnoreCase(name)) {
                permMap.put(levelNum, "permanentBarangay");
                presMap.put(levelNum, "presentBarangay");
            }
        }

        addressGroupMap.put("permanentAddress", permMap);
        addressGroupMap.put("presentAddress", presMap);
        addressGroupMap.put("placeOfBirth", pobMap);

        return addressGroupMap;
    }

    @Override
    public List<LocationHierarchyDTO> getLocationHierarchyTree() {
        List<Location> allLocations = locationRepository.findByIsActiveTrue();
        Map<String, LocationHierarchyDTO> dtoMap = new HashMap<>();
        List<LocationHierarchyDTO> rootNodes = new ArrayList<>();

        for (Location loc : allLocations) {
            LocationHierarchyDTO dto = mapEntityToHierarchyDto(loc);
            dtoMap.put(loc.getCode(), dto);
        }

        for (Location loc : allLocations) {
            LocationHierarchyDTO currentNode = dtoMap.get(loc.getCode());
            String parentCode = loc.getParentLocCode();

            if (parentCode != null && !parentCode.trim().isEmpty() && dtoMap.containsKey(parentCode)) {
                LocationHierarchyDTO parentNode = dtoMap.get(parentCode);
                parentNode.addChild(currentNode);
            } else {
                rootNodes.add(currentNode);
            }
        }

        return rootNodes;
    }

    private LocationDTO mapEntityToDto(Location entity) {
        return new LocationDTO(
                entity.getCode(),
                entity.getName(),
                entity.getHierarchyLevel(),
                entity.getHierarchyName(),
                entity.getParentLocCode(),
                entity.getLangCode(),
                entity.getIsActive()
        );
    }

    private LocationHierarchyDTO mapEntityToHierarchyDto(Location entity) {
        return new LocationHierarchyDTO(
                entity.getCode(),
                entity.getName(),
                entity.getHierarchyLevel(),
                entity.getHierarchyName(),
                entity.getParentLocCode(),
                entity.getLangCode(),
                entity.getIsActive()
        );
    }

    private LocationHierarchyListDTO mapHierarchyListEntityToDto(LocationHierarchyList entity) {
        return new LocationHierarchyListDTO(
                entity.getHierarchyLevel(),
                entity.getHierarchyName(),
                entity.getLangCode(),
                entity.getIsActive()
        );
    }
}
