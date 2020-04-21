package scw.async;

import scw.aop.ProxyUtils;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.metadata.BeanFactoryAware;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractAsyncExecutor implements AsyncExecutor,
		BeanFactoryAware {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private transient BeanFactory beanFactory;

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected Object executeInternal(AsyncRunnable asyncRunnable)
			throws Exception {
		if (asyncRunnable == null) {
			return null;
		}

		if (getBeanFactory() != null) {
			Class<?> clazz = ProxyUtils.getProxyAdapter().getUserClass(
					asyncRunnable.getClass());
			BeanDefinition definition = getBeanFactory().getDefinition(clazz);
			if (definition != null) {
				definition.init(asyncRunnable);
			}
		}
		return asyncRunnable.call();
	}
}
