package shuchaowen.web.servlet;

import javax.servlet.ServletConfig;

import shuchaowen.core.application.HttpServerConfigFactory;
import shuchaowen.core.util.ConfigUtils;

public class ServletConfigFactory implements HttpServerConfigFactory{
	private final ServletConfig servletConfig;
	
	public ServletConfigFactory(ServletConfig servletConfig){
		this.servletConfig = servletConfig;
	}
	
	public String getConfig(String name) {
		String value = servletConfig.getInitParameter(name);
		if(value == null){
			value = ConfigUtils.getSystemProperty(name);
		}
		return value;
	}
}
