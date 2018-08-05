package helloworld;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.commons.lang3.StringUtils;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom", "somevalue");

        try {
            final String functionVersion = context.getFunctionVersion();

            if (mustFail(input, functionVersion)) {
                throw new RuntimeException("Failed by query parameter injection");
            }

            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"function-version\": \"%s\", \"location\": \"%s\" }", functionVersion, pageContents);
            APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
            apiGatewayProxyResponseEvent.setHeaders(headers);
            apiGatewayProxyResponseEvent.setStatusCode(200);
            apiGatewayProxyResponseEvent.setBody(output);
            return apiGatewayProxyResponseEvent;

        } catch (IOException e) {
            APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent();
            apiGatewayProxyResponseEvent.setHeaders(headers);
            apiGatewayProxyResponseEvent.setStatusCode(500);
            apiGatewayProxyResponseEvent.setBody(String.format("{ \"error\": \"%s\"}", e.getMessage()));
            return apiGatewayProxyResponseEvent;
        }
    }

    private boolean mustFail(APIGatewayProxyRequestEvent input, String functionVersion) {
        if(input.getQueryStringParameters() == null || functionVersion == null)
            return false;

         return StringUtils.equalsIgnoreCase(input.getQueryStringParameters().get("failversion"), functionVersion);

    }

    private String getPageContents(String address) throws IOException {

        StringJoiner lines = new StringJoiner(System.lineSeparator());

        URL url = new URL(address);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lines.add(line);
            }
        }

        return lines.toString();
    }
}
