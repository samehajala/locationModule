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

    @Transactional
    public List<LocationDTO> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        // Get all locations (or a subset of them) from the repository
        List<Location> locations = locationRepository.listAll(); // Adjust as needed

        // Filter locations by radius
        return locations.stream()
            .filter(location -> isWithinRadius(location, latitude, longitude, radiusInMeters))
            .map(this::convertToDTO) // Correctly map each location to its DTO
            .collect(Collectors.toList());
    }

    private boolean isWithinRadius(Location location, double latitude, double longitude, double radiusInMeters) {
        // Calculate the distance using the Haversine formula or other method
        double distance = calculateDistance(latitude, longitude, location.getCoordinates().getX(), location.getCoordinates().getY());
        return distance <= radiusInMeters;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Log values to see the input
        System.out.println("lat1: " + lat1 + ", lon1: " + lon1 + ", lat2: " + lat2 + ", lon2: " + lon2);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // Distance in meters

        // Log the calculated distance
        System.out.println("Calculated distance: " + distance);

        return distance;
    }

    private LocationDTO convertToDTO(Location location) {
        // Convert Location entity to LocationDTO
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setLatitude(location.getCoordinates().getX());
        dto.setLongitude(location.getCoordinates().getY());
        dto.setCoordinates(location.getCoordinates()); // Assuming location has coordinates field
        return dto;
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
