package location.service;

import location.domain.Location;
import location.dto.LocationDTO;
import location.mapper.LocationMapper;
import location.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LocationService {

    @Inject
    LocationRepository locationRepository;

    public List<LocationDTO> getAllLocations() {
        return locationRepository.listAll().stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = LocationMapper.toEntity(locationDTO);
        locationRepository.save(location);
        return LocationMapper.toDTO(location);
    }

    public List<LocationDTO> findLocationsByName(String name) {
        return locationRepository.findLocationsByName(name).stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    
    public List<LocationDTO> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        System.out.println("Searching for locations within " + radiusInMeters + " meters from point (" + latitude + ", " + longitude + ")");

        // Use the repository method to find locations within the specified radius
        List<Location> locationsWithinRadius = locationRepository.findLocationsWithinRadius(latitude, longitude, radiusInMeters);

        // Debugging: Print details of found locations
        if (locationsWithinRadius.isEmpty()) {
            System.out.println("No locations found within the specified radius.");
        } else {
            System.out.println("Found " + locationsWithinRadius.size() + " locations:");
            for (Location loc : locationsWithinRadius) {
                System.out.println("Location ID: " + loc.getId() + ", Name: " + loc.getName() +
                                   ", Coordinates: (" + loc.getCoordinates().getX() + ", " + loc.getCoordinates().getY() + ")");
            }
        }

        // Convert the list of Location entities to LocationDTOs
        return locationsWithinRadius.stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    

    

    

    public double getDistance(Long location1Id, Long location2Id) {
        Location location1 = locationRepository.findById(location1Id);
        Location location2 = locationRepository.findById(location2Id);
        if (location1 == null || location2 == null) {
            throw new RuntimeException("One or both locations not found");
        }
        return locationRepository.getDistance(location1, location2);
    }
}
