package scw.beans.builder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import scw.beans.BeanFactory;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.value.property.PropertyFactory;

public class ThreadPoolExecutorBeanBuilder extends AutoBeanBuilder {
	private static final int DEFAULT_CORE_POOL_SIZE = StringUtils.parseInt(
			GlobalPropertyFactory.getInstance().getString(
					"executor.pool.core.size"), 16);
	private static final int DEFAULT_MAXMUM_POOL_SIZE = StringUtils.parseInt(
			GlobalPropertyFactory.getInstance().getString(
					"executor.pool.max.size"), 512);
	private static final long DEFAULT_KEEP_ALIVE_TIME = StringUtils.parseLong(
			GlobalPropertyFactory.getInstance().getString(
					"executor.pool.keepAliveTime"), 1);
	private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit
			.valueOf(StringUtils.toString(GlobalPropertyFactory.getInstance()
					.getString("executor.pool.keepAliveTime.unit"),
					TimeUnit.HOURS.name()));

	public ThreadPoolExecutorBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		super(beanFactory, propertyFactory, ThreadPoolExecutor.class);
	}

	@Override
	public boolean isInstance() {
		return true;
	}

	@Override
	protected boolean isProxy() {
		return false;
	}

	@Override
	public Object create() throws Exception {
		return new ThreadPoolExecutor(DEFAULT_CORE_POOL_SIZE,
				DEFAULT_MAXMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME,
				DEFAULT_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	public void destroy(Object instance) throws Exception {
		if (instance instanceof ThreadPoolExecutor) {
			((ThreadPoolExecutor) instance).shutdownNow();
		}
	}
}
