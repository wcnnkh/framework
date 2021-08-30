package io.basc.framework.annotation;

import io.basc.framework.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @see Annotation
 * @see AnnotationAttributeExtractor
 * @see AnnotationUtils#synthesizeAnnotation(Annotation, AnnotatedElement)
 */
class SynthesizedAnnotationInvocationHandler implements InvocationHandler {

	private final AnnotationAttributeExtractor<?> attributeExtractor;

	private final Map<String, Object> valueCache = new ConcurrentHashMap<String, Object>(8);

	/**
	 * Construct a new {@code SynthesizedAnnotationInvocationHandler} for the
	 * supplied {@link AnnotationAttributeExtractor}.
	 * 
	 * @param attributeExtractor
	 *            the extractor to delegate to
	 */
	SynthesizedAnnotationInvocationHandler(AnnotationAttributeExtractor<?> attributeExtractor) {
		Assert.notNull(attributeExtractor, "AnnotationAttributeExtractor must not be null");
		this.attributeExtractor = attributeExtractor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (ReflectionUtils.isEqualsMethod(method)) {
			return annotationEquals(args[0]);
		}
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return annotationHashCode();
		}
		if (ReflectionUtils.isToStringMethod(method)) {
			return annotationToString();
		}
		if (AnnotationUtils.isAnnotationTypeMethod(method)) {
			return annotationType();
		}
		if (!AnnotationUtils.isAttributeMethod(method)) {
			throw new AnnotationConfigurationException(String.format(
					"Method [%s] is unsupported for synthesized annotation type [%s]", method, annotationType()));
		}
		return getAttributeValue(method);
	}

	private Class<? extends Annotation> annotationType() {
		return this.attributeExtractor.getAnnotationType();
	}

	private Object getAttributeValue(Method attributeMethod) {
		String attributeName = attributeMethod.getName();
		Object value = this.valueCache.get(attributeName);
		if (value == null) {
			value = this.attributeExtractor.getAttributeValue(attributeMethod);
			if (value == null) {
				String msg = String.format("%s returned null for attribute name [%s] from attribute source [%s]",
						this.attributeExtractor.getClass().getName(), attributeName,
						this.attributeExtractor.getSource());
				throw new IllegalStateException(msg);
			}

			// Synthesize nested annotations before returning them.
			if (value instanceof Annotation) {
				value = AnnotationUtils.synthesizeAnnotation((Annotation) value,
						this.attributeExtractor.getAnnotatedElement());
			} else if (value instanceof Annotation[]) {
				value = AnnotationUtils.synthesizeAnnotationArray((Annotation[]) value,
						this.attributeExtractor.getAnnotatedElement());
			}

			this.valueCache.put(attributeName, value);
		}

		// Clone arrays so that users cannot alter the contents of values in our
		// cache.
		if (value.getClass().isArray()) {
			value = cloneArray(value);
		}

		return value;
	}

	/**
	 * Clone the provided array, ensuring that original component type is
	 * retained.
	 * 
	 * @param array
	 *            the array to clone
	 */
	private Object cloneArray(Object array) {
		if (array instanceof boolean[]) {
			return ((boolean[]) array).clone();
		}
		if (array instanceof byte[]) {
			return ((byte[]) array).clone();
		}
		if (array instanceof char[]) {
			return ((char[]) array).clone();
		}
		if (array instanceof double[]) {
			return ((double[]) array).clone();
		}
		if (array instanceof float[]) {
			return ((float[]) array).clone();
		}
		if (array instanceof int[]) {
			return ((int[]) array).clone();
		}
		if (array instanceof long[]) {
			return ((long[]) array).clone();
		}
		if (array instanceof short[]) {
			return ((short[]) array).clone();
		}

		// else
		return ((Object[]) array).clone();
	}

	/**
	 * See {@link Annotation#equals(Object)} for a definition of the required
	 * algorithm.
	 * 
	 * @param other
	 *            the other object to compare against
	 */
	private boolean annotationEquals(Object other) {
		if (this == other) {
			return true;
		}
		if (!annotationType().isInstance(other)) {
			return false;
		}

		for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotationType())) {
			Object thisValue = getAttributeValue(attributeMethod);
			Object otherValue = ReflectionUtils.invokeMethod(attributeMethod, other);
			if (!ObjectUtils.nullSafeEquals(thisValue, otherValue)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * See {@link Annotation#hashCode()} for a definition of the required
	 * algorithm.
	 */
	private int annotationHashCode() {
		int result = 0;

		for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotationType())) {
			Object value = getAttributeValue(attributeMethod);
			int hashCode;
			if (value.getClass().isArray()) {
				hashCode = hashCodeForArray(value);
			} else {
				hashCode = value.hashCode();
			}
			result += (127 * attributeMethod.getName().hashCode()) ^ hashCode;
		}

		return result;
	}

	/**
	 * @param array
	 *            the array to compute the hash code for
	 */
	private int hashCodeForArray(Object array) {
		if (array instanceof boolean[]) {
			return Arrays.hashCode((boolean[]) array);
		}
		if (array instanceof byte[]) {
			return Arrays.hashCode((byte[]) array);
		}
		if (array instanceof char[]) {
			return Arrays.hashCode((char[]) array);
		}
		if (array instanceof double[]) {
			return Arrays.hashCode((double[]) array);
		}
		if (array instanceof float[]) {
			return Arrays.hashCode((float[]) array);
		}
		if (array instanceof int[]) {
			return Arrays.hashCode((int[]) array);
		}
		if (array instanceof long[]) {
			return Arrays.hashCode((long[]) array);
		}
		if (array instanceof short[]) {
			return Arrays.hashCode((short[]) array);
		}

		// else
		return Arrays.hashCode((Object[]) array);
	}

	/**
	 * See {@link Annotation#toString()} for guidelines on the recommended
	 * format.
	 */
	private String annotationToString() {
		StringBuilder sb = new StringBuilder("@").append(annotationType().getName()).append("(");

		Iterator<Method> iterator = AnnotationUtils.getAttributeMethods(annotationType()).iterator();
		while (iterator.hasNext()) {
			Method attributeMethod = iterator.next();
			sb.append(attributeMethod.getName());
			sb.append('=');
			sb.append(attributeValueToString(getAttributeValue(attributeMethod)));
			sb.append(iterator.hasNext() ? ", " : "");
		}

		return sb.append(")").toString();
	}

	private String attributeValueToString(Object value) {
		if (value instanceof Object[]) {
			return "[" + StringUtils.arrayToDelimitedString((Object[]) value, ", ") + "]";
		}
		return String.valueOf(value);
	}

}
