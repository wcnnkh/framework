package run.soeasy.framework.core.collection;

import java.util.Arrays;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.collection.merge.Merger;

public class MergedElements<E> implements ElementsWrapper<E, Elements<E>> {
	private static final int JOIN_MAX_LENGTH = Integer.max(4,
			Integer.getInteger(MergedElements.class.getName() + ".maxLength", 256));
	private final Elements<? extends E>[] members;
	private final Merger<Elements<E>> merger;

	@SafeVarargs
	public MergedElements(@NonNull Elements<? extends E>... members) {
		this(Merger.flat(), members);
	}

	@SafeVarargs
	public MergedElements(@NonNull Merger<Elements<E>> merger, @NonNull Elements<? extends E>... members) {
		this.members = members;
		this.merger = merger;
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

	@Override
	public Elements<E> getSource() {
		Elements<Elements<? extends E>> members = Elements.forArray(this.members);
		return merger.select(members.map((elements) -> elements.map(Function.identity())));
	}
}