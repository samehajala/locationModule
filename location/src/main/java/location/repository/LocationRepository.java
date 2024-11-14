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


    private static final double EARTH_RADIUS = 6371000; // Radius in meters

    // Method to find locations within a specified radius
    public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        // Use Haversine formula directly in the query for distance calculation
        String query = """
            SELECT l
            FROM Location l
            WHERE (6371000 * ACOS(
                COS(RADIANS(:latitude)) * COS(RADIANS(l.coordinates.y)) * 
                COS(RADIANS(l.coordinates.x) - RADIANS(:longitude)) + 
                SIN(RADIANS(:latitude)) * SIN(RADIANS(l.coordinates.y))
            )) <= :radius
        """;

        // Create the query with parameters for latitude, longitude, and radius
        TypedQuery<Location> locationQuery = (TypedQuery<Location>) entityManager.createQuery(query);
        locationQuery.setParameter("latitude", latitude);
        locationQuery.setParameter("longitude", longitude);
        locationQuery.setParameter("radius", radiusInMeters);

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
