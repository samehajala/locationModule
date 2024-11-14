package location.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import location.deserializer.PointDeserializer;

import org.locationtech.jts.geom.Point;

@Entity
public class Location extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    @JsonbTypeDeserializer(PointDeserializer.class) // Register the custom deserializer
    public Point coordinates;

    public Location() {
    }

    public Location(String name, Point coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

}
