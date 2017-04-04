package edu.teco.serverless.test.auth.jwtCreation;

import edu.teco.serverless.auth.jwtCreation.TokenCreator;
import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Created by satan on 20/01/2017.
 */
public class TokenCreatorTest {

    TokenCreator tokenCreator;
    @Mock
    AccessRights accessRights;
    String name;
    Collection<? extends GrantedAuthority> authorities;
    String expiryDate;

    @Before
    public void setUp() throws Exception {
        accessRights = Mockito.mock(AccessRights.class);
        name = "test";
        expiryDate = null;
        authorities = AuthorityUtils.createAuthorityList("ROLE_MASTER");
        tokenCreator = new TokenCreator();
        when(accessRights.getLambdaName()).thenReturn(name);
        doReturn(authorities).when(accessRights).getAuthorities();
        ReflectionTestUtils.setField(tokenCreator, "secret", "test123");
    }

    

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void generateToken() throws Exception {
        when(accessRights.getExpiryDate()).thenReturn(null);
        String result = tokenCreator.generateToken(accessRights);
        assertNotNull(result);
    }

    @Test
    public void generateTokenExpiryDateNotNull() throws Exception {
        String expiryDate = "2017-05-12 12-00-00";
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh-mm-ss");
        Date exp = null;
        try {
            exp = ft.parse(expiryDate);
        } catch (ParseException e) {
        }
        assertNotNull(exp);
        when(accessRights.getExpiryDate()).thenReturn(exp);
        String result = tokenCreator.generateToken(accessRights);
        System.out.print(result);
        assertNotNull(result);
    }

    @Test
    public void parseToken() throws Exception {
        String result = tokenCreator.generateToken(accessRights);
        AccessRights a = tokenCreator.parseToken(result);
        assertEquals(a.getLambdaName(), name);
        assertEquals(a.getAuthorities(), authorities);
        assertEquals(a.getExpiryDate(), expiryDate);
    }

    @Test
    public void parseTokenExpiryDateNotNull() throws Exception {
        String expiryDate = "2017-05-12 12-00-00";
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh-mm-ss");
        Date exp = null;
        try {
            exp = ft.parse(expiryDate);
        } catch (ParseException e) {
        }
        assertNotNull(exp);
        when(accessRights.getExpiryDate()).thenReturn(exp);
        String result = tokenCreator.generateToken(accessRights);
        AccessRights a = tokenCreator.parseToken(result);
        assertEquals(a.getLambdaName(), name);
        assertEquals(a.getAuthorities(), authorities);
        assertEquals(a.getExpiryDate(), exp);
    }

    @Test
    public void parseTokenWrongJWT() throws Exception {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZSI6IlJPTEVfTUFTVEVSIiwiZXhwaXJ5RGF0ZSI6IjE0OTQ1NDAwMDAwMDAifQ.IsG93pZkQhGbEKOwH48oxXlM-7HA4qU36wIhMlUmQCucggQVZybcpPDEDqgIzRNoYXOUH67dEih7EXQnr6dZWi";
        AccessRights a = tokenCreator.parseToken(token);
        assertNull(a);
    }

}