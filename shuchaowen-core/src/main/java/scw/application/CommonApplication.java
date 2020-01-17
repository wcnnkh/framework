package scw.application;

import scw.beans.BeanUtils;
import scw.beans.XmlBeanFactory;
import scw.core.utils.StringUtils;
import scw.io.resource.ResourceUtils;
import scw.logger.LoggerUtils;
import scw.util.FormatUtils;

public class CommonApplication extends XmlBeanFactory implements Application {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean start = false;

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH : xmlConfigPath);
	}

	public final XmlBeanFactory getBeanFactory() {
		return this;
	}

	@Override
	protected String getServicePackage() {
		return ApplicationConfigUtils.getServiceAnnotationPackage(getPropertyFactory());
	}

	@Override
	protected String getInitStaticPackage() {
		return ApplicationConfigUtils.getInitStaticPackage(getPropertyFactory());
	}

	protected String getStaticAnnotationPackage() {
		return ApplicationConfigUtils.getInitStaticPackage(getPropertyFactory());
	}

	public void init() {
		if (start) {
			throw new RuntimeException("已经启动了");
		}

		synchronized (this) {
			if (start) {
				throw new RuntimeException("已经启动了");
			}

			start = true;
		}

		/**
		 * 使用容器进行初始化时，如果未找到log4j配置文件使用默认配置
		 */
		if (LoggerUtils.defaultConfigEnable() == null) {
			LoggerUtils.setDefaultConfigenable(true);
		}

		LoggerUtils.init();
		super.init();
	}

	public void destroy() {
		if (!start) {
			throw new RuntimeException("还未启动，无法销毁");
		}

		synchronized (this) {
			if (!start) {
				throw new RuntimeException("还未启动，无法销毁");
			}

			start = false;
		}

		super.destroy();
		LoggerUtils.destroy();
	}

	public synchronized static void run(final Class<?> clazz, String beanXml) {
		if (!ResourceUtils.getResourceOperations().isExist(beanXml)) {
			FormatUtils.warn(CommonApplication.class, "not found " + beanXml);
		}

		CommonApplication application = new CommonApplication(beanXml);
		if (clazz != null) {
			ApplicationConfigUtils.setRootPackage(BeanUtils.parseRootPackage(clazz));
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
