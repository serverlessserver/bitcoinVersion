package edu.teco.serverless.test.model.servicelayer.lambdaruntime;

import edu.teco.serverless.model.lambda.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by steffen on 06.03.17.
 *
 * contains some utility methods to make testing easier
 */
public abstract class TestUtils {
    public static Lambda buildLambda(String name, String language, String lambdaCode, String[] libraries) {
        Identifier id = new Identifier(name);
        Language lan = new Language(language);
        Code code = new Code(lambdaCode);
        List<Library> libs = new ArrayList<>();
        if (libraries != null) {
            for (String l : libraries) {
                libs.add(new Library(l));
            }
        }

        Lambda.Builder lambdaBuilder = Lambda.newLambda();
        RuntimeAttributes.Builder attrBuilder = RuntimeAttributes.newRuntimeAttributes();
        return lambdaBuilder.name(id).runtimeAttributes(attrBuilder.code(code).language(lan).libraries(libs).build())
                .build();
    }

    public static ExecuteConfig buildExecConfig(int cycles, String[] parameters) {
        ExecuteConfig.Builder builder = ExecuteConfig.newExecuteConfig();
        RunCycles runCycles = new RunCycles(cycles);
        List<Parameter>  pars = new ArrayList<>();
        if (parameters != null) {
            for (String p : parameters) {
                pars.add(new Parameter(p));
            }
        }

        return builder.runCycles(runCycles).parameterList(pars).build();
    }

    public static String loadFileContent(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line + '\n');
        }
        return sb.toString();
    }
}
