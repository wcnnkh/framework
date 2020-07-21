package scw.io.event;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.utils.XTime;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.event.support.EventType;
import scw.io.Resource;

public class DefaultResourceEventDispatcher extends DefaultBasicEventDispatcher<ResourceEvent>
		implements ResourceEventDispatcher {
	static final Timer TIMER = new Timer(true);//守护进程，自动退出
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
			boolean exist = resource.exists();
			long last = lastModified();
			if (exist != this.exist) {
				this.last = last;
				this.exist = exist;
				publishEvent(new ResourceEvent(exist ? EventType.CREATE : EventType.DELETE, resource));
			} else if (this.last != last) {
				this.last = last;
				publishEvent(new ResourceEvent(EventType.UPDATE, resource));
			}
		}
	}
}
