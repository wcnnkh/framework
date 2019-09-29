package scw.application.embedded.tomcat;

import java.lang.reflect.Method;

import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.JarScanner;

import scw.application.embedded.EmbeddedUtils;
import scw.application.embedded.ServletEmbedded;
import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.LoggerUtils;

public final class TomcatServletEmbedded implements ServletEmbedded {
	private Tomcat tomcat;

	private Tomcat createTomcat(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		Tomcat tomcat = new Tomcat();
		int port = EmbeddedUtils.getPort(propertyFactory);
		tomcat.setPort(port);

		String basedir = EmbeddedUtils.getBaseDir(propertyFactory);
		if (StringUtils.isEmpty(basedir)) {
			basedir = SystemPropertyUtils.getWorkPath();
		}

		if (!StringUtils.isEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		configureConnector(tomcat, port, beanFactory, propertyFactory);
		tomcat.getHost().setAutoDeploy(false);
		return tomcat;
	}

	private String getDocBase(PropertyFactory propertyFactory) {
		return SystemPropertyUtils.getWorkPath();
	}

	private String getContextPath(PropertyFactory propertyFactory) {
		String contextPath = EmbeddedUtils.getContextPath(propertyFactory);
		return StringUtils.isEmpty(contextPath) ? "" : contextPath;
	}

	private JarScanner getJarScanner(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		return InstanceUtils.getInstance("scw.application.embedded.tomcat.Tomcat8AboveStandardJarScanner",
				propertyFactory);
	}

	private Context createContext(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		Context context = tomcat.addContext(getContextPath(propertyFactory), getDocBase(propertyFactory));
		JarScanner jarScanner = getJarScanner(beanFactory, propertyFactory);
		if (jarScanner != null) {
			context.setJarScanner(jarScanner);
		}

		ReflectUtils.loadMethod(context, "tomcat.context.", propertyFactory, beanFactory,
				CollectionUtils.asSet("jarScanner", "docBase", "path"));
		return context;
	}

	private boolean isVersion(String version) {
		return StringUtils.startsWithIgnoreCase(ServerInfo.getServerNumber(), version);
	}

	private void configureConnector(Tomcat tomcat, int port, BeanFactory beanFactory, PropertyFactory propertyFactory) {
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

	private void configureLifecycleListener(Context context) {
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}
	}

	private void configureJSP(Context context, PropertyFactory propertyFactory) {
		if (ClassUtils.isExist("org.apache.jasper.servlet.JspServlet")) {
			ServletContainerInitializer containerInitializer = InstanceUtils
					.getInstance("org.apache.jasper.servlet.JasperInitializer");
			if (containerInitializer != null) {
				context.addServletContainerInitializer(containerInitializer, null);
			} // else Probably not Tomcat 8

			Tomcat.addServlet(context, "jsp", "org.apache.jasper.servlet.JspServlet");
			addServletMapping(context, "*.jsp", "jsp");
			addServletMapping(context, "*.jspx", "jsp");
		}
	}

	private void configShutdown(Context context, PropertyFactory propertyFactory, Servlet destroy) {
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

	private void configureServlet(Context context, Servlet servlet, PropertyFactory propertyFactory) {
		Tomcat.addServlet(context, "scw", servlet);
		addServletMapping(context, "/", "scw");
		String sourceMapping = EmbeddedUtils.getSource(propertyFactory);
		if (!StringUtils.isEmpty(sourceMapping)) {
			String[] patternArr = StringUtils.commonSplit(sourceMapping);
			if (!ArrayUtils.isEmpty(patternArr)) {
				Tomcat.addServlet(context, "default", "org.apache.catalina.servlets.DefaultServlet");
				for (String pattern : patternArr) {
					LoggerUtils.getLogger(TomcatServletEmbedded.class).info("source mapping [{}]", pattern);
					addServletMapping(context, pattern, "default");
				}
			}
		}
	}

	private void tomcat8() throws Throwable {
		Class<?> clz = Class.forName("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
		Method method = clz.getDeclaredMethod("disable");
		method.invoke(null);
	}

	public void destroy() {
		if (tomcat != null) {
			try {
				tomcat.destroy();
			} catch (LifecycleException e) {
				// ignore
			}
		}
	}

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory, Servlet destroy, Servlet service) {
		try {
			tomcat8();
		} catch (Throwable e1) {
		}

		this.tomcat = createTomcat(beanFactory, propertyFactory);
		Context context = createContext(beanFactory, propertyFactory);
		configureLifecycleListener(context);
		configureJSP(context, propertyFactory);
		configureServlet(context, service, propertyFactory);
		configShutdown(context, propertyFactory, destroy);
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new RuntimeException(e);
		}
	}
}
