package io.basc.framework.timer.boot;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.reflect.MethodExecutor;
import io.basc.framework.timer.Delayed;
import io.basc.framework.timer.ScheduleTaskConfig;
import io.basc.framework.timer.Task;
import io.basc.framework.timer.TaskListener;
import io.basc.framework.timer.Timer;
import io.basc.framework.timer.boot.annotation.Crontab;
import io.basc.framework.timer.boot.annotation.Schedule;
import io.basc.framework.timer.support.SimpleCrontabConfig;
import io.basc.framework.timer.support.SimpleTimerTaskConfig;
import io.basc.framework.util.element.Elements;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public final class TimerApplicationBoot implements ApplicationPostProcessor {

	public void postProcessApplication(ConfigurableApplication application) {
		for (Timer timer : application.getServiceLoader(Timer.class).getServices()) {
			for (String beanName : application.getBeanNames()) {
				if (!application.isSingleton(beanName)) {
					continue;
				}

				Object bean = application.getBean(beanName);
				Class<?> clazz = bean.getClass();
				ReflectionUtils.getDeclaredMethods(clazz).all().getElements()
						.filter((m) -> m.isAnnotationPresent(Schedule.class)).forEach((method) -> {
							Schedule schedule = method.getAnnotation(Schedule.class);
							schedule(application, clazz, method, timer, schedule);
						});

				ReflectionUtils.getDeclaredMethods(clazz).all().getElements()
						.filter((m) -> m.isAnnotationPresent(Crontab.class)).forEach((method) -> {
							Crontab c = method.getAnnotation(Crontab.class);
							crontab(application, clazz, method, timer, c);
						});
			}
		}
	}

	private Task getTask(Object bean, Class<?> clz, Method method) {
		MethodExecutor methodExecutor = new MethodExecutor(clz, method, bean);
		return new CrontabRunnable(methodExecutor, parameterType);
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
		private final Executor executor;

		public CrontabRunnable(Executor executor) {
			this.executor = executor;
		}

		public void run(long executionTime) throws Throwable {
			if (parameterType == null) {
				executor.execute();
			} else {
				Object value = executionTime;
				if (parameterType == Calendar.class) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(executionTime);
					value = calendar;
				} else if (parameterType == Date.class) {
					value = new Date(executionTime);
				}
				executor.execute(Elements.singleton(value));
			}
		}
	}
}
