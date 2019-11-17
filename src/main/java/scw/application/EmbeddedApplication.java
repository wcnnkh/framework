package scw.application;

import scw.application.embedded.EmbeddedServlet;
import scw.application.embedded.EmbeddedUtils;
import scw.application.embedded.ServletEmbedded;
import scw.application.embedded.ShutdownHttpServlet;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;
import scw.mvc.servlet.ServletService;
import scw.mvc.servlet.ServletUtils;

public class EmbeddedApplication extends CommonApplication {
	private ServletEmbedded embedded;

	public EmbeddedApplication(String configXml) {
		super(configXml);
	}

	private String getSupportServletEmbeddedClassName() {
		String[] classNames = new String[] { "scw.application.embedded.tomcat.TomcatServletEmbedded", };

		for (String name : classNames) {
			if (ClassUtils.isAvailable(name)) {
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
				LoggerUtils.warn(EmbeddedApplication.class, "未找到支持的embedded, 如需支持请导入对应的jar");
			} else {
				initEmbedded(name);
			}
		} else {
			initEmbedded(embeddedName);
		}
	}

	private void initEmbedded(String embeddedName) {
		ServletService service = ServletUtils.getServletService(getBeanFactory(), getPropertyFactory());
		embedded = getBeanFactory().getInstance(embeddedName);
		embedded.init(getBeanFactory(), getPropertyFactory(), new ShutdownHttpServlet(getPropertyFactory(), this),
				new EmbeddedServlet(service));
	}

	@Override
	public void destroy() {
		LoggerUtils.info(TomcatApplication.class, "---------------shutdown---------------");
		if (embedded != null) {
			embedded.destroy();
		}
		super.destroy();
		System.exit(0);
	}

	private static class Run extends Thread {
		private Class<?> clazz;
		private String beanXml;

		public Run(Class<?> clazz, String beanXml) {
			this.clazz = clazz;
			this.beanXml = beanXml;
		}

		public void run() {
			if (!ResourceUtils.isExist(beanXml)) {
				LoggerUtils.warn(TomcatApplication.class, "not found " + beanXml);
			}

			Application application;
			if (clazz == null) {
				application = new EmbeddedApplication(beanXml);
			} else {
				application = new EmbeddedApplication(beanXml) {
					@Override
					protected String getAnnotationPackage() {
						String[] arr = StringUtils.split(clazz.getName(), '.');
						if (arr.length < 2) {
							return super.getAnnotationPackage();
						} else if (arr.length == 2) {
							String p = super.getAnnotationPackage();
							if (StringUtils.isEmpty(p)) {
								return arr[0];
							} else {
								return p + "," + arr[0];
							}
						} else {
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < 2; i++) {
								if (i != 0) {
									sb.append(".");
								}
								sb.append(arr[i]);
							}

							String p = super.getAnnotationPackage();
							if (StringUtils.isEmpty(p)) {
								return sb.toString();
							} else {
								return p + "," + sb.toString();
							}
						}
					}
				};
			}
			application.init();
		}
	}

	public synchronized static void run(final Class<?> clazz, String beanXml) {
		Run run = new Run(clazz, beanXml);
		run.setName("embedded-application");
		run.setDaemon(false);
		run.start();
	}

	public static void run(Class<?> clazz) {
		run(clazz, DEFAULT_BEANS_PATH);
	}

	/**
	 * 推荐使用run(java.lang.Class clazz)方法
	 */
	public static void run() {
		run(null);
	}
}
