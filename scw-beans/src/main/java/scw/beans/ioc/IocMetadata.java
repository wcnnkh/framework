package scw.beans.ioc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;

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
