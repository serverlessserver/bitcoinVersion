package edu.teco.serverless.test.auth.authFacade;

import edu.teco.serverless.auth.authFacade.AuthFacade;
import edu.teco.serverless.auth.authFacade.AuthFacadeImpl;
import edu.teco.serverless.auth.jwtContentValidation.ContentValidator;
import edu.teco.serverless.auth.jwtCreation.SubtokenCreator;
import edu.teco.serverless.auth.jwtCreation.TokenCreator;
import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Created by Anna on 26.01.2017.
 */
public class AuthTest {

    AuthFacade facade;
    TokenCreator tokenCreator;
    SubtokenCreator subtokenCreator;
    ContentValidator contentValidator;
    Identifier id;
    AuthKey authKey;
    Collection<? extends GrantedAuthority> authorities;
    RuntimeController rc = Mockito.mock(RuntimeController.class);


    @Before
    public void setUp() throws Exception {
        when(rc.lambdaExists(any())).thenReturn(true);
        contentValidator = new ContentValidator(rc);
        facade = new AuthFacadeImpl(tokenCreator, subtokenCreator, contentValidator);
        tokenCreator = new TokenCreator();
        subtokenCreator = new SubtokenCreator();

        ReflectionTestUtils.setField(tokenCreator, "secret", "test1233453");
        ReflectionTestUtils.setField(subtokenCreator, "tokenCreator", tokenCreator);
        ReflectionTestUtils.setField(facade, "subtokenCreator", subtokenCreator);
        ReflectionTestUtils.setField(facade, "tokenCreator", tokenCreator);
        id = new Identifier("test");
        authKey = new AuthKey("13490");
        authorities = AuthorityUtils.createAuthorityList("ROLE_MASTER");

    }


    /**
     * tests creation of a master-token
     * <p>
     * before: the credentials for the token are valid
     * after: a valid master-token now exists, the credentials stay the same
     * <p>
     * Here a test AuthKey and Identifier are used to create a token, then it is compared to a token
     * created by the jwt.io
     */
    @Test
    public void getMasterToken() throws Exception {
        String result = facade.generateMasterToken(authKey, id);
        assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZSI6IlJPTEVfTUFTVEVSIiwiZG9ja2VySGFzaCI6IjEzNDkwIn0.knjZc83w8htsHEv0ZFGQ17x9cm7wsYgfMipZ6vEqSEyFpxRKGyfKViq0DGxphT5qL2S1K1oms5FszVxk9-dolw", result);
    }

    /**
     * tests creation of a subtoken
     * <p>
     * before: the credentials for the subtoken are valid, the expiry date is valid, not null and lies in the future
     * after: a valid subtoken now exists
     * <p>
     * Here test accessRights and expiryDate are used to create a token, then it is compared to a token
     * created by the jwt.io
     */
    @Test
    public void getSubToken() throws Exception {
        Date date = new Date((long) 1494583200000.0);
        AccessRights accessRights = new AccessRights(id.getIdentifier(), date, authorities, authKey.getAuthKey());
        String result = facade.generateSubToken(accessRights, "2017-05-12 12-00-00");
        System.out.print(result);
        assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZSI6IlJPTEVfU1VCIiwiZXhwaXJ5RGF0ZSI6IjE0OTQ1NDAwMDAwMDAiLCJkb2NrZXJIYXNoIjoiMTM0OTAifQ.jYuyftWqsmWJDpDRzu0QsSgl9VBMIZBOlG048xDY9OZZ3zvc0QL2HsXizMjg9oVfkzq8TT4k_ZrXUdWQio5dZQ", result);

    }

    /**
     * tests validation of a valid master-token
     * <p>
     * before: a valid master-token exists
     * after: the token stays the same and is validated
     * <p>
     * Here test accessRights are used to create a token to be validated.
     */
    @Test
    public void validateMasterToken() throws Exception {

        AccessRights accessRights = new AccessRights(id.getIdentifier(), null, authorities, authKey.getAuthKey());
        AuthKey authKey = new AuthKey(accessRights.getAuthKey());
        doReturn(authKey).when(rc).getAuthKey(any());
        assertTrue(facade.validate(accessRights, id));
    }

    /**
     * tests validation of a valid subtoken
     * <p>
     * before: a valid subtoken exists
     * after: the token stays the same and is validated
     * <p>
     * Here test accessRights and expiryDate are used to create a token to be validated.
     */
    @Test
    public void validateSubtoken() throws Exception {
        Date date = new Date((long) 1494583200000.0);
        AccessRights accessRights = new AccessRights(id.getIdentifier(), date, authorities, authKey.getAuthKey());
        AuthKey authKey = new AuthKey(accessRights.getAuthKey());
        doReturn(authKey).when(rc).getAuthKey(any());
        assertTrue(facade.validate(accessRights, id));
    }

    /**
     * tests validation of an invalid subtoken
     * <p>
     * before: a subtoken with invalid AccessRights (invalid AuthKey) exists
     * after: the token stays the same, but the method validate returns false
     * <p>
     * This test can only be implemented after implementing some runtime functionality. Then
     * we will generate an AccessRights object with an invalid AuthKey and/or Identifier and pass it to the
     * validate method in the facade, expecting return false.
     *
     */
    @Test //(expected = Exception)
    public void validateInvalidSubtoken() throws Exception {

    }

    /**
     * tests validation of a subtoken past due date
     * <p>
     * before: an outdated subtoken exists
     * after: the token stays the same, but the method validate returns false
     * <p>
     * Here a test test subtoken with its expiry date before current date is used for validation.
     */
    @Test //(expected = Exception)
    public void validateSubtokenPastDate() throws Exception {
        Date date = new Date((long) 000000000.0);
        AccessRights accessRights = new AccessRights(id.getIdentifier(), date, authorities, authKey.getAuthKey());
        assertFalse(facade.validate(accessRights, id));
    }


}