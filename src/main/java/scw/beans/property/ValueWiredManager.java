package scw.beans.property;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public class ValueWiredManager {
	private ConcurrentHashMap<Object, TimerTask> taskMap = new ConcurrentHashMap<Object, TimerTask>();
	private Timer timer;
	private long refreshPeriod;
	private PropertyFactory propertyFactory;
	private BeanFactory beanFactory;

	public ValueWiredManager(PropertyFactory propertyFactory, BeanFactory beanFactory, Timer timer,
			long refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
		this.timer = timer;
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
	}

	public void write(ValueWired valueWired) throws Throwable {
		valueWired.wired(beanFactory, propertyFactory);
		if (valueWired.getValueAnnotation().refresh()) {
			long t = valueWired.getValueAnnotation().timeUnit().toMillis(valueWired.getValueAnnotation().period());
			t = t > 0 ? t : refreshPeriod;
			if (t > 0) {
				ValueWiredTask valueWiredTask = new ValueWiredTask(valueWired);
				if (taskMap.putIfAbsent(valueWired.getId(), valueWiredTask) == null)
					timer.scheduleAtFixedRate(valueWiredTask, t, t);
			}
		}
	}

	public void cancel(Object id) {
		TimerTask task = taskMap.remove(id);
		if (task == null) {
			return;
		}

		task.cancel();
	}

	private final class ValueWiredTask extends TimerTask {
		private final ValueWired valueWired;

		public ValueWiredTask(ValueWired valueWired) {
			this.valueWired = valueWired;
		}

		@Override
		public void run() {
			try {
				valueWired.wired(beanFactory, propertyFactory);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
