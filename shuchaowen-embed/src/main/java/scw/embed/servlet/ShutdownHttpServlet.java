package scw.embed.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.Destroy;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.embed.EmbeddedUtils;
import scw.http.server.cors.CorsUtils;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;
import scw.value.property.PropertyFactory;

public final class ShutdownHttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String[] ips;
	private final String username;
	private final String password;
	private final Destroy destroy;

	public ShutdownHttpServlet(PropertyFactory propertyFactory, Destroy destroy) {
		this.username = EmbeddedUtils.getShutdownUserName(propertyFactory);
		this.password = EmbeddedUtils.getShutdownPassword(propertyFactory);
		String ip = EmbeddedUtils.getShutdownIp(propertyFactory);
		this.ips = StringUtils.commonSplit(ip);
		this.destroy = destroy;
	}

	private boolean checkIp(String requestIp) {
		if (StringUtils.isEmpty(requestIp)) {
			return false;
		}

		for (String ip : ips) {
			if (ip.equals(requestIp)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletServerHttpRequest request = new ServletServerHttpRequest(req);
		ServletServerHttpResponse response = new ServletServerHttpResponse(resp);
		CorsUtils.write(scw.http.server.cors.CorsConfig.DEFAULT, response);
		if (!ArrayUtils.isEmpty(ips)) {
			String requestIp = request.getIp();
			if (!checkIp(requestIp)) {
				resp.getWriter().write("Illegal IP [" + requestIp + "]");
				return;
			}
		}

		if (!StringUtils.isEmpty(username)) {
			String requestUsername = req.getParameter("username");
			if (!username.equals(requestUsername)) {
				resp.getWriter().write("username error");
				return;
			}
		}

		if (!StringUtils.isEmpty(password)) {
			String requestPassword = req.getParameter("password");
			if (!password.equals(requestPassword)) {
				resp.getWriter().write("password error");
				return;
			}
		}
		try {
			destroy.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
