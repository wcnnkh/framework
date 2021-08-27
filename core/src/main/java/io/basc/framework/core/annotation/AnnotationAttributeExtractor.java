package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * An {@code AnnotationAttributeExtractor} is responsible for
 * {@linkplain #getAttributeValue extracting} annotation attribute values
 * from an underlying {@linkplain #getSource source} such as an
 * {@code Annotation} or a {@code Map}.
 *
 * @param <S> the type of source supported by this extractor
 * @see SynthesizedAnnotationInvocationHandler
 */
interface AnnotationAttributeExtractor<S> {

	/**
	 * Get the type of annotation that this extractor extracts attribute
	 * values for.
	 */
	Class<? extends Annotation> getAnnotationType();

	/**
	 * Get the element that is annotated with an annotation of the annotation
	 * type supported by this extractor.
	 * @return the annotated element, or {@code null} if unknown
	 */
	Object getAnnotatedElement();

	/**
	 * Get the underlying source of annotation attributes.
	 */
	S getSource();

	/**
	 * Get the attribute value from the underlying {@linkplain #getSource source}
	 * that corresponds to the supplied attribute method.
	 * @param attributeMethod an attribute method from the annotation type
	 * supported by this extractor
	 * @return the value of the annotation attribute
	 */
	Object getAttributeValue(Method attributeMethod);

}
