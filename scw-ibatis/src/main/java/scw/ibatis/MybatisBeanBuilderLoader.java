package scw.ibatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.io.Resource;
import scw.io.ResourceUtils;

@Provider(order = Integer.MIN_VALUE, value = BeanDefinitionLoader.class)
public class MybatisBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == SqlSessionFactory.class) {
			return new SqlSessionFactoryBeanBuilder(beanFactory, sourceClass);
		}
		return loaderChain.load(beanFactory, sourceClass);
	}

	private static final class SqlSessionFactoryBeanBuilder extends DefaultBeanDefinition {

		public SqlSessionFactoryBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws BeansException {
			Resource resource = beanFactory.getEnvironment().getResource("mybatis-config.xml");
			if (resource != null && resource.exists()) {
				return new SqlSessionFactoryBuilder().build(ResourceUtils.getInputStream(resource));
			} else {
				return new SqlSessionFactoryBuilder()
						.build(beanFactory.getInstance(org.apache.ibatis.session.Configuration.class));
			}
		}
	}
}
