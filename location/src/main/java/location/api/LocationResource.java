package location.api;

import location.dto.LocationDTO;
import location.service.LocationService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
    // Endpoint to get all locations
    public List<LocationDTO> getAllLocations() {
        return locationService.getAllLocations();
    }

    @POST
    @Transactional
    // Endpoint to create a new location
    public Response createLocation(LocationDTO locationDTO) {
        LocationDTO savedLocation = locationService.createLocation(locationDTO);
        return Response.status(Response.Status.CREATED).entity(savedLocation).build();
    }

    @GET
    @Path("/search/{name}")
    // Endpoint to search for locations by name
    public List<LocationDTO> searchLocationsByName(@PathParam("name") String name) {
        return locationService.findLocationsByName(name);
    }

    @GET
    @Path("/within-radius")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findLocationsWithinRadius(@QueryParam("latitude") double latitude,
                                              @QueryParam("longitude") double longitude,
                                              @QueryParam("radius") double radius) {
        // Call the service layer method
        List<LocationDTO> locations = locationService.findLocationsWithinRadius(latitude, longitude, radius);
    
        // Return NOT_FOUND if the list is empty
        if (locations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No locations found within the given radius")
                    .build();
        }
        // Return OK response with list of locations
        return Response.ok(locations).build();
    }
    

    @GET
    @Path("/distance")
    // Endpoint to get the distance between two locations
    public Response getDistance(@QueryParam("location1Id") Long location1Id,
                                @QueryParam("location2Id") Long location2Id) {
        double distance = locationService.getDistance(location1Id, location2Id);
        return Response.ok(distance).build();
    }
}
