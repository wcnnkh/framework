package scw.db.ibatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import scw.beans.annotation.Bean;
import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.loader.BeanBuilderLoader;
import scw.beans.loader.BeanBuilderLoaderChain;
import scw.beans.loader.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.io.ResourceUtils;

@Configuration(order = Integer.MIN_VALUE, value = BeanBuilderLoader.class)
@Bean(proxy=false)
public class IbatisBeanBuilderLoader implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain) throws Exception {
		if (context.getTargetClass() == SqlSessionFactory.class) {
			return new SqlSessionFactoryBeanBuilder(context);
		}
		return loaderChain.loading(context);
	}

	private static final class SqlSessionFactoryBeanBuilder extends
			AbstractBeanBuilder {

		public SqlSessionFactoryBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws Exception {
			String resource = "mybatis-config.xml";
			if (ResourceUtils.getResourceOperations().isExist(resource)) {
				return new SqlSessionFactoryBuilder().build(ResourceUtils
						.getResourceOperations().getInputStream(resource));
			} else {
				return new SqlSessionFactoryBuilder()
						.build(beanFactory
								.getInstance(org.apache.ibatis.session.Configuration.class));
			}
		}
	}
}
