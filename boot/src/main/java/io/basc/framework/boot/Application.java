package io.basc.framework.boot;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.config.ApplicationContextInitializers;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Application {
	@NonNull
	private ApplicationContextFactory applicationContextFactory = ApplicationContextFactory.DEFAULT;
	private final ApplicationContextInitializers applicationContextInitializers = new ApplicationContextInitializers();
	
	@NonNull
	private ApplicationType applicationType = null;

	private Class<?> mainApplicationClass;

	public Application(Class<?>... primarySources) {
		this.mainApplicationClass = deduceMainApplicationClass();
	}

	protected ConfigurableApplicationContext createApplicationContext() {
		return getApplicationContextFactory().create(getApplicationType());
	}

	private Class<?> deduceMainApplicationClass() {
		try {
			StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				if ("main".equals(stackTraceElement.getMethodName())) {
					return Class.forName(stackTraceElement.getClassName());
				}
			}
		} catch (ClassNotFoundException ex) {
			// Swallow and continue
		}
		return null;
	}

	/**
	 * Refresh the underlying {@link ApplicationContext}.
	 * 
	 * @param applicationContext the application context to refresh
	 */
	protected void refresh(ConfigurableApplicationContext applicationContext) {
		applicationContext.refresh();
	}

	public ConfigurableApplicationContext run(String... args) {
		ConfigurableApplicationContext context = createApplicationContext();
		run(context);
		return context;
	}
	
	protected void run(ConfigurableApplicationContext applicationContext) {
		getApplicationContextInitializers().initialize(applicationContext);
		refresh(applicationContext);
	}

	public static ConfigurableApplicationContext main(String[] args) {
		return run(new Class<?>[0], args);
	}

	public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
		return run(new Class<?>[] { primarySource }, args);
	}

	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		return new Application(primarySources).run(args);
	}
}
