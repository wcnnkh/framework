package scw.io.event;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.utils.XTime;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.io.Resource;

public class DefaultResourceEventDispatcher extends DefaultBasicEventDispatcher<ResourceEvent>
		implements ResourceEventDispatcher {
	static final Timer TIMER = new Timer();
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TIMER.cancel();
			}
		});
	}

	private volatile AtomicBoolean lock = new AtomicBoolean(false);
	private final Resource resource;

	public DefaultResourceEventDispatcher(Resource resource) {
		super(true);
		this.resource = resource;
	}

	protected void listener() {
		TIMER.schedule(new DefaultEventTimerTask(), XTime.ONE_SECOND, XTime.ONE_SECOND);
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public EventRegistration registerListener(EventListener<ResourceEvent> eventListener) {
		if (!lock.get() && lock.compareAndSet(false, true)) {
			listener();
		}
		return super.registerListener(eventListener);
	}

	class DefaultEventTimerTask extends TimerTask {
		private long last;

		public DefaultEventTimerTask() {
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
			long last = lastModified();
			if (this.last != last) {
				this.last = last;
				publishEvent(new ResourceEvent(resource, last));
			}
		}
	}
}
