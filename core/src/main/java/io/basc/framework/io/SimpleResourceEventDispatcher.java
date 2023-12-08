package io.basc.framework.io;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.value.ValueChangeEvent;
import io.basc.framework.util.Registration;

public class SimpleResourceEventDispatcher extends StandardBroadcastEventDispatcher<ObservableEvent<Resource>> {
	private static Logger logger = LoggerFactory.getLogger(SimpleResourceEventDispatcher.class);
	/**
	 * 默认的监听周期5s(经过多次尝试，在性能和实时性间取舍)
	 */
	static final long LISTENER_PERIOD = Math.max(1, Integer.getInteger("resource.listener.period", 5)) * 1000L;
	static final Timer TIMER = new Timer(SimpleResourceEventDispatcher.class.getSimpleName(), true);// 守护进程，自动退出
	private volatile AtomicBoolean lock = new AtomicBoolean(false);
	private final AbstractResource resource;
	private final long period;

	public SimpleResourceEventDispatcher(AbstractResource resource) {
		this(resource, LISTENER_PERIOD);
	}

	/**
	 * @param resource
	 * @param period
	 */
	public SimpleResourceEventDispatcher(AbstractResource resource, long period) {
		this.resource = resource;
		this.period = Math.max(LISTENER_PERIOD, period);
	}

	private volatile TimerTask timerTask;

	protected void listener() {
		if (timerTask != null) {
			return;
		}

		timerTask = new DefaultEventTimerTask();
		TIMER.schedule(timerTask, period, period);
	}

	protected void cancelListener() {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}

	public AbstractResource getResource() {
		return resource;
	}

	@Override
	public void publishEvent(ObservableEvent<Resource> event) {
		if (logger.isDebugEnabled()) {
			logger.debug(event.toString());
		}
		super.publishEvent(event);
	}

	@Override
	public Registration registerListener(EventListener<ObservableEvent<Resource>> eventListener) {
		if (!lock.get() && lock.compareAndSet(false, true)) {
			listener();
		}
		return super.registerListener(eventListener);
	}

	class DefaultEventTimerTask extends TimerTask {
		private long last;
		private boolean exist;

		public DefaultEventTimerTask() {
			this.exist = resource.exists();
			this.last = lastModified();
		}

		protected long lastModified() {
			if (!resource.exists()) {
				return -1;
			}

			try {
				return resource.lastModified();
			} catch (IOException e) {
				// ignore
			}
			return -2;
		}

		@Override
		public void run() {
			try {
				boolean exist = resource.exists();
				long last = lastModified();
				if (exist != this.exist) {
					this.last = last;
					this.exist = exist;
					publishEvent(new ValueChangeEvent<Resource>(exist ? ChangeType.CREATE : ChangeType.DELETE,
							resource, resource));
				} else if (this.last != last) {
					this.last = last;
					publishEvent(new ValueChangeEvent<Resource>(ChangeType.UPDATE, resource, resource));
				}
			} catch (Exception e) {
				logger.error(e, resource.toString());
			}
		}
	}
}
