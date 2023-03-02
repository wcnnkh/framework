package io.basc.framework.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionUtils;

/**
 * Miscellaneous object utility methods.
 * 
 * @author wcnnkh
 *
 */
public abstract class ObjectUtils {
	public static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Return whether the given throwable is a checked exception: that is, neither a
	 * RuntimeException nor an Error.
	 * 
	 * @param ex the throwable to check
	 * @return whether the throwable is a checked exception
	 * @see java.lang.Exception
	 * @see java.lang.RuntimeException
	 * @see java.lang.Error
	 */
	public static boolean isCheckedException(Throwable ex) {
		return !(ex instanceof RuntimeException || ex instanceof Error);
	}

	/**
	 * Check whether the given exception is compatible with the exceptions declared
	 * in a throws clause.
	 * 
	 * @param ex                 the exception to checked
	 * @param declaredExceptions the exceptions declared in the throws clause
	 * @return whether the given exception is compatible
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean isCompatibleWithThrowsClause(Throwable ex, Class[] declaredExceptions) {
		if (!isCheckedException(ex)) {
			return true;
		}
		if (declaredExceptions != null) {
			int i = 0;
			while (i < declaredExceptions.length) {
				if (declaredExceptions[i].isAssignableFrom(ex.getClass())) {
					return true;
				}
				i++;
			}
		}
		return false;
	}

	/**
	 * Determine whether the given object is an array: either an Object array or a
	 * primitive array.
	 * 
	 * @param obj the object to check
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	/**
	 * Determine whether the given array is empty: i.e. {@code null} or of zero
	 * length.
	 * 
	 * @param array the array to check
	 */
	/*
	 * public static boolean isEmpty(Object[] array) { return (array == null ||
	 * array.length == 0); }
	 */

	/**
	 * 判断是否为空的，自动识别类型
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof CharSequence) {
			return StringUtils.isEmpty((CharSequence) obj);
		} else if (obj instanceof Collection) {
			return CollectionUtils.isEmpty((Collection) obj);
		} else if (obj instanceof Map) {
			return CollectionUtils.isEmpty((Map) obj);
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		return false;
	}

	/**
	 * Check whether the given array contains the given element.
	 * 
	 * @param array   the array to check (may be {@code null}, in which case the
	 *                return value will always be {@code false})
	 * @param element the element to check for
	 * @return whether the element has been found in the given array
	 */
	public static boolean containsElement(Object[] array, Object element) {
		if (array == null) {
			return false;
		}
		for (Object arrayEle : array) {
			if (equals(arrayEle, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given array of enum constants contains a constant with the
	 * given name, ignoring case when determining a match.
	 * 
	 * @param enumValues the enum values to check, typically the product of a call
	 *                   to MyEnum.values()
	 * @param constant   the constant name to find (must not be null or empty
	 *                   string)
	 * @return whether the constant has been found in the given array
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
		return containsConstant(enumValues, constant, false);
	}

	/**
	 * Check whether the given array of enum constants contains a constant with the
	 * given name.
	 * 
	 * @param enumValues    the enum values to check, typically the product of a
	 *                      call to MyEnum.values()
	 * @param constant      the constant name to find (must not be null or empty
	 *                      string)
	 * @param caseSensitive whether case is significant in determining a match
	 * @return whether the constant has been found in the given array
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
		for (Enum<?> candidate : enumValues) {
			if (caseSensitive ? candidate.toString().equals(constant)
					: candidate.toString().equalsIgnoreCase(constant)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Case insensitive alternative to {@link Enum#valueOf(Class, String)}.
	 * 
	 * @param <E>        the concrete Enum type
	 * @param enumValues the array of all Enum constants in question, usually per
	 *                   Enum.values()
	 * @param constant   the constant to get the enum value of
	 * @throws IllegalArgumentException if the given constant is not found in the
	 *                                  given array of enum values. Use
	 *                                  {@link #containsConstant(Enum[], String)} as
	 *                                  a guard to avoid this exception.
	 */
	public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
		for (E candidate : enumValues) {
			if (candidate.toString().equalsIgnoreCase(constant)) {
				return candidate;
			}
		}
		throw new IllegalArgumentException(String.format("constant [%s] does not exist in enum type %s", constant,
				enumValues.getClass().getComponentType().getName()));
	}

	/**
	 * Append the given object to the given array, returning a new array consisting
	 * of the input array contents plus the given object.
	 * 
	 * @param array the array to append to (can be {@code null})
	 * @param obj   the object to append
	 * @return the new array (of the same component type; never {@code null})
	 */
	public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		} else if (obj != null) {
			compType = obj.getClass();
		}
		int newArrLength = (array != null ? array.length + 1 : 1);
		@SuppressWarnings("unchecked")
		A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
		if (array != null) {
			System.arraycopy(array, 0, newArr, 0, array.length);
		}
		newArr[newArr.length - 1] = obj;
		return newArr;
	}

