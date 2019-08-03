package scw.application;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;

import scw.core.PropertiesFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.exception.NotFoundException;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.LoggerUtils;
import scw.servlet.ServletService;
import scw.servlet.ServletUtils;
import scw.servlet.http.filter.CrossDomainFilter;

public class TomcatApplication extends CommonApplication implements Servlet {
	private Tomcat tomcat;
	private ServletService servletService;

	public TomcatApplication(String configXml) {
		super(configXml, true);
	}

	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		servletService.service(req, res);
	}

	private Tomcat createTomcat() {
		Tomcat tomcat = new Tomcat();
		int port = StringUtils.parseInt(
				getPropertiesFactory().getValue("tomcat.port"), 8080);
		tomcat.setPort(port);

		String basedir = getPropertiesFactory().getValue("tomcat.basedir");
		if (StringUtils.isEmpty(basedir)) {
			basedir = SystemPropertyUtils.getWorkPath();
		}

		if (!StringUtils.isEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		return tomcat;
	}

	private Context createContext() {
		String contextPath = getPropertiesFactory().getValue(
				"tomcat.contextPath");
		contextPath = StringUtils.isEmpty(contextPath) ? "" : contextPath;
		return tomcat
				.addContext(contextPath, SystemPropertyUtils.getWorkPath());
	}

	private void configureLifecycleListener(Context context) {
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}
	}

	private void configureJarScanner(Context context) {
		context.setJarScanner(new JarScanner() {

			public void scan(ServletContext context, ClassLoader classloader,
					JarScannerCallback callback, Set<String> jarsToSkip) {
				// ignore
			}
		});
	}

	private void configureJSP(Context context) {
		if (StringUtils.parseBoolean(getPropertiesFactory().getValue(
				"tomcat.jsp"))) {
			ServletContainerInitializer containerInitializer = InstanceUtils
					.getInstance("org.apache.jasper.servlet.JasperInitializer",
							true);
			if (containerInitializer != null) {
				context.addServletContainerInitializer(containerInitializer,
						null);
			} // else Probably not Tomcat 8

			Tomcat.addServlet(context, "jsp",
					"org.apache.jasper.servlet.JspServlet");
			addServletMapping(context, "*.jsp", "jsp");
			addServletMapping(context, "*.jspx", "jsp");
		}
	}

	private void configShutdown(Context context) {
		String tomcatShutdownServletPath = getPropertiesFactory().getValue(
				"tomcat.shutdown.path");
		if (StringUtils.isEmpty(tomcatShutdownServletPath)) {
			return;
		}

		String tomcatShutdownServletName = getPropertiesFactory().getValue(
				"tomcat.shutdown.name");
		if (StringUtils.isEmpty(tomcatShutdownServletName)) {
			tomcatShutdownServletName = "shutdown";
		}

		Tomcat.addServlet(context, tomcatShutdownServletName,
				new ShutdownHttpServlet(getPropertiesFactory()));
		addServletMapping(context, tomcatShutdownServletPath,
				tomcatShutdownServletName);
	}

	private static class ShutdownHttpServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private final String[] ips;
		private final String username;
		private final String password;

		public ShutdownHttpServlet(PropertiesFactory propertiesFactory) {
			this.username = propertiesFactory
					.getValue("tomcat.shutdown.username");
			this.password = propertiesFactory
					.getValue("tomcat.shutdown.password");
			String ip = propertiesFactory.getValue("tomcat.shutdown.ip");
			this.ips = StringUtils.commonSplit(ip);
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
		protected void service(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			CrossDomainFilter.DEFAULT.write(resp);
			if (!ArrayUtils.isEmpty(ips)) {
				String requestIp = ServletUtils.getIP(req);
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

			shutdown();
		}
	}

	private void configureServlet(Context context) {
		Tomcat.addServlet(context, "scw", this);
		addServletMapping(context, "/", "scw");
		String sourceMapping = getPropertiesFactory().getValue("tomcat.source");
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				Tomcat.addServlet(context, "default",
						"org.apache.catalina.servlets.DefaultServlet");
				for (String pattern : patternArr) {
					LoggerUtils.info(TomcatApplication.class,
							"source mapping [{}]", pattern);
					addServletMapping(context, pattern, "default");
				}
			}
		}
	}

	private void addServletMapping(Context context, String pattern,
			String servletName) {
		Method method = ReflectUtils.findMethod(Context.class,
				"addServletMappingDecoded", String.class, String.class);
		if (method == null) {// tomcat8以下
			method = ReflectUtils.findMethod(Context.class,
					"addServletMapping", String.class, String.class);
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
		this.servletService = ServletUtils.getServletService(getBeanFactory(),
				getPropertiesFactory(), getConfigPath(), getBeanFactory()
						.getFilterNames());

		this.tomcat = createTomcat();
		Context context = createContext();
		configureJarScanner(context);
		configureLifecycleListener(context);
		configureJSP(context);
		configureServlet(context);
		configShutdown(context);
		try {
			tomcat8();
		} catch (Throwable e1) {
		}

		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new RuntimeException(e);
		}
	}

	private void tomcat8() throws Throwable {
		Class<?> clz = Class
				.forName("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
		Method method = clz.getDeclaredMethod("disable");
		method.invoke(null);
	}

	@Override
	public void destroy() {
		try {
			tomcat.destroy();
		} catch (LifecycleException e) {
			// Ignore
		}

		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public String getServletInfo() {
		return null;
	}

	private static volatile Application application;

	public synchronized static void run(String beanXml) {
		if (application != null) {
			throw new AlreadyExistsException("The service has been started");
		}

		if (!ResourceUtils.isExist(beanXml)) {
			LoggerUtils.warn(TomcatApplication.class, "not found " + beanXml);
		}

		application = new TomcatApplication(beanXml);
		application.init();
	}

	public synchronized static void shutdown() {
		if (application == null) {
			throw new NotFoundException("Service not started");
		}

		LoggerUtils.info(TomcatApplication.class,
				"---------------shutdown---------------");
		application.destroy();
		application = null;
		System.exit(0);
	}

	public static void run() {
		run("classpath:beans.xml");
	}
}
