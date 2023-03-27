package io.basc.framework.spring.context.boot;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import io.basc.framework.spring.context.SpringContextUtils;

public class SpringBootApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		Class<?> mainClass = event.getSpringApplication().getMainApplicationClass();
		if (mainClass != null) {
			SpringContextUtils.setMainClass(event.getEnvironment(), mainClass);
		}
	}
}
