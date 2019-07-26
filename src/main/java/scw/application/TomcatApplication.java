package scw.application;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import scw.application.CommonApplication;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;
import scw.servlet.ServletService;
import scw.servlet.ServletUtils;

public class TomcatApplication extends CommonApplication implements Servlet {
	private Tomcat tomcat;
	private ServletService servletService;

	public TomcatApplication(String configXml) {
		super(configXml, false);
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		servletService.service(req, res);
	}

	protected Tomcat createTomcat() {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(StringUtils.parseInt(getPropertiesFactory().getValue("servlet.port"), 8080));

		String basedir = getPropertiesFactory().getValue("servlet.basedir");
		if (StringUtils.isEmpty(basedir)) {
			basedir = ConfigUtils.getWorkPath();
		}

		if (!StringUtils.isEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		String protocol = getPropertiesFactory().getValue("servlet.protocol");
		if (!StringUtils.isEmpty(protocol)) {
			tomcat.setConnector(new Connector(protocol));
		}

		return tomcat;
	}

	@Override
	public void init() {
		super.init();
		this.servletService = ServletUtils.getServletService(getBeanFactory(), getPropertiesFactory(), getConfigPath(),
				getBeanFactory().getFilterNames());

		this.tomcat = createTomcat();
		String contextPath = getPropertiesFactory().getValue("servlet.contextPath");
		contextPath = StringUtils.isEmpty(contextPath) ? "" : contextPath;
		Context context = tomcat.addContext(contextPath, ConfigUtils.getWorkPath());
		String servletName = getPropertiesFactory().getValue("servlet.name");
		servletName = StringUtils.isEmpty(servletName) ? "def" : servletName;
		Tomcat.addServlet(context, servletName, this);
		context.addServletMapping("/", servletName,
				StringUtils.parseBoolean(getPropertiesFactory().getValue("servlet.jsp")));

		String sourceMapping = getPropertiesFactory().getValue("servlet.source");
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				Tomcat.addServlet(context, "default", new DefaultServlet());
				for (String pattern : patternArr) {
					LoggerUtils.info(TomcatApplication.class, "source mapping [{}]", pattern);
					context.addServletMapping(pattern, "default");
				}
			}
		}

		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			tomcat.destroy();
			super.destroy();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public String getServletInfo() {
		return null;
	}

	public static void run(String beanXml) {
		if (StringUtils.isEmpty(beanXml)) {
			LoggerUtils.warn(TomcatApplication.class, "No default beans.xml exists");
		}

		TomcatApplication application = new TomcatApplication(beanXml);
		application.init();
	}

	public static void run() {
		run(getDefaultConfigPath());
	}
}
