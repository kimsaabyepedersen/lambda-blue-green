package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;


public class AppTest {

    @Test
    public void successfulResponse() {
        helloworld.App app = new helloworld.App();

        Context ctx = Mockito.mock(Context.class);
        given(ctx.getFunctionVersion()).willReturn("v1");

        APIGatewayProxyResponseEvent result = app.handleRequest(new APIGatewayProxyRequestEvent(), ctx);
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals(result.getHeaders().get("Content-Type"), "application/json");
        String content = result.getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"function-version\": \"v1\""));
        assertTrue(content.contains("\"location\": \""));
        int index = content.indexOf("\"location\": ") + 13;
        String ip = content.substring(index, content.indexOf("\"", index));
        assertTrue(ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"));

    }

    @Test(expected = RuntimeException.class)
    public void failureInjection() {
        helloworld.App app = new helloworld.App();

        Context ctx = Mockito.mock(Context.class);
        given(ctx.getFunctionVersion()).willReturn("v1");

        APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = new APIGatewayProxyRequestEvent();
        HashMap<String, String> queryStringParameters = new HashMap<>();
        queryStringParameters.put("failversion", "v1");
        apiGatewayProxyRequestEvent.setQueryStringParameters(queryStringParameters);
        app.handleRequest(apiGatewayProxyRequestEvent, ctx);

    }

    @Test
    public void failureInjectionOtherVersion() {
        helloworld.App app = new helloworld.App();

        Context ctx = Mockito.mock(Context.class);
        given(ctx.getFunctionVersion()).willReturn("v1");

        APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = new APIGatewayProxyRequestEvent();
        HashMap<String, String> queryStringParameters = new HashMap<>();
        queryStringParameters.put("failversion", "v110");
        apiGatewayProxyRequestEvent.setQueryStringParameters(queryStringParameters);
        String content = app.handleRequest(apiGatewayProxyRequestEvent, ctx).getBody();
        assertNotNull(content);
        assertTrue(content.contains("\"function-version\": \"v1\""));

    }

}
