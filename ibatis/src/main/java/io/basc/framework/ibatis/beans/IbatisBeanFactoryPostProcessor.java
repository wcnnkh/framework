package io.basc.framework.ibatis.beans;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.ibatis.beans.annotation.MapperScan;

@Provider
public class IbatisBeanFactoryPostProcessor implements ContextPostProcessor {
	@Override
	public void postProcessContext(ConfigurableContext context) {
		if (!context.containsDefinition(SqlSessionFactory.class.getName())) {
			context.registerDefinition(new SqlSessionFactoryBeanDefinition(context));
		}

		if (!context.containsDefinition(Configuration.class.getName())) {
			context.registerDefinition(new ConfigurationDefinition(context));
		}

		for (Class<?> clazz : context.getContextClasses()) {
			if (clazz.isAnnotationPresent(Mapper.class)) {
				ConfigurationUtils.registerMapperDefinition(context, clazz);
			}
		}

		for (Class<?> clazz : context.getSourceClasses()) {
			MapperScan mapperScan = clazz.getAnnotation(MapperScan.class);
			if (mapperScan != null) {
				for (String scan : mapperScan.value()) {
					for (Class<?> mapperClass : context.getClassesLoaderFactory().getClassesLoader(scan,
							(e, m) -> e.getClassMetadata().isInterface())) {
						ConfigurationUtils.registerMapperDefinition(context, mapperClass);
					}
				}
			}
		}
	}

}
