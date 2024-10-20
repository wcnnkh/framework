package io.basc.framework.timer.boot;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.Invoker;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.NameInstanceSupplier;
import io.basc.framework.timer.Delayed;
import io.basc.framework.timer.ScheduleTaskConfig;
import io.basc.framework.timer.Task;
import io.basc.framework.timer.TaskListener;
import io.basc.framework.timer.Timer;
import io.basc.framework.timer.boot.annotation.Crontab;
import io.basc.framework.timer.boot.annotation.Schedule;
import io.basc.framework.timer.support.SimpleCrontabConfig;
import io.basc.framework.timer.support.SimpleTimerTaskConfig;
import io.basc.framework.util.ArrayUtils;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public final class TimerApplicationBoot implements ApplicationPostProcessor {

	public void postProcessApplication(ConfigurableApplication application) {
		Timer timer = application.getInstance(Timer.class);
		for (Class<?> clz : application.getContextClasses()) {
			ReflectionUtils.getDeclaredMethods(clz).all().stream().filter((m) -> m.isAnnotationPresent(Schedule.class))
					.forEach((method) -> {
						Schedule schedule = method.getAnnotation(Schedule.class);
						schedule(application, clz, method, timer, schedule);
					});

			ReflectionUtils.getDeclaredMethods(clz).all().stream().filter((m) -> m.isAnnotationPresent(Crontab.class))
					.forEach((method) -> {
						Crontab c = method.getAnnotation(Crontab.class);
						crontab(application, clz, method, timer, c);
					});
		}
	}

	private Task getTask(BeanFactory beanFactory, Class<?> clz, Method method) {
		Class<?> parameterType = ArrayUtils.isEmpty(method.getParameterTypes()) ? null : method.getParameterTypes()[0];
		MethodInvoker invoker = beanFactory.getAop().getProxyMethod(clz,
				new NameInstanceSupplier<Object>(beanFactory, clz.getName()), method);
		return new CrontabRunnable(invoker, parameterType);
	}

	private void schedule(BeanFactory beanFactory, Class<?> clz, Method method, Timer timer, Schedule schedule) {
		Delayed delayed = beanFactory.isInstance(schedule.delay()) ? beanFactory.getInstance(schedule.delay()) : null;
		ScheduleTaskConfig config = new SimpleTimerTaskConfig(schedule.name(), getTask(beanFactory, clz, method),
				getTaskListener(beanFactory, schedule.listener()), delayed, schedule.period(), schedule.timeUnit());
		timer.schedule(config);
	}

	private TaskListener getTaskListener(BeanFactory beanFactory, Class<? extends TaskListener> taskListenerClazz) {
		return beanFactory.isInstance(taskListenerClazz) ? beanFactory.getInstance(taskListenerClazz) : null;
	}

	private void crontab(BeanFactory beanFactory, Class<?> clz, Method method, Timer timer, Crontab crontab) {
		timer.crontab(new SimpleCrontabConfig(crontab, getTask(beanFactory, clz, method),
				getTaskListener(beanFactory, crontab.listener())));
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
