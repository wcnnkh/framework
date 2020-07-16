package scw.db.hikaricp;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanDefinition;
import scw.beans.builder.AutoBeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.db.DBUtils;
import scw.io.ResourceUtils;

@Configuration(order = Integer.MIN_VALUE)
public class HikariCPBeanBuilderLoader implements BeanBuilderLoader {

	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == HikariConfig.class) {
			return new HikariConfigBeanBuilder(context);
		} else if (DataSource.class.isAssignableFrom(context.getTargetClass())) {
			return new HikariDataSourceBeanBuilder(new LoaderContext(HikariDataSource.class, context));
		}
		return loaderChain.loading(context);
	}

	private static final class HikariConfigBeanBuilder extends AbstractBeanDefinition {

		public HikariConfigBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return ResourceUtils.getResourceOperations().isExist(DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws Exception {
			HikariConfig config = new HikariConfig();
			DBUtils.loadProperties(config,
					ResourceUtils.getResourceOperations().getFormattedProperties(DBUtils.DEFAULT_CONFIGURATION).getResource());
			return config;
		}
	}

	protected static final class HikariDataSourceBeanBuilder extends AutoBeanDefinition {

		public HikariDataSourceBeanBuilder(LoaderContext context) {
			super(context);
		}

		@Override
		public void destroy(Object instance) throws Exception {
			if (instance instanceof HikariDataSource) {
				if (!((HikariDataSource) instance).isClosed()) {
					((HikariDataSource) instance).close();
				}
			}
			super.destroy(instance);
		}
	}
}
