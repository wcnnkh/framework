package scw.embed.tomcat;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import scw.application.MainArgs;
import scw.beans.BeanFactory;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.embed.EmbeddedUtils;
import scw.embed.annotation.ErrorCodeController;
import scw.embed.servlet.FilterConfiguration;
import scw.embed.servlet.MultiFilter;
import scw.embed.servlet.ServletContainerInitializerConfiguration;
import scw.embed.servlet.ServletEmbedded;
import scw.embed.servlet.support.RootServletContainerInitializerConfiguration;
import scw.embed.servlet.support.ServletRootFilterConfiguration;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.action.Action;
import scw.mvc.action.ActionManager;
import scw.value.Value;
import scw.value.property.PropertyFactory;

public class TomcatServletEmbedded implements ServletEmbedded {
	private static Logger logger = LoggerUtils.getLogger(TomcatServletEmbedded.class);
	private Tomcat tomcat;

	protected Tomcat createTomcat(BeanFactory beanFactory, PropertyFactory propertyFactory, MainArgs args) {
		Tomcat tomcat = new Tomcat();
		Value value = args.getInstruction("-p");
		int port = value == null ? EmbeddedUtils.getPort(propertyFactory) : value.getAsInteger();
		tomcat.setPort(port);

		String basedir = EmbeddedUtils.getBaseDir(propertyFactory);
		if (StringUtils.isNotEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		configureConnector(tomcat, port, beanFactory, propertyFactory);
		tomcat.getHost().setAutoDeploy(false);
		return tomcat;
	}

	protected String getDocBase(PropertyFactory propertyFactory) {
		return GlobalPropertyFactory.getInstance().getWorkPath();
	}

	protected String getContextPath(PropertyFactory propertyFactory) {
		String contextPath = EmbeddedUtils.getContextPath(propertyFactory);
		return StringUtils.isEmpty(contextPath) ? "" : contextPath;
	}

	protected JarScanner getJarScanner(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		return InstanceUtils.loadService(JarScanner.class, beanFactory, propertyFactory,
				"scw.embed.tomcat.Tomcat8AboveStandardJarScanner");
	}

	protected Context createContext(BeanFactory beanFactory, PropertyFactory propertyFactory, ClassLoader classLoader) {
		Context context = tomcat.addContext(getContextPath(propertyFactory), getDocBase(propertyFactory));
		context.setParentClassLoader(classLoader);
		JarScanner jarScanner = getJarScanner(beanFactory, propertyFactory);
		if (jarScanner != null) {
			context.setJarScanner(jarScanner);
		}

		addServletContainerInitializer(context,
				new RootServletContainerInitializerConfiguration(beanFactory, propertyFactory));
		for (ServletContainerInitializerConfiguration configuration : InstanceUtils
				.getConfigurationList(ServletContainerInitializerConfiguration.class, beanFactory, propertyFactory)) {
			addServletContainerInitializer(context, configuration);
		}

		addFilter(context, new ServletRootFilterConfiguration(beanFactory, propertyFactory));
		for (FilterConfiguration filterConfiguration : InstanceUtils.getConfigurationList(FilterConfiguration.class,
				beanFactory, propertyFactory)) {
			addFilter(context, filterConfiguration);
		}

		addErrorPage(context, beanFactory, propertyFactory);

		if (beanFactory.isInstance(JspConfigDescriptor.class)) {
			context.setJspConfigDescriptor(beanFactory.getInstance(JspConfigDescriptor.class));
		}
		return context;
	}

	protected void addErrorPage(Context context, BeanFactory beanFactory, PropertyFactory propertyFactory) {
		if (beanFactory.isInstance(ActionManager.class)) {
			for (Action action : beanFactory.getInstance(ActionManager.class).getActions()) {
				ErrorCodeController errorCodeController = action.getMethodAnnotatedElement()
						.getAnnotation(ErrorCodeController.class);
				if (errorCodeController == null) {
					continue;
				}

				HttpControllerDescriptor controllerDescriptorToUse = null;
				for (HttpControllerDescriptor httpControllerDescriptor : action.getHttpControllerDescriptors()) {
					if (httpControllerDescriptor.getMethod() == HttpMethod.GET
							&& !httpControllerDescriptor.getRestful().isRestful()) {
						controllerDescriptorToUse = httpControllerDescriptor;
					}
				}

				if (controllerDescriptorToUse == null) {
					logger.warn("not support error controller action: {}", action);
					continue;
				}

				if (errorCodeController != null) {
					for (int code : errorCodeController.value()) {
						ErrorPage errorPage = new ErrorPage();
						errorPage.setErrorCode(code);
						errorPage.setLocation(controllerDescriptorToUse.getPath());
						context.addErrorPage(errorPage);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void addServletContainerInitializer(Context context,
			ServletContainerInitializerConfiguration configuration) {
		Collection<? extends ServletContainerInitializer> initializers = configuration
				.getServletContainerInitializers();
		if (CollectionUtils.isEmpty(initializers)) {
			return;
		}

		Set<Class<?>> classSet = configuration.getClassSet();
		classSet = classSet == null ? Collections.EMPTY_SET : classSet;
		for (ServletContainerInitializer initializer : initializers) {
			if (logger.isDebugEnabled()) {
				logger.debug("add ServletContainerInitializer: {} by config: {}", initializer.getClass().getName(),
						configuration.getClass().getName());
			}

			context.addServletContainerInitializer(initializer, classSet);
		}
	}

	protected void addFilter(Context context, FilterConfiguration filterConfiguration) {
		Collection<? extends Filter> filters = filterConfiguration.getFilters();
		if (CollectionUtils.isEmpty(filters)) {
			return;
		}

		FilterDef filterDef = new FilterDef();
		MultiFilter multiFilter = new MultiFilter();
		for (Filter filter : filters) {
			if (logger.isDebugEnabled()) {
				logger.debug("add Filter: {}", filter.getClass().getName());
			}
			multiFilter.add(filter);
		}
		filterDef.setFilter(multiFilter);
		filterDef.setFilterName(filterConfiguration.getName());
		context.addFilterDef(filterDef);

		FilterMap filterMap = new FilterMap();
		filterMap.setFilterName(filterConfiguration.getName());
		for (String url : filterConfiguration.getURLPatterns()) {
			filterMap.addURLPattern(url);
		}
	}

	protected boolean isVersion(String version) {
		return StringUtils.startsWithIgnoreCase(ServerInfo.getServerNumber(), version);
	}

	protected void configureConnector(Tomcat tomcat, int port, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		Connector connector = null;
		String connectorName = EmbeddedUtils.getTomcatConnectorName(propertyFactory);
		if (!StringUtils.isEmpty(connectorName)) {
			connector = beanFactory.getInstance(connectorName);
		} else {
			String protocol = EmbeddedUtils.getTomcatProtocol(propertyFactory);
			if (!StringUtils.isEmpty(protocol)) {
				connector = new Connector(protocol);
			} else {
				if (isVersion("9.0")) {
					connector = new Connector();
				}
			}
		}

		if (connector != null) {
			connector.setPort(port);
			tomcat.setConnector(connector);
		}
	}

	protected void configureLifecycleListener(Context context) {
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}
	}

	protected void configureJSP(Context context, PropertyFactory propertyFactory) throws Exception {
		if (ClassUtils.isPresent("org.apache.jasper.servlet.JspServlet")) {
			ServletContainerInitializer containerInitializer = InstanceUtils.INSTANCE_FACTORY
					.getInstance("org.apache.jasper.servlet.JasperInitializer");
			if (containerInitializer != null) {
				context.addServletContainerInitializer(containerInitializer, null);
			} // else Probably not Tomcat 8

			Tomcat.addServlet(context, "jsp", "org.apache.jasper.servlet.JspServlet");
			addServletMapping(context, "*.jsp", "jsp");
			addServletMapping(context, "*.jspx", "jsp");
		}
	}

	protected void configShutdown(Context context, PropertyFactory propertyFactory, Servlet destroy) throws Exception {
		String tomcatShutdownServletPath = EmbeddedUtils.getShutdownPath(propertyFactory);
		if (StringUtils.isEmpty(tomcatShutdownServletPath)) {
			return;
		}

		String tomcatShutdownServletName = EmbeddedUtils.getShutdownName(propertyFactory);
		if (StringUtils.isEmpty(tomcatShutdownServletName)) {
			tomcatShutdownServletName = "shutdown";
		}

		Tomcat.addServlet(context, tomcatShutdownServletName, destroy);
		addServletMapping(context, tomcatShutdownServletPath, tomcatShutdownServletName);
	}

	protected void addServletMapping(Context context, String pattern, String servletName) throws Exception {
		Method method = ReflectionUtils.getMethod(Context.class, "addServletMappingDecoded", String.class,
				String.class);
		if (method == null) {// tomcat8以下
			method = ReflectionUtils.getMethod(Context.class, "addServletMapping", String.class, String.class);
		}
		method.invoke(context, pattern, servletName);
	}

	protected void configureServlet(Context context, Servlet servlet, PropertyFactory propertyFactory,
			Class<?> mainClass) throws Exception {
		String servletName = mainClass == null ? "scw" : mainClass.getSimpleName();
		Tomcat.addServlet(context, servletName, servlet);
		addServletMapping(context, "/", servletName);
		String sourceMapping = EmbeddedUtils.getDefaultServletMapping(propertyFactory);
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				Tomcat.addServlet(context, "default", "org.apache.catalina.servlets.DefaultServlet");
				for (String pattern : patternArr) {
					LoggerUtils.getLogger(TomcatServletEmbedded.class).info("default mapping [{}]", pattern);
					addServletMapping(context, pattern, "default");
				}
			}
		}
	}

	private void tomcat8(ClassLoader classLoader) throws Throwable {
		Class<?> clz = ClassUtils.forNameNullable("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory",
				classLoader);
		if (clz != null) {
			Method method = clz.getDeclaredMethod("disable");
			method.invoke(null);
		}
	}

	public void destroy() throws Exception {
		if (tomcat != null) {
			try {
				tomcat.destroy();
			} catch (LifecycleException e) {
				// ignore
			}
		}
	}

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory, Servlet destroy, Servlet service,
			Class<?> mainClass, MainArgs args) throws Exception {
		try {
			tomcat8(mainClass.getClassLoader());
		} catch (Throwable e1) {
		}

		this.tomcat = createTomcat(beanFactory, propertyFactory, args);
		Context context = createContext(beanFactory, propertyFactory, mainClass.getClassLoader());
		configureLifecycleListener(context);
		configureJSP(context, propertyFactory);
		configureServlet(context, service, propertyFactory, mainClass);
		configShutdown(context, propertyFactory, destroy);
		tomcat.start();
	}
}
