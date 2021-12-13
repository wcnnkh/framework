package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import io.basc.framework.core.annotation.MergedAnnotation.Adapt;
import io.basc.framework.util.LinkedMultiValueMap;
import io.basc.framework.util.MultiValueMap;

/**
 * {@link Collector} implementations that provide various reduction operations
 * for {@link MergedAnnotation} instances.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/MergedAnnotationCollectors.java
 */
public abstract class MergedAnnotationCollectors {

	private static final Characteristics[] NO_CHARACTERISTICS = {};

	private static final Characteristics[] IDENTITY_FINISH_CHARACTERISTICS = { Characteristics.IDENTITY_FINISH };

	private MergedAnnotationCollectors() {
	}

	/**
	 * Create a new {@link Collector} that accumulates merged annotations to a
	 * {@link LinkedHashSet} containing {@linkplain MergedAnnotation#synthesize()
	 * synthesized} versions.
	 * <p>
	 * The collector returned by this method is effectively equivalent to
	 * {@code Collectors.mapping(MergedAnnotation::synthesize, Collectors.toCollection(LinkedHashSet::new))}
	 * but avoids the creation of a composite collector.
	 * 
	 * @param <A> the annotation type
	 * @return a {@link Collector} which collects and synthesizes the annotations
	 *         into a {@link Set}
	 */
	public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, Set<A>> toAnnotationSet() {
		return Collector.of(LinkedHashSet::new, (set, annotation) -> set.add(annotation.synthesize()),
				MergedAnnotationCollectors::combiner);
	}

	/**
	 * Create a new {@link Collector} that accumulates merged annotations to an
	 * {@link Annotation} array containing {@linkplain MergedAnnotation#synthesize()
	 * synthesized} versions.
	 * 
	 * @param <A> the annotation type
	 * @return a {@link Collector} which collects and synthesizes the annotations
	 *         into an {@code Annotation[]}
	 * @see #toAnnotationArray(IntFunction)
	 */
	public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, Annotation[]> toAnnotationArray() {
		return toAnnotationArray(Annotation[]::new);
	}

	/**
	 * Create a new {@link Collector} that accumulates merged annotations to an
	 * {@link Annotation} array containing {@linkplain MergedAnnotation#synthesize()
	 * synthesized} versions.
	 * 
	 * @param <A>       the annotation type
	 * @param <R>       the resulting array type
	 * @param generator a function which produces a new array of the desired type
	 *                  and the provided length
	 * @return a {@link Collector} which collects and synthesizes the annotations
	 *         into an annotation array
	 * @see #toAnnotationArray
	 */
	public static <R extends Annotation, A extends R> Collector<MergedAnnotation<A>, ?, R[]> toAnnotationArray(
			IntFunction<R[]> generator) {
		return Collector.of(ArrayList::new, (list, annotation) -> list.add(annotation.synthesize()),
				MergedAnnotationCollectors::combiner, list -> list.toArray(generator.apply(list.size())));
	}

	/**
	 * Create a new {@link Collector} that accumulates merged annotations to a
	 * {@link MultiValueMap} with items
	 * {@linkplain MultiValueMap#add(Object, Object) added} from each merged
	 * annotation {@linkplain MergedAnnotation#asMap(Adapt...) as a map}.
	 * 
	 * @param <A>         the annotation type
	 * @param adaptations the adaptations that should be applied to the annotation
	 *                    values
	 * @return a {@link Collector} which collects and synthesizes the annotations
	 *         into a {@link LinkedMultiValueMap}
	 * @see #toMultiValueMap(Function, MergedAnnotation.Adapt...)
	 */
	public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, MultiValueMap<String, Object>> toMultiValueMap(
			Adapt... adaptations) {
		return toMultiValueMap(Function.identity(), adaptations);
	}

	/**
	 * Create a new {@link Collector} that accumulates merged annotations to a
	 * {@link MultiValueMap} with items
	 * {@linkplain MultiValueMap#add(Object, Object) added} from each merged
	 * annotation {@linkplain MergedAnnotation#asMap(Adapt...) as a map}.
	 * 
	 * @param <A>         the annotation type
	 * @param finisher    the finisher function for the new {@link MultiValueMap}
	 * @param adaptations the adaptations that should be applied to the annotation
	 *                    values
	 * @return a {@link Collector} which collects and synthesizes the annotations
	 *         into a {@link LinkedMultiValueMap}
	 * @see #toMultiValueMap(MergedAnnotation.Adapt...)
	 */
	public static <A extends Annotation> Collector<MergedAnnotation<A>, ?, MultiValueMap<String, Object>> toMultiValueMap(
			Function<MultiValueMap<String, Object>, MultiValueMap<String, Object>> finisher, Adapt... adaptations) {
		Characteristics[] characteristics = (isSameInstance(finisher, Function.identity())
				? IDENTITY_FINISH_CHARACTERISTICS
				: NO_CHARACTERISTICS);
		return Collector.of(LinkedMultiValueMap::new,
				(map, annotation) -> annotation.asMap(adaptations).forEach(map::add),
				MergedAnnotationCollectors::combiner, finisher, characteristics);
	}

	private static boolean isSameInstance(Object instance, Object candidate) {
		return instance == candidate;
	}

	/**
	 * {@link Collector#combiner() Combiner} for collections.
	 * <p>
	 * This method is only invoked if the {@link java.util.stream.Stream} is
	 * processed in {@linkplain java.util.stream.Stream#parallel() parallel}.
	 */
	private static <E, C extends Collection<E>> C combiner(C collection, C additions) {
		collection.addAll(additions);
		return collection;
	}

	/**
	 * {@link Collector#combiner() Combiner} for multi-value maps.
	 * <p>
	 * This method is only invoked if the {@link java.util.stream.Stream} is
	 * processed in {@linkplain java.util.stream.Stream#parallel() parallel}.
	 */
	private static <K, V> MultiValueMap<K, V> combiner(MultiValueMap<K, V> map, MultiValueMap<K, V> additions) {
		map.addAll(additions);
		return map;
	}

}