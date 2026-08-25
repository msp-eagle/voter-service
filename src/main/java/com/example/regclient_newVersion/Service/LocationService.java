package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.LocationDTO;
import com.example.regclient_newVersion.dto.LocationHierarchyDTO;
import com.example.regclient_newVersion.dto.LocationHierarchyListDTO;

import java.util.List;
import java.util.Map;

public interface LocationService {
    List<LocationDTO> getAllLocations();
    List<LocationDTO> getRootLocations();
    List<LocationDTO> getLocationsByParent(String parentLocCode);
    List<LocationDTO> getLocationsByLevel(Integer level);
    List<LocationDTO> getLocationsByLevelName(String levelName);
    List<LocationHierarchyDTO> getLocationHierarchyTree();
    List<LocationHierarchyListDTO> getHierarchyLevels();
    Map<String, Map<Integer, String>> getAddressOrderMapByGroup();
}
