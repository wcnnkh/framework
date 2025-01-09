package io.basc.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.io.FilenameUtils;

/**
 * Assertion utility class that assists in validating arguments. Useful for
 * identifying programmer errors early and clearly at runtime.
 *
 * <p>
 * For example, if the contract of a public method states it does not allow
 * {@code null} arguments, Assert can be used to validate that contract. Doing
 * this clearly indicates a contract violation when it occurs and protects the
 * class's invariants.
 *
 * <p>
 * Typically used to validate method arguments rather than configuration
 * properties, to check for cases that are usually programmer errors rather than
 * configuration errors. In contrast to config initialization code, there is
 * usally no point in falling back to defaults in such methods.
 *
 * <p>
 * This class is similar to JUnit's assertion library. If an argument value is
 * deemed invalid, an {@link IllegalArgumentException} is thrown (typically).
 * For example:
 *
 * <pre class="code">
 * Assert.notNull(clazz, &quot;The class must not be null&quot;);
 * Assert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
 * </pre>
 *
 * Mainly for internal use within the framework; consider Jakarta's Commons Lang
 * &gt;= 2.0 for a more comprehensive suite of assertion utilities.
 *
 */
public abstract class Assert {

	/**
	 * Assert a boolean expression, throwing {@code IllegalArgumentException} if the
	 * test result is {@code false}.
	 * 
	 * <pre class="code">
	 * Assert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
	 * </pre>
	 * 
	 * @param expression a boolean expression
	 * @param message    the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert a boolean expression, throwing {@code IllegalArgumentException} if the
	 * test result is {@code false}.
	 * 
	 * <pre class="code">
	 * Assert.isTrue(i &gt; 0);
	 * </pre>
	 * 
	 * @param expression a boolean expression
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	/**
	 * Assert that an object is {@code null} .
	 * 
	 * <pre class="code">
	 * Assert.isNull(value, &quot;The value must be null&quot;);
	 * </pre>
	 * 
	 * @param object  the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is not {@code null}
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an object is {@code null} .
	 * 
	 * <pre class="code">
	 * Assert.isNull(value);
	 * </pre>
	 * 
	 * @param object the object to check
	 * @throws IllegalArgumentException if the object is not {@code null}
	 */
	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}

	/**
	 * Assert that an object is not {@code null} .
	 * 
	 * <pre class="code">
	 * Assert.notNull(clazz, &quot;The class must not be null&quot;);
	 * </pre>
	 * 
	 * @param object  the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object object, java.util.function.Supplier<String> message) {
		if (object == null) {
			throw new IllegalArgumentException(message.get());
		}
	}

	/**
	 * 断言参数
	 * 
	 * @see #requiredArgument(boolean, String, Object)
	 * @param expression
	 * @param name
	 */
	public static void requiredArgument(boolean expression, String name) {
		requiredArgument(expression, name, null);
	}

	/**
	 * 断言参数
	 * 
	 * @param <T>
	 * @param expression false的情况下会抛出异常
	 * @param name
	 * @param value
	 * @return
	 */
	public static <T> T requiredArgument(boolean expression, String name, T value) {
		isTrue(expression, "[Assertion failed] - [" + name + "] argument is required");
		return value;
	}

	/**
	 * Assert that an object is not {@code null} .
	 * 
	 * <pre class="code">
	 * Assert.notNull(clazz);
	 * </pre>
	 * 
	 * @param object the object to check
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - this argument is required; it must not be null");
	}

	/**
	 * Assert that the given String is not empty; that is, it must not be
	 * {@code null} and not the empty String.
	 * 
	 * <pre class="code">
	 * Assert.hasLength(name, &quot;Name must not be empty&quot;);
	 * </pre>
	 * 
	 * @param text    the String to check
	 * @param message the exception message to use if the assertion fails
	 * @see StringUtils#isEmpty(CharSequence)
	 */
	public static void hasLength(String text, String message) {
		if (StringUtils.isEmpty(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that the given String is not empty; that is, it must not be
	 * {@code null} and not the empty String.
	 * 
	 * <pre class="code">
	 * Assert.hasLength(name);
	 * </pre>
	 * 
	 * @param text the String to check
	 * @see Assert#hasLength(String, String)
	 */
	public static void hasLength(String text) {
		hasLength(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
	}

	/**
	 * Assert that the given String has valid text content; that is, it must not be
	 * {@code null} and must contain at least one non-whitespace character.
	 * 
	 * <pre class="code">
	 * Assert.hasText(name, &quot;'name' must not be empty&quot;);
	 * </pre>
	 * 
	 * @param text    the String to check
	 * @param message the exception message to use if the assertion fails
	 * @see StringUtils#hasText
	 */
	public static void hasText(String text, String message) {
		if (!StringUtils.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that the given String has valid text content; that is, it must not be
	 * {@code null} and must contain at least one non-whitespace character.
	 * 
	 * <pre class="code">
	 * Assert.hasText(name, &quot;'name' must not be empty&quot;);
	 * </pre>
	 * 
	 * @param text the String to check
	 * @see StringUtils#hasText
	 */
	public static void hasText(String text) {
		hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
	}

	/**
	 * Assert that the given text does not contain the given substring.
	 * 
	 * <pre class="code">
	 * Assert.doesNotContain(name, &quot;rod&quot;, &quot;Name must not contain 'rod'&quot;);
	 * </pre>
	 * 
	 * @param textToSearch the text to search
	 * @param substring    the substring to find within the text
	 * @param message      the exception message to use if the assertion fails
	 */
	public static void doesNotContain(String textToSearch, String substring, String message) {
		if (StringUtils.isEmpty(textToSearch) && StringUtils.isEmpty(substring) && textToSearch.contains(substring)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that the given text does not contain the given substring.
	 * 
	 * <pre class="code">
	 * Assert.doesNotContain(name, &quot;rod&quot;);
	 * </pre>
	 * 
	 * @param textToSearch the text to search
	 * @param substring    the substring to find within the text
	 */
	public static void doesNotContain(String textToSearch, String substring) {
		doesNotContain(textToSearch, substring,
				"[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
	}

	/**
	 * Assert that an array has elements; that is, it must not be {@code null} and
	 * must have at least one element.
	 * 
	 * <pre class="code">
	 * Assert.notEmpty(array, &quot;The array must have elements&quot;);
	 * </pre>
	 * 
	 * @param array   the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array is {@code null} or has
	 *                                  no elements
	 */
	public static void notEmpty(Object[] array, String message) {
		if (ObjectUtils.isEmpty(array)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an array has elements; that is, it must not be {@code null} and
	 * must have at least one element.
	 * 
	 * <pre class="code">
	 * Assert.notEmpty(array);
	 * </pre>
	 * 
	 * @param array the array to check
	 * @throws IllegalArgumentException if the object array is {@code null} or has
	 *                                  no elements
	 */
	public static void notEmpty(Object[] array) {
		notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
	}

	/**
	 * Assert that an array has no null elements. Note: Does not complain if the
	 * array is empty!
	 * 
	 * <pre class="code">
	 * Assert.noNullElements(array, &quot;The array must have non-null elements&quot;);
	 * </pre>
	 * 
	 * @param array   the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array contains a {@code null}
	 *                                  element
	 */
	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	/**
	 * Assert that an array has no null elements. Note: Does not complain if the
	 * array is empty!
	 * 
	 * <pre class="code">
	 * Assert.noNullElements(array);
	 * </pre>
	 * 
	 * @param array the array to check
	 * @throws IllegalArgumentException if the object array contains a {@code null}
	 *                                  element
	 */
	public static void noNullElements(Object[] array) {
		noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
	}

	/**
	 * Assert that a collection has elements; that is, it must not be {@code null}
	 * and must have at least one element.
	 * 
	 * <pre class="code">
	 * Assert.notEmpty(collection, &quot;Collection must have elements&quot;);
	 * </pre>
	 * 
	 * @param collection the collection to check
	 * @param message    the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the collection is {@code null} or has no
	 *                                  elements
	 */
	@SuppressWarnings("rawtypes")
	public static void notEmpty(Collection collection, String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that a collection has elements; that is, it must not be {@code null}
	 * and must have at least one element.
	 * 
	 * <pre class="code">
	 * Assert.notEmpty(collection, &quot;Collection must have elements&quot;);
	 * </pre>
	 * 
	 * @param collection the collection to check
	 * @throws IllegalArgumentException if the collection is {@code null} or has no
	 *                                  elements
	 */
	@SuppressWarnings("rawtypes")
	public static void notEmpty(Collection collection) {
		notEmpty(collection,
				"[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
	}

	/**
	 * Assert that a Map has entries; that is, it must not be {@code null} and must
	 * have at least one entry.
	 * 
	 * <pre class="code">
	 * Assert.notEmpty(map, &quot;Map must have entries&quot;);
	 * </pre>
	 * 
	 * @param map     the map to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the map is {@code null} or has no entries
	 */
	@SuppressWarnings("rawtypes")
	public static void notEmpty(Map map, String message) {
		if (CollectionUtils.isEmpty(map)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that a Map has entries; that is, it must not be {@code null} and must
	 * have at least one entry.
	 * 
	 * <pre class="code">
	 * Assert.notEmpty(map);
	 * </pre>
	 * 
	 * @param map the map to check
	 * @throws IllegalArgumentException if the map is {@code null} or has no entries
	 */
	@SuppressWarnings("rawtypes")
	public static void notEmpty(Map map) {
		notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
	}

	/**
	 * Assert that the provided object is an instance of the provided class.
	 * 
	 * <pre class="code">
	 * Assert.instanceOf(Foo.class, foo);
	 * </pre>
	 * 
	 * @param clazz the required class
	 * @param obj   the object to check
	 * @throws IllegalArgumentException if the object is not an instance of clazz
	 * @see Class#isInstance
	 */
	public static void isInstanceOf(Class<?> clazz, Object obj) {
		isInstanceOf(clazz, obj, "");
	}

	/**
	 * Assert that the provided object is an instance of the provided class.
	 * 
	 * <pre class="code">
	 * Assert.instanceOf(Foo.class, foo);
	 * </pre>
	 * 
	 * @param type    the type to check against
	 * @param obj     the object to check
	 * @param message a message which will be prepended to the message produced by
	 *                the function itself, and which may be used to provide context.
	 *                It should normally end in a ": " or ". " so that the function
	 *                generate message looks ok when prepended to it.
	 * @throws IllegalArgumentException if the object is not an instance of clazz
	 * @see Class#isInstance
	 */
	public static void isInstanceOf(Class<?> type, Object obj, String message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			throw new IllegalArgumentException((StringUtils.isEmpty(message) ? message + " " : "") + "Object of class ["
					+ (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
		}
	}

	/**
	 * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
	 * 
	 * <pre class="code">
	 * Assert.isAssignable(Number.class, myClass);
	 * </pre>
	 * 
	 * @param superType the super type to check
	 * @param subType   the sub type to check
	 * @throws IllegalArgumentException if the classes are not assignable
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType) {
		isAssignable(superType, subType, "");
	}

	/**
	 * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
	 * 
	 * <pre class="code">
	 * Assert.isAssignable(Number.class, myClass);
	 * </pre>
	 * 
	 * @param superType the super type to check against
	 * @param subType   the sub type to check
	 * @param message   a message which will be prepended to the message produced by
	 *                  the function itself, and which may be used to provide
	 *                  context. It should normally end in a ": " or ". " so that
	 *                  the function generate message looks ok when prepended to it.
	 * @throws IllegalArgumentException if the classes are not assignable
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
		}
	}

	public static void isAssignable(Class<?> superType, Class<?> subType, java.util.function.Supplier<String> message) {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException(message.get() + subType + " is not assignable to " + superType);
		}
	}

	/**
	 * Assert a boolean expression, throwing {@code IllegalStateException} if the
	 * test result is {@code false}. Call isTrue if you wish to throw
	 * IllegalArgumentException on an assertion failure.
	 * 
	 * <pre class="code">
	 * Assert.state(id == null, &quot;The id property must not already be initialized&quot;);
	 * </pre>
	 * 
	 * @param expression a boolean expression
	 * @param message    the exception message to use if the assertion fails
	 * @throws IllegalStateException if expression is {@code false}
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Assert a boolean expression, throwing {@link IllegalStateException} if the
	 * test result is {@code false}.
	 * <p>
	 * Call {@link #isTrue(boolean)} if you wish to throw
	 * {@link IllegalArgumentException} on an assertion failure.
	 * 
	 * <pre class="code">
	 * Assert.state(id == null);
	 * </pre>
	 * 
	 * @param expression a boolean expression
	 * @throws IllegalStateException if the supplied expression is {@code false}
	 */
	public static void state(boolean expression) {
		state(expression, "[Assertion failed] - this state invariant must be true");
	}

	/**
	 * 安全路径验证
	 * 
	 * @see FilenameUtils#normalize(String)
	 * @see #secureFilePath(String, Supplier)
	 * @param path
	 * @return 返回安全路径
	 * @throws IllegalStateException
	 */
	public static String secureFilePath(String path) throws IllegalStateException {
		return secureFilePath(path, () -> "Unsafe file path: " + path);
	}

	/**
	 * 安全路径验证
	 * 
	 * @see FilenameUtils#normalize(String)
	 * @param path
	 * @param message
	 * @return 返回安全路径
	 * @throws IllegalStateException 不安全的路径
	 */
	public static String secureFilePath(String path, Supplier<String> message) throws IllegalStateException {
		if (StringUtils.isEmpty(path)) {
			return path;
		}

		String pathToUse = FilenameUtils.normalize(path);
		if (pathToUse == null) {
			throw new IllegalStateException(message.get());
		}

		if (pathToUse.indexOf("../") != -1 || pathToUse.indexOf("..\\") != -1) {
			throw new IllegalStateException(message.get());
		}
		return pathToUse;
	}

	/**
	 * 安全的路径参数验证
	 * 
	 * @see FilenameUtils#normalize(String)
	 * @param path 路径
	 * @param name 参数名
	 * @return 返回安全的路径
	 * @throws IllegalArgumentException 不安全的路径
	 */
	public static String secureFilePathArgument(String path, String name) throws IllegalArgumentException {
		try {
			return secureFilePath(path);
		} catch (IllegalStateException e) {
			throw new IllegalArgumentException("[Assertion failed] - [" + name + "] argument is unsafe path: " + path,
					e);
		}
	}
}
