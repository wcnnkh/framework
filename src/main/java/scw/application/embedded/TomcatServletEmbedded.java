package scw.application.embedded;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.JarScanner;

import scw.application.TomcatApplication;
import scw.beans.BeanFactory;
import scw.core.PropertiesFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.EmptyInvocationHandler;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.LoggerUtils;

public final class TomcatServletEmbedded implements ServletEmbedded {
	private Tomcat tomcat;

	private Tomcat createTomcat(BeanFactory beanFactory, PropertiesFactory propertiesFactory) {
		Tomcat tomcat = new Tomcat();
		int port = EmbeddedUtils.getPort(propertiesFactory);
		tomcat.setPort(port);

		String basedir = EmbeddedUtils.getBaseDir(propertiesFactory);
		if (StringUtils.isEmpty(basedir)) {
			basedir = SystemPropertyUtils.getWorkPath();
		}

		if (!StringUtils.isEmpty(basedir)) {
			tomcat.setBaseDir(basedir);
		}

		configureConnector(tomcat, port, beanFactory, propertiesFactory);
		return tomcat;
	}

	private boolean isVersion(String version) {
		return StringUtils.startsWithIgnoreCase(ServerInfo.getServerNumber(), version);
	}

	private JarScanner createNullScanner() {
		return (JarScanner) Proxy.newProxyInstance(JarScanner.class.getClassLoader(), new Class[] { JarScanner.class },
				new EmptyInvocationHandler());
	}

	private void configureConnector(Tomcat tomcat, int port, BeanFactory beanFactory,
			PropertiesFactory propertiesFactory) {
		Connector connector = null;
		String connectorName = EmbeddedUtils.getTomcatConnectorName(propertiesFactory);
		if (!StringUtils.isEmpty(connectorName)) {
			connector = beanFactory.getInstance(connectorName);
		} else {
			String protocol = EmbeddedUtils.getTomcatProtocol(propertiesFactory);
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

	private Context createContext(PropertiesFactory propertiesFactory) {
		String contextPath = EmbeddedUtils.getContextPath(propertiesFactory);
		contextPath = StringUtils.isEmpty(contextPath) ? "" : contextPath;
		return tomcat.addContext(contextPath, SystemPropertyUtils.getWorkPath());
	}

	private void configureTLD(Context context) {
		context.setTldValidation(false);
		context.setJarScanner(createNullScanner());
	}

	private void configureLifecycleListener(Context context) {
		if (AprLifecycleListener.isAprAvailable()) {
			context.addLifecycleListener(new AprLifecycleListener());
		}
	}

	private void configureJSP(Context context, PropertiesFactory propertiesFactory) {
		if (ClassUtils.isExist("org.apache.jasper.servlet.JspServlet")) {
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

	private void configShutdown(Context context, PropertiesFactory propertiesFactory, Servlet destroy) {
		String tomcatShutdownServletPath = EmbeddedUtils.getShutdownPath(propertiesFactory);
		if (StringUtils.isEmpty(tomcatShutdownServletPath)) {
			return;
		}

		String tomcatShutdownServletName = EmbeddedUtils.getShutdownName(propertiesFactory);
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

	private void configureServlet(Context context, Servlet servlet, PropertiesFactory propertiesFactory) {
		Tomcat.addServlet(context, "scw", servlet);
		addServletMapping(context, "/", "scw");
		String sourceMapping = EmbeddedUtils.getSource(propertiesFactory);
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
				e.printStackTrace();
			}
		}
	}

	public void init(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Servlet destroy, Servlet service) {
		try {
			tomcat8();
		} catch (Throwable e1) {
		}

		this.tomcat = createTomcat(beanFactory, propertiesFactory);
		Context context = createContext(propertiesFactory);
		configureTLD(context);
		configureLifecycleListener(context);
		configureJSP(context, propertiesFactory);
		configureServlet(context, service, propertiesFactory);
		configShutdown(context, propertiesFactory, destroy);
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			throw new RuntimeException(e);
		}
	}
}
