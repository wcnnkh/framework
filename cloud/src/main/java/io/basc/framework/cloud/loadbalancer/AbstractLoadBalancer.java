package io.basc.framework.cloud.loadbalancer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.select.Selector;

public abstract class AbstractLoadBalancer<T extends Node> implements LoadBalancer<T>, EnvironmentAware {
	private static Logger logger = LoggerFactory.getLogger(AbstractLoadBalancer.class);
	private static final Timer TIMER = new Timer(AbstractLoadBalancer.class.getName(), true);
	private volatile TimerTask autoReloadTask;
	private Environment environment;
	private final Selector<T> selector;

	private AtomicBoolean started = new AtomicBoolean();

	public AbstractLoadBalancer(Selector<T> selector) {
		Assert.requiredArgument(selector != null, "selector");
		this.selector = selector;
	}

	@Override
	public final T choose() {
		return LoadBalancer.super.choose();
	}

	protected T choose(Elements<T> services) {
		if (services == null) {
			return null;
		}
		return getSelector().apply(services);
	}

	@Override
	public final T choose(Predicate<? super T> accept) {
		startAutoReload();
		Elements<T> servers = accept == null ? this.getServices() : getServices().filter(accept);
		return choose(servers);
	}

	@Override
	public final T choose(String name) {
		return LoadBalancer.super.choose(name);
	}

	@Override
	public final T choose(String name, Predicate<? super T> accept) {
		startAutoReload();
		Elements<T> elements = chooses(name);
		if (elements == null) {
			return null;
		}
		return choose(elements);
	}

	@Override
	public Elements<T> chooses(String name) {
		return LoadBalancer.super.chooses(name);
	}

	@Nullable
	public Environment getEnvironment() {
		return environment;
	}

	public Selector<T> getSelector() {
		return selector;
	}

	public boolean isAutoReload() {
		return autoReloadTask != null;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * 默认1分钟刷新
	 * 
	 * @return
	 */
	public boolean startAutoReload() {
		if (started.get() || !started.compareAndSet(false, true)) {
			return false;
		}

		/**
		 * 单位分钟
		 */
		long time = 1;
		if (environment != null) {
			Value period = environment.get("basc.loadbalancer.refresh.period");
			if (period.isPresent() && period.getAsLong() == 0) {
				// 不启动
				return false;
			}

			time = period.or(1).getAsLong();
		}
		logger.info("Start automatic reload with a cycle of {} minutes", time);
		return startAutoReload(time, TimeUnit.MINUTES);
	}

	public boolean startAutoReload(long period, TimeUnit unit) {
		long time = unit.toMillis(period);
		Assert.isTrue(time > 0, "The period time should be greater than 0ms");
		if (autoReloadTask == null) {
			synchronized (this) {
				if (autoReloadTask == null) {
					autoReloadTask = new TimerTask() {

						@Override
						public void run() {
							reload();
						}
					};

					TIMER.schedule(autoReloadTask, time, time);
					return true;
				}
			}
		}
		return false;
	}

	public boolean stopAutoReload() {
		started.compareAndSet(true, false);
		if (autoReloadTask != null) {
			synchronized (this) {
				if (autoReloadTask != null) {
					autoReloadTask.cancel();
					autoReloadTask = null;
					return true;
				}
			}
		}
		return false;
	}
}
