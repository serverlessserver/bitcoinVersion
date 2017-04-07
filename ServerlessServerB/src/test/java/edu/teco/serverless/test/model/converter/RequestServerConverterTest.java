package edu.teco.serverless.test.model.converter;

import edu.teco.serverless.model.converter.RequestServerConverter;
import edu.teco.serverless.model.lambda.Code;
import edu.teco.serverless.model.lambda.ExecuteConfig;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.lambda.Lambda;
import edu.teco.serverless.model.lambda.Language;
import edu.teco.serverless.model.lambda.Library;
import edu.teco.serverless.model.lambda.Parameter;
import edu.teco.serverless.model.lambda.RunCycles;
import edu.teco.serverless.model.lambda.RuntimeAttributes;
import edu.teco.serverless.model.messages.ExecuteLambdaRequest;
import edu.teco.serverless.model.messages.RuntimeAttributesRequest;
import edu.teco.serverless.model.messages.UploadLambdaRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Kristina on 07/03/2017.
 */
public class RequestServerConverterTest {
    RuntimeAttributes runtimeAttributes;
    RuntimeAttributesRequest runtimeAttributesRequest;
    Lambda lambda;
    UploadLambdaRequest lambdaRequest;
    ExecuteConfig executeConfig;
    ExecuteLambdaRequest executeRequest;

    @Before
    public void setUp() throws Exception {
        RuntimeAttributes.Builder runtimeBuilder = RuntimeAttributes.newRuntimeAttributes();
        RuntimeAttributesRequest.Builder runtimeRequestBuilder = RuntimeAttributesRequest.newRuntimeAttributesRequest();
        Lambda.Builder lambdaBuilder = Lambda.newLambda();
        UploadLambdaRequest.Builder lambdaRequestBuilder = UploadLambdaRequest.newUploadLambdaRequest();
        ExecuteConfig.Builder executeBuilder = ExecuteConfig.newExecuteConfig();
        ExecuteLambdaRequest.Builder executeRequestBuilder = ExecuteLambdaRequest.newExecuteLambdaRequest();
        String code = "print('Hello, world!')";
        String language = "Python3";
        String lib1 = "urlib3";
        List<Library> libs = new ArrayList<>();
        libs.add(new Library(lib1));
        List<String> stringLibs = new ArrayList<>();
        stringLibs.add(lib1);
        runtimeAttributes = runtimeBuilder.code(new Code(code)).language(new Language(language)).
                libraries(libs).build();
        runtimeAttributesRequest = runtimeRequestBuilder.code(code).language(language).libraries(stringLibs).build();
        String name = "printing";
        lambda = lambdaBuilder.runtimeAttributes(runtimeAttributes).name(new Identifier(name)).build();
        lambdaRequest = lambdaRequestBuilder.name(name).runtimeAttributes(runtimeAttributesRequest).build();
        int times = 1;
        List<Parameter> params = new ArrayList<>();
        String param1= "String";
        params.add(new Parameter(param1));
        executeRequest = executeRequestBuilder.times(times).parameters(stringLibs).build();
        executeConfig = executeBuilder.runCycles(new RunCycles(times)).parameterList(params).build();


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void runtimeAttributesRequestToRuntimeAttributes() throws Exception {
        assertEquals(runtimeAttributes, RequestServerConverter.runtimeAttributesRequestToRuntimeAttributes(runtimeAttributesRequest));
    }

    @Test
    public void uploadRequestToLambda() throws Exception {
        assertEquals(lambda, RequestServerConverter.uploadRequestToLambda(lambdaRequest));
    }

    @Test
    public void executeRequestToExecuteConfig() throws Exception {
        assertEquals(executeConfig, RequestServerConverter.executeRequestToExecuteConfig(executeRequest));
    }

    @Test
    public void runtimeAttributesToRuntimeAttributesRequest() throws Exception {
        assertEquals(runtimeAttributesRequest,
                RequestServerConverter.runtimeAttributesToRuntimeAttributesRequest(runtimeAttributes));
    }

    @Test
    public void lambdaToUploadRequest() throws Exception {
        assertEquals(lambdaRequest, RequestServerConverter.lambdaToUploadRequest(lambda));
    }

    @Test
    public void arrayToString() throws Exception {
        List<String> arr = new ArrayList<>();
        String input1 = "line";
        String input2 = "code";
        arr.add(input1);
        arr.add(input2);
        RequestServerConverter.arrayToString(arr);
    }

    @Test
    public void setToTrue() throws Exception {
        RequestServerConverter.setToTrue();
    }

}