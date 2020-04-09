package scw.application;

import scw.beans.XmlBeanFactory;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.io.resource.ResourceUtils;
import scw.logger.LoggerUtils;
import scw.util.FormatUtils;

public class CommonApplication extends XmlBeanFactory implements Application {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean start = false;

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH
				: xmlConfigPath);
	}

	public final XmlBeanFactory getBeanFactory() {
		return this;
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
			GlobalPropertyFactory.getInstance().setBasePackageName(
					parseRootPackage(clazz));
		}
		application.init();
	}

	public static void run(Class<?> clazz) {
		run(clazz, DEFAULT_BEANS_PATH);
	}

	public static void run() {
		run(null);
	}

	public static String parseRootPackage(Class<?> clazz) {
		String[] arr = StringUtils.split(clazz.getName(), '.');
		if (arr.length < 2) {
			return null;
		} else if (arr.length == 2) {
			return arr[0];
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 2; i++) {
				if (i != 0) {
					sb.append(".");
				}
				sb.append(arr[i]);
			}

			return sb.toString();
		}
	}
}
