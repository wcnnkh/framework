package scw.timer;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import scw.aop.Invoker;
import scw.beans.AbstractBeanConfiguration;
import scw.beans.AutoProxyMethodInvoker;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.SimpleBeanConfiguration;
import scw.beans.property.ValueWiredManager;
import scw.core.GlobalPropertyFactory;
import scw.core.Init;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.timer.annotation.Crontab;
import scw.timer.annotation.Schedule;
import scw.timer.support.SimpleCrontabConfig;
import scw.timer.support.SimpleTimerTaskConfig;
import scw.util.value.property.PropertyFactory;

@Configuration
public final class TimerAnnotationBeanConfiguration extends AbstractBeanConfiguration implements
		SimpleBeanConfiguration {
	public void init(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory) {
		addInit(new SannTimer(beanFactory));
	}

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.crontab.package", String.class,
				BeanUtils.getScanAnnotationPackageName());
	}

	private static class SannTimer implements Init {
		private BeanFactory beanFactory;

		public SannTimer(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		public void init() {
			scanningAnnotation(ClassUtils.getClassSet(getScanAnnotationPackageName()), beanFactory);
		}

		public static void scanningAnnotation(Collection<Class<?>> classList,
				BeanFactory beanFactory) {
			Timer timer = beanFactory.getInstance(Timer.class);
			for (Class<?> clz : classList) {
				for (Method method : AnnotationUtils.getAnnoationMethods(clz,
						true, true, Schedule.class)) {
					Schedule schedule = method.getAnnotation(Schedule.class);
					schedule(beanFactory, clz, method, timer, schedule);
				}

				for (Method method : AnnotationUtils.getAnnoationMethods(clz,
						true, true, Crontab.class)) {
					Crontab c = method.getAnnotation(Crontab.class);
					crontab(beanFactory, clz, method, timer, c);
				}
			}
		}

		private static Task getTask(BeanFactory beanFactory, Class<?> clz,
				Method method) {
			Class<?> parameterType = ArrayUtils.isEmpty(method
					.getParameterTypes()) ? null
					: method.getParameterTypes()[0];
			return new CrontabRunnable(new AutoProxyMethodInvoker(beanFactory,
					clz, method), parameterType);
		}

		private static void schedule(BeanFactory beanFactory, Class<?> clz,
				Method method, Timer timer, Schedule schedule) {
			Delayed delayed = beanFactory.isInstance(schedule.delay()) ? beanFactory
					.getInstance(schedule.delay()) : null;
			ScheduleTaskConfig config = new SimpleTimerTaskConfig(
					schedule.name(), getTask(beanFactory, clz, method),
					getTaskListener(beanFactory, schedule.listener()), delayed,
					schedule.period(), schedule.timeUnit());
			timer.schedule(config);
		}

		private static TaskListener getTaskListener(BeanFactory beanFactory,
				Class<? extends TaskListener> taskListenerClazz) {
			return beanFactory.isInstance(taskListenerClazz) ? beanFactory
					.getInstance(taskListenerClazz) : null;
		}

		private static void crontab(BeanFactory beanFactory, Class<?> clz,
				Method method, Timer timer, Crontab crontab) {
			timer.crontab(new SimpleCrontabConfig(crontab, getTask(beanFactory,
					clz, method), getTaskListener(beanFactory,
					crontab.listener())));
		}
	}

	private static final class CrontabRunnable implements Task {
		private final Invoker invoker;
		private final Class<?> parameterType;

		public CrontabRunnable(Invoker invoker, Class<?> parameterType) {
			this.invoker = invoker;
			this.parameterType = parameterType;
		}

		public void run(long executionTime) throws Throwable {
			if (parameterType == null) {
				invoker.invoke();
			} else {
				Object value = executionTime;
				if (parameterType == Calendar.class) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(executionTime);
					value = calendar;
				} else if (parameterType == Date.class) {
					value = new Date(executionTime);
				}
				invoker.invoke(value);
			}
		}
	}
}
