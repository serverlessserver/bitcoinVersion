package edu.teco.serverless.test.model.servicelayer.service;

import edu.teco.serverless.auth.authFacade.AuthFacade;
import edu.teco.serverless.auth.authFacade.AuthFacadeImpl;
import edu.teco.serverless.model.exception.lambda.LambdaDuplicatedNameException;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade;
import edu.teco.serverless.model.servicelayer.service.LambdaManagerFacadeImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Created by Kolner on 1/28/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LambdaManagerFacadeImplTest {
    @Autowired
    private LambdaManagerFacade lambdaManager;
    private Lambda lambda;
    private Lambda lambda1;
    private Lambda lambda2;
    private Lambda updatedLambda;
    private ExecuteConfig executeConfig;


     AuthFacade authenticatior= Mockito.mock(AuthFacadeImpl.class);



    @Before
    public void setUp() throws Exception {
        Lambda.Builder lambdaBuilder = Lambda.newLambda();
        RuntimeAttributes.Builder runtimeBuilder = RuntimeAttributes.newRuntimeAttributes();
        ExecuteConfig.Builder executeBuilder = ExecuteConfig.newExecuteConfig();
        String code = "print('Hello, world!')";
        String code3 = "print('Hello, world!')"+'\n';
        String code2 = "print('Hello, OS!')";
        String language = "Python3";
       // String lib1 = "urlib3";
        String lib1 = "";
        List<Library> libs = new ArrayList<>();
        List<Parameter> params = new ArrayList<>();
        libs.add(new Library(lib1));
        params.add(new Parameter(lib1));
        RuntimeAttributes runtimeAttributes = runtimeBuilder.code(new Code(code)).language(new Language(language)).
                libraries(libs).build();
        RuntimeAttributes runtimeAttributes2 = runtimeBuilder.code(new Code(code2)).language(new Language(language)).
                libraries(libs).build();
        RuntimeAttributes runtimeAttributes3 = runtimeBuilder.code(new Code(code3)).language(new Language(language)).
                libraries(libs).build();
        String name = "printing";
        String name1 = "osprinting";
        lambda = lambdaBuilder.runtimeAttributes(runtimeAttributes).name(new Identifier(name)).build();
        lambda1 = lambdaBuilder.runtimeAttributes(runtimeAttributes3).name(new Identifier(name)).build();
        lambda2=lambdaBuilder.runtimeAttributes(runtimeAttributes2).name(new Identifier(name1)).build();
        int times = 1;
        executeConfig = executeBuilder.parameterList(params).runCycles(new RunCycles(times)).build();
        updatedLambda = lambdaBuilder.runtimeAttributes(runtimeAttributes2).name(new Identifier(name)).build();
       //doReturn(new AuthKey("12345")).when(runTime).buildImage(lambda);

        //when(runTime.buildImage(lambda)).thenReturn(new AuthKey("12345"));
        when(authenticatior.generateMasterToken(any(),any())).thenReturn("345");


    }

    @After
    public void tearDown() throws Exception {
    }


    /**
     * tests adding lambda to the system.
     * before : lambda must NOT exist in the system, lambda must be valid.
     * after : any exceptions can not be thrown, added lambda can be executed, updated, extracted, deleted.
     */
    @Test
    public void addValidLambda() throws RuntimeConnectException, IOException, LanguageNotSupportedException, TimeExceededException {
        when(authenticatior.generateMasterToken(any(),any())).thenReturn("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmludGluZyIsInJvbGUiOiJST0xFX01BU1RFUiIsImRvY2tlckhhc2giOiJlM2I1YTY1NDBiMzgifQ.OqBMy5aZHEdIaMS0kW9WN7ZShDo7LEJiwnK4C8BSGVWqLj6X9y0fQxDKjCULUtFsIVv3m9keFiaCU4UXFGaA9w");
        assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmludGluZyIsInJvbGUiOiJST0xFX01BU1RFUiIsImRvY2tlckhhc2giOiJlM2I1YTY1NDBiMzgifQ.OqBMy5aZHEdIaMS0kW9WN7ZShDo7LEJiwnK4C8BSGVWqLj6X9y0fQxDKjCULUtFsIVv3m9keFiaCU4UXFGaA9w", lambdaManager.addLambda(lambda));
    }



    /**
     * tests adding lambda to the system.
     * before : lambda must already exist in the system.
     * after : must be thrown exception, because lambda to be added already exists in the system.
     *
     * @throws LambdaDuplicatedNameException
     */
    @Test(expected = LambdaDuplicatedNameException.class)
    public void addExistedLambda() throws LambdaDuplicatedNameException, RuntimeConnectException, FileNotFoundException, UnsupportedEncodingException, LanguageNotSupportedException {

        lambdaManager.addLambda(lambda);
        lambdaManager.addLambda(lambda);
    }


    /**
     * tests execution of lambda.
     * before : lambda must exist in the system.
     * after : any exceptions can not be thrown, executed lambda can be one more executed, updated, extracted, deleted.
     */
    @Test
    public void executeValidLambda() throws RuntimeConnectException, FileNotFoundException, TimeExceededException, UnsupportedEncodingException, LanguageNotSupportedException {
        //lambdaManager.addLambda(lambda);

        assertEquals("Hello, world!"+'\n', lambdaManager.executeLambda(lambda.getName().getIdentifier(), executeConfig));
    }

    /**
     * tests execution of lambda.
     * before : lambda must NOT exist in the system.
     * after : must be thrown exception, because lambda to be executed was NOT found.
     *
     * @throws LambdaNotFoundException
     */
    @Test(expected = LambdaNotFoundException.class)
    public void executeInvalidLambda() throws LambdaNotFoundException, RuntimeConnectException, TimeExceededException {

        lambdaManager.executeLambda(lambda2.getName().getIdentifier(), executeConfig);
    }

    /**
     * tests updating of lambda.
     * before : lambda to be updated must exist in the system
     * after : any exceptions can not be thrown, updated lambda can be executed, updated, extracted, deleted.
     */
    @Test
    public void updateLambda() throws RuntimeConnectException, LanguageNotSupportedException, TimeExceededException, UnsupportedEncodingException {
        when(authenticatior.generateMasterToken(any(),any())).thenReturn("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmludGluZyIsInJvbGUiOiJST0xFX01BU1RFUiIsImRvY2tlckhhc2giOiIwM2NhMTkwZjlhNDAifQ.u48xFj1VuLZhuf6CzDt22qsCmmQoU6R4IV7yeDbzZXciPnsm-FqJ3q8FzHYpxUk2N9dfmItNHoAW2PT28u4vLA");

        assertEquals("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmludGluZyIsInJvbGUiOiJST0xFX01BU1RFUiIsImRvY2tlckhhc2giOiIwM2NhMTkwZjlhNDAifQ.u48xFj1VuLZhuf6CzDt22qsCmmQoU6R4IV7yeDbzZXciPnsm-FqJ3q8FzHYpxUk2N9dfmItNHoAW2PT28u4vLA", lambdaManager.updateLambda(lambda.getName().getIdentifier(), updatedLambda));


    }

    /**
     * tests extracting of lambda.
     * before : lambda must exist in the system.
     * after : any exceptions can not be thrown, extracted lambda can be executed, updated, extracted, deleted.
     */
    @Test
    public void getLambda() throws RuntimeConnectException, FileNotFoundException, UnsupportedEncodingException, LanguageNotSupportedException {

       // lambdaManager.addLambda(lambda);
        //assertEquals(lambda1, lambdaManager.getLambda(lambda.getName().getIdentifier()));
        lambdaManager.getLambda(lambda.getName().getIdentifier());

    }

    /**
     * tests deletion of lambda.
     * before : lambda must exist in the system.
     * after : exception must be thrown while deleted lambda is tried to be extracted.
     */
    @Test(expected = LambdaNotFoundException.class)
    public void deleteLambda() throws LambdaNotFoundException, RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException, TimeExceededException, FileNotFoundException {
      //  lambdaManager.addLambda(lambda);
        lambdaManager.deleteLambda(lambda.getName().getIdentifier());
        lambdaManager.getLambda(lambda.getName().getIdentifier());

    }


}