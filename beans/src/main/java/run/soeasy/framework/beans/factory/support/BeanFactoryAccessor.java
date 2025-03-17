package run.soeasy.framework.beans.factory.support;

import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.beans.factory.config.BeanFactoryAware;

public class BeanFactoryAccessor implements BeanFactoryAware {
	private transient BeanFactory beanFactory;

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
