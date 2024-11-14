package location.mapper;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import location.domain.Location;
import location.dto.LocationDTO;
import org.locationtech.jts.geom.Coordinate;

public class LocationMapper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    // Converts LocationDTO to Location entity
    public static Location toEntity(LocationDTO dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.coordinates = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        location.coordinates.setSRID(4326); // Set SRID to 4326 (WGS84)
        return location;
    }

    // Converts Location entity to LocationDTO
    public static LocationDTO toDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setLatitude(location.coordinates.getY());
        dto.setLongitude(location.coordinates.getX());
        return dto;
    }
}
