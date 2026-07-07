package com.spmf;

import com.spmf.tv.IPTVChannel;
import com.spmf.tv.IPTVSource;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/iptv")
@Produces(MediaType.APPLICATION_JSON)
public class IPTVResource {

    @GET
    @Path("/providers")
    public List<IPTVSource> providers() {

        return IPTVSource.listAll();

    }

    @GET
    @Path("/{providerId}")
    public List<IPTVChannel> channels(
            @PathParam("providerId")
            Long providerId
    ) {

        return IPTVChannel.list(
                "sourceId",
                providerId
        );

    }

}