	/**
	 * Convert the given array (which may be a primitive array) to an object array
	 * (if necessary of primitive wrapper objects).
	 * <p>
	 * A {@code null} source value will be converted to an empty Object array.
	 * 
	 * @param source the (potentially primitive) array
	 * @return the corresponding object array (never {@code null})
	 * @throws IllegalArgumentException if the parameter is not an array
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		Class wrapperType = Array.get(source, 0).getClass();
		Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}

	public static String toString(Object source, boolean deep) {
		if (source == null) {
			return null;
		} else if (source.getClass().isArray()) {
			return ArrayUtils.toString(source, deep);
		} else {
			return source.toString();
		}
	}

	public static String toString(Object source) {
		return toString(source, true);
	}

	public static int hashCode(Object source, boolean deep) {
		if (source == null) {
			return 0;
		} else if (source.getClass().isArray()) {
			return ArrayUtils.hashCode(source, deep);
		} else {
			return source.hashCode();
		}
	}

	public static int hash(Object... sources) {
		return ArrayUtils.hashCode(sources);
	}

	public static int hashCode(Object source) {
		return hashCode(source, true);
	}

	public static boolean equals(Object left, Object right, boolean deep) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		if (left.equals(right)) {
			return true;
		}

		if (left.getClass().isArray() && right.getClass().isArray()) {
			return ArrayUtils.equals(left, right, deep);
		}
		return false;
	}

	public static boolean equals(Object left, Object right) {
		return equals(left, right, true);
	}

	/**
	 * 浅拷贝
	 * 
	 * @see ObjectUtils#clone(Object, boolean)
	 * @param <T>
	 * @param source
	 * @return 如果无法克隆就返回本身
	 */
	public static <T> T clone(T source) {
		return clone(source, false);
	}

	/**
	 * 克隆 (如果对象实现了Cloneable接口,那么会调用clone方法)
	 * 
	 * 以下情况会进行克隆： 实现{@link Cloneable}接口 是一个数组
	 * 
	 * 
	 * 不可以在{@link Object#clone()}中调用此方法(当deep=false时), 因为会尝试调用clone方法来完成克隆，这会造成死循环
	 * 
	 * @see Cloneable
	 * @see ArrayUtils#clone(Object, boolean)
	 * @see CollectionFactory#clone(Collection, boolean)
	 * @see CollectionFactory#clone(Map, boolean)
	 * @see ReflectionUtils#invokeCloneMethod(Object)
	 * @see ReflectionUtils#clone(Object, boolean)
	 * @param <T>
	 * @param source
	 * @param deep   对集合的操作
	 * @return 如果无法克隆就返回本身
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T source, boolean deep) {
		if (source == null) {
			return null;
		}

		if (source instanceof String || source instanceof Class) {
			return source;
		}

		if (ClassUtils.isPrimitiveOrWrapper(source.getClass()) || source.getClass().isEnum()) {
			return source;
		}

		if (source.getClass().isArray()) {
			return ArrayUtils.clone(source, deep);
		}

		if (source instanceof Collection) {
			return (T) CollectionFactory.clone((Collection<?>) source, deep);
		}

		if (source instanceof Map) {
			return (T) CollectionFactory.clone((Map<?, ?>) source, deep);
		}

		if (!deep) {
			try {
				T target = ReflectionUtils.invokeCloneMethod(source);
				if (target != null) {
					return target;
				}
			} catch (StackOverflowError e) {
				// 忽略此异常，这可能是在clone()方法中调用造成的
			}
		}

		// 是否可以直接return source, 应该这样吗？
		return ReflectionUtils.clone(source, deep);
	}
}
