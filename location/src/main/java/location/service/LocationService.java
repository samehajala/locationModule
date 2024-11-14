package location.service;

import location.domain.Location;
import location.repository.LocationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Point;
import java.util.List;

@ApplicationScoped
public class LocationService {

    @Inject
    LocationRepository locationRepository;

    // Method to get all locations
    public List<Location> getAllLocations() {
        return locationRepository.listAll();
    }

    // Method to create a new location
    @Transactional
    public Location createLocation(String name, double latitude, double longitude) {
        // Create the point using the coordinates (longitude, latitude)
        Point coordinates = new org.locationtech.jts.geom.GeometryFactory().createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
        Location location = new Location(name, coordinates);
        locationRepository.persist(location);
        return location;
    }

    // Method to find locations by name
    public List<Location> findLocationsByName(String name) {
        return locationRepository.findLocationsByName(name);
    }

    // Method to find locations within a specific radius (in meters)
    public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        return locationRepository.findLocationsWithinRadius(latitude, longitude, radiusInMeters);
    }

    // Method to get the distance between two locations
    public double getDistance(Long location1Id, Long location2Id) {
        Location location1 = locationRepository.findById(location1Id);
        Location location2 = locationRepository.findById(location2Id);

        if (location1 == null || location2 == null) {
            throw new RuntimeException("One or both locations not found");
        }

        return locationRepository.getDistance(location1, location2);
    }
}
