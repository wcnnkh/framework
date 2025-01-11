package io.basc.framework.autoconfigure.beans.factory;

import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;
import io.basc.framework.beans.factory.di.BeanFactoryConfigurationPropertiesBeanPostProcessor;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.env.PropertyFactory;
import io.basc.framework.mapper.support.DefaultMappingStrategy;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.support.OrmUtils;
import io.basc.framework.transform.strategy.filter.ParameterNamePrefixFilter;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnotationConfigurationPropertiesBeanPostProcessor
		extends BeanFactoryConfigurationPropertiesBeanPostProcessor {
	private EntityMapper entityMapper = OrmUtils.getMapper();

	public AnnotationConfigurationPropertiesBeanPostProcessor(PropertyFactory propertyFactory,
			ConfigurableListableBeanFactory configurableBeanFactory) {
		super(propertyFactory, configurableBeanFactory);
	}

	@Override
	protected void transform(Object source, Object target, Elements<String> prefixs) {
		DefaultMappingStrategy strategy = new DefaultMappingStrategy();
		strategy.and(new ParameterNamePrefixFilter(prefixs));
		entityMapper.transform(source, TypeDescriptor.forObject(source), null, target, TypeDescriptor.forObject(target),
				null, strategy);
	}

	@Override
	protected Elements<String> getConfigurationPropertiesPrefixs(Object bean, String beanName) {
		ConfigurationProperties configurationProperties = bean.getClass().getAnnotation(ConfigurationProperties.class);
		if (configurationProperties == null) {
			return Elements.empty();
		}

		String prefix = configurationProperties.prefix();
		if (StringUtils.isEmpty(prefix)) {
			prefix = configurationProperties.value();
		}

		return StringUtils.isEmpty(prefix) ? Elements.empty() : Elements.singleton(prefix);
	}

}
