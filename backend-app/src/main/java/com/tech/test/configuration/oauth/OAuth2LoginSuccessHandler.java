package com.tech.test.configuration.oauth;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User oauth2User = (CustomerOAuth2User) authentication.getPrincipal();

		String name = oauth2User.getName();
//		String email = oauth2User.getEmail();
//		String countryCode = request.getLocale().getCountry();
//		String clientName = oauth2User.getClientName();

		// Return the token to the frontend (in response body)
//		response.setStatus(HttpServletResponse.SC_OK);
//		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//		response.getWriter().write("{\"name\":\"" + name + "\"}");

		response.sendRedirect("http://localhost:3001/users");
	}

}