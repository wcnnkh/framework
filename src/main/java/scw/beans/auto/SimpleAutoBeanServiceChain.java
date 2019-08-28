package scw.beans.auto;

import java.util.Collection;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.CollectionUtils;

public class SimpleAutoBeanServiceChain implements AutoBeanServiceChain {
	private Iterator<AutoBeanService> iterator;

	public SimpleAutoBeanServiceChain(
			Collection<AutoBeanService> autoBeanServices) {
		if (!CollectionUtils.isEmpty(autoBeanServices)) {
			iterator = autoBeanServices.iterator();
		}
	}

	public AutoBean service(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception{
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next().doService(clazz, beanFactory,
					propertyFactory, this);
		}
		return null;
	}

}
