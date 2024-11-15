package location.mapper;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import location.domain.Location;
import location.dto.LocationDTO;

public class LocationMapper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Location toEntity(LocationDTO dto) {
        Location location = new Location();
        location.setName(dto.getName());

        // Create Point from latitude and longitude
        location.coordinates = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        location.coordinates.setSRID(4326); // Set SRID to 4326 (WGS84)
        return location;
    }

    public static LocationDTO toDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setLatitude(location.coordinates.getY()); // Latitude (Y coordinate)
        dto.setLongitude(location.coordinates.getX()); // Longitude (X coordinate)
        return dto;
    }
}
