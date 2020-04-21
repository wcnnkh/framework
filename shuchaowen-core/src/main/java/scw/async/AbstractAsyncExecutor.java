package scw.async;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryAccessor;
import scw.beans.BeanFactoryAware;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractAsyncExecutor extends BeanFactoryAccessor
		implements AsyncExecutor, BeanFactoryAware {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected Object executeInternal(AsyncRunnable asyncRunnable)
			throws Exception {
		if (asyncRunnable == null) {
			return null;
		}

		if (getBeanFactory() != null) {
			Class<?> clazz = getBeanFactory().getAop().getUserClass(asyncRunnable.getClass());
			BeanDefinition definition = getBeanFactory().getDefinition(clazz);
			if (definition != null) {
				definition.init(asyncRunnable);
			}
		}
		return asyncRunnable.call();
	}
}
