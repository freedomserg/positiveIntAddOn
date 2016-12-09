package net.freedomserg.jira.plugins.positiveIntAddOn.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

public class PositiveIntValidator extends HttpServlet{

    private static final Logger log = LoggerFactory.getLogger(PositiveIntValidator.class);
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final TemplateRenderer templateRenderer;

    public PositiveIntValidator(
            UserManager userManager,
            LoginUriProvider loginUriProvider,
            TemplateRenderer templateRenderer) {

        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
                                        ) throws ServletException, IOException {

        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username))
        {
            redirectToLogin(request, response);
            return;
        }
        templateRenderer.render("/view/validator.vm", response.getWriter());
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
                                        ) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }
    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null)
        {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

}