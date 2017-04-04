package edu.teco.serverless.test.model.servicelayer.lambdaruntime;

import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import org.junit.*;

import static org.junit.Assert.assertFalse;

/**
 * Created by steffen on 04.03.17.
 *
 * tests the functionality of the runtime module
 *
 * negative tests
 */
public class RuntimeTestNegative {
    private static final String LONG_LAMBDA = "long";

    private RuntimeController controller;
    private Lambda notExisting;
    private int originalTimeLimit;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // upload test lambdas
        RuntimeController controller = RuntimeController.getInstance();
        Lambda longLambda = TestUtils.buildLambda(LONG_LAMBDA, "Python3", "import sleep\ntime.sleep(10)", null);
        controller.buildImage(longLambda);
    }

    @Before
    public void setUp() throws Exception {
        controller = RuntimeController.getInstance();
        notExisting = TestUtils.buildLambda("lambda", "Python3", "# just a comment", null);
        originalTimeLimit = controller.getGlobalTimeLimit();
    }

    /**
     * tests creating a lambda in unknown language
     *
     * before: none
     * after: lambda should not exist
     */
    @Test(expected = LanguageNotSupportedException.class)
    public void testUnsupportedLanguage() throws Exception {
        Lambda lambda = TestUtils.buildLambda("lambda", "imaginary", "//", null);
        controller.buildImage(lambda);
    }

    /**
     * tests trying to execute a not existing lambda
     *
     * before: the lambda to be executed should NOT exist
     * after: lambda should still not exists, throws LambdaNotFoundException
     */
    @Test (expected = LambdaNotFoundException.class)
    public void testRunNotExisting() throws Exception {
        //check before condition
        assertFalse(controller.lambdaExists(notExisting.getName()));

        controller.run(notExisting.getName(), TestUtils.buildExecConfig(1, null));

        //check after
        assertFalse(controller.lambdaExists(notExisting.getName()));
    }

    /**
     * tests trying to get a not existing lambda
     *
     * before: the lambda should NOT exist
     * after: lambda should still not exist, throws LambdaNotFoundException
     */
    @Test (expected = LambdaNotFoundException.class)
    public void testGetNotExisting() throws Exception {
        //check before condition
        assertFalse(controller.lambdaExists(notExisting.getName()));

        controller.getLambda(notExisting.getName());

        //check after
        assertFalse(controller.lambdaExists(notExisting.getName()));
    }

    /** tests trying to get auth key of a not existing lambda
     *
     * before: the lambda should not exist
     * after: lambda should still not exist
     */
    @Test (expected = LambdaNotFoundException.class)
    public void testGetAuthKeyNotExisting() throws Exception {
        //check before condition
        assertFalse(controller.lambdaExists(notExisting.getName()));

        controller.getAuthKey(notExisting.getName());

        //check after
        assertFalse(controller.lambdaExists(notExisting.getName()));
    }

    /**
     * tests exceeding time limit
     *
     * before: the lambda exists
     * after: the lambda still exists
     */
    @Test (expected = TimeExceededException.class)
    public void testTimeLimitExceeded() throws Exception {
        // check before condition
        controller.setGlobalTimeLimit(1);
        ExecuteConfig config = TestUtils.buildExecConfig(1, null);
        controller.run(new Identifier(LONG_LAMBDA), config);
    }

    /**
     * tests reaction to passing an illegal time limit
     */
    @Test (expected = IllegalArgumentException.class)
    public void testIllegalTimeLimit() throws Exception {
        controller.setGlobalTimeLimit(0);
    }

    /**
     * tests reaction of build to null-argument
     */
    @Test (expected = IllegalArgumentException.class)
    public void testBuildNull() throws Exception {
        controller.buildImage(null);
    }

    /**
     * tests reaction of rebuild to null-argument
     */
    @Test (expected = IllegalArgumentException.class)
    public void testRebuildNull() throws Exception {
        controller.buildImage(null);
    }

    /**
     * tests reaction of delete to null-argument
     */
    @Test (expected = IllegalArgumentException.class)
    public void testDeleteNull() throws Exception {
        controller.buildImage(null);
    }

    /**
     * tests reaction of run to null-argument
     */
    @Test (expected = IllegalArgumentException.class)
    public void testRunNull() throws Exception {
        controller.run(null, null);
    }

    /**
     * tests reaction of getLambda to null-argument
     */
    @Test (expected = IllegalArgumentException.class)
    public void testGetLambdaNull() throws Exception {
        controller.getLambda(null);
    }

    /**
     * tests reaction of getAuthKey to null-argument
     */
    @Test (expected = IllegalArgumentException.class)
    public void testGetAuthKeyNull() throws Exception {
        controller.getAuthKey(null);
    }

    @After
    public void tearDown() {
        // restore original time limit
        controller.setGlobalTimeLimit(originalTimeLimit);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        // delete created test lambdas
        RuntimeController controller = RuntimeController.getInstance();
        controller.deleteImage(new Identifier(LONG_LAMBDA));
    }
}
