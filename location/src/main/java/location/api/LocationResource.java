package location.api;

import location.domain.Location;
import location.service.LocationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {

    @Inject
    LocationService locationService;

    @GET
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @POST
    public Location createLocation(Location location) {
        return locationService.createLocation(location.name, location.coordinates.x, location.coordinates.y);
    }

    @GET
    @Path("/search/{name}")
    public List<Location> searchLocationsByName(@PathParam("name") String name) {
        return locationService.findLocationsByName(name);
    }
    @GET
    @Path("/within-radius")
    public Response findLocationsWithinRadius(@QueryParam("latitude") double latitude,
                                              @QueryParam("longitude") double longitude,
                                              @QueryParam("radius") double radius) {
        List<Location> locations = locationService.findLocationsWithinRadius(latitude, longitude, radius);
        if (locations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No locations found within the given radius").build();
        }
        return Response.ok(locations).build();
    }

    // Endpoint to get the distance between two locations
    @GET
    @Path("/distance")
    public Response getDistance(@QueryParam("location1Id") Long location1Id,
                                @QueryParam("location2Id") Long location2Id) {
        try {
            double distance = locationService.getDistance(location1Id, location2Id);
            return Response.ok("{\"distance\": " + distance + "}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
