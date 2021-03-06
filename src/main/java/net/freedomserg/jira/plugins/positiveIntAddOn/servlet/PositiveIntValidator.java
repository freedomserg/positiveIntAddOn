package net.freedomserg.jira.plugins.positiveIntAddOn.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class PositiveIntValidator extends HttpServlet {

    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final TemplateRenderer templateRenderer;
    public final static String INVALID_INPUT_RESPONSE = "Sorry, invalid input";
    public final static String VALID_INPUT_RESPONSE = "Good work";

    public PositiveIntValidator(
            UserManager userManager,
            LoginUriProvider loginUriProvider,
            TemplateRenderer templateRenderer) {

        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response
                                                                    ) throws ServletException, IOException {
        UserProfile userProfile = userManager.getRemoteUser(request);
        if (userProfile != null) {
            response.setContentType("text/html;charset=utf-8");
            templateRenderer.render("/view/validator-get.vm", response.getWriter());
        } else {
            redirectToLogin(request, response);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response
                                                                    ) throws ServletException, IOException {
        String userInput = request.getParameter("number");
        final Map<String, Object> context = new HashMap<String, Object>();
        String userResponse = validate(userInput);
        context.put("response", userResponse);
        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("/view/validator-post.vm", context, response.getWriter());
    }

    public String validate(String userInput) {
        int result = 0;
        try {
            result = Integer.parseInt(userInput);
        } catch (NumberFormatException ex) {
            return INVALID_INPUT_RESPONSE;
        }
        return result <= 0 ? INVALID_INPUT_RESPONSE : VALID_INPUT_RESPONSE;
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI uri = URI.create(request.getRequestURL().toString());
        response.sendRedirect(loginUriProvider.getLoginUri(uri).toASCIIString());
    }
}