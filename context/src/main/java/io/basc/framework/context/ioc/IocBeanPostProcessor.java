package io.basc.framework.context.ioc;

import java.lang.reflect.Method;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Mapping;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.Setter;
import lombok.Data;

@Data
public class IocBeanPostProcessor implements BeanPostProcessor {
	private final IocResolver iocResolver;
	private final MappingFactory mappingFactory;
	private final ExecutionParametersExtractor executionParametersExtractor;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Mapping<? extends Field> mapping = mappingFactory.getMapping(bean.getClass());
		for (Field field : mapping.getElements()) {
			for(Setter setter : field.getSetters()) {
				
			}
		}
	}

	@Override
	public void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		for (Method method : ReflectionUtils.getMethods(bean.getClass()).all().getElements()) {
			for (Executor executor : iocResolver.resolveInitializeExecutors(bean, beanName,
					method)) {
				if(!executionParametersExtractor.canExtractExecutionParameters(executor)) {
					//无法执行
					
				}
			}
		}
	}

	@Override
	public void postProcessBeforeDestory(Object bean, String beanName) throws BeansException {
		for (Method method : ReflectionUtils.getMethods(bean.getClass()).all().getElements()) {

		}
	}
}
