package scw.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.boot.Application;

public interface ServletApplicationStartup {
	
	StartUp start(ServletContext servletContext) throws ServletException;
	
	boolean start(ServletContext servletContext, Application application) throws ServletException;
	
	class StartUp{
		private final Application application;
		private final boolean isNew;
		
		public StartUp(Application application, boolean isNew){
			this.application = application;
			this.isNew = isNew;
		}

		public Application getApplication() {
			return application;
		}

		public boolean isNew() {
			return isNew;
		}
	}
}
