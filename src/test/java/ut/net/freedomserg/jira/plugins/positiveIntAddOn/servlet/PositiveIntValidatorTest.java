package ut.net.freedomserg.jira.plugins.positiveIntAddOn.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import net.freedomserg.jira.plugins.positiveIntAddOn.servlet.PositiveIntValidator;
import org.junit.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PositiveIntValidatorTest {

    UserManager mockUserManager;
    UserProfile mockUserProfile;
    LoginUriProvider mockLoginUriProvider;
    TemplateRenderer mockTemplateRenderer;
    PositiveIntValidator spyValidator;
    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;

    @Before
    public void setup() {
        mockUserManager = mock(UserManager.class);
        mockUserProfile = mock(UserProfile.class);
        mockLoginUriProvider = mock(LoginUriProvider.class);
        mockTemplateRenderer = mock(TemplateRenderer.class);
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        spyValidator = spy(new PositiveIntValidator
                (mockUserManager, mockLoginUriProvider, mockTemplateRenderer));
    }

    @Test
    public void testDoGetValidUserProfile() throws IOException, ServletException {
        when(mockUserManager.getRemoteUser(mockRequest)).thenReturn(mockUserProfile);

        spyValidator.doGet(mockRequest, mockResponse);

        verify(mockUserManager, times(1)).getRemoteUser(mockRequest);
        verify(mockResponse, times(1)).setContentType(any(String.class));
        verify(mockResponse, times(1)).getWriter();
        verify(mockTemplateRenderer, times(1)).render(any(String.class), any(PrintWriter.class));
        verify(spyValidator, never()).redirectToLogin(mockRequest, mockResponse);
    }

    @Test
    public void testDoGetInvalidUserProfile() throws ServletException, IOException {
        when(mockUserManager.getRemoteUser(mockRequest)).thenReturn(null);
        doNothing().when(spyValidator).redirectToLogin(mockRequest, mockResponse);

        spyValidator.doGet(mockRequest, mockResponse);

        verify(mockUserManager, times(1)).getRemoteUser(mockRequest);
        verify(spyValidator, times(1)).redirectToLogin(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        String userInput = "5";
        String userResponse = "Good work";
        when(mockRequest.getParameter("number")).thenReturn(userInput);
        when(spyValidator.validate(userInput)).thenReturn(userResponse);
        doNothing().when(mockTemplateRenderer).render(
                any(String.class),
                any(HashMap.class),
                any(PrintWriter.class));

        spyValidator.doPost(mockRequest, mockResponse);

        verify(mockRequest, times(1)).getParameter("number");
        verify(spyValidator, times(1)).validate(userInput);
        verify(mockResponse, times(1)).setContentType(any(String.class));
        verify(mockTemplateRenderer, times(1)).render(
                any(String.class),
                any(HashMap.class),
                any(PrintWriter.class));
    }

    @Test
    public void testValidateWithPositiveInt() {
        String userInput = "7";
        String result = spyValidator.validate(userInput);

        assertEquals(PositiveIntValidator.VALID_INPUT_RESPONSE, result);
    }

    @Test
    public void testValidateWithMaxInt() {
        int maxInt = Integer.MAX_VALUE;
        String userInput = String.valueOf(maxInt);
        String result = spyValidator.validate(userInput);

        assertEquals(PositiveIntValidator.VALID_INPUT_RESPONSE, result);
    }

    @Test
    public void testValidateWithLongNumber() {
        int maxInt = Integer.MAX_VALUE;
        String userInput = String.valueOf(maxInt + 1);
        String result = spyValidator.validate(userInput);

        assertEquals(PositiveIntValidator.INVALID_INPUT_RESPONSE, result);
    }

    @Test
    public void testValidateWithNegativeInt() {
        String userInput = "-7";
        String result = spyValidator.validate(userInput);

        assertEquals(PositiveIntValidator.INVALID_INPUT_RESPONSE, result);
    }

    @Test
    public void testValidateWithDoubleNumber() {
        String userInput = "7.1";
        String result = spyValidator.validate(userInput);

        assertEquals(PositiveIntValidator.INVALID_INPUT_RESPONSE, result);
    }

    @Test
    public void testValidateWithRandomString() {
        String userInput = "abrakadabra";
        String result = spyValidator.validate(userInput);

        assertEquals(PositiveIntValidator.INVALID_INPUT_RESPONSE, result);
    }
}
