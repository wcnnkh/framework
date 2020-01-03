package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public abstract class AbstractAutoBeanServiceChain implements AutoBeanServiceChain {
	private AutoBeanServiceChain chain;

	public AbstractAutoBeanServiceChain(AutoBeanServiceChain chain) {
		this.chain = chain;
	}

	public final AutoBean service(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		AutoBeanService autoBeanService = getNext(clazz, beanFactory, propertyFactory);
		if (autoBeanService == null) {
			return chain == null ? null : chain.service(clazz, beanFactory, propertyFactory);
		}

		return autoBeanService.doService(clazz, beanFactory, propertyFactory, this);
	}

	protected abstract AutoBeanService getNext(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory);
}
