package io.basc.framework.core;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Streamable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 类成员
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
@Data
@AllArgsConstructor
public class Members<E> implements Cloneable {
	private final ResolvableType source;
	private final Elements<E> elements;

	@Override
	public Members<E> clone() {
		return new Members<>(source, elements);
	}

	public Members<E> concat(Elements<? extends E> elements) {
		Assert.requiredArgument(elements != null, "elements");
		return convert((e) -> Elements.concat(e, elements));
	}

	public <T> Members<T> convert(Function<? super Elements<E>, ? extends Elements<T>> converter) {
		Assert.requiredArgument(converter != null, "converter");
		Elements<T> elements = converter.apply(this.elements);
		return new Members<>(source, elements == null ? Elements.empty() : elements);
	}

	public Members<E> exclude(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((elements) -> elements.exclude(predicate));
	}

	public Members<E> filter(Predicate<? super E> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((elements) -> elements.filter(predicate));
	}

	public <T> Members<T> flatMap(Function<? super E, ? extends Streamable<T>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((elements) -> elements.flatMap(mapper));
	}

	public <T> Members<T> map(Function<? super E, ? extends T> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((elements) -> elements.map(mapper));
	}

	public Members<E> peek(Consumer<? super E> consumer) {
		Assert.requiredArgument(consumer != null, "consumer");
		return convert((e) -> e.peek(consumer));
	}
}
