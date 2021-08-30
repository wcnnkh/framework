package io.basc.framework.consistency.policy;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanFactoryAware;
import io.basc.framework.consistency.CompensatePolicy;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public abstract class AbstractCompensatePolicy implements CompensatePolicy,
		BeanFactoryAware {
	static final String CONNECTOR = "-";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private BeanFactory beanFactory;

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	protected static void checkParameter(String group, String id) {
		Assert.requiredArgument(StringUtils.isNotEmpty(group) && group.indexOf(CONNECTOR) == -1, "group");
		Assert.requiredArgument(StringUtils.isNotEmpty(id) && id.indexOf(CONNECTOR) == -1, "id");
	}

	public abstract boolean exists(String group, String id);

	public abstract boolean remove(String group, String id);

	protected abstract Runnable getRunnable(String group, String id);

	@Override
	public Runnable get(String group, String id) {
		Runnable runnable = getRunnable(group, id);
		if (runnable == null) {
			return null;
		}

		if (beanFactory != null) {
			BeanDefinition definition = beanFactory.getDefinition(beanFactory
					.getAop().getUserClass(runnable.getClass()));
			if (definition != null) {
				definition.dependence(runnable);
				definition.init(runnable);
			}
		}
		return runnable;
	}

	@Override
	public boolean isCancelled(String group, String id) {
		return !exists(group, id);
	}

	@Override
	public boolean cancel(String group, String id) {
		return remove(group, id);
	}

	@Override
	public boolean isDone(String group, String id) {
		return !exists(group, id);
	}

	@Override
	public boolean done(String group, String id) {
		return remove(group, id);
	}

}
