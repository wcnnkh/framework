package scw.beans.auto;

import java.util.Collection;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.CollectionUtils;

public class SimpleAutoBeanServiceChain extends AbstractAutoBeanServiceChain {
	private Iterator<AutoBeanService> iterator;

	public SimpleAutoBeanServiceChain(Collection<AutoBeanService> autoBeanServices, AutoBeanServiceChain chain) {
		super(chain);
		if (!CollectionUtils.isEmpty(autoBeanServices)) {
			iterator = autoBeanServices.iterator();
		}
	}

	@Override
	protected AutoBeanService getNext(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}
}
