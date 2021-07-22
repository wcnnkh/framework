package scw.ibatis.beans;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.ibatis.beans.annotation.MapperScan;

@Provider
public class IbatisBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(SqlSessionFactory.class.getName())) {
			beanFactory.registerDefinition(new SqlSessionFactoryBeanDefinition(beanFactory));
		}
		
		if(!beanFactory.containsDefinition(Configuration.class.getName())) {
			beanFactory.registerDefinition(new ConfigurationDefinition(beanFactory));
		}
		
		for(Class<?> clazz : beanFactory.getContextClasses()) {
			if(clazz.isAnnotationPresent(Mapper.class)) {
				ConfigurationUtils.registerMapperDefinition(beanFactory, clazz);
			}
		}

		for (Class<?> clazz : beanFactory.getSourceClasses()) {
			MapperScan mapperScan = clazz.getAnnotation(MapperScan.class);
			if (mapperScan != null) {
				for (String scan : mapperScan.value()) {
					for (Class<?> mapperClass : beanFactory.getClassesLoaderFactory().getClassesLoader(scan)) {
						ConfigurationUtils.registerMapperDefinition(beanFactory, mapperClass);
					}
				}
			}
		}
	}
}
