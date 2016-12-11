package it.net.freedomserg.jira.plugins.positiveIntAddOn.servlet;

import net.freedomserg.jira.plugins.positiveIntAddOn.servlet.PositiveIntValidator;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class PositiveIntValidatorIntegrationTest {

    private HttpClient httpClient;
    private String baseUrl;
    private String servletUrlWithLogin;
    private String servletUrlWithoutLogin;
    private String userName;
    private String password;

    @Before
    public void setup() {
        httpClient = new DefaultHttpClient();
        baseUrl = System.getProperty("baseurl");
        userName = "admin";
        password = "admin";
        servletUrlWithLogin = baseUrl + "/plugins/servlet/positiveintvalidator" +
                "?" + "os_username=" + userName + "&os_password=" + password;
        servletUrlWithoutLogin = baseUrl + "/plugins/servlet/positiveintvalidator";
    }

    @After
    public void tearDown() {
        httpClient.getConnectionManager().shutdown();
    }

    @Test
    public void testDoGetWithLogin() throws IOException {
        String responseBody = executeDoGet(servletUrlWithLogin);

        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        assertTrue(responseBody.contains("Please, enter a positive integer:"));
    }

    @Test
    public void testDoGetWithoutLogin() throws IOException {
        String responseBody = executeDoGet(servletUrlWithoutLogin);

        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        assertFalse(responseBody.contains("Please, enter a positive integer:"));
        assertTrue(responseBody.contains("login-form-username"));
        assertTrue(responseBody.contains("login-form-password"));
    }

    @Test
    public void testDoPostWithPositiveIntEntered() throws IOException {
        String doGetResponseBody = executeDoGet(servletUrlWithLogin);

        assertNotNull(doGetResponseBody);
        assertFalse(doGetResponseBody.isEmpty());
        assertTrue(doGetResponseBody.contains("Please, enter a positive integer:"));

        String input = "17";

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.VALID_INPUT_RESPONSE));
    }

    @Test
    public void testDoPostWithPositiveMaxIntEntered() throws IOException {
        httpClient.execute(new HttpGet(servletUrlWithLogin), new BasicResponseHandler());

        int maxInt = Integer.MAX_VALUE;
        String input = String.valueOf(maxInt);

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.VALID_INPUT_RESPONSE));
    }

    @Test
    public void testDoPostWithLongNumberEntered() throws IOException {
        httpClient.execute(new HttpGet(servletUrlWithLogin), new BasicResponseHandler());

        int maxInt = Integer.MAX_VALUE;
        String input = String.valueOf(maxInt + 1);

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.INVALID_INPUT_RESPONSE));
    }

    @Test
    public void testDoPostWithNegativeIntEntered() throws IOException {
        httpClient.execute(new HttpGet(servletUrlWithLogin), new BasicResponseHandler());

        String input = "-10";

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.INVALID_INPUT_RESPONSE));
    }

    @Test
    public void testDoPostWithDoubleNumberEntered() throws IOException {
        httpClient.execute(new HttpGet(servletUrlWithLogin), new BasicResponseHandler());

        String input = "7.5";

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.INVALID_INPUT_RESPONSE));
    }

    @Test
    public void testDoPostWithRandomTextEntered() throws IOException {
        httpClient.execute(new HttpGet(servletUrlWithLogin), new BasicResponseHandler());

        String input = "abrakadabra";

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.INVALID_INPUT_RESPONSE));
    }

    @Test
    public void testDoPostAndFollowedByDoGet() throws IOException {
        httpClient.execute(new HttpGet(servletUrlWithLogin), new BasicResponseHandler());

        String input = "abrakadabra";

        String doPostResponseBody = executeDoPost(servletUrlWithoutLogin, input);

        assertNotNull(doPostResponseBody);
        assertFalse(doPostResponseBody.isEmpty());
        assertTrue(doPostResponseBody.contains(PositiveIntValidator.INVALID_INPUT_RESPONSE));

        String doGetResponseBody = executeDoGet(servletUrlWithoutLogin);

        assertNotNull(doGetResponseBody);
        assertFalse(doGetResponseBody.isEmpty());
        assertTrue(doGetResponseBody.contains("Please, enter a positive integer:"));
        assertFalse(doGetResponseBody.contains(PositiveIntValidator.INVALID_INPUT_RESPONSE));
        assertFalse(doGetResponseBody.contains(PositiveIntValidator.VALID_INPUT_RESPONSE));
    }

    private String executeDoGet(String servletUrlString) throws IOException {
        HttpGet httpGet = new HttpGet(servletUrlString);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        return httpClient.execute(httpGet, responseHandler);
    }

    private String executeDoPost(String servletUrlString, String userInputParam) throws IOException {
        HttpPost httpPost = new HttpPost(servletUrlString);
        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("number", userInputParam));
        httpPost.setEntity(new UrlEncodedFormEntity(postParameters, StandardCharsets.UTF_8.name()));
        ResponseHandler<String> postResponseHandler = new BasicResponseHandler();
        return httpClient.execute(httpPost, postResponseHandler);
    }
}