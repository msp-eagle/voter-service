package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.dto.LocationDTO;
import com.example.regclient_newVersion.dto.LocationHierarchyDTO;
import com.example.regclient_newVersion.dto.LocationHierarchyListDTO;
import com.example.regclient_newVersion.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        List<LocationDTO> locations = locationService.getAllLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/root")
    public ResponseEntity<List<LocationDTO>> getRootLocations() {
        List<LocationDTO> locations = locationService.getRootLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/parent/{parentLocCode}")
    public ResponseEntity<List<LocationDTO>> getLocationsByParent(@PathVariable String parentLocCode) {
        List<LocationDTO> locations = locationService.getLocationsByParent(parentLocCode);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<LocationDTO>> getLocationsByLevel(@PathVariable Integer level) {
        List<LocationDTO> locations = locationService.getLocationsByLevel(level);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/level-name/{levelName}")
    public ResponseEntity<List<LocationDTO>> getLocationsByLevelName(@PathVariable String levelName) {
        List<LocationDTO> locations = locationService.getLocationsByLevelName(levelName);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/hierarchy-levels")
    public ResponseEntity<List<LocationHierarchyListDTO>> getHierarchyLevels() {
        List<LocationHierarchyListDTO> levels = locationService.getHierarchyLevels();
        return new ResponseEntity<>(levels, HttpStatus.OK);
    }

    @GetMapping("/hierarchy-tree")
    public ResponseEntity<List<LocationHierarchyDTO>> getLocationHierarchyTree() {
        List<LocationHierarchyDTO> hierarchyTree = locationService.getLocationHierarchyTree();
        return new ResponseEntity<>(hierarchyTree, HttpStatus.OK);
    }

    @GetMapping("/order-by-group")
    public ResponseEntity<java.util.Map<String, java.util.Map<Integer, String>>> getAddressOrderMapByGroup() {
        java.util.Map<String, java.util.Map<Integer, String>> orderMap = locationService.getAddressOrderMapByGroup();
        return new ResponseEntity<>(orderMap, HttpStatus.OK);
    }
}
