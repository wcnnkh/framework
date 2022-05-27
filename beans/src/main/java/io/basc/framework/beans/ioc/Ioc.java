package io.basc.framework.beans.ioc;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.beans.annotation.Destroy;
import io.basc.framework.beans.annotation.InitMethod;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.ConcurrentReferenceHashMap;

public final class Ioc {
	private static Logger logger = LoggerFactory.getLogger(Ioc.class);
	private final IocMetadata init = new IocMetadata();
	private final IocMetadata destroy = new IocMetadata();
	private final IocMetadata dependence = new IocMetadata();

	public Ioc() {
	};

	private Ioc(Class<?> targetClass) {
		ReflectionUtils.getDeclaredMethods(targetClass).stream().filter((m) -> m.isAnnotationPresent(InitMethod.class))
				.forEach((method) -> {
					ReflectionUtils.makeAccessible(method);
					init.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
				});

		ReflectionUtils.getDeclaredMethods(targetClass).stream().filter((m) -> m.isAnnotationPresent(Destroy.class))
				.forEach((method) -> {
					ReflectionUtils.makeAccessible(method);
					destroy.getIocProcessors().add(new NoArgumentMethodIocProcessor(method));
				});

		for (Field field : Fields.getFields(targetClass).filter(FieldFeature.SUPPORT_SETTER)) {
			AnnotatedElement annotatedElement = field.getSetter();
			Autowired autowired = annotatedElement.getAnnotation(Autowired.class);
			if (autowired != null) {
				this.dependence.getIocProcessors().add(new AutowiredIocProcessor(field));
			}

			Value value = annotatedElement.getAnnotation(Value.class);
			if (value != null) {
				this.dependence.getIocProcessors().add(new ValueIocProcessor(field));
			}
		}

		ReflectionUtils.getDeclaredMethods(targetClass).stream().filter((m) -> m.isAnnotationPresent(Value.class))
				.forEach((method) -> {
					if (method.getParameterTypes().length != 1) {
						logger.error("@Value method one parameter must exis: {}", method);
						return;
					}

					Field field = new Field(null, targetClass, ParameterUtils.getParameterNames(method)[0], null, null,
							method);
					this.dependence.getIocProcessors().add(new ValueIocProcessor(field));
				});
		readyOnly();
	}

	public IocMetadata getInit() {
		return init;
	}

	public IocMetadata getDestroy() {
		return destroy;
	}

	public IocMetadata getDependence() {
		return dependence;
	}

	public void readyOnly() {
		init.readyOnly();
		dependence.readyOnly();
		destroy.readyOnly();
	}

	private static ConcurrentReferenceHashMap<Class<?>, Ioc> iocCache = new ConcurrentReferenceHashMap<Class<?>, Ioc>();

	public static Iterable<Ioc> forClass(Class<?> clazz) {
		if (clazz.getName().startsWith("java")) {
			return Collections.emptyList();
		}

		return new IocIterable(clazz);
	}

	private static final class IocIterable implements Iterable<Ioc> {
		private Class<?> clazz;

		public IocIterable(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Iterator<Ioc> iterator() {
			return new IocIterator(clazz);
		}
	}

	private static final class IocIterator extends AbstractIterator<Ioc> {
		private Class<?> clazz;

		public IocIterator(Class<?> clazz) {
			this.clazz = clazz;
		}

		public boolean hasNext() {
			if (clazz == null || clazz == Object.class) {
				return false;
			}

			return true;
		}

		public Ioc next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			Ioc ioc = iocCache.get(clazz);
			if (ioc == null) {
				ioc = new Ioc(clazz);
				Ioc old = iocCache.putIfAbsent(clazz, ioc);
				if (old != null) {
					ioc = old;
				} else {
					iocCache.purgeUnreferencedEntries();
				}
			}

			this.clazz = clazz.getSuperclass();
			return ioc;
		}
	}
}
