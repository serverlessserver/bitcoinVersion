package edu.teco.serverless.test.auth.authFacade;

import edu.teco.serverless.auth.authFacade.AuthFacadeImpl;
import edu.teco.serverless.auth.jwtContentValidation.ContentValidator;
import edu.teco.serverless.auth.jwtCreation.SubtokenCreator;
import edu.teco.serverless.auth.jwtCreation.TokenCreator;
import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Created by Anna on 22.01.2017.
 */
public class AuthFacadeImplTest {

    AuthFacadeImpl facade;
    AccessRights rights;
    private TokenCreator tokenCreator;
    private SubtokenCreator subtokenCreator;
    private  ContentValidator contentValidator;
    @Mock
    AccessRights accessRights;
    String name;
    Collection<? extends GrantedAuthority> authorities;
    String expiryDate;
    AuthKey authKey;
    Identifier id;

    @Before
    public void setUp() throws Exception {


        facade = new AuthFacadeImpl(tokenCreator, subtokenCreator, contentValidator);

        id = new Identifier("test");
        authKey = new AuthKey("13490");

        name = "test";
        expiryDate = "2017-05-12 12-00-00";
        authorities = AuthorityUtils.createAuthorityList("ROLE_MASTER");
        accessRights = Mockito.mock(AccessRights.class);
        when(accessRights.getAuthKey()).thenReturn("12345");
        when(accessRights.getLambdaName()).thenReturn(name);
        doReturn(authorities).when(accessRights).getAuthorities();
        tokenCreator = new TokenCreator();
        ReflectionTestUtils.setField(tokenCreator, "secret", "test1233453");
        subtokenCreator = new SubtokenCreator(tokenCreator);
        ReflectionTestUtils.setField(facade, "tokenCreator", tokenCreator);
        ReflectionTestUtils.setField(facade, "subtokenCreator", subtokenCreator);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void generateSubToken() throws Exception {
        String result = facade.generateSubToken(accessRights, expiryDate);
        assertEquals(subtokenCreator.generateSubToken(accessRights, expiryDate), result);

    }

    @Test
    public void generateMasterToken() throws Exception {
        String result = facade.generateMasterToken(authKey, id);
        AccessRights rights = new AccessRights(id.getIdentifier(), null, AuthorityUtils.createAuthorityList("ROLE_MASTER"), authKey.getAuthKey());
        assertEquals(tokenCreator.generateToken(rights), result);
    }

    @Test
    public void validate() throws Exception {

    }

}