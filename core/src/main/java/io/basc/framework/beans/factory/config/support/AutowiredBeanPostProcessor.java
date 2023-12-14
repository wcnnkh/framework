package io.basc.framework.beans.factory.config.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.mapper.Member;
import io.basc.framework.mapper.MappingRegistry;
import lombok.Data;

/**
 * 自动注入
 * 
 * @author wcnnkh
 *
 */
@Data
public abstract class AutowiredBeanPostProcessor implements BeanPostProcessor {
	private final MappingRegistry mappingFactory;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		for (Member field : mappingFactory.getMapping(bean.getClass()).getElements()) {
			if (!canAutwired(bean, beanName, field)) {
				continue;
			}

			autowired(bean, beanName, field);
		}
	}

	protected abstract boolean canAutwired(Object bean, String beanName, Member field);

	protected abstract void autowired(Object bean, String beanName, Member field);
}
