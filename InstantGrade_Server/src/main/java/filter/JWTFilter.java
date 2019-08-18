package filter;

import util.jwt.JWTHelper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class JWTFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext rc) throws IOException {
        // Excludes these paths from filtering --> No JWT set yet!
        if (rc.getUriInfo().getPath().contains("login")
                || rc.getUriInfo().getPath().contains("register")
                || rc.getUriInfo().getPath().contains("verify")
                || rc.getMethod().equals("OPTIONS")
        ) {
            return;
        }
        JWTHelper jwth = new JWTHelper();
        try {
            String authorizationHeader = rc.getHeaderString(HttpHeaders.AUTHORIZATION);
            String token = authorizationHeader.substring("Bearer".length()).trim();
            jwth.checkSubject(token);
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}
