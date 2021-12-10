package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import io.basc.framework.core.MethodParameter;

/**
 * A {@link MethodParameter} variant which synthesizes annotations that declare
 * attribute aliases via {@link AliasFor @AliasFor}.
 *
 * @see AnnotationUtils#synthesizeAnnotation
 * @see AnnotationUtils#synthesizeAnnotationArray
 */
public class SynthesizingMethodParameter extends MethodParameter {

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given method, with
	 * nesting level 1.
	 * 
	 * @param method         the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method return
	 *                       type; 0 for the first method parameter; 1 for the
	 *                       second method parameter, etc.
	 */
	public SynthesizingMethodParameter(Method method, int parameterIndex) {
		super(method, parameterIndex);
	}

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given method.
	 * 
	 * @param method         the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method return
	 *                       type; 0 for the first method parameter; 1 for the
	 *                       second method parameter, etc.
	 * @param nestingLevel   the nesting level of the target type (typically 1; e.g.
	 *                       in case of a List of Lists, 1 would indicate the nested
	 *                       List, whereas 2 would indicate the element of the
	 *                       nested List)
	 */
	public SynthesizingMethodParameter(Method method, int parameterIndex, int nestingLevel) {
		super(method, parameterIndex, nestingLevel);
	}

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given constructor,
	 * with nesting level 1.
	 * 
	 * @param constructor    the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 */
	public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex) {
		super(constructor, parameterIndex);
	}

	/**
	 * Create a new {@code SynthesizingMethodParameter} for the given constructor.
	 * 
	 * @param constructor    the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 * @param nestingLevel   the nesting level of the target type (typically 1; e.g.
	 *                       in case of a List of Lists, 1 would indicate the nested
	 *                       List, whereas 2 would indicate the element of the
	 *                       nested List)
	 */
	public SynthesizingMethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
		super(constructor, parameterIndex, nestingLevel);
	}

	/**
	 * Copy constructor, resulting in an independent
	 * {@code SynthesizingMethodParameter} based on the same metadata and cache
	 * state that the original object was in.
	 * 
	 * @param original the original SynthesizingMethodParameter object to copy from
	 */
	protected SynthesizingMethodParameter(SynthesizingMethodParameter original) {
		super(original);
	}

	@Override
	protected <A extends Annotation> A adaptAnnotation(A annotation) {
		return AnnotationUtils.synthesizeAnnotation(annotation, getAnnotatedElement());
	}

	@Override
	protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
		return AnnotationUtils.synthesizeAnnotationArray(annotations, getAnnotatedElement());
	}

	@Override
	public SynthesizingMethodParameter clone() {
		return new SynthesizingMethodParameter(this);
	}

}
