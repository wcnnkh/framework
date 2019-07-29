package scw.application;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;

import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
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
		super(configXml, true);
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		servletService.service(req, res);
	}

	private Tomcat createTomcat() {
		Tomcat tomcat = new Tomcat();
		int port = StringUtils.parseInt(getPropertiesFactory().getValue("servlet.port"), 8080);
		tomcat.setPort(port);

		String basedir = getPropertiesFactory().getValue("servlet.basedir");
		if (StringUtils.isEmpty(basedir)) {
			basedir = ConfigUtils.getWorkPath();
		}

		if (!StringUtils.isEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}
		return tomcat;
	}

	private Context createContext() {
		String contextPath = getPropertiesFactory().getValue("servlet.contextPath");
		contextPath = StringUtils.isEmpty(contextPath) ? "" : contextPath;
		return tomcat.addContext(contextPath, ConfigUtils.getWorkPath());
	}

	private void configureLifecycleListener(Context context) {
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}
	}

	private void configureJSP(Context context) {
		if (StringUtils.parseBoolean(getPropertiesFactory().getValue("servlet.jsp"))) {
			ServletContainerInitializer containerInitializer = InstanceUtils
					.getInstance("org.apache.jasper.servlet.JasperInitializer", true);
			if (containerInitializer != null) {
				context.addServletContainerInitializer(containerInitializer, null);
			} // else Probably not Tomcat 8

			Tomcat.addServlet(context, "jsp", "org.apache.jasper.servlet.JspServlet");
			addServletMapping(context, "*.jsp", "jsp");
			addServletMapping(context, "*.jspx", "jsp");
		}
	}

	private void configureServlet(Context context) {
		String servletName = getPropertiesFactory().getValue("servlet.name");
		servletName = StringUtils.isEmpty(servletName) ? "scw" : servletName;
		Tomcat.addServlet(context, servletName, this);

		String servletPattern = getPropertiesFactory().getValue("servlet.pattern");
		addServletMapping(context, StringUtils.isEmpty(servletPattern) ? "/" : servletPattern, servletName);
		String sourceMapping = getPropertiesFactory().getValue("servlet.source");
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				Tomcat.addServlet(context, "default", "org.apache.catalina.servlets.DefaultServlet");
				for (String pattern : patternArr) {
					LoggerUtils.info(TomcatApplication.class, "source mapping [{}]", pattern);
					addServletMapping(context, pattern, "default");
				}
			}
		}
	}

	private void addServletMapping(Context context, String pattern, String servletName) {
		Method method = ReflectUtils.findMethod(Context.class, "addServletMappingDecoded", String.class, String.class);
		if (method == null) {// tomcat8以下
			method = ReflectUtils.findMethod(Context.class, "addServletMapping", String.class, String.class);
		}
		try {
			method.invoke(context, pattern, servletName);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init() {
		super.init();
		this.servletService = ServletUtils.getServletService(getBeanFactory(), getPropertiesFactory(), getConfigPath(),
				getBeanFactory().getFilterNames());

		this.tomcat = createTomcat();
		Context context = createContext();
		configureLifecycleListener(context);
		configureJSP(context);
		configureServlet(context);

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
		String path = getDefaultConfigPath();
		if (!StringUtils.isEmpty(path)) {
			LoggerUtils.info(TomcatApplication.class, "{}", path);
		}
		run(path);
	}
}
