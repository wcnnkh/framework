package scw.beans.ioc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class IocMetadata {
	private List<IocProcessor> iocProcessors = new ArrayList<IocProcessor>();

	public List<IocProcessor> getIocProcessors() {
		return iocProcessors;
	}

	public void process(BeanDefinition beanDefinition, Object instance,
			BeanFactory beanFactory, PropertyFactory propertyFactory,
			boolean global) throws Exception {
		for (IocProcessor iocProcessor : iocProcessors) {
			if (global) {
				if (iocProcessor.isGlobal()) {
					iocProcessor.process(beanDefinition, instance, beanFactory,
							propertyFactory);
				}
			} else {
				if (!iocProcessor.isGlobal()) {
					iocProcessor.process(beanDefinition, instance, beanFactory,
							propertyFactory);
				}
			}
		}
	}

	public void readyOnly() {
		this.iocProcessors = Arrays.asList(iocProcessors.toArray(new IocProcessor[0]));
	}
}
