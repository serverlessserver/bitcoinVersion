package edu.teco.serverless.test.auth.jwtCreation;

import edu.teco.serverless.auth.jwtCreation.SubtokenCreator;
import edu.teco.serverless.auth.jwtCreation.TokenCreator;
import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Created by satan on 20/01/2017.
 */
public class SubtokenCreatorTest {

    SubtokenCreator subtokenCreator;
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
        subtokenCreator = new SubtokenCreator(tokenCreator);
        when(accessRights.getAuthKey()).thenReturn("12345");
        when(accessRights.getLambdaName()).thenReturn(name);
        doReturn(authorities).when(accessRights).getAuthorities();
        ReflectionTestUtils.setField(tokenCreator, "secret", "test1233453");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void generateSubToken() throws Exception {
        expiryDate = "2017-05-12 12-00-00";
        String result = subtokenCreator.generateSubToken(accessRights, expiryDate);
        authorities = AuthorityUtils.createAuthorityList("ROLE_SUB");
        doReturn(authorities).when(accessRights).getAuthorities();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh-mm-ss");
        Date exp = null;
        try {
            exp = ft.parse(expiryDate);
        } catch (ParseException e) {
        }
        when(accessRights.getExpiryDate()).thenReturn(exp);
        assertEquals(tokenCreator.generateToken(accessRights), result);

    }

}