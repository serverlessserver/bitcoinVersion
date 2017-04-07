package edu.teco.serverless.test.model.servicelayer.lambdaruntime;

import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by steffen on 26.01.17.
 *
 * tests the functionality of the runtime module
 *
 * positive tests
 */
public class RuntimeTestPositive {
    private static final String STATELESS_PATH = "src/main/resources/test/runtime/stateless_lambda.py";
    private static final String RUN_ARG_PATH = "src/main/resources/test/runtime/run_arg_lambda.py";

    private static final String UPDATE = "update_lambda";
    private static final String DELETE = "delete_lambda";
    private static final String RUN = "run_lambda";
    private static final String GET = "get_lambda";
    private static final String STATELESS = "stateless_lambda";
    private static final String RUN_ARG = "run_arg_lambda";

    private static List<Lambda> lambdas;

    private RuntimeController controller;

    @BeforeClass
    public static void beforeClass() throws Exception {
        // upload test lambdas
        RuntimeController controller = RuntimeController.getInstance();

        lambdas = new ArrayList<>();

        Lambda update = TestUtils.buildLambda(UPDATE, "Python3", "print('Hello world')", null);
        Lambda delete = TestUtils.buildLambda(DELETE, "Python3", "# just a test", null);
        Lambda run = TestUtils.buildLambda(RUN, "Python3", "print('Hello world')", null);
        Lambda get = TestUtils.buildLambda(GET, "Python3", "import urllib3", new String[] {"urllib3"});
        Lambda stateless = TestUtils.buildLambda(STATELESS, "Python3", TestUtils.loadFileContent(STATELESS_PATH), null);
        Lambda runArg = TestUtils.buildLambda(RUN_ARG, "Python3", TestUtils.loadFileContent(RUN_ARG_PATH), null);

        lambdas.add(update);
        lambdas.add(delete);
        lambdas.add(run);
        lambdas.add(get);
        lambdas.add(stateless);
        lambdas.add(runArg);

        for (Lambda lambda : lambdas) {
            controller.buildImage(lambda);
        }
    }

    @Before
    public void setUp() throws Exception {
        controller = RuntimeController.getInstance();
    }

    /**
     * tests the uploading of lambdas and building images
     *
     * before: the lambda shouldn't already exist
     * after: lambda should exist und be executable
     */
    @Test
    public void testUpload() throws Exception {
        Lambda lambda = TestUtils.buildLambda("print", "Python3", "print(\"Hello world\")", null);

        //check before condition
        assertFalse(controller.lambdaExists(lambda.getName()));

        AuthKey key = controller.buildImage(lambda);

        //check after condition
        assertTrue(controller.lambdaExists(lambda.getName()));
        assertNotNull(key);
    }

    /**
     * tests the updating of lambdas and rebuilding the image
     *
     * before: the lambda to be updated must exist
     * after: lambda it should still exist and be executable
     *
     * the hello_world-lambda is modified, after modification is should return 'Hello serverless'
     * instead of 'Hello world'
     */
    @Test
    public void testUpdate() throws Exception {
        final String UPDATED_CODE = "print('Hello serverless')";
        Identifier id = new Identifier(UPDATE);

        //check before condition
        assertTrue(controller.lambdaExists(id));
        ExecuteConfig config = TestUtils.buildExecConfig(1, null);
        assertEquals("Hello world\n",controller.run(id, config));

        // update the lambda
        Lambda lambda = TestUtils.buildLambda(UPDATE, "Python3", UPDATED_CODE, null);
        controller.rebuildImage(lambda);

        //check after condition
        assertTrue(controller.lambdaExists(id));
        assertEquals("Hello serverless\n", controller.run(id, config));
    }

    /**
     * tests the deletion of lambdas
     *
     * before: the lambda to be deleted must exist
     * after: lambda should not exists anymore, so it also should not be callable
     *
     * here the delete.py lambda is deleted
     */
    @Test
    public void testDelete() throws Exception {
        Identifier id = new Identifier(DELETE);

        //check before condition
        assertTrue(controller.lambdaExists(id));

        controller.deleteImage(id);

        //check after condition
        assertFalse(controller.lambdaExists(id));
    }

    /**
     * tests the execution of a lambda
     *
     * before: the lambda to be executed must exist
     * after: lambda still exists, can be executed again
     *
     * here a test lambda is used which should return 'Hello world'
     */
    @Test
    public void testRun() throws Exception {
        Identifier id = new Identifier(RUN);

        //check before condition
        assertTrue(controller.lambdaExists(id));

        ExecuteConfig config = TestUtils.buildExecConfig(1, null);
        String result = controller.run(id, config);

        //check after condition
        assertEquals("Hello world\n", result);
        assertTrue(controller.lambdaExists(id));
    }

    /**
     * tests the execution of a lambda with an argument
     *
     * before: the lambda to be executed must exist
     * after: lambda still exists, can be executed again
     *
     * here a test lambda is used which returns the passed argument
     */
    @Test
    public void testRunWithArg() throws Exception {
        final String ARG = "test";
        Identifier id = new Identifier(RUN_ARG);

        // check before condition
        assertTrue(controller.lambdaExists(id));

        ExecuteConfig config = TestUtils.buildExecConfig(1, new String[] {ARG});
        String result = controller.run(id, config);

        // check after condition
        assertEquals(ARG + '\n', result);
    }

    /**
     * test the get lambda function
     *
     * before: lambda should exist
     * after: lambda should still exist
     */
    @Test
    public void testGetLambda() throws Exception {
        Identifier id = new Identifier(GET);

        // check before condition
        assertTrue(controller.lambdaExists(id));

        Lambda lambda = controller.getLambda(id);

        assertEquals(id, lambda.getName());
        assertEquals(new Language("Python3"), lambda.getRuntimeAttributes().getLanguage());
        assertEquals("import urllib3", lambda.getRuntimeAttributes().getCode().getCode().trim());
        assertEquals("urllib3", lambda.getRuntimeAttributes().getLibraries().get(0).getLibrary());
    }

    /**
     * tests the get auth key function
     *
     * before: lambda should exist
     * after: lambda should still exist
     */
    @Test
    public void testGetAuthKey() throws Exception {
        Identifier id = new Identifier(GET);

        // check before condition
        assertTrue(controller.lambdaExists(id));

        AuthKey key = controller.getAuthKey(id);

        assertNotNull(key);
    }

    /**
     * tests statelessness of lambdas
     *
     * a function should always return the same result on execution
     *
     * before: the lambda to be executed exists
     * after: the lambda returns the same result on every execution
     *
     * here also a test lambda is used to show it
     * the lambda tries to create a file; if does not exists it returns 'created'
     * if the file doesn't exist it returns 'already exists'
     * every call should return 'created' if the lambda is stateless
     */
    @Test
    public void testStatelessness() throws Exception {
        Identifier id = new Identifier(STATELESS);

        // check before condition
        assertTrue(controller.lambdaExists(id));

        ExecuteConfig config = TestUtils.buildExecConfig(1, null);
        String result1 = controller.run(id, config);
        String result2 = controller.run(id, config);

        assertEquals(result1, result2);
        assertTrue(controller.lambdaExists(id));
    }

    @After
    public void tearDown() {
    }

    @AfterClass
    public static void afterClass() throws Exception {
        RuntimeController controller = RuntimeController.getInstance();

        // delete uploaded test images
        for (Lambda lambda : lambdas) {
            if (controller.lambdaExists(lambda.getName())) {
                controller.deleteImage(lambda.getName());
            }
        }
    }
}