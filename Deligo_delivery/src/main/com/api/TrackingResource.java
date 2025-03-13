package main.com.api;

import main.com.core.TrackingService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/tracking")
public class TrackingResource {
    private final TrackingService trackingService = new TrackingService();

    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return trackingService.getTrackingStatus();
    }
}