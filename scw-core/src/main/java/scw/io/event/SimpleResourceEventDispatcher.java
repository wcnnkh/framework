package scw.io.event;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.utils.XTime;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.EventType;
import scw.event.support.DefaultEventDispatcher;
import scw.io.AbstractResource;
import scw.io.Resource;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class SimpleResourceEventDispatcher extends DefaultEventDispatcher<ChangeEvent<Resource>> {
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
	 * @param period 不能小于1000ms
	 */
	public SimpleResourceEventDispatcher(AbstractResource resource, long period) {
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
	
	public AbstractResource getResource() {
		return resource;
	}
	
	protected void onChange(ChangeEvent<Resource> resourceEvent){
		if(logger.isDebugEnabled()){
			logger.debug(resourceEvent.toString());
		}
		publishEvent(resourceEvent);
	}

	@Override
	public EventRegistration registerListener(EventListener<ChangeEvent<Resource>> eventListener) {
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
					onChange(new ChangeEvent<Resource>(exist ? EventType.CREATE : EventType.DELETE, resource));
				} else if (this.last != last) {
					this.last = last;
					onChange(new ChangeEvent<Resource>(EventType.UPDATE, resource));
				}
			} catch (Exception e) {
				logger.error(e, resource.toString());
			}
		}
	}
}
