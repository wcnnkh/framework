package io.basc.framework.context.annotation;

import io.basc.framework.beans.factory.component.ComponentRegistryPostProcessor;
import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.config.ApplicationContextInitializer;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AnnotationApplicationContextInitializer extends ComponentRegistryPostProcessor implements ApplicationContextInitializer{
	private final ServiceRegistry<Class<?>> primaryClassRegistry = new ServiceRegistry<>(); 
 	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		for(Class<?> clazz : scan(applicationContext)) {
			
		}
	}
	
	public abstract Elements<Class<?>> scan(ApplicationContext context);
}
