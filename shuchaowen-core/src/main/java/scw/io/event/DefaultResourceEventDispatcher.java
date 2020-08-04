package scw.io.event;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.GlobalPropertyFactory;
import scw.core.utils.XTime;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.EventType;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class DefaultResourceEventDispatcher extends SimpleResourceEventDispatcher {
	private static Logger logger = LoggerUtils.getLogger(DefaultResourceEventDispatcher.class);
	/**
	 * 默认的监听周期5s(经过多次尝试，在性能和实时性间取舍)
	 */
	static final long LISTENER_PERIOD = Math.max(1,
			GlobalPropertyFactory.getInstance().getValue("resource.listener.period", int.class, 5)) * 1000L;
	static final Timer TIMER = new Timer(DefaultResourceEventDispatcher.class.getSimpleName(), true);// 守护进程，自动退出
	private volatile AtomicBoolean lock = new AtomicBoolean(false);
	private final Resource resource;
	private final long period;

	public DefaultResourceEventDispatcher(Resource resource) {
		this(resource, LISTENER_PERIOD);
	}

	/**
	 * @param resource
	 * @param period 不能小于1000ms
	 */
	public DefaultResourceEventDispatcher(Resource resource, long period) {
		super(true);
		this.resource = resource;
		this.period = period < XTime.ONE_SECOND ? LISTENER_PERIOD : period;
	}
	
	private volatile TimerTask timerTask;
	protected void listener() {
		if(timerTask != null){
			return ;
		}
		
		timerTask = new DefaultEventTimerTask();
		TIMER.schedule(timerTask, period, period);
	}
	
	protected void cancelListener(){
		if(timerTask != null){
			timerTask.cancel();
			timerTask = null;
		}
	}
	
	public Resource getResource() {
		return resource;
	}
	
	protected void onChange(ResourceEvent resourceEvent){
		publishEvent(resourceEvent);
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
			try {
				boolean exist = resource.exists();
				long last = lastModified();
				if (exist != this.exist) {
					this.last = last;
					this.exist = exist;
					onChange(new ResourceEvent(exist ? EventType.CREATE : EventType.DELETE, resource));
				} else if (this.last != last) {
					this.last = last;
					onChange(new ResourceEvent(EventType.UPDATE, resource));
				}
			} catch (Exception e) {
				logger.error(e, resource);
			}
		}
	}
}
