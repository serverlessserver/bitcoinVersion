package edu.teco.serverless.test.apicontroller;

import com.jayway.restassured.RestAssured;
import edu.teco.serverless.apicontroller.RestApiController;
import edu.teco.serverless.model.messages.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Kolner on 1/27/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RestApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RestApiController restApiController;

    private UploadLambdaRequest uploadLambdaRequest;
    private UploadLambdaRequest uploadLambdaRequestWithSameName;
    private UploadLambdaRequest badUploadLambdaRequest;
    private UploadLambdaResponse uploadResponse;
    private ExecuteLambdaResponse executeLambdaResponse;
    private UploadLambdaRequest updateLambdaRequest;
    private ExecuteLambdaRequest executeLambdaRequest;
    private ExecuteLambdaRequest badExecuteLambdaRequest;


    @Before
    public void setUp() throws Exception {
        RuntimeAttributesRequest.Builder runtimeRequestBuilder = RuntimeAttributesRequest.newRuntimeAttributesRequest();
        UploadLambdaRequest.Builder lambdaRequestBuilder = UploadLambdaRequest.newUploadLambdaRequest();
        ExecuteLambdaRequest.Builder executeRequestBuilder = ExecuteLambdaRequest.newExecuteLambdaRequest();
        UploadLambdaResponse.Builder lambdaResponse = UploadLambdaResponse.newUploadLambdaResponse();
        ExecuteLambdaResponse.Builder executeResponse = ExecuteLambdaResponse.newExecuteLambdaResponse();
        String code = "print('Hello, world!')";
        String code1 = "print('Hello, OS!')";
        String language = "Python3";
        //String lib1 = "urlib3";
        String lib1 = "";
        List<String> stringLibs = new ArrayList<>();
        stringLibs.add(lib1);
        RuntimeAttributesRequest runtimeAttributesRequest = runtimeRequestBuilder.code(code).
                language(language).libraries(stringLibs).build();
        RuntimeAttributesRequest runtimeAttributesRequest1 = runtimeRequestBuilder.code(code).
                libraries(stringLibs).build();
        RuntimeAttributesRequest runtimeAttributesRequest2 = runtimeRequestBuilder.code(code1).
                language(language).libraries(stringLibs).build();
        String name = "hello";
        uploadLambdaRequest = lambdaRequestBuilder.name(name).runtimeAttributes(runtimeAttributesRequest).build();
        badUploadLambdaRequest = lambdaRequestBuilder.name(name).runtimeAttributes(runtimeAttributesRequest1).build();
        uploadLambdaRequestWithSameName = lambdaRequestBuilder.name(name).runtimeAttributes(runtimeAttributesRequest2).build();

        int times = 1;
        executeLambdaRequest = executeRequestBuilder.times(times).parameters(stringLibs).build();
        badExecuteLambdaRequest = executeRequestBuilder.parameters(stringLibs).build();
        uploadResponse=lambdaResponse.token("").link("").build();
        executeLambdaResponse=executeResponse.message("").build();


    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * tests the lambda's upload into system.
     * before : system must be run, lambda must NOT exist in system, request must be valid.
     * after : lambda exists in system, can be updated, executed, deleted, showed and can be produced
     *         subtoken for third-party access.
     */
    @Test
    public void postValidLambda() throws RuntimeConnectException, FileNotFoundException, UnsupportedEncodingException, LanguageNotSupportedException {


        //assertEquals(restApiController.getLambda(uploadLambdaRequest.getName()).getStatusCode(), HttpStatus.NOT_FOUND);

        //ResponseEntity<UploadLambdaResponse> response = restApiController.postLambda(uploadLambdaRequest);
        //assertEquals(new ResponseEntity<UploadLambdaResponse> (uploadResponse,HttpStatus.CREATED),response);

        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);
    }





    /**
     * tests the lambda's upload into system.
     * before : system must be run, lambda must already exist in system.
     * after : lambda to be uploaded is NOT in system, received response from system with status "FORBIDDEN".
     */
    @Test
    public void postExistedLambda() throws RuntimeConnectException, LanguageNotSupportedException, UnsupportedEncodingException {

        //restApiController.postLambda(uploadLambdaRequest);
        //assertEquals(restApiController.postLambda(uploadLambdaRequestWithSameName).getStatusCode(), HttpStatus.FORBIDDEN);
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(403);

    }

    /**
     * tests the lambda's upload into system.
     * before : system must be run, lambda must NOT exist in system, user's JSON with request must be invalid.
     * after : lambda to be uploaded is NOT in system, received response from system with status "BAD REQUEST",
     *         can NOT be executed, updated, showed and can NOT be produced subtoken for third-party access.
     */
    @Test
    public void postInvalidLambda() throws RuntimeConnectException, FileNotFoundException, UnsupportedEncodingException, LanguageNotSupportedException {


        //assertEquals(restApiController.postLambda(badUploadLambdaRequest).getStatusCode(), HttpStatus.BAD_REQUEST);
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +

                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(400);

    }


    /**
     * tests the execution of the lambda.
     * before : lambda must exist in the system, user's JSON request must be valid.
     * after : received response with status "Unauthorized", executed lambda exists in system, can be updated, executed, deleted,
     *         showed and can be produced subtoken for third-party access.
     */
    @Test
    public void postLambdaUnauthExecute() throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException, TimeExceededException {
      // restApiController.postLambda(uploadLambdaRequest);

        // assertEquals(new ResponseEntity<ExecuteLambdaResponse> (executeLambdaResponse,HttpStatus.OK),
         //        restApiController.postLambda(uploadLambdaRequest.getName(), executeLambdaRequest));
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
       RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);

        String body1 = "{" +

                "}";
        RestAssured.given().port(port).contentType("application/json").body(body1).when().post("/lambdas/hello/execute").then().statusCode(401);

    }

    /**
     * tests the execution of the lambda.
     *
     * before : lambda must exist in the system, user's JSON request must be NOT valid.
     * after : received response with status "BAD REQUEST", NOT executed lambda exists in system, can be updated, executed, deleted,
     *         showed and can be produced subtoken for third-party access..
     */
    @Test
    public void postLambdaInvalidExecute() throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException, TimeExceededException {
        //restApiController.postLambda(uploadLambdaRequest);
     // assertEquals(restApiController.postLambda(uploadLambdaRequest.getName(), badExecuteLambdaRequest).getStatusCode(), HttpStatus.BAD_REQUEST);
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);

        String body1 = " ";
        RestAssured.given().port(port).contentType("application/json").body(body1).when().post("/lambdas/{hello}/execute").then().statusCode(400);

        }

    /**
     * tests updating of the lambda
     *
     * before : lambda must exist in the system
     * after : updated lambda exists in system, can be updated, executed, deleted, showed and can be produced
     *         subtoken for third-party access.
     */
    @Test
    public void putLambda() throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException, TimeExceededException {
       // restApiController.postLambda(uploadLambdaRequest);

       //assertEquals(new ResponseEntity<UploadLambdaResponse> (uploadResponse,HttpStatus.OK),
       //        restApiController.putLambda(updateLambdaRequest.getName(), updateLambdaRequest));
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);

        String body1 = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body1).when().put("/lambdas/{hello}").then().statusCode(200);


    }

    /**
     * tests extracting of lambda's configuration.
     * before : lambda must exist in the system.
     * after : lambda must be still in the system and can be updated, executed, deleted, showed and can be produced
     *         subtoken for third-party access
     */
    @Test
    public void getLambda() throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException, FileNotFoundException {
      // restApiController.postLambda(uploadLambdaRequest);

       //assertEquals(new ResponseEntity<UploadLambdaResponse> (uploadResponse,HttpStatus.OK),restApiController.getLambda(uploadLambdaRequest.getName()));
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);


        RestAssured.given().port(port).contentType("application/json").when().get("/lambdas/{hello}").then().statusCode(200);


    }

    /**
     * tests deletion of lambda
     * before : lambda must exist in the system
     * after : lambda must NOT exist in the system, can NOT be updated, executed, deleted, showed and can be produced
     *         subtoken for third-party access.
     */
    @Test
    public void deleteLambda() throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException, TimeExceededException {
       // restApiController.postLambda(uploadLambdaRequest);

        // assertEquals(restApiController.deleteLambda(uploadLambdaRequest.getName()).getStatusCode(), HttpStatus.OK);
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);


        RestAssured.given().pathParameter("name", "hello").port(port).contentType("application/json").when().delete("/lambdas/{hello}").then().statusCode(200);

    }

    /**
     * tests production of subtoken for lambda.
     * before : lambda must exist in the system.
     *
     * after : lambda must be still in the system and can be updated, executed, deleted, showed and can be produced
     *         subtoken for third-party access.
     */
    @Test
    public void getSubtoken() throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException {
        //restApiController.postLambda(uploadLambdaRequest);

        //assertEquals(restApiController.getSubtoken(uploadLambdaRequest.getName(), "70 minutes").getStatusCode(), HttpStatus.OK);
        String body = "{" +
                "\"name\":\"hello\"," +
                "\"runtimeAttributes\": {" +
                "\"language\": \"Python3\"," +
                "\"code\": \"print('Hello world')\"" +
                "}" +
                "}";
        RestAssured.given().port(port).contentType("application/json").body(body).when().post("/lambdas").then().statusCode(201);

        RestAssured.given().port(port).contentType("application/json").parameter("expiryDate","70 minutes").when().get("/lambdas/{hello}").then().statusCode(201);



    }

}
