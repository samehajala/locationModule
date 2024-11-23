package location.api;

import location.api.dto.LocationDTO;
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
    // Endpoint to get all locations
    public List<LocationDTO> getAllLocations() {
        return locationService.getAllLocations();
    }

    @POST
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
    // Endpoint to find locations within a specified radius
    public Response findLocationsWithinRadius(@QueryParam("latitude") double latitude,
                                              @QueryParam("longitude") double longitude,
                                              @QueryParam("radius") double radiusInMeters) {
        if (radiusInMeters <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Radius must be a positive value.")
                    .build();
        }

        try {
            List<LocationDTO> locations = locationService.findLocationsWithinRadius(latitude, longitude, radiusInMeters);
            return Response.ok(locations).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while retrieving locations within the radius.")
                    .build();
        }
    } 
    

    @GET
    @Path("/distance")
    // Endpoint to get the distance between two locations
    public Response getDistance(@QueryParam("location1Id") Long location1Id,
                                @QueryParam("location2Id") Long location2Id) {
        if (location1Id == null || location2Id == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Location IDs must be provided.")
                    .build();
        }
        
        try {
            double distance = locationService.getDistance(location1Id, location2Id);
            return Response.ok(distance).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while calculating the distance.")
                    .build();
        }
    }
}
