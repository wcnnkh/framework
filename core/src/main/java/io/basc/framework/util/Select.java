package io.basc.framework.util;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;

public final class Select<E> implements Supplier<E> {
	private final ResultSet<E> elements;
	@Nullable
	private final Selector<E> selector;

	/**
	 * @param elements
	 * @param selector 如果为空，那么消费所以元素{@link #consume(ConsumeProcessor)}
	 */
	public Select(ResultSet<E> elements, @Nullable Selector<E> selector) {
		Assert.requiredArgument(elements != null, "elements");
		this.elements = elements;
		this.selector = selector;
	}

	public ResultSet<E> getElements() {
		return elements;
	}

	@Nullable
	public Selector<E> getSelector() {
		return selector;
	}

	@Override
	public E get() {
		if (selector == null) {
			throw new UnsupportedException("Selector not found");
		}

		return elements.export((e) -> selector.apply(e));
	}

	public <T, X extends Throwable> T process(Processor<? super E, ? extends T, ? extends X> processor) throws X {
		Assert.requiredArgument(processor != null, "processor");
		if (selector == null) {
			throw new UnsupportedException("Selector not found");
		}

		E element = elements.export((e) -> selector.apply(e));
		if (element == null) {
			return null;
		}

		return processor.process(element);
	}

	/**
	 * 如果selector为空，那么消费所有元素
	 * 
	 * @param <X>
	 * @param consumer
	 * @throws X
	 */
	public <X extends Throwable> void consume(ConsumeProcessor<? super E, ? extends X> consumer) throws X {
		Assert.requiredArgument(consumer != null, "consumer");
		if (selector == null) {
			elements.transfer((e) -> ConsumeProcessor.consumeAll(e.iterator(), consumer));
		} else {
			E element = elements.export((e) -> selector.apply(e));
			if (element == null) {
				return;
			}
			consumer.process(element);
		}
	}
}
