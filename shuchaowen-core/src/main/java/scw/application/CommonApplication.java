package scw.application;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;

public class CommonApplication extends XmlBeanFactory implements Application {
	public static final String DEFAULT_BEANS_PATH = "beans.xml";
	private volatile boolean start = false;

	public CommonApplication(String xmlConfigPath) {
		super(StringUtils.isEmpty(xmlConfigPath) ? DEFAULT_BEANS_PATH
				: xmlConfigPath);
	}

	public BeanFactory getBeanFactory() {
		return this;
	}

	public final synchronized void init() {
		if (start) {
			throw new ApplicationException("已经启动了");
		}

		synchronized (this) {
			if (start) {
				throw new ApplicationException("已经启动了");
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
		initInternal();
	}

	protected void initInternal() {
		try {
			super.init();
		} catch (Exception e) {
			throw new ApplicationException("BeanFactory初始化异常", e);
		}
	}

	protected void destroyInternal() {
		try {
			super.destroy();
		} catch (Exception e) {
			throw new ApplicationException("销毁异常", e);
		}
	}

	public final synchronized void destroy() {
		if (!start) {
			throw new ApplicationException("还未启动，无法销毁");
		}

		synchronized (this) {
			if (!start) {
				throw new ApplicationException("还未启动，无法销毁");
			}

			start = false;
		}

		destroyInternal();
		LoggerUtils.destroy();
	}
}
