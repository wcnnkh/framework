package io.basc.framework.core;

/**
 * Any object can implement this interface to provide its actual
 * {@link ResolvableType}.
 *
 * <p>
 * Such information is very useful when figuring out if the instance matches a
 * generic signature as Java does not convey the signature at runtime.
 *
 * <p>
 * Users of this interface should be careful in complex hierarchy scenarios,
 * especially when the generic type signature of the class changes in
 * sub-classes. It is always possible to return {@code null} to fallback on a
 * default behavior.
 *
 */
public interface ResolvableTypeProvider {

	/**
	 * Return the {@link ResolvableType} describing this instance (or {@code null}
	 * if some sort of default should be applied instead).
	 */
	ResolvableType getResolvableType();

}
