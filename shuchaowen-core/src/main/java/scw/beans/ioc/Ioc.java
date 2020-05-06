package scw.beans.ioc;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

import scw.beans.annotation.Autowired;
import scw.beans.annotation.Config;
import scw.beans.annotation.InitMethod;
import scw.beans.annotation.Value;
import scw.core.annotation.AnnotationUtils;
import scw.mapper.FieldContext;
import scw.mapper.FieldContextFilter;
import scw.mapper.MapperUtils;

public class Ioc {
	private final IocMetadata init = new IocMetadata();
	private final IocMetadata destroy = new IocMetadata();
	private final IocMetadata autowired = new IocMetadata();

	public Ioc() {
	};

	public Ioc(Class<?> targetClass) {
		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			init.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		for (Method method : AnnotationUtils.getAnnoationMethods(targetClass, true, true, InitMethod.class)) {
			method.setAccessible(true);
			destroy.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
		}

		List<FieldContext> autowrites = MapperUtils.getFieldFactory().getFieldContexts(targetClass, null,
				new FieldContextFilter() {

					public boolean accept(FieldContext fieldContext) {
						if (!fieldContext.getField().isSupportSetter()) {
							return false;
						}

						AnnotatedElement annotatedElement = fieldContext.getField().getSetter().getAnnotatedElement();
						if (AnnotationUtils.isDeprecated(annotatedElement)) {
							return false;
						}
						return true;
					}
				});

		for (FieldContext fieldContext : autowrites) {
			AnnotatedElement annotatedElement = fieldContext.getField().getSetter().getAnnotatedElement();
			Autowired autowired = annotatedElement.getAnnotation(Autowired.class);
			if (autowired != null) {
				this.autowired.getIocProcessors().add(new AutowiredIocProcessor(fieldContext));
			}

			Config config = annotatedElement.getAnnotation(Config.class);
			if (config != null) {
				this.autowired.getIocProcessors().add(new ConfigIocProcessor(fieldContext));
			}

			Value value = annotatedElement.getAnnotation(Value.class);
			if (value != null) {
				this.autowired.getIocProcessors().add(new ValueIocProcessor(fieldContext));
			}
		}
	}

	public final IocMetadata getInit() {
		return init;
	}

	public final IocMetadata getDestroy() {
		return destroy;
	}

	public final IocMetadata getAutowired() {
		return autowired;
	}
}
