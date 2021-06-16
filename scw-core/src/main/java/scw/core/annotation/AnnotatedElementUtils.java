package scw.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.core.Assert;
import scw.core.BridgeMethodResolver;
import scw.core.utils.CollectionUtils;
import scw.lang.DefaultValue;
import scw.lang.Description;
import scw.lang.Ignore;
import scw.lang.Nullable;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

public class AnnotatedElementUtils {

	/**
	 * {@code null} constant used to denote that the search algorithm should
	 * continue.
	 */
	private static final Boolean CONTINUE = null;

	public static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new EmptyAnnotatedElement();

	private static final Processor<Boolean> alwaysTrueAnnotationProcessor = new AlwaysTrueBooleanAnnotationProcessor();

	/**
	 * Build an adapted {@link AnnotatedElement} for the given annotations,
	 * typically for use with other methods on {@link AnnotatedElementUtils}.
	 * 
	 * @param annotations the annotations to expose through the
	 *                    {@code AnnotatedElement}
	 */
	public static AnnotatedElement forAnnotations(final Annotation... annotations) {
		return new AnnotationArrayAnnotatedElement(annotations);
	}

	public static Enumeration<AnnotatedElement> forAnnotations(Annotation[]... annotations) {
		return new EnumerationAnnotatedElement(annotations);
	}

	/**
	 * Get the fully qualified class names of all meta-annotation types
	 * <em>present</em> on the annotation (of the specified {@code annotationType})
	 * on the supplied {@link AnnotatedElement}.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type on which to find meta-annotations
	 * @return the names of all meta-annotations present on the annotation, or
	 *         {@code null} if not found
	 * @see #getMetaAnnotationTypes(AnnotatedElement, String)
	 * @see #hasMetaAnnotationTypes
	 */
	public static Set<String> getMetaAnnotationTypes(AnnotatedElement element,
			Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		return getMetaAnnotationTypes(element, element.getAnnotation(annotationType));
	}

	/**
	 * Get the fully qualified class names of all meta-annotation types
	 * <em>present</em> on the annotation (of the specified {@code annotationName})
	 * on the supplied {@link AnnotatedElement}.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       on which to find meta-annotations
	 * @return the names of all meta-annotations present on the annotation, or
	 *         {@code null} if not found
	 * @see #getMetaAnnotationTypes(AnnotatedElement, Class)
	 * @see #hasMetaAnnotationTypes
	 */
	public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.hasLength(annotationName, "'annotationName' must not be null or empty");

