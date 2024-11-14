package location.deserializer;

import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.json.JsonObject;
import org.postgresql.geometric.PGpoint;

public class PointDeserializer implements JsonbDeserializer<PGpoint> {

    @Override
    public PGpoint deserialize(JsonParser parser, jakarta.json.bind.serializer.DeserializationContext ctx, java.lang.reflect.Type rtType) {
        JsonObject jsonObject = parser.getObject();
        
        // Extract x and y coordinates from the JSON object
        double x = jsonObject.getJsonNumber("x").doubleValue();
        double y = jsonObject.getJsonNumber("y").doubleValue();
        
        // Create a new PGpoint instance with the parsed coordinates
        return new PGpoint(x, y);
    }
}
