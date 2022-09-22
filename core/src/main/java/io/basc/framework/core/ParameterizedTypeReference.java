package io.basc.framework.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.basc.framework.util.Assert;

/**
 * The purpose of this class is to enable capturing and passing a generic
 * {@link Type}. In order to capture the generic type and retain it at runtime,
 * you need to create a subclass (ideally as anonymous inline class) as follows:
 *
 * <pre class="code">
 * ParameterizedTypeReference&lt;List&lt;String&gt;&gt; typeRef = new ParameterizedTypeReference&lt;List&lt;String&gt;&gt;() {
 * };
 * </pre>
 *
 * <p>
 * The resulting {@code typeRef} instance can then be used to obtain a
 * {@link Type} instance that carries the captured parameterized type
 * information at runtime. For more information on "super type tokens" see the
 * link to Neal Gafter's blog post.
 *
 * @see <a href="https://gafter.blogspot.nl/2006/12/super-type-tokens.html">Neal
 *      Gafter on Super Type Tokens</a>
 */
public abstract class ParameterizedTypeReference<T> {

	private final Type type;

	protected ParameterizedTypeReference() {
		this.type = getParameterizedType(getClass());
	}

	private ParameterizedTypeReference(Type type) {
		this.type = type;
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof ParameterizedTypeReference
				&& this.type.equals(((ParameterizedTypeReference<?>) obj).type)));
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public String toString() {
		return "ParameterizedTypeReference<" + this.type + ">";
	}

	/**
	 * Build a {@code ParameterizedTypeReference} wrapping the given type.
	 * 
	 * @param type a generic type (possibly obtained via reflection, e.g. from
	 *             {@link java.lang.reflect.Method#getGenericReturnType()})
	 * @return a corresponding reference which may be passed into
	 *         {@code ParameterizedTypeReference}-accepting methods
	 */
	public static <T> ParameterizedTypeReference<T> forType(Type type) {
		return new ParameterizedTypeReference<T>(type) {
		};
	}

	private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
		Class<?> parent = child.getSuperclass();
		if (Object.class == parent) {
			throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
		} else if (ParameterizedTypeReference.class == parent) {
			return child;
		} else {
			return findParameterizedTypeReferenceSubclass(parent);
		}
	}

	public static Type getParameterizedType(Class<?> clazz) throws IllegalStateException {
		Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(clazz);
		Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
		Assert.isInstanceOf(ParameterizedType.class, type, "Type must be a parameterized type");
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Assert.isTrue(actualTypeArguments.length == 1, "Number of type arguments must be 1");
		return actualTypeArguments[0];
	}
}