		return getMetaAnnotationTypes(element, AnnotationUtils.getAnnotation(element, annotationName));
	}

	private static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Annotation composed) {
		if (composed == null) {
			return null;
		}

		try {
			final Set<String> types = new LinkedHashSet<String>();
			searchWithGetSemantics(composed.annotationType(), null, null, null,
					new SimpleAnnotationProcessor<Object>(true) {
						public Object process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
							types.add(annotation.annotationType().getName());
							return CONTINUE;
						}
					}, new HashSet<AnnotatedElement>(), 1);
			return (!types.isEmpty() ? types : null);
		} catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
		}
	}

	/**
	 * Determine if the supplied {@link AnnotatedElement} is annotated with a
	 * <em>composed annotation</em> that is meta-annotated with an annotation of the
	 * specified {@code annotationType}.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the meta-annotation type to find
	 * @return {@code true} if a matching meta-annotation is present
	 * @see #getMetaAnnotationTypes
	 */
	public static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		return hasMetaAnnotationTypes(element, annotationType, null);
	}

	/**
	 * Determine if the supplied {@link AnnotatedElement} is annotated with a
	 * <em>composed annotation</em> that is meta-annotated with an annotation of the
	 * specified {@code annotationName}.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationName the fully qualified class name of the meta-annotation
	 *                       type to find
	 * @return {@code true} if a matching meta-annotation is present
	 * @see #getMetaAnnotationTypes
	 */
	public static boolean hasMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.hasLength(annotationName, "'annotationName' must not be null or empty");

		return hasMetaAnnotationTypes(element, null, annotationName);
	}

	private static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName) {

		return Boolean.TRUE.equals(searchWithGetSemantics(element, annotationType, annotationName,
				new SimpleAnnotationProcessor<Boolean>() {
					public Boolean process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
						return (metaDepth > 0 ? Boolean.TRUE : CONTINUE);
					}
				}));
	}

	/**
	 * Determine if an annotation of the specified {@code annotationType} is
	 * <em>present</em> on the supplied {@link AnnotatedElement} or within the
	 * annotation hierarchy <em>above</em> the specified element.
	 * <p>
	 * If this method returns {@code true}, then
	 * {@link #getMergedAnnotationAttributes} will return a non-null value.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @return {@code true} if a matching annotation is present
	 * @see #hasAnnotation(AnnotatedElement, Class)
	 */
	public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		// Shortcut: directly present on the element, with no processing needed?
		if (element.isAnnotationPresent(annotationType)) {
			return true;
		}
		return Boolean.TRUE
				.equals(searchWithGetSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
	}

	/**
	 * Determine if an annotation of the specified {@code annotationName} is
	 * <em>present</em> on the supplied {@link AnnotatedElement} or within the
	 * annotation hierarchy <em>above</em> the specified element.
	 * <p>
	 * If this method returns {@code true}, then
	 * {@link #getMergedAnnotationAttributes} will return a non-null value.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find
	 * @return {@code true} if a matching annotation is present
	 */
	public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.hasLength(annotationName, "'annotationName' must not be null or empty");

		return Boolean.TRUE
				.equals(searchWithGetSemantics(element, null, annotationName, alwaysTrueAnnotationProcessor));
	}

	/**
	 * Get the first annotation of the specified {@code annotationType} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element} and merge
	 * that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within the annotation hierarchy.
	 * <p>
	 * This method delegates to
	 * {@link #getMergedAnnotationAttributes(AnnotatedElement, String)}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 */
	public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element,
			Class<? extends Annotation> annotationType) {

		Assert.notNull(annotationType, "'annotationType' must not be null");
		AnnotationAttributes attributes = searchWithGetSemantics(element, annotationType, null,
				new MergedAnnotationAttributesProcessor());
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
		return attributes;
	}

	/**
	 * Get the first annotation of the specified {@code annotationName} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element} and merge
	 * that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within the annotation hierarchy.
	 * <p>
	 * This method delegates to
	 * {@link #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)}
	 * , supplying {@code false} for {@code classValuesAsString} and
	 * {@code nestedAnnotationsAsMap}.
	 * 
	 * @param element        the annotated element
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String)
	 */
	public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName) {
		return getMergedAnnotationAttributes(element, annotationName, false, false);
	}

	/**
	 * Get the first annotation of the specified {@code annotationName} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element} and merge
	 * that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>
	 * Attributes from lower levels in the annotation hierarchy override attributes
	 * of the same name from higher levels, and {@link AliasFor
	 * 
	 * @AliasFor} semantics are fully supported, both within a single annotation and
	 *            within the annotation hierarchy.
	 *            <p>
	 *            In contrast to {@link #getAllAnnotationAttributes}, the search
	 *            algorithm used by this method will stop searching the annotation
	 *            hierarchy once the first annotation of the specified
	 *            {@code annotationName} has been found. As a consequence,
	 *            additional annotations of the specified {@code annotationName}
	 *            will be ignored.
	 *            <p>
	 *            This method follows <em>get semantics</em> as described in the
	 *            {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element                the annotated element
	 * @param annotationName         the fully qualified class name of the
	 *                               annotation type to find
	 * @param classValuesAsString    whether to convert Class references into
	 *                               Strings or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances
	 *                               into {@code AnnotationAttributes} maps or to
	 *                               preserve them as Annotation instances
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 */
	public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName,
			boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		Assert.hasLength(annotationName, "'annotationName' must not be null or empty");
		AnnotationAttributes attributes = searchWithGetSemantics(element, null, annotationName,
				new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString,
				nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Get the first annotation of the specified {@code annotationType} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element}, merge that
	 * annotation's attributes with <em>matching</em> attributes from annotations in
	 * lower levels of the annotation hierarchy, and synthesize the result back into
	 * an annotation of the specified {@code annotationType}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within the annotation hierarchy.
	 * <p>
	 * This method delegates to
	 * {@link #getMergedAnnotationAttributes(AnnotatedElement, Class)} and
	 * {@link AnnotationUtils#synthesizeAnnotation(Map, Class, AnnotatedElement)}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @return the merged, synthesized {@code Annotation}, or {@code null} if not
	 *         found
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, Class)
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see AnnotationUtils#synthesizeAnnotation(Map, Class, AnnotatedElement)
	 */
	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Assert.notNull(annotationType, "'annotationType' must not be null");

		// Shortcut: directly present on the element, with no merging needed?
		if (!(element instanceof Class)) {
			// Do not use this shortcut against a Class: Inherited annotations
			// would get preferred over locally declared composed annotations.
			A annotation = element.getAnnotation(annotationType);
			if (annotation != null) {
				return AnnotationUtils.synthesizeAnnotation(annotation, element);
			}
		}

		// Exhaustive retrieval of merged annotation attributes...
		AnnotationAttributes attributes = getMergedAnnotationAttributes(element, annotationType);
		return AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element);
	}

	/**
	 * Get <strong>all</strong> annotations of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the
	 * annotation hierarchy and synthesize the results back into an annotation of
	 * the specified {@code annotationType}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find (never {@code null})
	 * @return the set of all merged, synthesized {@code Annotations} found, or an
	 *         empty set if none were found
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String)
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> Set<A> getAllMergedAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithGetSemantics(element, annotationType, null, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Get all <em>repeatable annotations</em> of the specified
	 * {@code annotationType} within the annotation hierarchy <em>above</em> the
	 * supplied {@code element}; and for each annotation found, merge that
	 * annotation's attributes with <em>matching</em> attributes from annotations in
	 * lower levels of the annotation hierarchy and synthesize the results back into
	 * an annotation of the specified {@code annotationType}.
	 * <p>
	 * The container type that holds the repeatable annotations will be looked up
	 * via {@link java.lang.annotation.Repeatable}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find (never {@code null})
	 * @return the set of all merged repeatable {@code Annotations} found, or an
	 *         empty set if none were found
	 * @throws IllegalArgumentException if the {@code element} or
	 *                                  {@code annotationType} is {@code null}, or
	 *                                  if the container type cannot be resolved
	 *                                  getMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllMergedAnnotations(AnnotatedElement, Class)
	 * @see #getMergedRepeatableAnnotations(AnnotatedElement, Class, Class)
	 */
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		return getMergedRepeatableAnnotations(element, annotationType, null);
	}

	/**
	 * Get all <em>repeatable annotations</em> of the specified
	 * {@code annotationType} within the annotation hierarchy <em>above</em> the
	 * supplied {@code element}; and for each annotation found, merge that
	 * annotation's attributes with <em>matching</em> attributes from annotations in
	 * lower levels of the annotation hierarchy and synthesize the results back into
	 * an annotation of the specified {@code annotationType}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find (never {@code null})
	 * @param containerType  the type of the container that holds the annotations;
	 *                       may be {@code null} if the container type should be
	 *                       looked up via {@link java.lang.annotation.Repeatable}
	 * @return the set of all merged repeatable {@code Annotations} found, or an
	 *         empty set if none were found
	 * @throws IllegalArgumentException         if the {@code element} or
	 *                                          {@code annotationType} is
	 *                                          {@code null}, or if the container
	 *                                          type cannot be resolved
	 * @throws AnnotationConfigurationException if the supplied
	 *                                          {@code containerType} is not a valid
	 *                                          container annotation for the
	 *                                          supplied {@code annotationType}
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllMergedAnnotations(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType, Class<? extends Annotation> containerType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		if (containerType == null) {
			containerType = resolveContainerType(annotationType);
		} else {
			validateContainerType(annotationType, containerType);
		}

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithGetSemantics(element, annotationType, null, containerType, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Get the annotation attributes of <strong>all</strong> annotations of the
	 * specified {@code annotationName} in the annotation hierarchy above the
	 * supplied {@link AnnotatedElement} and store the results in a
	 * {@link MultiValueMap}.
	 * <p>
	 * Note: in contrast to
	 * {@link #getMergedAnnotationAttributes(AnnotatedElement, String)}, this method
	 * does <em>not</em> support attribute overrides.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find
	 * @return a {@link MultiValueMap} keyed by attribute name, containing the
	 *         annotation attributes from all annotations found, or {@code null} if
	 *         not found
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 */
	public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element,
			String annotationName) {
		return getAllAnnotationAttributes(element, annotationName, false, false);
	}

	/**
	 * Get the annotation attributes of <strong>all</strong> annotations of the
	 * specified {@code annotationName} in the annotation hierarchy above the
	 * supplied {@link AnnotatedElement} and store the results in a
	 * {@link MultiValueMap}.
	 * <p>
	 * Note: in contrast to
	 * {@link #getMergedAnnotationAttributes(AnnotatedElement, String)}, this method
	 * does <em>not</em> support attribute overrides.
	 * <p>
	 * This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element                the annotated element
	 * @param annotationName         the fully qualified class name of the
	 *                               annotation type to find
	 * @param classValuesAsString    whether to convert Class references into
	 *                               Strings or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances
	 *                               into {@code AnnotationAttributes} maps or to
	 *                               preserve them as Annotation instances
	 * @return a {@link MultiValueMap} keyed by attribute name, containing the
	 *         annotation attributes from all annotations found, or {@code null} if
	 *         not found
	 */
	public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element,
			String annotationName, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {

		final MultiValueMap<String, Object> attributesMap = new LinkedMultiValueMap<String, Object>();

		searchWithGetSemantics(element, null, annotationName, new SimpleAnnotationProcessor<Object>() {
			public Object process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
				AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(annotation,
						classValuesAsString, nestedAnnotationsAsMap);
				for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
					attributesMap.add(entry.getKey(), entry.getValue());
				}
				return CONTINUE;
			}
		});

		return (!attributesMap.isEmpty() ? attributesMap : null);
	}

	/**
	 * Determine if an annotation of the specified {@code annotationType} is
	 * <em>available</em> on the supplied {@link AnnotatedElement} or within the
	 * annotation hierarchy <em>above</em> the specified element.
	 * <p>
	 * If this method returns {@code true}, then
	 * {@link #findMergedAnnotationAttributes} will return a non-null value.
	 * <p>
	 * This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @return {@code true} if a matching annotation is present
	 * @see #isAnnotated(AnnotatedElement, Class)
	 */
	public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		// Shortcut: directly present on the element, with no processing needed?
		if (element.isAnnotationPresent(annotationType)) {
			return true;
		}
		return Boolean.TRUE
				.equals(searchWithFindSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
	}

	/**
	 * Find the first annotation of the specified {@code annotationType} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element} and merge
	 * that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>
	 * Attributes from lower levels in the annotation hierarchy override attributes
	 * of the same name from higher levels, and {@link AliasFor
	 * 
	 * @AliasFor} semantics are fully supported, both within a single annotation and
	 *            within the annotation hierarchy.
	 *            <p>
	 *            In contrast to {@link #getAllAnnotationAttributes}, the search
	 *            algorithm used by this method will stop searching the annotation
	 *            hierarchy once the first annotation of the specified
	 *            {@code annotationType} has been found. As a consequence,
	 *            additional annotations of the specified {@code annotationType}
	 *            will be ignored.
	 *            <p>
	 *            This method follows <em>find semantics</em> as described in the
	 *            {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element                the annotated element
	 * @param annotationType         the annotation type to find
	 * @param classValuesAsString    whether to convert Class references into
	 *                               Strings or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances
	 *                               into {@code AnnotationAttributes} maps or to
	 *                               preserve them as Annotation instances
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 */
	public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element,
			Class<? extends Annotation> annotationType, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		AnnotationAttributes attributes = searchWithFindSemantics(element, annotationType, null,
				new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString,
				nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Find the first annotation of the specified {@code annotationName} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element} and merge
	 * that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>
	 * Attributes from lower levels in the annotation hierarchy override attributes
	 * of the same name from higher levels, and {@link AliasFor
	 * 
	 * @AliasFor} semantics are fully supported, both within a single annotation and
	 *            within the annotation hierarchy.
	 *            <p>
	 *            In contrast to {@link #getAllAnnotationAttributes}, the search
	 *            algorithm used by this method will stop searching the annotation
	 *            hierarchy once the first annotation of the specified
	 *            {@code annotationName} has been found. As a consequence,
	 *            additional annotations of the specified {@code annotationName}
	 *            will be ignored.
	 *            <p>
	 *            This method follows <em>find semantics</em> as described in the
	 *            {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element                the annotated element
	 * @param annotationName         the fully qualified class name of the
	 *                               annotation type to find
	 * @param classValuesAsString    whether to convert Class references into
	 *                               Strings or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances
	 *                               into {@code AnnotationAttributes} maps or to
	 *                               preserve them as Annotation instances
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 */
	public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element, String annotationName,
			boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		AnnotationAttributes attributes = searchWithFindSemantics(element, null, annotationName,
				new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString,
				nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Find the first annotation of the specified {@code annotationType} within the
	 * annotation hierarchy <em>above</em> the supplied {@code element}, merge that
	 * annotation's attributes with <em>matching</em> attributes from annotations in
	 * lower levels of the annotation hierarchy, and synthesize the result back into
	 * an annotation of the specified {@code annotationType}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within the annotation hierarchy.
	 * <p>
	 * This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @return the merged, synthesized {@code Annotation}, or {@code null} if not
	 *         found
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean,
	 *      boolean)
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Assert.notNull(annotationType, "'annotationType' must not be null");

		// Shortcut: directly present on the element, with no merging needed?
		if (!(element instanceof Class)) {
			// Do not use this shortcut against a Class: Inherited annotations
			// would get preferred over locally declared composed annotations.
			A annotation = element.getAnnotation(annotationType);
			if (annotation != null) {
				return AnnotationUtils.synthesizeAnnotation(annotation, element);
			}
		}

		// Exhaustive retrieval of merged annotation attributes...
		AnnotationAttributes attributes = findMergedAnnotationAttributes(element, annotationType, false, false);
		return AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element);
	}

	/**
	 * Find <strong>all</strong> annotations of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the
	 * annotation hierarchy and synthesize the results back into an annotation of
	 * the specified {@code annotationType}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>
	 * This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find (never {@code null})
	 * @return the set of all merged, synthesized {@code Annotations} found, or an
	 *         empty set if none were found
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllMergedAnnotations(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> Set<A> findAllMergedAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithFindSemantics(element, annotationType, null, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Find all <em>repeatable annotations</em> of the specified
	 * {@code annotationType} within the annotation hierarchy <em>above</em> the
	 * supplied {@code element}; and for each annotation found, merge that
	 * annotation's attributes with <em>matching</em> attributes from annotations in
	 * lower levels of the annotation hierarchy and synthesize the results back into
	 * an annotation of the specified {@code annotationType}.
	 * <p>
	 * The container type that holds the repeatable annotations will be looked up
	 * via {@link java.lang.annotation.Repeatable}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>
	 * This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find (never {@code null})
	 * @return the set of all merged repeatable {@code Annotations} found, or an
	 *         empty set if none were found
	 * @throws IllegalArgumentException if the {@code element} or
	 *                                  {@code annotationType} is {@code null}, or
	 *                                  if the container type cannot be resolved
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 * @see #findMergedRepeatableAnnotations(AnnotatedElement, Class, Class)
	 */
	public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		return findMergedRepeatableAnnotations(element, annotationType, null);
	}

	/**
	 * Find all <em>repeatable annotations</em> of the specified
	 * {@code annotationType} within the annotation hierarchy <em>above</em> the
	 * supplied {@code element}; and for each annotation found, merge that
	 * annotation's attributes with <em>matching</em> attributes from annotations in
	 * lower levels of the annotation hierarchy and synthesize the results back into
	 * an annotation of the specified {@code annotationType}.
	 * <p>
	 * {@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>
	 * This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find (never {@code null})
	 * @param containerType  the type of the container that holds the annotations;
	 *                       may be {@code null} if the container type should be
	 *                       looked up via {@link java.lang.annotation.Repeatable}
	 * @return the set of all merged repeatable {@code Annotations} found, or an
	 *         empty set if none were found
	 * @throws IllegalArgumentException         if the {@code element} or
	 *                                          {@code annotationType} is
	 *                                          {@code null}, or if the container
	 *                                          type cannot be resolved
	 * @throws AnnotationConfigurationException if the supplied
	 *                                          {@code containerType} is not a valid
	 *                                          container annotation for the
	 *                                          supplied {@code annotationType}
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType, Class<? extends Annotation> containerType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "'annotationType' must not be null");

		if (containerType == null) {
			containerType = resolveContainerType(annotationType);
		} else {
			validateContainerType(annotationType, containerType);
		}

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithFindSemantics(element, annotationType, null, containerType, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	public static Boolean isNullable(AnnotatedElement annotatedElement, Boolean defaultValue) {
		Nullable nullable = getMergedAnnotation(annotatedElement, Nullable.class);
		return nullable == null ? defaultValue : nullable.value();
	}

	public static String getCharsetName(AnnotatedElement annotatedElement, String defaultValue) {
		CharsetName charsetName = getMergedAnnotation(annotatedElement, CharsetName.class);
		return charsetName == null ? defaultValue : charsetName.value();
	}

	public static Boolean ignore(AnnotatedElement annotatedElement, Boolean defaultValue) {
		Ignore ignore = getMergedAnnotation(annotatedElement, Ignore.class);
		return ignore == null ? defaultValue : ignore.value();
	}

	public static String getDescription(AnnotatedElement annotatedElement) {
		Description description = getMergedAnnotation(annotatedElement, Description.class);
		return description == null ? null : description.value();
	}

	public static String getDefaultValue(AnnotatedElement annotatedElement) {
		DefaultValue defaultValue = getMergedAnnotation(annotatedElement, DefaultValue.class);
		return defaultValue == null ? null : defaultValue.value();
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following <em>get
	 * semantics</em>.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param processor      the processor to delegate to
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Processor<T> processor) {

		return searchWithGetSemantics(element, annotationType, annotationName, null, processor);
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following <em>get
	 * semantics</em>.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param containerType  the type of the container that holds repeatable
	 *                       annotations, or {@code null} if the annotation is not
	 *                       repeatable
	 * @param processor      the processor to delegate to
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor) {

		try {
			return searchWithGetSemantics(element, annotationType, annotationName, containerType, processor,
					new HashSet<AnnotatedElement>(), 0);
		} catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
		}
	}

	/**
	 * Perform the search algorithm for the {@link #searchWithGetSemantics} method,
	 * avoiding endless recursion by tracking which annotated elements have already
	 * been <em>visited</em>.
	 * <p>
	 * The {@code metaDepth} parameter is explained in the {@link Processor#process
	 * process()} method of the {@link Processor} API.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param containerType  the type of the container that holds repeatable
	 *                       annotations, or {@code null} if the annotation is not
	 *                       repeatable
	 * @param processor      the processor to delegate to
	 * @param visited        the set of annotated elements that have already been
	 *                       visited
	 * @param metaDepth      the meta-depth of the annotation
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth) {

		Assert.notNull(element, "AnnotatedElement must not be null");

		if (visited.add(element)) {
			try {
				// Start searching within locally declared annotations
				List<Annotation> declaredAnnotations = Arrays.asList(element.getDeclaredAnnotations());
				T result = searchWithGetSemanticsInAnnotations(element, declaredAnnotations, annotationType,
						annotationName, containerType, processor, visited, metaDepth);
				if (result != null) {
					return result;
				}

				if (element instanceof Class) { // otherwise getAnnotations does
												// not return anything new
					List<Annotation> inheritedAnnotations = new ArrayList<Annotation>();
					for (Annotation annotation : element.getAnnotations()) {
						if (!declaredAnnotations.contains(annotation)) {
							inheritedAnnotations.add(annotation);
						}
					}

					// Continue searching within inherited annotations
					result = searchWithGetSemanticsInAnnotations(element, inheritedAnnotations, annotationType,
							annotationName, containerType, processor, visited, metaDepth);
					if (result != null) {
						return result;
					}
				}
			} catch (Throwable ex) {
				AnnotationUtils.handleIntrospectionFailure(element, ex);
			}
		}

		return null;
	}

	/**
	 * This method is invoked by {@link #searchWithGetSemantics} to perform the
	 * actual search within the supplied list of annotations.
	 * <p>
	 * This method should be invoked first with locally declared annotations and
	 * then subsequently with inherited annotations, thereby allowing local
	 * annotations to take precedence over inherited annotations.
	 * <p>
	 * The {@code metaDepth} parameter is explained in the {@link Processor#process
	 * process()} method of the {@link Processor} API.
	 * 
	 * @param element        the element that is annotated with the supplied
	 *                       annotations, used for contextual logging; may be
	 *                       {@code null} if unknown
	 * @param annotations    the annotations to search in
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param containerType  the type of the container that holds repeatable
	 *                       annotations, or {@code null} if the annotation is not
	 *                       repeatable
	 * @param processor      the processor to delegate to
	 * @param visited        the set of annotated elements that have already been
	 *                       visited
	 * @param metaDepth      the meta-depth of the annotation
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithGetSemanticsInAnnotations(AnnotatedElement element, List<Annotation> annotations,
			Class<? extends Annotation> annotationType, String annotationName,
			Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited,
			int metaDepth) {

		// Search in annotations
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
			if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
				if (currentAnnotationType == annotationType || currentAnnotationType.getName().equals(annotationName)
						|| processor.alwaysProcesses()) {
					T result = processor.process(element, annotation, metaDepth);
					if (result != null) {
						if (processor.aggregates() && metaDepth == 0) {
							processor.getAggregatedResults().add(result);
						} else {
							return result;
						}
					}
				}
				// Repeatable annotations in container?
				else if (currentAnnotationType == containerType) {
					for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
						T result = processor.process(element, contained, metaDepth);
						if (result != null) {
							// No need to post-process since repeatable
							// annotations within a
							// container cannot be composed annotations.
							processor.getAggregatedResults().add(result);
						}
					}
				}
			}
		}

		// Recursively search in meta-annotations
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
			if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
				T result = searchWithGetSemantics(currentAnnotationType, annotationType, annotationName, containerType,
						processor, visited, metaDepth + 1);
				if (result != null) {
					processor.postProcess(element, annotation, result);
					if (processor.aggregates() && metaDepth == 0) {
						processor.getAggregatedResults().add(result);
					} else {
						return result;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following <em>find
	 * semantics</em>.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param processor      the processor to delegate to
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithFindSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Processor<T> processor) {

		return searchWithFindSemantics(element, annotationType, annotationName, null, processor);
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following <em>find
	 * semantics</em>.
	 * 
	 * @param element        the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param containerType  the type of the container that holds repeatable
	 *                       annotations, or {@code null} if the annotation is not
	 *                       repeatable
	 * @param processor      the processor to delegate to
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithFindSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor) {

		if (containerType != null && !processor.aggregates()) {
			throw new IllegalArgumentException(
					"Searches for repeatable annotations must supply an aggregating Processor");
		}

		try {
			return searchWithFindSemantics(element, annotationType, annotationName, containerType, processor,
					new HashSet<AnnotatedElement>(), 0);
		} catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
		}
	}

	/**
	 * Perform the search algorithm for the {@link #searchWithFindSemantics} method,
	 * avoiding endless recursion by tracking which annotated elements have already
	 * been <em>visited</em>.
	 * <p>
	 * The {@code metaDepth} parameter is explained in the {@link Processor#process
	 * process()} method of the {@link Processor} API.
	 * 
	 * @param element        the annotated element (never {@code null})
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to find (as an alternative to {@code annotationType})
	 * @param containerType  the type of the container that holds repeatable
	 *                       annotations, or {@code null} if the annotation is not
	 *                       repeatable
	 * @param processor      the processor to delegate to
	 * @param visited        the set of annotated elements that have already been
	 *                       visited
	 * @param metaDepth      the meta-depth of the annotation
	 * @return the result of the processor (potentially {@code null})
	 */
	private static <T> T searchWithFindSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth) {

		Assert.notNull(element, "AnnotatedElement must not be null");

		if (visited.add(element)) {
			try {
				// Locally declared annotations (ignoring @Inherited)
				Annotation[] annotations = element.getDeclaredAnnotations();
				if (annotations.length > 0) {
					List<T> aggregatedResults = (processor.aggregates() ? new ArrayList<T>() : null);

					// Search in local annotations
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
						if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
							if (currentAnnotationType == annotationType
									|| currentAnnotationType.getName().equals(annotationName)
									|| processor.alwaysProcesses()) {
								T result = processor.process(element, annotation, metaDepth);
								if (result != null) {
									if (aggregatedResults != null && metaDepth == 0) {
										aggregatedResults.add(result);
									} else {
										return result;
									}
								}
							}
							// Repeatable annotations in container?
							else if (currentAnnotationType == containerType) {
								for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
									T result = processor.process(element, contained, metaDepth);
									if (aggregatedResults != null && result != null) {
										// No need to post-process since
										// repeatable annotations within a
										// container cannot be composed
										// annotations.
										aggregatedResults.add(result);
									}
								}
							}
						}
					}

					// Recursively search in meta-annotations
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
						if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
							T result = searchWithFindSemantics(currentAnnotationType, annotationType, annotationName,
									containerType, processor, visited, metaDepth + 1);
							if (result != null) {
								processor.postProcess(currentAnnotationType, annotation, result);
								if (aggregatedResults != null && metaDepth == 0) {
									aggregatedResults.add(result);
								} else {
									return result;
								}
							}
						}
					}

					if (!CollectionUtils.isEmpty(aggregatedResults)) {
						// Prepend to support top-down ordering within class
						// hierarchies
						processor.getAggregatedResults().addAll(0, aggregatedResults);
					}
				}

				if (element instanceof Method) {
					Method method = (Method) element;
					T result;

					// Search on possibly bridged method
					Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (resolvedMethod != method) {
						result = searchWithFindSemantics(resolvedMethod, annotationType, annotationName, containerType,
								processor, visited, metaDepth);
						if (result != null) {
							return result;
						}
					}

					// Search on methods in interfaces declared locally
					Class<?>[] ifcs = method.getDeclaringClass().getInterfaces();
					if (ifcs.length > 0) {
						result = searchOnInterfaces(method, annotationType, annotationName, containerType, processor,
								visited, metaDepth, ifcs);
						if (result != null) {
							return result;
						}
					}

					// Search on methods in class hierarchy and interface
					// hierarchy
					Class<?> clazz = method.getDeclaringClass();
					while (true) {
						clazz = clazz.getSuperclass();
						if (clazz == null || Object.class == clazz) {
							break;
						}
						try {
							Method equivalentMethod = clazz.getDeclaredMethod(method.getName(),
									method.getParameterTypes());
							Method resolvedEquivalentMethod = BridgeMethodResolver.findBridgedMethod(equivalentMethod);
							result = searchWithFindSemantics(resolvedEquivalentMethod, annotationType, annotationName,
									containerType, processor, visited, metaDepth);
							if (result != null) {
								return result;
							}
						} catch (NoSuchMethodException ex) {
							// No equivalent method found
						}
						// Search on interfaces declared on superclass
						result = searchOnInterfaces(method, annotationType, annotationName, containerType, processor,
								visited, metaDepth, clazz.getInterfaces());
						if (result != null) {
							return result;
						}
					}
				} else if (element instanceof Class) {
					Class<?> clazz = (Class<?>) element;

					// Search on interfaces
					for (Class<?> ifc : clazz.getInterfaces()) {
						T result = searchWithFindSemantics(ifc, annotationType, annotationName, containerType,
								processor, visited, metaDepth);
						if (result != null) {
							return result;
						}
					}

					// Search on superclass
					Class<?> superclass = clazz.getSuperclass();
					if (superclass != null && Object.class != superclass) {
						T result = searchWithFindSemantics(superclass, annotationType, annotationName, containerType,
								processor, visited, metaDepth);
						if (result != null) {
							return result;
						}
					}
				}
			} catch (Throwable ex) {
				AnnotationUtils.handleIntrospectionFailure(element, ex);
			}
		}
		return null;
	}

	private static <T> T searchOnInterfaces(Method method, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth, Class<?>[] ifcs) {

		for (Class<?> iface : ifcs) {
			if (AnnotationUtils.isInterfaceWithAnnotatedMethods(iface)) {
				try {
					Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
					T result = searchWithFindSemantics(equivalentMethod, annotationType, annotationName, containerType,
							processor, visited, metaDepth);
					if (result != null) {
						return result;
					}
				} catch (NoSuchMethodException ex) {
					// Skip this interface - it doesn't have the method...
				}
			}
		}

		return null;
	}

	/**
	 * Get the array of raw (unsynthesized) annotations from the {@code value}
	 * attribute of the supplied repeatable annotation {@code container}.
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A[] getRawAnnotationsFromContainer(AnnotatedElement element,
			Annotation container) {

		try {
			return (A[]) AnnotationUtils.getValue(container);
		} catch (Throwable ex) {
			AnnotationUtils.handleIntrospectionFailure(element, ex);
		}
		// Unable to read value from repeating annotation container -> ignore
		// it.
		return (A[]) AnnotationUtils.EMPTY_ANNOTATION_ARRAY;
	}

	/**
	 * Resolve the container type for the supplied repeatable
	 * {@code annotationType}.
	 * <p>
	 * Delegates to {@link AnnotationUtils#resolveContainerAnnotationType(Class)}.
	 * 
	 * @param annotationType the annotation type to resolve the container for
	 * @return the container type (never {@code null})
	 * @throws IllegalArgumentException if the container type cannot be resolved
	 */
	private static Class<? extends Annotation> resolveContainerType(Class<? extends Annotation> annotationType) {
		Class<? extends Annotation> containerType = AnnotationUtils.resolveContainerAnnotationType(annotationType);
		if (containerType == null) {
			throw new IllegalArgumentException(
					"Annotation type must be a repeatable annotation: failed to resolve container type for "
							+ annotationType.getName());
		}
		return containerType;
	}

	/**
	 * Validate that the supplied {@code containerType} is a proper container
	 * annotation for the supplied repeatable {@code annotationType} (i.e., that it
	 * declares a {@code value} attribute that holds an array of the
	 * {@code annotationType}).
	 * 
	 * @throws AnnotationConfigurationException if the supplied
	 *                                          {@code containerType} is not a valid
	 *                                          container annotation for the
	 *                                          supplied {@code annotationType}
	 */
	private static void validateContainerType(Class<? extends Annotation> annotationType,
			Class<? extends Annotation> containerType) {

		try {
			Method method = containerType.getDeclaredMethod(AnnotationUtils.VALUE);
			Class<?> returnType = method.getReturnType();
			if (!returnType.isArray() || returnType.getComponentType() != annotationType) {
				String msg = String.format(
						"Container type [%s] must declare a 'value' attribute for an array of type [%s]",
						containerType.getName(), annotationType.getName());
				throw new AnnotationConfigurationException(msg);
			}
		} catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			String msg = String.format("Invalid declaration of container type [%s] for repeatable annotation [%s]",
					containerType.getName(), annotationType.getName());
			throw new AnnotationConfigurationException(msg, ex);
		}
	}

	private static <A extends Annotation> Set<A> postProcessAndSynthesizeAggregatedResults(AnnotatedElement element,
			Class<A> annotationType, List<AnnotationAttributes> aggregatedResults) {

		Set<A> annotations = new LinkedHashSet<A>();
		for (AnnotationAttributes attributes : aggregatedResults) {
			AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
			annotations.add(AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element));
		}
		return annotations;
	}

	/**
	 * Callback interface that is used to process annotations during a search.
	 * <p>
	 * Depending on the use case, a processor may choose to {@linkplain #process} a
	 * single target annotation, multiple target annotations, or all annotations
	 * discovered by the currently executing search. The term "target" in this
	 * context refers to a matching annotation (i.e., a specific annotation type
	 * that was found during the search).
	 * <p>
	 * Returning a non-null value from the {@link #process} method instructs the
	 * search algorithm to stop searching further; whereas, returning {@code null}
	 * from the {@link #process} method instructs the search algorithm to continue
	 * searching for additional annotations. One exception to this rule applies to
	 * processors that {@linkplain #aggregates aggregate} results. If an aggregating
	 * processor returns a non-null value, that value will be added to the list of
	 * {@linkplain #getAggregatedResults aggregated results} and the search
	 * algorithm will continue.
	 * <p>
	 * Processors can optionally {@linkplain #postProcess post-process} the result
	 * of the {@link #process} method as the search algorithm goes back down the
	 * annotation hierarchy from an invocation of {@link #process} that returned a
	 * non-null value down to the {@link AnnotatedElement} that was supplied as the
	 * starting point to the search algorithm.
	 * 
	 * @param <T> the type of result returned by the processor
	 */
	private interface Processor<T> {

		/**
		 * Process the supplied annotation.
		 * <p>
		 * The supplied annotation will be an actual target annotation that has been
		 * found by the search algorithm, unless this processor is configured to
		 * {@linkplain #alwaysProcesses always process} annotations in which case it may
		 * be some other annotation within an annotation hierarchy. In the latter case,
		 * the {@code metaDepth} will have a value greater than {@code 0}. In any case,
		 * it is up to concrete implementations of this method to decide what to do with
		 * the supplied annotation.
		 * <p>
		 * The {@code metaDepth} parameter represents the depth of the annotation
		 * relative to the first annotated element in the annotation hierarchy. For
		 * example, an annotation that is <em>present</em> on a non-annotation element
		 * will have a depth of 0; a meta-annotation will have a depth of 1; and a
		 * meta-meta-annotation will have a depth of 2; etc.
		 * 
		 * @param annotatedElement the element that is annotated with the supplied
		 *                         annotation, used for contextual logging; may be
		 *                         {@code null} if unknown
		 * @param annotation       the annotation to process
		 * @param metaDepth        the meta-depth of the annotation
		 * @return the result of the processing, or {@code null} to continue searching
		 *         for additional annotations
		 */
		T process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth);

		/**
		 * Post-process the result returned by the {@link #process} method.
		 * <p>
		 * The {@code annotation} supplied to this method is an annotation that is
		 * present in the annotation hierarchy, between the initial
		 * {@link AnnotatedElement} and an invocation of {@link #process} that returned
		 * a non-null value.
		 * 
		 * @param annotatedElement the element that is annotated with the supplied
		 *                         annotation, used for contextual logging; may be
		 *                         {@code null} if unknown
		 * @param annotation       the annotation to post-process
		 * @param result           the result to post-process
		 */
		void postProcess(AnnotatedElement annotatedElement, Annotation annotation, T result);

		/**
		 * Determine if this processor always processes annotations regardless of
		 * whether or not the target annotation has been found.
		 * 
		 * @return {@code true} if this processor always processes annotations
		 */
		boolean alwaysProcesses();

		/**
		 * Determine if this processor aggregates the results returned by
		 * {@link #process}.
		 * <p>
		 * If this method returns {@code true}, then {@link #getAggregatedResults()}
		 * must return a non-null value.
		 * 
		 * @return {@code true} if this processor supports aggregated results
		 * @see #getAggregatedResults
		 */
		boolean aggregates();

		/**
		 * Get the list of results aggregated by this processor.
		 * <p>
		 * NOTE: the processor does <strong>not</strong> aggregate the results itself.
		 * Rather, the search algorithm that uses this processor is responsible for
		 * asking this processor if it {@link #aggregates} results and then adding the
		 * post-processed results to the list returned by this method.
		 * 
		 * @return the list of results aggregated by this processor (never {@code null})
		 * @see #aggregates
		 */
		List<T> getAggregatedResults();
	}

	/**
	 * {@link Processor} that
	 * {@linkplain #process(AnnotatedElement, Annotation, int) processes}
	 * annotations but does not {@linkplain #postProcess post-process} or
	 * {@linkplain #aggregates aggregate} results.
	 * 
	 */
	private abstract static class SimpleAnnotationProcessor<T> implements Processor<T> {

		private final boolean alwaysProcesses;

		public SimpleAnnotationProcessor() {
			this(false);
		}

		public SimpleAnnotationProcessor(boolean alwaysProcesses) {
			this.alwaysProcesses = alwaysProcesses;
		}

		public final boolean alwaysProcesses() {
			return this.alwaysProcesses;
		}

		public final void postProcess(AnnotatedElement annotatedElement, Annotation annotation, T result) {
			// no-op
		}

		public final boolean aggregates() {
			return false;
		}

		public final List<T> getAggregatedResults() {
			throw new UnsupportedOperationException("SimpleAnnotationProcessor does not support aggregated results");
		}
	}

	/**
	 * {@link SimpleAnnotationProcessor} that always returns {@link Boolean#TRUE}
	 * when asked to {@linkplain #process(AnnotatedElement, Annotation, int)
	 * process} an annotation.
	 * 
	 */
	static class AlwaysTrueBooleanAnnotationProcessor extends SimpleAnnotationProcessor<Boolean> {

		public final Boolean process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
			return Boolean.TRUE;
		}
	}

	/**
	 * {@link Processor} that gets the {@code AnnotationAttributes} for the target
	 * annotation during the {@link #process} phase and then merges annotation
	 * attributes from lower levels in the annotation hierarchy during the
	 * {@link #postProcess} phase.
	 * <p>
	 * A {@code MergedAnnotationAttributesProcessor} may optionally be configured to
	 * {@linkplain #aggregates aggregate} results.
	 * 
	 * @see AnnotationUtils#retrieveAnnotationAttributes
	 * @see AnnotationUtils#postProcessAnnotationAttributes
	 */
	private static class MergedAnnotationAttributesProcessor implements Processor<AnnotationAttributes> {

		private final boolean classValuesAsString;

		private final boolean nestedAnnotationsAsMap;

		private final boolean aggregates;

		private final List<AnnotationAttributes> aggregatedResults;

		MergedAnnotationAttributesProcessor() {
			this(false, false, false);
		}

		MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
			this(classValuesAsString, nestedAnnotationsAsMap, false);
		}

		MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap,
				boolean aggregates) {

			this.classValuesAsString = classValuesAsString;
			this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
			this.aggregates = aggregates;
			this.aggregatedResults = (aggregates ? new ArrayList<AnnotationAttributes>() : null);
		}

		public boolean alwaysProcesses() {
			return false;
		}

		public boolean aggregates() {
			return this.aggregates;
		}

		public List<AnnotationAttributes> getAggregatedResults() {
			return this.aggregatedResults;
		}

		public AnnotationAttributes process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
			return AnnotationUtils.retrieveAnnotationAttributes(annotatedElement, annotation, this.classValuesAsString,
					this.nestedAnnotationsAsMap);
		}

		public void postProcess(AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes) {
			annotation = AnnotationUtils.synthesizeAnnotation(annotation, element);
			Class<? extends Annotation> targetAnnotationType = attributes.annotationType();

			// Track which attribute values have already been replaced so that
			// we can short
			// circuit the search algorithms.
			Set<String> valuesAlreadyReplaced = new HashSet<String>();

			for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotation.annotationType())) {
				String attributeName = attributeMethod.getName();
				String attributeOverrideName = AnnotationUtils.getAttributeOverrideName(attributeMethod,
						targetAnnotationType);

				// Explicit annotation attribute override declared via @AliasFor
				if (attributeOverrideName != null) {
					if (valuesAlreadyReplaced.contains(attributeOverrideName)) {
						continue;
					}

					List<String> targetAttributeNames = new ArrayList<String>();
					targetAttributeNames.add(attributeOverrideName);
					valuesAlreadyReplaced.add(attributeOverrideName);

					// Ensure all aliased attributes in the target annotation
					// are overridden. (SPR-14069)
					List<String> aliases = AnnotationUtils.getAttributeAliasMap(targetAnnotationType)
							.get(attributeOverrideName);
					if (aliases != null) {
						for (String alias : aliases) {
							if (!valuesAlreadyReplaced.contains(alias)) {
								targetAttributeNames.add(alias);
								valuesAlreadyReplaced.add(alias);
							}
						}
					}

					overrideAttributes(element, annotation, attributes, attributeName, targetAttributeNames);
				}
				// Implicit annotation attribute override based on convention
				else if (!AnnotationUtils.VALUE.equals(attributeName) && attributes.containsKey(attributeName)) {
					overrideAttribute(element, annotation, attributes, attributeName, attributeName);
				}
			}
		}

		private void overrideAttributes(AnnotatedElement element, Annotation annotation,
				AnnotationAttributes attributes, String sourceAttributeName, List<String> targetAttributeNames) {

			Object adaptedValue = getAdaptedValue(element, annotation, sourceAttributeName);

			for (String targetAttributeName : targetAttributeNames) {
				attributes.put(targetAttributeName, adaptedValue);
			}
		}

		private void overrideAttribute(AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes,
				String sourceAttributeName, String targetAttributeName) {

			attributes.put(targetAttributeName, getAdaptedValue(element, annotation, sourceAttributeName));
		}

		private Object getAdaptedValue(AnnotatedElement element, Annotation annotation, String sourceAttributeName) {
			Object value = AnnotationUtils.getValue(annotation, sourceAttributeName);
			return AnnotationUtils.adaptValue(element, value, this.classValuesAsString, this.nestedAnnotationsAsMap);
		}
	}

	private static final class EnumerationAnnotatedElement implements Enumeration<AnnotatedElement> {
		private final Annotation[][] annotations;
		private int index = 0;

		public EnumerationAnnotatedElement(Annotation[][] annotations) {
			this.annotations = annotations;
		}

		public boolean hasMoreElements() {
			return index < annotations.length;
		}

		public AnnotatedElement nextElement() {
			Annotation[] annotationsToUse = annotations[index++];
			return AnnotatedElementUtils.forAnnotations(annotationsToUse);
		}
	}
}
