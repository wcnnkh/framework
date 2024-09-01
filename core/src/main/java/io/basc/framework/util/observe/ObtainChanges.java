package io.basc.framework.util.observe;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;

public class ObtainChanges {

	public static <T> Elements<ChangeEvent<T>> getChanges(Elements<? extends T> leftElements,
			Elements<? extends T> rightElements, BiPredicate<? super T, ? super T> equals) {
		if (leftElements.isEmpty()) {
			return rightElements.map((e) -> new ChangeEvent<>(e, ChangeType.CREATE));
		} else if (rightElements.isEmpty()) {
			return leftElements.map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
		}

		List<T> leftList = leftElements.collect(Collectors.toList());
		List<T> rightList = rightElements.collect(Collectors.toList());

		// 移除相同的原因
		Iterator<T> leftIterator = leftList.iterator();
		while (leftIterator.hasNext()) {
			T left = leftIterator.next();
			Iterator<T> rightIterator = rightList.iterator();
			while (rightIterator.hasNext()) {
				T right = rightIterator.next();
				if (equals.test(left, right)) {
					// 相同的忽略
					leftIterator.remove();
					rightIterator.remove();
				}
			}
		}

		// 左边剩下的说明被删除了
		Elements<ChangeEvent<T>> leftEvents = Elements.of(leftList).map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
		// 右边剩下的说明是创建的
		Elements<ChangeEvent<T>> rightEvents = Elements.of(rightList)
				.map((e) -> new ChangeEvent<>(e, ChangeType.CREATE));
		return leftEvents.concat(rightEvents);
	}

}
