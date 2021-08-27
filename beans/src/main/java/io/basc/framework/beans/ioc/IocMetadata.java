package io.basc.framework.beans.ioc;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IocMetadata {
	private List<IocProcessor> iocProcessors = new ArrayList<IocProcessor>();

	public List<IocProcessor> getIocProcessors() {
		return iocProcessors;
	}

	public void process(BeanDefinition beanDefinition, Object instance, BeanFactory beanFactory) throws BeansException {
		for (IocProcessor iocProcessor : iocProcessors) {
			iocProcessor.process(beanDefinition, instance, beanFactory);
		}
	}

	public void readyOnly() {
		this.iocProcessors = Arrays.asList(iocProcessors.toArray(new IocProcessor[0]));
	}
}
