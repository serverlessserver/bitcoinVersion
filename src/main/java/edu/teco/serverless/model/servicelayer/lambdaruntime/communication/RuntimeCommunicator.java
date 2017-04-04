package edu.teco.serverless.model.servicelayer.lambdaruntime.communication;

import edu.teco.serverless.model.lambda.Parameter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages the lambdaruntime - put, set and send command to execute.
 */
@Component
public class RuntimeCommunicator {
    private static RuntimeCommunicator instance;

    private Runtime runtime;

    private RuntimeCommunicator() {

    }

    /**
     * @return the only instance of RuntimeCommunicator
     */
    public static RuntimeCommunicator getInstance() {
	    if (instance == null) {
		    instance = new RuntimeCommunicator();
	    }
	    return instance;
    }

    private void startRuntimeService() throws RuntimeConnectException {
        /*try {
            if (runtime.exec("service docker start") == null) {
                throw new RuntimeConnectException("runtime can't be started");
            }
        } catch (IOException e) {
            throw new RuntimeConnectException("runtime can't be started");
        }*/
    }

    /**
     * initializes the RuntimeCommunicator, e.g. starts the runtime service
     * @throws RuntimeConnectException if the runtime service cannot be started or communication doesn't work
     */
    public void init() throws RuntimeConnectException {
        runtime = Runtime.getRuntime();

        startRuntimeService();
        sendTestRequest();
    }

    private void sendTestRequest() {

    }

    /**
     * sends a command to the runtime service
     * @param cmd the command
     * @return the result of the command
     * @throws RuntimeConnectException if the runtime service doesn't answer
     */
    public String executeCommand(RuntimeCommand cmd) throws RuntimeConnectException, TimeExceededException {
        /*ProcessBuilder pb = new ProcessBuilder(generateArgsForProcess(cmd));
        Process p;
        try {
            p = pb.start();
        }
        catch (IOException e) {
            throw new RuntimeConnectException();
        }
        String output = handleProcess(p);
        return trimOutput(cmd.getType(), output);*/
        return executeCommand(cmd, 0);
    }

    /**
     * sends a command to the runtime service
     * @param cmd the command
     * @param limit the time limit for the command (if limit > 0)
     * @return the result of the command
     * @throws RuntimeConnectException if the runtime service doesn't answer
     */
    public String executeCommand(RuntimeCommand cmd, int limit) throws RuntimeConnectException, TimeExceededException {
        ProcessBuilder pb = new ProcessBuilder(generateArgsForProcess(cmd));
        Process p;
        try {
            p = pb.start();

            try {
                if (limit > 0) {
                    p.waitFor(limit, TimeUnit.SECONDS);
                    if (p.isAlive()) {
                        p.destroy();
                        throw new TimeExceededException();
                    }
                }
                else {
                    p.waitFor();
                }
            }
            catch (InterruptedException e) {}


        }
        catch (IOException e) {
            throw new RuntimeConnectException();
        }
        String output = handleProcess(p);
        return trimOutput(cmd.getType(), output);
    }

    // method to extract relevant information out of the output of the runtime
    private String trimOutput(CommandType type, String output) {
        switch (type) {
            case BUILD:
                // extract image id
                final String search = "Successfully built ";
                int index = output.indexOf(search);
                return output.substring(index + search.length(), output.indexOf("\n", index));

            default:
                return output;
        }
    }

    private List<String> generateArgsForProcess(RuntimeCommand cmd) {
        List<String> args = new ArrayList<String>();

        // Support for Windows Systems
       // if (System.getProperty("os.name").toLowerCase().contains("windows")) {


         // args.add("C:\\Program Files\\Docker Toolbox\\docker.exe");
      // }

        // docker command and sub-command
        args.add("docker");
        args.add(cmd.getType().toString());

        // set options for the call
        switch (cmd.getType()) {
            case BUILD:
                args.add("-t");
                args.add(cmd.getParameters().get(0).getParameter());
                args.add("-f");     // Dockerfile not in current directory
                args.add(cmd.getParameters().get(1).getParameter());
                args.add(".");
                break;
            case RUN:
                args.add("--rm");   // remove container after run
                args.add("-i");
                copyArgs(args, cmd.getParameters());
                break;

            case REMOVE:
                args.add("-f");     // force removal
                copyArgs(args, cmd.getParameters());
                break;

            case PULL:
            case INFO:
                copyArgs(args, cmd.getParameters());
            default:
                break;
        }

        return args;
    }

    private void copyArgs(List<String> target, List<Parameter> source) {
        for (Parameter par : source) {
            target.add(par.getParameter());
        }
    }

    private String handleProcess(Process p) throws RuntimeConnectException {
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        StringBuilder sbOut = new StringBuilder();
        StringBuilder sbErr = new StringBuilder();
        String lineStdout;
        String lineStderr;

        try {
            /*while ((lineStdout = stdout.readLine()) != null | (lineStderr = stderr.readLine()) != null) {
                sbOut.append(lineStdout != null ? lineStdout + "\n" : "");
                sbErr.append(lineStderr != null ? lineStderr + "\n" : "");
            }*/
            while ((lineStdout = stdout.readLine()) != null) {
                sbOut.append(lineStdout + "\n");
            }
            while ((lineStderr = stderr.readLine()) != null) {
                sbErr.append(lineStderr + "\n");
            }
        }
        catch (IOException e) {
            throw new RuntimeConnectException("error while reading runtime output");
        }

        /*if (sbErr.length() != 0 && !sbErr.toString().startsWith("SECURITY WARNING")) {
            throw new RuntimeConnectException(sbErr.toString());
        }*/

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sbOut.toString();
    }





    /*
    This way of communicating with the socket doesn't work. The special socket /var/run/docker.sock
    is needed, which is not natively supported by Java

    private HttpURLConnection con;
    private OutputStreamWriter runtimeWriter;
    private BufferedReader runtimeReader;

    public void init() throws RuntimeConnectException {
        startRuntimeService();

        // open the connection the docker daemon
        try {
            URL url = new URL(RUNTIME_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            runtimeWriter = new OutputStreamWriter(con.getOutputStream());
            runtimeReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        catch (IOException e) {
            // catch the IOException and throw a RuntimeConnectException instead
            throw new RuntimeConnectException();
        }

        sendTestRequest();
    }

    private void sendTestRequest() throws RuntimeConnectException {
        RuntimeCommand command = new RuntimeCommand(CommandType.INFO);
        String result = executeCommand(command);
        if (!result.startsWith("Containers")) {
            throw new RuntimeConnectException("can't send test request to runtime");
        }
    }

    private String convertCommand(RuntimeCommand cmd) {
        switch (cmd.getType()) {
            case BUILD:
                break;
            case RUN:
                break;
            default:
        }

        return "";
    }

    public String executeCommand(RuntimeCommand cmd) {
	    String result = "";
	    try {
		    runtimeWriter.write(convertCommand(cmd));
		    result = runtimeReader.readLine();
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
	    
        return result;
    }*/
}
