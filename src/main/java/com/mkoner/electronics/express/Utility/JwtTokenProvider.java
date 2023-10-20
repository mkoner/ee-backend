package com.mkoner.electronics.express.Utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mkoner.electronics.express.entity.Admin;
import com.mkoner.electronics.express.entity.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    public static final String TOKEN_PROVIDER = "Electronics express";
    public static final String AUTHORITIES = "Authorities";
    public static final Long EXPIRATION_TIME = 30 * 60 * 60 * 60 * 1000L;

    //public static final Long EXPIRATION_TIME =10L;
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token could not be verified";

    @Value("{jwt-secret}")
    private String secret;

    public String generateJwtToken (UserDetails userDetails) {
        String[] claims = getClaimsFromUser(userDetails);

        if (userDetails instanceof Admin)
            return JWT.create().withAudience("ADMIN").withIssuer(TOKEN_PROVIDER).withArrayClaim(AUTHORITIES,
                            claims).withIssuedAt(new Date()).withSubject(userDetails.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(Algorithm.HMAC512(secret.getBytes()));

        if (userDetails instanceof Customer)
            return JWT.create().withAudience("CUSTOMER").withIssuer(TOKEN_PROVIDER).withArrayClaim(AUTHORITIES,
                            claims).withIssuedAt(new Date()).withSubject(userDetails.getUsername())
                    .sign(Algorithm.HMAC512(secret.getBytes()));

        return null;
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken = new
                UsernamePasswordAuthenticationToken(username, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJWTVerifier();
        return !username.isEmpty() && !isTokenExpired(verifier, token);
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(TOKEN_PROVIDER).build();
        }catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }



    private String[] getClaimsFromUser(UserDetails userPrincipal) {
        List<String> authorities = new ArrayList<>();
        for(GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }
}
