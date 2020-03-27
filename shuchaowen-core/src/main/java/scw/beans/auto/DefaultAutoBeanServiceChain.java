package scw.beans.auto;

import java.util.Collection;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.core.utils.CollectionUtils;
import scw.util.value.property.PropertyFactory;

public class DefaultAutoBeanServiceChain extends AbstractAutoBeanServiceChain {
	private Iterator<Class<? extends AutoBeanService>> iterator;

	public DefaultAutoBeanServiceChain(Collection<Class<? extends AutoBeanService>> collection,
			AutoBeanServiceChain chain) {
		super(chain);
		if (!CollectionUtils.isEmpty(collection)) {
			this.iterator = collection.iterator();
		}
	}

	@Override
	protected AutoBeanService getNext(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory) {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return beanFactory.getInstance(iterator.next());
		}

		return null;
	}
}
