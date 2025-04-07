package run.soeasy.framework.core;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.transform.stereotype.Property;
import run.soeasy.framework.util.collection.ArrayUtils;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.collection.LRULinkedHashMap;
import run.soeasy.framework.util.reflect.ReflectionUtils;

@RequiredArgsConstructor
@Getter
public class MergedAnnotation<A extends Annotation> extends AbstractAnnotationProperties<A> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Map<Class<?>, Method[]> methodsMap = new LRULinkedHashMap<>(256);

	private static Method[] getMethods(Class<? extends Annotation> annotationType) {
		Method[] methods = methodsMap.get(annotationType);
		if (methods == null) {
			synchronized (methodsMap) {
				methods = methodsMap.get(annotationType);
				if (methods == null) {
					methods = annotationType.getMethods();
					if (methods != null && methods.length > 0) {
						List<Method> list = new ArrayList<>(methods.length);
						for (Method method : methods) {
							if (ReflectionUtils.isObjectMethod(method)) {
								continue;
							}
							list.add(method);
						}
						methods = list.toArray(new Method[0]);
					}
					methodsMap.put(annotationType, methods);
				}
			}
		}
		return methods;
	}

	@NonNull
	private final Class<A> type;
	@NonNull
	private final Iterable<? extends A> annotations;

	@Override
	public Elements<Property> getElements() {
		return Elements.forArray(annotations).flatMap((annotation) -> {
			Method[] methods = getMethods(type);
			return Elements.forArray(methods).map((method) -> {
				Object value = ReflectionUtils.invoke(method, annotation);
				return Property.of(method.getName(), Source.of(value));
			});
		});
	}

	@Override
	public String toString() {
		return annotations.toString();
	}

	@Override
	public int hashCode() {
		return type.hashCode() + ArrayUtils.hashCode(annotations);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Annotation) {
			Annotation annotation = (Annotation) obj;
			MergedAnnotation mergedAnnotation = new MergedAnnotation(annotation.getClass(), Arrays.asList(annotation));
			return equals(mergedAnnotation);
		}
		return super.equals(obj);
	}
}
