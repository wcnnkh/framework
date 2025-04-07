package run.soeasy.framework.util.collection;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;
import run.soeasy.framework.util.Assert;

@Data
@AllArgsConstructor
public class ConvertibleIterator<S, E> implements Iterator<E> {
	private final Iterator<? extends S> iterator;
	private final Function<? super S, ? extends E> converter;

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		S s = iterator.next();
		return converter.apply(s);
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		Assert.requiredArgument(action != null, "action");
		iterator.forEachRemaining((s) -> {
			E e = converter.apply(s);
			action.accept(e);
		});
	}
}
