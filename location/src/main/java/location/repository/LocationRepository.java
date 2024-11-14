package location.repository;

import location.domain.Location;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import org.locationtech.jts.geom.Point;

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    // Method to find locations by name
    public List<Location> findLocationsByName(String name) {
        return find("name", name).list();
    }
    
    // Method to find locations within a specified radius (in meters)
    @Transactional
    public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        // Create a Point from latitude and longitude (assuming WGS 84, SRID 4326)
        Point point = new org.locationtech.jts.geom.GeometryFactory().createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));

        // PostGIS function ST_DWithin returns true if two geometries are within a certain distance
        String query = "SELECT l FROM Location l WHERE ST_DWithin(l.coordinates, :point, :radius)";
        TypedQuery<Location> typedQuery = getEntityManager().createQuery(query, Location.class);
        typedQuery.setParameter("point", point);
        typedQuery.setParameter("radius", radiusInMeters);
        return typedQuery.getResultList();
    }

    // Method to calculate the distance between two locations (in meters)
    @Transactional
    public double getDistance(Location location1, Location location2) {
        String query = "SELECT ST_Distance(ST_SetSRID(l1.coordinates, 4326), ST_SetSRID(l2.coordinates, 4326)) " +
                       "FROM Location l1, Location l2 " +
                       "WHERE l1.id = :id1 AND l2.id = :id2";
        TypedQuery<Double> typedQuery = getEntityManager().createQuery(query, Double.class);
        typedQuery.setParameter("id1", location1.id);
        typedQuery.setParameter("id2", location2.id);
        return typedQuery.getSingleResult();
    }
}
