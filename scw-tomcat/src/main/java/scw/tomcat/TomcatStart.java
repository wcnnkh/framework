package scw.tomcat;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import scw.boot.Application;
import scw.boot.ConfigurableApplication;
import scw.boot.Main;
import scw.boot.servlet.support.ApplicationServletContainerInitializer;
import scw.boot.servlet.support.ServletContextUtils;
import scw.boot.support.ApplicationUtils;
import scw.context.Destroy;
import scw.context.servlet.ServletContextPropertyFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.env.Environment;
import scw.env.MainArgs;
import scw.env.Sys;
import scw.http.HttpMethod;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.action.Action;
import scw.mvc.action.ActionManager;
import scw.web.pattern.HttpPattern;

public class TomcatStart implements Main, Destroy {
	private static Logger logger = LoggerFactory.getLogger(TomcatStart.class);
	private Tomcat tomcat;

	protected String getContextPath(Environment environment) {
		String contextPath = TomcatUtils.getContextPath(environment);
		return StringUtils.isEmpty(contextPath) ? "" : contextPath;
	}

	protected Context createContext(Application application) {
		Context context = tomcat.addContext(getContextPath(application.getBeanFactory().getEnvironment()),
				application.getBeanFactory().getEnvironment().getWorkPath());
		context.setParentClassLoader(application.getClassLoader());
		return context;
	}

	protected void addErrorPage(Context context, Application application) {
		if (application.getBeanFactory().isInstance(ActionManager.class)) {
			for (Action action : application.getBeanFactory().getInstance(ActionManager.class)) {
				ErrorPageController errorCodeController = action.getAnnotation(ErrorPageController.class);
				if (errorCodeController == null) {
					continue;
				}

				for (HttpPattern pattern : action.getPatternts()) {
					if (pattern.isPattern() && HttpMethod.GET.name().equals(pattern.getMethod())) {
						continue;
					}

					if (errorCodeController != null) {
						for (int code : errorCodeController.value()) {
							ErrorPage errorPage = new ErrorPage();
							errorPage.setErrorCode(code);

							if (StringUtils.isNotEmpty(errorCodeController.charset())) {
								errorPage.setCharset(Charset.forName(errorCodeController.charset()));
							}

							if (StringUtils.isNotEmpty(errorCodeController.exceptionType())) {
								errorPage.setExceptionType(errorCodeController.exceptionType());
							}
							errorPage.setLocation(pattern.getPath());
							context.addErrorPage(errorPage);
						}
					} else {
						logger.warn("not support error controller action: {}", action);
					}
				}
			}
		}
	}

	protected boolean isVersion(String version) {
		return StringUtils.startsWithIgnoreCase(ServerInfo.getServerNumber(), version);
	}

	protected void configureConnector(Tomcat tomcat, int port, Application application) {
		Connector connector = null;
		String connectorName = TomcatUtils.getTomcatConnectorName(application.getBeanFactory().getEnvironment());
		if (!StringUtils.isEmpty(connectorName)) {
			connector = application.getBeanFactory().getInstance(connectorName);
		} else {
			connector = new Connector(TomcatUtils.getTomcatProtocol(application.getBeanFactory().getEnvironment()));
		}

		connector.setPort(port);
		tomcat.setConnector(connector);
	}

	protected void configureJSP(Context context, Application application) throws Exception {
		if (application.getBeanFactory().isInstance(JspConfigDescriptor.class)) {
			context.setJspConfigDescriptor(application.getBeanFactory().getInstance(JspConfigDescriptor.class));
		}

		if (ClassUtils.isPresent("org.apache.jasper.servlet.JspServlet", application.getClassLoader())) {
			ServletContainerInitializer containerInitializer = Sys.env
					.getInstance("org.apache.jasper.servlet.JasperInitializer");
			if (containerInitializer != null) {
				context.addServletContainerInitializer(containerInitializer, null);
			} // else Probably not Tomcat 8

			Tomcat.addServlet(context, "jsp", "org.apache.jasper.servlet.JspServlet");
			addServletMapping(context, "*.jsp", "jsp");
			addServletMapping(context, "*.jspx", "jsp");
		}
	}

