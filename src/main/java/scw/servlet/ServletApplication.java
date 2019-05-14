package scw.servlet;

import javax.servlet.ServletConfig;

import scw.application.Application;
import scw.application.CommonApplication;
import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.core.utils.StringUtils;

public class ServletApplication implements Application {
	private final CommonApplication commonApplication;

	public ServletApplication(ServletConfig servletConfig) throws Throwable {
		ServletConfigPropertiesFactory propertiesFactory = new ServletConfigPropertiesFactory(servletConfig);
		String initStaticStr = propertiesFactory.getServletConfig("init-static");
		if (StringUtils.isNull(initStaticStr)) {
			this.commonApplication = new CommonApplication(propertiesFactory.getConfigXml(), false, propertiesFactory);
		} else {
			this.commonApplication = new CommonApplication(propertiesFactory.getConfigXml(),
					Boolean.parseBoolean(initStaticStr), propertiesFactory);
		}
	}

	public BeanFactory getBeanFactory() {
		return commonApplication.getBeanFactory();
	}

	public PropertiesFactory getPropertiesFactory() {
		return commonApplication.getPropertiesFactory();
	}

	public void init() {
		commonApplication.init();
	}

	public void destroy() {
		commonApplication.destroy();
	}

	public CommonApplication getCommonApplication() {
		return commonApplication;
	}
}
