package location.repository;

import location.domain.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
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
        // Radius in degrees (approximation)
        double radiusInDegrees = radiusInMeters / 111320; // Convert meters to degrees (approx. at equator)

        // Define a simple bounding box around the point (latitude, longitude)
        double minLat = latitude - radiusInDegrees;
        double maxLat = latitude + radiusInDegrees;
        double minLon = longitude - radiusInDegrees;
        double maxLon = longitude + radiusInDegrees;

        // Create the query with bounding box constraints
        String query = "SELECT l FROM Location l WHERE l.coordinates.x BETWEEN :minLon AND :maxLon " +
                       "AND l.coordinates.y BETWEEN :minLat AND :maxLat";

        // Create the TypedQuery
        TypedQuery<Location> locationQuery = entityManager.createQuery(query, Location.class);
        locationQuery.setParameter("minLon", minLon);
        locationQuery.setParameter("maxLon", maxLon);
        locationQuery.setParameter("minLat", minLat);
        locationQuery.setParameter("maxLat", maxLat);

        // Execute the query and return the result
        return locationQuery.getResultList();
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
