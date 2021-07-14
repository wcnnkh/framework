package scw.ibatis.beans;

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
		if(beanFactory.containsDefinition(SqlSessionFactory.class.getName())) {
			beanFactory.registerDefinition(new SqlSessionFactoryBeanDefinition(beanFactory));
		}
		
		for(Class<?> clazz : beanFactory.getSourceClasses()) {
			MapperScan scan = clazz.getAnnotation(MapperScan.class);
			if(scan == null) {
				continue;
			}

			
		}
	}

}
