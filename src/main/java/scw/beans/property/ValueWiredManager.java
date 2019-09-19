package scw.beans.property;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class ValueWiredManager {
	private static Logger logger = LoggerUtils.getLogger(ValueWiredManager.class);
	private ConcurrentHashMap<Object, ObjectValueWired> taskMap = new ConcurrentHashMap<Object, ObjectValueWired>();

	private Timer timer;
	private int refreshPeriod;
	private PropertyFactory propertyFactory;
	private BeanFactory beanFactory;

	public ValueWiredManager(PropertyFactory propertyFactory, BeanFactory beanFactory, Timer timer, int refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
		this.timer = timer;
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
	}

	public void write(Object objectId, Collection<ValueWired> valueWireds) throws Exception {
		if (CollectionUtils.isEmpty(valueWireds)) {
			return;
		}

		boolean find = false;
		for (ValueWired valueWired : valueWireds) {
			valueWired.wired(beanFactory, propertyFactory);
			if (!find) {
				if (valueWired.isCanRefresh()) {
					find = true;
				}
			}
		}

		if (!find) {
			return;
		}

		ObjectValueWired valueWired = new ObjectValueWired();
		if (taskMap.putIfAbsent(objectId, valueWired) == null) {// 不存在
			valueWired.start(timer, valueWireds);
		} else {
			logger.warn("已经存在相同的@Value刷新任务了：{}", objectId);
		}
	}

	public void cancel(Object id) {
		ObjectValueWired task = taskMap.remove(id);
		if (task == null) {
			return;
		}

		task.cancel();
	}

	private final class ObjectValueWired {
		private LinkedList<TimerTask> timerTasks = new LinkedList<TimerTask>();

		public void start(Timer timer, Collection<ValueWired> valueWireds) throws Exception {
			for (ValueWired valueWired : valueWireds) {
				if (valueWired.isCanRefresh()) {
					long t = valueWired.getValueAnnotation().timeUnit()
							.toSeconds(valueWired.getValueAnnotation().period());
					t = t > 0 ? t : refreshPeriod;
					if (t > 0) {
						t = t * 1000;
						ValueWiredTask valueWiredTask = new ValueWiredTask(valueWired);
						timerTasks.add(valueWiredTask);
						timer.scheduleAtFixedRate(valueWiredTask, t, t);
					}
				}
			}
		}

		public void cancel() {
			for (TimerTask timerTask : timerTasks) {
				timerTask.cancel();
			}
		}
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