	protected void addServletMapping(Context context, String pattern, String servletName) throws Exception {
		Method method = ReflectionUtils.getMethod(Context.class, "addServletMappingDecoded", String.class,
				String.class);
		if (method == null) {// tomcat8以下
			method = ReflectionUtils.getMethod(Context.class, "addServletMapping", String.class, String.class);
		}
		method.invoke(context, pattern, servletName);
	}

	protected void configureServlet(Context context, Application application, Class<?> mainClass) throws Exception {
		String servletName = mainClass.getSimpleName();
		Servlet servlet = ServletContextUtils.createServlet(application.getBeanFactory());
		Wrapper wrapper = Tomcat.addServlet(context, servletName, servlet);
		wrapper.setAsyncSupported(true);

		if (application.getBeanFactory().isInstance(MultipartConfigElement.class)) {
			wrapper.setMultipartConfigElement(application.getBeanFactory().getInstance(MultipartConfigElement.class));
		} else {
			for (Class<?> clazz : application.getSourceClasses()) {
				if (clazz.isAnnotationPresent(MultipartConfig.class)) {
					wrapper.setMultipartConfigElement(
							new MultipartConfigElement(clazz.getAnnotation(MultipartConfig.class)));
					break;
				}
			}
		}

		Properties properties = TomcatUtils
				.getServletInitParametersConfig(application.getBeanFactory().getEnvironment(), servletName, true);
		for (Entry<Object, Object> entry : properties.entrySet()) {
			wrapper.addInitParameter(entry.getKey().toString(), entry.getValue().toString());
		}

		addServletMapping(context, "/", servletName);
		String sourceMapping = TomcatUtils.getDefaultServletMapping(application.getBeanFactory().getEnvironment());
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				String tempServletName = "default";
				Wrapper tempWrapper = Tomcat.addServlet(context, tempServletName,
						"org.apache.catalina.servlets.DefaultServlet");
				Properties tempProperties = TomcatUtils.getServletInitParametersConfig(
						application.getBeanFactory().getEnvironment(), tempServletName, false);
				for (Entry<Object, Object> entry : tempProperties.entrySet()) {
					tempWrapper.addInitParameter(entry.getKey().toString(), entry.getValue().toString());
				}
				for (String pattern : patternArr) {
					logger.info("default mapping [{}]", pattern);
					addServletMapping(context, pattern, tempServletName);
				}
			}
		}
	}

	private void tomcat8(ClassLoader classLoader) throws Throwable {
		Class<?> clz = ClassUtils.getClass("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory",
				classLoader);
		if (clz != null) {
			Method method = clz.getDeclaredMethod("disable");
			method.invoke(null);
		}
	}

	public void main(ConfigurableApplication application, Class<?> mainClass, MainArgs args) throws Throwable {
		if (application.getEnvironment().getValue("tomcat.log.enable", boolean.class, true)) {
			java.util.logging.Logger.getLogger("org.apache").setLevel(Level.WARNING);
		}

		try {
			tomcat8(application.getClassLoader());
		} catch (Throwable e1) {
		}

		this.tomcat = new Tomcat();
		int port = ApplicationUtils.getApplicationPort(application);
		tomcat.setPort(port);
		logger.info("The boot port is {}", port);

		String basedir = TomcatUtils.getBaseDir(application.getEnvironment());
		if (StringUtils.isNotEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		configureConnector(tomcat, port, application);
		tomcat.getHost().setAutoDeploy(false);

		final Context context = createContext(application);
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}

		application.getEnvironment().addFactory(new ServletContextPropertyFactory(context.getServletContext()));

		configureJSP(context, application);
		configureServlet(context, application, mainClass);

		for (TomcatContextConfiguration configuration : application.getBeanFactory()
				.getServiceLoader(TomcatContextConfiguration.class)) {
			configuration.configuration(application, context);
		}

		ServletContextUtils.setApplication(context.getServletContext(), application);
		Set<Class<?>> classes = application.getContextClassesLoader().toSet();
		context.addServletContainerInitializer(new ApplicationServletContainerInitializer(), classes);

		// init websocket
		TomcatUtils.addWsSci(context, classes, application.getClassLoader());

		tomcat.start();
		addErrorPage(context, application);
	}

	public void destroy() throws Throwable {
		tomcat.stop();
	}
}
