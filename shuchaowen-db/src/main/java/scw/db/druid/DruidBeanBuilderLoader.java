package scw.db.druid;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.builder.AbstractBeanBuilder;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.db.DBUtils;
import scw.io.ResourceUtils;

@Configuration(order=Integer.MIN_VALUE)
public class DruidBeanBuilderLoader implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (DataSource.class.isAssignableFrom(context.getTargetClass())) {
			return new DruidDataSourceBeanBuilder(new LoaderContext(DruidDataSource.class, context));
		}
		return loaderChain.loading(context);
	}

	private static final class DruidDataSourceBeanBuilder extends AbstractBeanBuilder {

		public DruidDataSourceBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return ResourceUtils.getResourceOperations().isExist(DBUtils.DEFAULT_CONFIGURATION);
		}

		public Object create() throws Exception {
			DruidDataSource dataSource = new DruidDataSource();
			DBUtils.loadProperties(dataSource,
					ResourceUtils.getResourceOperations().getFormattedProperties(DBUtils.DEFAULT_CONFIGURATION).getResource());
			if (!dataSource.isPoolPreparedStatements()) {// 如果配置文件中没有开启psCache
				dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
			}
			dataSource.setRemoveAbandoned(false);
			return dataSource;
		}

		@Override
		public void destroy(Object instance) throws Exception {
			if (instance instanceof DruidDataSource) {
				if (!((DruidDataSource) instance).isClosed()) {
					((DruidDataSource) instance).close();
				}
			}
			super.destroy(instance);
		}
	}
}
