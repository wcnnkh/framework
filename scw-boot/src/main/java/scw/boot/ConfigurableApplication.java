package scw.boot;

import scw.context.ConfigurableContext;

public interface ConfigurableApplication extends Application, ConfigurableContext {
	void addPostProcessor(ApplicationPostProcessor postProcessor);
}
