package io.basc.framework.core.annotation;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for {@link AnnotationAttributeExtractor} implementations
 * that transparently enforce attribute alias semantics for annotation
 * attributes that are annotated with {@link AliasFor @AliasFor}.
 *
 * @param <S> the type of source supported by this extractor
 * @see Annotation
 * @see AliasFor
 * @see AnnotationUtils#synthesizeAnnotation(Annotation, Object)
 */
abstract class AbstractAliasAwareAnnotationAttributeExtractor<S> implements AnnotationAttributeExtractor<S> {

	private final Class<? extends Annotation> annotationType;

	private final Object annotatedElement;

	private final S source;

	private final Map<String, List<String>> attributeAliasMap;

	/**
	 * Construct a new {@code AbstractAliasAwareAnnotationAttributeExtractor}.
	 * 
	 * @param annotationType   the annotation type to synthesize; never {@code null}
	 * @param annotatedElement the element that is annotated with the annotation of
	 *                         the supplied type; may be {@code null} if unknown
	 * @param source           the underlying source of annotation attributes; never
	 *                         {@code null}
	 */
	AbstractAliasAwareAnnotationAttributeExtractor(Class<? extends Annotation> annotationType, Object annotatedElement,
			S source) {

		Assert.notNull(annotationType, "annotationType must not be null");
		Assert.notNull(source, "source must not be null");
		this.annotationType = annotationType;
		this.annotatedElement = annotatedElement;
		this.source = source;
		this.attributeAliasMap = AnnotationUtils.getAttributeAliasMap(annotationType);
	}

	public final Class<? extends Annotation> getAnnotationType() {
		return this.annotationType;
	}

	public final Object getAnnotatedElement() {
		return this.annotatedElement;
	}

	public final S getSource() {
		return this.source;
	}

	public final Object getAttributeValue(Method attributeMethod) {
		String attributeName = attributeMethod.getName();
		Object attributeValue = getRawAttributeValue(attributeMethod);

		List<String> aliasNames = this.attributeAliasMap.get(attributeName);
		if (aliasNames != null) {
			Object defaultValue = AnnotationUtils.getDefaultValue(this.annotationType, attributeName);
			for (String aliasName : aliasNames) {
				Object aliasValue = getRawAttributeValue(aliasName);

				if (!ObjectUtils.equals(attributeValue, aliasValue) && !ObjectUtils.equals(attributeValue, defaultValue)
						&& !ObjectUtils.equals(aliasValue, defaultValue)) {
					String elementName = (this.annotatedElement != null ? this.annotatedElement.toString()
							: "unknown element");
					throw new AnnotationConfigurationException(String.format(
							"In annotation [%s] declared on %s and synthesized from [%s], attribute '%s' and its "
									+ "alias '%s' are present with values of [%s] and [%s], but only one is permitted.",
							this.annotationType.getName(), elementName, this.source, attributeName, aliasName,
							ObjectUtils.toString(attributeValue), ObjectUtils.toString(aliasValue)));
				}

				// If the user didn't declare the annotation with an explicit value,
				// use the value of the alias instead.
				if (ObjectUtils.equals(attributeValue, defaultValue)) {
					attributeValue = aliasValue;
				}
			}
		}

		return attributeValue;
	}

	/**
	 * Get the raw, unmodified attribute value from the underlying
	 * {@linkplain #getSource source} that corresponds to the supplied attribute
	 * method.
	 */
	protected abstract Object getRawAttributeValue(Method attributeMethod);

	/**
	 * Get the raw, unmodified attribute value from the underlying
	 * {@linkplain #getSource source} that corresponds to the supplied attribute
	 * name.
	 */
	protected abstract Object getRawAttributeValue(String attributeName);

}
