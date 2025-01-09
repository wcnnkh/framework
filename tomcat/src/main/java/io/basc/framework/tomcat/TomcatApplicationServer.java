package io.basc.framework.tomcat;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import io.basc.framework.beans.factory.config.DisposableBean;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.ApplicationServer;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.boot.servlet.ApplicationServletContainerInitializer;
import io.basc.framework.boot.servlet.DispatcherServlet;
import io.basc.framework.boot.servlet.ServletContextUtils;
import io.basc.framework.core.env.Environment;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionManager;
import io.basc.framework.servlet.ServletContextPropertyFactory;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collection.ArrayUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.reflect.ReflectionUtils;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.web.pattern.HttpPattern;

public class TomcatApplicationServer implements ApplicationServer, DisposableBean {
	private static Logger logger = LogManager.getLogger(TomcatApplicationServer.class);
	private Tomcat tomcat;

	protected String getContextPath(Environment environment) {
		String contextPath = TomcatUtils.getContextPath(environment);
		return StringUtils.isEmpty(contextPath) ? "" : contextPath;
	}

	protected Context createContext(Application application) {
		Context context = tomcat.addContext(getContextPath(application), application.getWorkPath());
		context.setParentClassLoader(application.getClassLoader());
		return context;
	}

	protected void addErrorPage(Context context, Application application) {
		for (String beanName : application.getBeanNamesForType(ActionManager.class)) {
			if (!application.isSingleton(beanName)) {
				continue;
			}

			ActionManager actionManager = application.getBean(beanName, ActionManager.class);
			for (Action action : actionManager.getActions()) {
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
		String connectorName = TomcatUtils.getTomcatConnectorName(application);
		if (!StringUtils.isEmpty(connectorName)) {
			connector = application.getBean(connectorName, Connector.class);
		} else {
			connector = new Connector(TomcatUtils.getTomcatProtocol(application));
		}

		connector.setPort(port);
		tomcat.setConnector(connector);
	}

	@SuppressWarnings("unchecked")
	protected void configureJSP(Context context, Application application) throws Exception {
		application.getBeanProvider(JspConfigDescriptor.class).getUnique().ifPresent((e) -> {
			context.setJspConfigDescriptor(e);
		});

		ClassUtils.findClass("org.apache.jasper.servlet.JspServlet", application.getClassLoader())
				.ifPresent((clazz) -> {
					ServletContainerInitializer containerInitializer = application
							.getBeanProvider((Class<ServletContainerInitializer>) clazz).getUnique().orElseGet(() -> {
								return (ServletContainerInitializer) ReflectionUtils.newInstance(clazz);
							});

					if (containerInitializer != null) {
						context.addServletContainerInitializer(containerInitializer, null);
					} // else Probably not Tomcat 8

					Tomcat.addServlet(context, "jsp", "org.apache.jasper.servlet.JspServlet");
					addServletMapping(context, "*.jsp", "jsp");
					addServletMapping(context, "*.jspx", "jsp");
				});
	}

	protected void addServletMapping(Context context, String pattern, String servletName) {
		Method method = ReflectionUtils.getDeclaredMethod(Context.class, "addServletMappingDecoded", String.class,
				String.class);
		if (method == null) {// tomcat8以下
			method = ReflectionUtils.getDeclaredMethod(Context.class, "addServletMapping", String.class, String.class);
		}
		ReflectionUtils.invoke(method, context, pattern, servletName);
	}

	protected void configureServlet(Context context, Application application) throws Exception {
		String servletName = application.getName().map((e) -> StringUtils.isEmpty(e) ? null : e).orElse("framework");
		DispatcherServlet servlet = new DispatcherServlet();
		Wrapper wrapper = Tomcat.addServlet(context, servletName, servlet);
		wrapper.setAsyncSupported(true);

		MultipartConfigElement multipartConfigElement = application.getBeanProvider(MultipartConfigElement.class)
				.getUnique().orElseGet(() -> {
					for (Class<?> clazz : application.getSourceClasses().getServices()) {
						if (clazz.isAnnotationPresent(MultipartConfig.class)) {
							return new MultipartConfigElement(clazz.getAnnotation(MultipartConfig.class));
						}
					}
					return null;
				});

		if (multipartConfigElement != null) {
			wrapper.setMultipartConfigElement(multipartConfigElement);
		}

		Properties properties = TomcatUtils.getServletInitParametersConfig(application, servletName, true);
		for (Entry<Object, Object> entry : properties.entrySet()) {
			wrapper.addInitParameter(entry.getKey().toString(), entry.getValue().toString());
		}

		addServletMapping(context, "/", servletName);
		String sourceMapping = TomcatUtils.getDefaultServletMapping(application);
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.splitToArray(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				String tempServletName = "default";
				Wrapper tempWrapper = Tomcat.addServlet(context, tempServletName,
						"org.apache.catalina.servlets.DefaultServlet");
				Properties tempProperties = TomcatUtils.getServletInitParametersConfig(application, tempServletName,
						false);
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

	@Override
	public void startup(ConfigurableApplication application) throws Throwable {
		if (application.getProperties().get("tomcat.log.enable").or(true).getAsBoolean()) {
			java.util.logging.Logger.getLogger("org.apache").setLevel(Level.WARNING);
		}

		try {
			tomcat8(application.getClassLoader());
		} catch (Throwable e1) {
		}

		this.tomcat = new Tomcat();
		int port = application.getPort().orElseGet(() -> Application.getAvailablePort());
		application.setPort(port);
		tomcat.setPort(port);
		logger.info("The boot port is {}", port);

		String basedir = TomcatUtils.getBaseDir(application);
		if (StringUtils.isNotEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		configureConnector(tomcat, port, application);
		tomcat.getHost().setAutoDeploy(false);

		final Context context = createContext(application);
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}

		application.getProperties().register(new ServletContextPropertyFactory(context.getServletContext()));

		configureJSP(context, application);
		configureServlet(context, application);

		for (TomcatContextConfiguration configuration : application.getServiceLoader(TomcatContextConfiguration.class)
				.getServices()) {
			configuration.configuration(application, context);
		}

		ServletContextUtils.setApplication(context.getServletContext(), application);
		Set<Class<?>> classes = application.getContextClasses().getServices().toSet();
		context.addServletContainerInitializer(new ApplicationServletContainerInitializer(), classes);

		// init websocket
		TomcatUtils.addWsSci(context, classes, application.getClassLoader());

		tomcat.start();
		addErrorPage(context, application);
	}

	public void destroy() {
		try {
			tomcat.stop();
		} catch (LifecycleException e) {
			logger.error(e, "Stop tomcat error");
		}
	}
}
