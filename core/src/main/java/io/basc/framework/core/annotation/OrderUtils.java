package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;

import io.basc.framework.util.ClassUtils;

/**
 * @see Order
 */
@SuppressWarnings("unchecked")
public abstract class OrderUtils {

	private static Class<? extends Annotation> priorityAnnotationType = null;

	static {
		try {
			priorityAnnotationType = (Class<? extends Annotation>) ClassUtils.forName("javax.annotation.Priority",
					OrderUtils.class.getClassLoader());
		} catch (Throwable ex) {
			// javax.annotation.Priority not available, or present but not loadable (on JDK
			// 6)
		}
	}

	/**
	 * Return the order on the specified {@code type}.
	 * <p>
	 * Takes care of {@link Order @Order} and {@code @javax.annotation.Priority}.
	 * 
	 * @param type the type to handle
	 * @return the order value, or {@code null} if none can be found
	 * @see #getPriority(Class)
	 */
	public static Integer getOrder(Class<?> type) {
		return getOrder(type, null);
	}

	/**
	 * Return the order on the specified {@code type}, or the specified default
	 * value if none can be found.
	 * <p>
	 * Takes care of {@link Order @Order} and {@code @javax.annotation.Priority}.
	 * 
	 * @param type the type to handle
	 * @return the priority value, or the specified default order if none can be
	 *         found
	 * @see #getPriority(Class)
	 */
	public static Integer getOrder(Class<?> type, Integer defaultOrder) {
		Order order = AnnotationUtils.findAnnotation(type, Order.class);
		if (order != null) {
			return order.value();
		}
		Integer priorityOrder = getPriority(type);
		if (priorityOrder != null) {
			return priorityOrder;
		}
		return defaultOrder;
	}

	/**
	 * Return the value of the {@code javax.annotation.Priority} annotation declared
	 * on the specified type, or {@code null} if none.
	 * 
	 * @param type the type to handle
	 * @return the priority value if the annotation is declared, or {@code null} if
	 *         none
	 */
	public static Integer getPriority(Class<?> type) {
		if (priorityAnnotationType != null) {
			Annotation priority = AnnotationUtils.findAnnotation(type, priorityAnnotationType);
			if (priority != null) {
				return (Integer) AnnotationUtils.getValue(priority);
			}
		}
		return null;
	}

}
