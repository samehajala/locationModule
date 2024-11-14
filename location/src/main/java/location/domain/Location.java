package location.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import location.deserializer.PointDeserializer;
import org.postgresql.geometric.PGpoint;

@Entity
public class Location extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    @JsonbTypeDeserializer(PointDeserializer.class) // Register the custom deserializer
    public PGpoint coordinates;

    public Location() {
    }

    public Location(String name, PGpoint coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
}
