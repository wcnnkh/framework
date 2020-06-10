package scw.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;

/**
 * Default implementation of the {@link AnnotationAttributeExtractor} strategy
 * that is backed by an {@link Annotation}.
 *
 * @see Annotation
 * @see AliasFor
 * @see AbstractAliasAwareAnnotationAttributeExtractor
 * @see MapAnnotationAttributeExtractor
 * @see AnnotationUtils#synthesizeAnnotation
 */
class DefaultAnnotationAttributeExtractor extends AbstractAliasAwareAnnotationAttributeExtractor<Annotation> {

	/**
	 * Construct a new {@code DefaultAnnotationAttributeExtractor}.
	 * @param annotation the annotation to synthesize; never {@code null}
	 * @param annotatedElement the element that is annotated with the supplied
	 * annotation; may be {@code null} if unknown
	 */
	DefaultAnnotationAttributeExtractor(Annotation annotation, Object annotatedElement) {
		super(annotation.annotationType(), annotatedElement, annotation);
	}


	@Override
	protected Object getRawAttributeValue(Method attributeMethod) {
		ReflectionUtils.makeAccessible(attributeMethod);
		return ReflectionUtils.invokeMethod(attributeMethod, getSource());
	}

	@Override
	protected Object getRawAttributeValue(String attributeName) {
		Method attributeMethod = ReflectionUtils.findMethod(getAnnotationType(), attributeName);
		return getRawAttributeValue(attributeMethod);
	}

}
