package util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import enums.SubscriptionStatus;

import java.util.Date;

public class JWTHelper {
    private String SECRET_KEY = "topSecretKey";
    private String ISSUER = "InstantGrade";

    public JWTHelper() {
    }

    public String createToken(String username, SubscriptionStatus subscriptionStatus) {
        // Validity time of the token
        int ACCESS_TOKEN_VALIDITY_SECONDS = 10; // 1h 3600s
        try {
            // The algorithm used for the signature of the Token
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTCreator.Builder builder = JWT.create();

            builder.withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withSubject(username)
                    .withIssuer(ISSUER) // Aussteller des Tokens
                    .withClaim("subscriptionStatus", subscriptionStatus.toString());
            return builder.sign(algorithm);
        } catch (JWTCreationException exception) {
            // SIGNING FAILED / Claims could not been converted
            System.err.println("JWT creation failed: " + exception.getMessage());
        }
        return null;
    }

    public String checkSubject(String token) {

        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
