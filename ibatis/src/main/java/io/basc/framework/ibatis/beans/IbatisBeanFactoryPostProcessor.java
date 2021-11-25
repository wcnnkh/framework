package io.basc.framework.ibatis.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.ibatis.beans.annotation.MapperScan;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

@Provider
public class IbatisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(SqlSessionFactory.class.getName())) {
			beanFactory.registerDefinition(new SqlSessionFactoryBeanDefinition(beanFactory));
		}

		if (!beanFactory.containsDefinition(Configuration.class.getName())) {
			beanFactory.registerDefinition(new ConfigurationDefinition(beanFactory));
		}

		for (Class<?> clazz : beanFactory.getContextClasses()) {
			if (clazz.isAnnotationPresent(Mapper.class)) {
				ConfigurationUtils.registerMapperDefinition(beanFactory, clazz);
			}
		}

		for (Class<?> clazz : beanFactory.getSourceClasses()) {
			MapperScan mapperScan = clazz.getAnnotation(MapperScan.class);
			if (mapperScan != null) {
				for (String scan : mapperScan.value()) {
					for (Class<?> mapperClass : beanFactory.getClassesLoaderFactory().getClassesLoader(scan,
							(e, m) -> e.getClassMetadata().isInterface())) {
						ConfigurationUtils.registerMapperDefinition(beanFactory, mapperClass);
					}
				}
			}
		}
	}
}
