package io.basc.framework.util.element;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.util.Assert;
import io.basc.framework.util.select.FlatMerger;
import io.basc.framework.util.select.Merger;

public class MergedElements<E> implements Elements<E> {
	private static final int JOIN_MAX_LENGTH = Integer.max(2,
			Integer.getInteger(MergedElements.class.getName() + ".maxLength", 256));
	private final Merger<Elements<E>> merger;
	private final Elements<? extends E>[] members;

	@SafeVarargs
	public MergedElements(Elements<? extends E>... members) {
		this(FlatMerger.global(), members);
	}

	@SafeVarargs
	public MergedElements(Merger<Elements<E>> merger, Elements<? extends E>... members) {
		Assert.requiredArgument(merger != null, "merger");
		Assert.requiredArgument(members != null, "members");
		this.members = members;
		this.merger = merger;
	}

	public Elements<E> getSelected() {
		Elements<Elements<? extends E>> members = Elements.forArray(this.members);
		return merger.merge(members.map((elements) -> elements.map(Function.identity())));
	}

	@Override
	public Stream<E> stream() {
		return getSelected().stream();
	}

	@Override
	public Iterator<E> iterator() {
		return getSelected().iterator();
	}

	@Override
	public Elements<E> concat(Elements<? extends E> elements) {
		if (members.length == JOIN_MAX_LENGTH) {
			// 如果数组已经达到最大值那么使用嵌套方式实现
			return new MergedElements<>(merger, this, elements);
		} else {
			// 使用数组实现，用来解决大量嵌套对象问题
			Elements<? extends E>[] newMembers = Arrays.copyOf(members, members.length + 1);
			newMembers[members.length] = elements;
			return new MergedElements<>(merger, newMembers);
		}
	}
}
