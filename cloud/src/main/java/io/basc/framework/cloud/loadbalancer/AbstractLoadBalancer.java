package io.basc.framework.cloud.loadbalancer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Selector;

public abstract class AbstractLoadBalancer<T extends Node> implements LoadBalancer<T> {
	private static final Timer TIMER = new Timer(AbstractLoadBalancer.class.getName(), true);
	private volatile TimerTask autoReloadTask;
	private final Selector<T> selector;

	public AbstractLoadBalancer(Selector<T> selector) {
		Assert.requiredArgument(selector != null, "selector");
		this.selector = selector;
	}

	public Selector<T> getSelector() {
		return selector;
	}

	public boolean isAutoReload() {
		return autoReloadTask != null;
	}

	@Override
	public final T choose() {
		return LoadBalancer.super.choose();
	}

	@Override
	public final T choose(Predicate<? super T> accept) {
		Elements<T> servers = accept == null ? this : filter(accept);
		return choose(servers);
	}

	@Override
	public final T choose(String name) {
		return LoadBalancer.super.choose(name);
	}

	protected T choose(Elements<T> services) {
		if (services == null) {
			return null;
		}
		return getSelector().apply(services);
	}

	@Override
	public final T choose(String name, Predicate<? super T> accept) {
		Elements<T> elements = chooses(name);
		if (elements == null) {
			return null;
		}

		return choose(elements);
	}

	public boolean autoReload(long period, TimeUnit unit) {
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

	public boolean stop() {
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
