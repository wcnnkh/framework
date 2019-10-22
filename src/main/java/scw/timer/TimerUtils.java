package scw.timer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import scw.beans.BeanFactory;
import scw.beans.MethodProxyInvoker;
import scw.core.aop.Invoker;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.AnnotationUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.TypeUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.timer.annotation.Crontab;
import scw.timer.annotation.Schedule;
import scw.timer.support.SimpleCrontabConfig;
import scw.timer.support.SimpleTimerTaskConfig;

public final class TimerUtils {
	private static Logger logger = LoggerUtils.getLogger(TimerUtils.class);

	private TimerUtils() {
	};

	public static void scanningAnnotation(Collection<Class<?>> classList, BeanFactory beanFactory) {
		Timer timer = beanFactory.getInstance(Timer.class);

		HashSet<String> taskNameSet = new HashSet<String>();
		for (Class<?> clz : classList) {
			for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, Schedule.class)) {
				Schedule schedule = method.getAnnotation(Schedule.class);
				if (taskNameSet.contains(schedule.name())) {
					throw new AlreadyExistsException("任务：" + schedule.name() + "已经存在");
				}

				schedule(beanFactory, clz, method, timer, schedule);
			}

			for (Method method : AnnotationUtils.getAnnoationMethods(clz, true, true, Crontab.class)) {
				Crontab c = method.getAnnotation(Crontab.class);
				if (taskNameSet.contains(c.name())) {
					throw new AlreadyExistsException("任务：" + c.name() + "已经存在");
				}

				crontab(beanFactory, clz, method, timer, c);
			}
		}
	}

	private static Task getTask(BeanFactory beanFactory, Class<?> clz, Method method) {
		boolean invokeTime = !ArrayUtils.isEmpty(method.getParameterTypes()) && method.getParameterTypes().length == 1
				&& TypeUtils.isLong(method.getParameterTypes()[0]);
		return new CrontabRunnable(new MethodProxyInvoker(beanFactory, clz, method), invokeTime);
	}

	private static void schedule(BeanFactory beanFactory, Class<?> clz, Method method, Timer timer, Schedule schedule) {
		Delayed delayed = beanFactory.isInstance(schedule.delay()) ? beanFactory.getInstance(schedule.delay()) : null;
		TimerTaskConfig timerTaskConfig = new SimpleTimerTaskConfig(schedule.name(), getTask(beanFactory, clz, method),
				getTaskListener(beanFactory, schedule.listener()), delayed, schedule.period(), schedule.timeUnit());
		timer.schedule(timerTaskConfig);
		logger.info("添加定时任务：name={},delay={},period={},timeunit={}", schedule.name(), schedule.delay(),
				schedule.period(), schedule.timeUnit());
	}

	private static TaskListener getTaskListener(BeanFactory beanFactory,
			Class<? extends TaskListener> taskListenerClazz) {
		return beanFactory.isInstance(taskListenerClazz) ? beanFactory.getInstance(taskListenerClazz) : null;
	}

	private static void crontab(BeanFactory beanFactory, Class<?> clz, Method method, Timer timer, Crontab crontab) {
		boolean invokeTime = !ArrayUtils.isEmpty(method.getParameterTypes()) && method.getParameterTypes().length == 1
				&& TypeUtils.isLong(method.getParameterTypes()[0]);
		CrontabRunnable crontabRun = new CrontabRunnable(new MethodProxyInvoker(beanFactory, clz, method), invokeTime);
		timer.crontab(new SimpleCrontabConfig(crontab, crontabRun, getTaskListener(beanFactory, crontab.listener())));
		LoggerUtils.getLogger(TimerUtils.class).info(
				"添加计划任务：{},dayOfWeek={},month={},dayOfMonth={},hour={},minute={}", crontab.name(), crontab.dayOfWeek(),
				crontab.month(), crontab.dayOfMonth(), crontab.hour(), crontab.minute());
	}
}

final class CrontabRunnable implements Task {
	private final Invoker invoker;
	private final boolean invokeTime;

	public CrontabRunnable(Invoker invoker, boolean invokeTime) {
		this.invoker = invoker;
		this.invokeTime = invokeTime;
	}

	public void run(long executionTime) throws Throwable{
		if (invokeTime) {
			invoker.invoke(executionTime);
		} else {
			invoker.invoke();
		}
	}
}
