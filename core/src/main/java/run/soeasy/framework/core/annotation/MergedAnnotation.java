package run.soeasy.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.LRULinkedHashMap;
import run.soeasy.framework.core.execute.reflect.ReflectionMethod;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.type.ReflectionUtils;

@RequiredArgsConstructor
@Getter
public class MergedAnnotation<A extends Annotation> extends AbstractAnnotationPropertyMapping<A>
		implements Serializable {
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
	public Iterator<PropertyAccessor> iterator() {
		return CollectionUtils.unknownSizeStream(annotations.iterator()).flatMap((annotation) -> {
			Method[] methods = getMethods(type);
			return Arrays.asList(methods).stream()
					.map((method) -> new ReflectionMethod(method).accessor(annotation));
		}).iterator();
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
