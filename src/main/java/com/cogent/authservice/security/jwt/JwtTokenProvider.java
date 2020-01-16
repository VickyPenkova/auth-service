package com.cogent.authservice.security.jwt;

import com.cogent.authservice.constants.ErrorMessage.TokenInvalid;
import com.cogent.authservice.constants.UIConstants;
import com.cogent.authservice.dto.UserDto;
import com.cogent.authservice.exceptionHandler.UnauthorisedException;
import com.cogent.authservice.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

import static com.cogent.authservice.constants.SecurityConstants.*;

/**
 * @author smriti on 6/27/19
 */

@Component
public class JwtTokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;

    private final CustomUserDetailsService userDetailsService;

    private String secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties, CustomUserDetailsService userDetailsService) {
        this.jwtProperties = jwtProperties;
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecretKey().getBytes());
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        System.out.println("getAuthentication:" + getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
//         String username = req.getUserPrincipal().getName();
//         UserDto user = getDoctorOrPatient(username);
//         String token = user.getAuthToken();
//        System.out.println("Db token: "+ token);
//        return (!Objects.isNull(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) ?
//              bearerToken.substring(7, bearerToken.length()) : null;
        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
        System.out.println(bearerToken);
        return (!Objects.isNull(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) ?
                bearerToken.substring(7, bearerToken.length()) : null;
    }

    private void updateUserToken(UserDto user, String token){
        String uri = "http://localhost:8082/api/user/update/" + user.getUsername() + "/" + token;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(uri, UserDto.class);
    }

    private UserDto getDoctorOrPatient(String username) {
        String uri = "http://localhost:8082/api/user/get/" + username;
        RestTemplate restTemplate = new RestTemplate();
        UserDto user = restTemplate.getForObject(uri, UserDto.class);
        return user;
    }
    public boolean validateToken(String token) {

        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            return (!claims.getBody().getExpiration().before(new Date()));
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.error("Expired or invalid JWT token");
            throw new UnauthorisedException(TokenInvalid.MESSAGE, TokenInvalid.DEVELOPER_MESSAGE);
        }
    }
}