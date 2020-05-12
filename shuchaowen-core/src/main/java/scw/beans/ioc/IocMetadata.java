package scw.beans.ioc;

import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class IocMetadata {
	protected final LinkedList<IocProcessor> iocProcessors = new LinkedList<IocProcessor>();
	
	public LinkedList<IocProcessor> getIocProcessors() {
		return iocProcessors;
	}

	public void process(Object instance, BeanFactory beanFactory,
			PropertyFactory propertyFactory, boolean global) throws Exception {
		for (IocProcessor iocProcessor : iocProcessors) {
			if (global) {
				if (iocProcessor.isGlobal()) {
					iocProcessor
							.process(instance, beanFactory, propertyFactory);
				}
			} else {
				if (!iocProcessor.isGlobal()) {
					iocProcessor
							.process(instance, beanFactory, propertyFactory);
				}
			}
		}
	}
}
