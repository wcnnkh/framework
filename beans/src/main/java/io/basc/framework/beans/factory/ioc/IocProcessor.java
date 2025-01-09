package io.basc.framework.beans.factory.ioc;

import java.lang.reflect.Method;

import io.basc.framework.beans.BeanMapper;
import io.basc.framework.beans.BeanUtils;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.FatalBeanException;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.core.execution.Parameters;
import io.basc.framework.core.execution.Setter;
import io.basc.framework.core.execution.reflect.ReflectionMethod;
import io.basc.framework.core.mapping.stereotype.FieldDescriptor;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.reflect.ReflectionUtils;
import lombok.Getter;
import lombok.NonNull;

/**
 * 自动注入
 * 
 * @author wcnnkh
 *
 */
@Getter
@lombok.Setter
public class IocProcessor extends ConfigurableIocResolver implements BeanPostProcessor {
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;
	@NonNull
	private BeanMapper beanMapper = BeanUtils.getMapper();

	public IocProcessor(AutowireCapableBeanFactory autowireCapableBeanFactory) {
		setLast(defaults());
		this.autowireCapableBeanFactory = autowireCapableBeanFactory;
	}

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean == null) {
			return;
		}

		for (FieldDescriptor item : beanMapper.getMapping(bean.getClass()).getElements()) {
			if (!item.isSupportSetter()) {
				continue;
			}
			Elements<Setter> setters = item.getAliasNames().map((e) -> item.setter().rename(e));
			for (Setter setter : setters) {
				if (autowireCapableBeanFactory.canExtractParameter(setter)) {
					Object value = autowireCapableBeanFactory.extractParameter(setter);
					try {
						setter.set(bean, value);
					} catch (Throwable e) {
						throw new BeansException(e);
					}
				}
			}
		}
	}

	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		for (Method method : ReflectionUtils.getDeclaredMethods(bean.getClass()).all().getElements()) {
			if (!isInitMethod(method)) {
				continue;
			}

			invoke(method, beanName);
		}
	}

	@Override
	public void postProcessBeforeDestroy(Object bean, String beanName) throws BeansException {
		for (Method method : ReflectionUtils.getDeclaredMethods(bean.getClass()).all().getElements()) {
			if (!isDestroyMethod(method)) {
				continue;
			}

			invoke(method, beanName);
		}
	}

	protected void invoke(Method method, String beanName) {
		ReflectionMethod reflectionMethod = new ReflectionMethod(method);
		if (!autowireCapableBeanFactory.hasParameters(reflectionMethod)) {
			throw new FatalBeanException("Unable to obtain the required parameters for this actuator " + method);
		}

		Parameters parameters = autowireCapableBeanFactory.getParameters(reflectionMethod);
		try {
			reflectionMethod.execute(parameters);
		} catch (Throwable e) {
			throw new FatalBeanException("Execution failed in '" + beanName + "'", e);
		}
	}
}
