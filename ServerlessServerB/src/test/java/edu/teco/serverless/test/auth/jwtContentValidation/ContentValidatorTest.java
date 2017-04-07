package edu.teco.serverless.test.auth.jwtContentValidation;

import edu.teco.serverless.auth.jwtContentValidation.ContentValidator;
import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;


/**
 * README (25.1.2017)
 * Does not work because RuntimeController is singleton and has a private contructor.
 * Static methods (aka getInstance()) can not be mocked with mockito.
 * Powermock can mock static methods but only supports mockito 1.
 * mockStatic() does not work with mockito 2 (see bugreport on github, not fixed now)
 */

public class ContentValidatorTest {

    ContentValidator contentValidator;
    RuntimeController rc = Mockito.mock(RuntimeController.class);
    Identifier id;
    AuthKey authKey;
    Collection<? extends GrantedAuthority> authorities;
    AccessRights accessRights;

    @Before
    public void setUp() throws Exception {
        id = new Identifier("test");
        when(rc.lambdaExists(id)).thenReturn(true);
        authKey = new AuthKey("13490");
        authorities = AuthorityUtils.createAuthorityList("ROLE_MASTER");
        accessRights = new AccessRights(id.getIdentifier(), null, authorities, authKey.getAuthKey());
        AuthKey authKey = new AuthKey(accessRights.getAuthKey());
        doReturn(authKey).when(rc).getAuthKey(any());
        contentValidator = new ContentValidator(rc);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void lambdaExists() throws Exception {
        assertTrue(contentValidator.lambdaExists(id));
    }

    @Test
    public void validate() throws Exception {
        assertTrue(contentValidator.validate(accessRights, id));
    }

}