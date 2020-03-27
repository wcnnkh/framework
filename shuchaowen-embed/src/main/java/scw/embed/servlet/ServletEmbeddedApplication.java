package scw.embed.servlet;

import scw.application.Application;
import scw.application.ApplicationConfigUtils;
import scw.application.CommonApplication;
import scw.beans.BeanUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.embed.EmbeddedUtils;
import scw.embed.tomcat.TomcatApplication;
import scw.io.resource.ResourceUtils;
import scw.servlet.mvc.DispatcherServlet;
import scw.util.FormatUtils;

public class ServletEmbeddedApplication extends CommonApplication {
	private ServletEmbedded embedded;
	private Class<?> mainClass;

	public ServletEmbeddedApplication(String configXml, Class<?> mainClass) {
		super(configXml);
		this.mainClass = mainClass;
	}

	private String getSupportServletEmbeddedClassName() {
		String[] classNames = new String[] { "scw.embed.tomcat.TomcatServletEmbedded", };

		for (String name : classNames) {
			if (ClassUtils.isPresent(name, mainClass.getClassLoader())) {
				return name;
			}
		}
		return null;
	}

	@Override
	public void init() {
		super.init();
		String embeddedName = EmbeddedUtils.getEmbeddedName(getPropertyFactory());
		if (StringUtils.isEmpty(embeddedName)) {
			String name = getSupportServletEmbeddedClassName();
			if (name == null) {
				FormatUtils.warn(ServletEmbeddedApplication.class, "未找到支持的embedded, 如需支持请导入对应的jar");
			} else {
				initEmbedded(name);
			}
		} else {
			initEmbedded(embeddedName);
		}
	}

	private void initEmbedded(String embeddedName) {
		embedded = getBeanFactory().getInstance(embeddedName);
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setCommonApplication(this);
		if(propertyFactory.getValue("servlet.service.startup", boolean.class, true)){
			dispatcherServlet.setDefaultServletService(false);
		}
		embedded.init(getBeanFactory(), getPropertyFactory(), new ShutdownHttpServlet(getPropertyFactory(), this),
				dispatcherServlet, mainClass);
	}

	@Override
	public void destroy() {
		FormatUtils.info(TomcatApplication.class, "---------------shutdown---------------");
		if (embedded != null) {
			embedded.destroy();
		}
		super.destroy();
		System.exit(0);
	}

	private static class Run extends Thread {
		private String beanXml;
		private Class<?> mainClass;

		public Run(String beanXml, Class<?> mainClass) {
			this.beanXml = beanXml;
			this.mainClass = mainClass;
		}

		public void run() {
			if (!ResourceUtils.getResourceOperations().isExist(beanXml)) {
				FormatUtils.warn(TomcatApplication.class, "not found " + beanXml);
			}

			Application application = new ServletEmbeddedApplication(beanXml, mainClass);
			application.init();
		}
	}

	public synchronized static void run(Class<?> mainClass, String beanXml) {
		ApplicationConfigUtils.setRootPackage(BeanUtils.parseRootPackage(mainClass));

		Run run = new Run(beanXml, mainClass);
		run.setContextClassLoader(mainClass.getClassLoader());
		run.setName(mainClass.getName());
		run.setDaemon(false);
		run.start();
	}

	public static void run(Class<?> clazz) {
		run(clazz, DEFAULT_BEANS_PATH);
	}
}
