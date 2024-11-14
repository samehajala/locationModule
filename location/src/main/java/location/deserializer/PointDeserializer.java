package location.deserializer;

import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.json.JsonObject;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;

public class PointDeserializer implements JsonbDeserializer<Point> {

    @Override
    public Point deserialize(JsonParser parser, jakarta.json.bind.serializer.DeserializationContext ctx, java.lang.reflect.Type rtType) {
        JsonObject jsonObject = parser.getObject();
        
        // Extract x and y coordinates from the JSON object
        double x = jsonObject.getJsonNumber("x").doubleValue();
        double y = jsonObject.getJsonNumber("y").doubleValue();
        
        // Create a new Point instance with the parsed coordinates
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}
