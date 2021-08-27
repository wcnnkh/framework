package io.basc.framework.tomcat;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tomcat.websocket.server.Constants;
import org.apache.tomcat.websocket.server.WsSci;

public class WsServletContainerInitializer extends WsSci{
	
	public boolean isInitialized(ServletContext servletContext){
		return servletContext.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE) != null;
	}
	
	@Override
	public void onStartup(Set<Class<?>> clazzes, ServletContext ctx)
			throws ServletException {
		if(isInitialized(ctx)){
			return ;
		}
		super.onStartup(clazzes, ctx);
	}
}
