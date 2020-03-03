package scw.servlet.mvc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.annotation.AutoImpl;

@AutoImpl(ConfigurationServletService.class)
public interface ServletService {
	void service(ServletRequest servletRequest, ServletResponse servletResponse);
}
