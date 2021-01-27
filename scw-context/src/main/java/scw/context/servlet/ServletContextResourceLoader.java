package scw.context.servlet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import scw.io.Resource;
import scw.io.ResourceLoader;
import scw.io.UrlResource;

public class ServletContextResourceLoader implements ResourceLoader{
	private final ServletContext servletContext;
	
	public ServletContextResourceLoader(ServletContext servletContext){
		this.servletContext = servletContext;
	}
	
	public Resource getResource(String location) {
		try {
			URL url = servletContext.getResource(location);
			if(url == null){
				return null;
			}
			return new UrlResource(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public ClassLoader getClassLoader() {
		return servletContext.getClassLoader();
	}
	
}
