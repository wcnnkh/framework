package scw.application;

import scw.application.embedded.EmbeddedServlet;
import scw.application.embedded.ServletEmbedded;
import scw.application.embedded.ShutdownHttpServlet;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;
import scw.servlet.ServletService;
import scw.servlet.ServletUtils;

public class EmbeddedApplication extends CommonApplication {
	private ServletEmbedded embedded;

	public EmbeddedApplication(String configXml) {
		super(configXml);
	}

	private String getSupportServletEmbeddedClassName() {
		String[] classNames = new String[] { "scw.application.embedded.TomcatServletEmbedded", };

		for (String name : classNames) {
			if (ClassUtils.isExist(name)) {
				return name;
			}
		}
		return null;
	}

	@Override
	public void init() {
		super.init();
		String servletEmbedded = getPropertiesFactory().getValue("application.embedded");
		if (StringUtils.isEmpty(servletEmbedded)) {
			String name = getSupportServletEmbeddedClassName();
			if (name == null) {
				LoggerUtils.warn(EmbeddedApplication.class, "未找到支持的embedded, 如需支持请导入对应的jar");
			} else {
				ServletService service = ServletUtils.getServletService(getBeanFactory(), getPropertiesFactory(),
						getConfigPath(), getBeanFactory().getFilterNames());
				embedded = InstanceUtils.getInstance(name);
				embedded.init(getBeanFactory(), getPropertiesFactory(),
						new ShutdownHttpServlet(getPropertiesFactory(), this), new EmbeddedServlet(service));
			}
		} else {
			ServletService service = ServletUtils.getServletService(getBeanFactory(), getPropertiesFactory(),
					getConfigPath(), getBeanFactory().getFilterNames());
			embedded = getBeanFactory().getInstance(servletEmbedded);
			embedded.init(getBeanFactory(), getPropertiesFactory(),
					new ShutdownHttpServlet(getPropertiesFactory(), this), new EmbeddedServlet(service));
		}

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

	public synchronized static void run(final Class<?> clazz, String beanXml) {
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

	public static void run(Class<?> clazz) {
		run(clazz, DEFAULT_BEANS_PATH);
	}

	public static void run() {
		run(null);
	}
}
