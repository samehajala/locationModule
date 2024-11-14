package location.repository;

import location.domain.Location;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

import org.postgresql.geometric.PGpoint;

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    // You can now use the `findAll` and `findById` methods automatically provided by Panache
    public List<Location> findLocationsByName(String name) {
        return find("name", name).list();
    }
    
    // Add other custom queries if needed
    @Transactional
    public List<Location> findLocationsWithinRadius(double latitude, double longitude, double radiusInMeters) {
        PGpoint point = new PGpoint(longitude, latitude);

        // PostGIS function ST_DWithin returns true if two geometries are within a certain distance
        String query = "SELECT l FROM Location l WHERE ST_DWithin(l.coordinates, :point, :radius)";
        TypedQuery<Location> typedQuery = getEntityManager().createQuery(query, Location.class);
        typedQuery.setParameter("point", point);
        typedQuery.setParameter("radius", radiusInMeters);
        return typedQuery.getResultList();
    }

    // Method to calculate the distance between two points (in meters)
    public double getDistance(Location location1, Location location2) {
        // Use PostGIS function ST_Distance and explicitly cast PGpoint to PostGIS POINT type
        String query = "SELECT ST_Distance(ST_SetSRID(ST_MakePoint(l1.coordinates.x, l1.coordinates.y), 4326), " +
                       "ST_SetSRID(ST_MakePoint(l2.coordinates.x, l2.coordinates.y), 4326)) " +
                       "FROM Location l1, Location l2 " +
                       "WHERE l1.id = :id1 AND l2.id = :id2";
        TypedQuery<Double> typedQuery = getEntityManager().createQuery(query, Double.class);
        typedQuery.setParameter("id1", location1.id);
        typedQuery.setParameter("id2", location2.id);
        return typedQuery.getSingleResult();
    }
}
