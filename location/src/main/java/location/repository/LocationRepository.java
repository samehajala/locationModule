package location.repository;

import location.domain.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;



@ApplicationScoped
public class LocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // Method to find a location by its ID
    public Location findById(Long id) {
        return entityManager.find(Location.class, id);
    }

    // Method to list all locations
    public List<Location> listAll() {
        String query = "SELECT l FROM Location l";
        TypedQuery<Location> typedQuery = entityManager.createQuery(query, Location.class);
        return typedQuery.getResultList();
    }

    // Method to find locations by name
    public List<Location> findLocationsByName(String name) {
        String query = "SELECT l FROM Location l WHERE l.name = :name";
        TypedQuery<Location> typedQuery = entityManager.createQuery(query, Location.class);
        typedQuery.setParameter("name", name);
        return typedQuery.getResultList();
    }

    // Method to save a location (persist)
    @Transactional
    public void save(Location location) {
            entityManager.persist(location);
    }

    // Method to find locations within a specified radius
    public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        System.out.println("Searching for locations within " + radiusInMeters + " meters from point (" + latitude + ", " + longitude + ")");

        String query = """
            SELECT * FROM location l
            WHERE ST_DWithin(
                l.coordinates,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326),
                :radiusInMeters
            )
        """;

        List<Location> locationsWithinRadius = new ArrayList<>();
        try {
            Query nativeQuery = entityManager.createNativeQuery(query, Location.class);

            nativeQuery.setParameter("latitude", latitude);
            nativeQuery.setParameter("longitude", longitude);
            nativeQuery.setParameter("radiusInMeters", radiusInMeters);

            // Add type safety by specifying the correct generic type
            locationsWithinRadius = ((List<?>) nativeQuery.getResultList())
                    .stream()
                    .filter(result -> result instanceof Location)
                    .map(result -> (Location) result)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("An error occurred during query execution: " + e.getMessage());
            e.printStackTrace();
        }

        if (locationsWithinRadius.isEmpty()) {
            System.out.println("No locations found within the specified radius.");
        } else {
            System.out.println("Found " + locationsWithinRadius.size() + " locations.");
        }

        return locationsWithinRadius;
    }

    // Method to calculate the distance between two locations (in meters)
    @Transactional
    public double getDistance(Location location1, Location location2) {
        String query = """
            SELECT ST_Distance(
                ST_SetSRID(ST_MakePoint(:x1, :y1), 4326),
                ST_SetSRID(ST_MakePoint(:x2, :y2), 4326)
            )
            """;

        TypedQuery<Double> distanceQuery = entityManager.createQuery(query, Double.class);
        distanceQuery.setParameter("x1", location1.getCoordinates().getX());
        distanceQuery.setParameter("y1", location1.getCoordinates().getY());
        distanceQuery.setParameter("x2", location2.getCoordinates().getX());
        distanceQuery.setParameter("y2", location2.getCoordinates().getY());
        return distanceQuery.getSingleResult();
    }
    


}
