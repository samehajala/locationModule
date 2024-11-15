package location.domain;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

@Entity
public class Location  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String name;
    @JsonbTransient
    @Column(columnDefinition = "geometry(Point, 4326)")
    public Point coordinates;

    public Location() {
    }

    public Location(String name, Point coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    
}
