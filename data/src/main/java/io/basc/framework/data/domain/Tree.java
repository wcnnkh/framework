package io.basc.framework.data.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.function.Function;
import lombok.Data;

/**
 * 树
 * 
 * @author wcnnkh
 *
 * @param <T> 节点类型
 */
@Data
public class Tree<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private T node;
	private List<Tree<T>> childNodes;

	public T getNode() {
		return node;
	}

	public void setNode(T node) {
		this.node = node;
	}

	public List<Tree<T>> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<Tree<T>> childNodes) {
		this.childNodes = childNodes;
	}

	public static <T, O, E extends Throwable, K> List<Tree<T>> parse(Collection<? extends O> options,
			Function<? super O, ? extends K, ? extends E> keyProcessor, @Nullable K parentId,
			Function<? super O, ? extends K, ? extends E> parentKeyProcessor,
			Function<? super O, ? extends T, ? extends E> processor) throws E {
		if (options == null) {
			return null;
		}

		if (options.isEmpty()) {
			return Collections.emptyList();
		}

		List<Tree<T>> list = new ArrayList<Tree<T>>();
		for (O option : options) {
			K parent = parentKeyProcessor.process(option);
			if (ObjectUtils.equals(parent, parentId)) {
				T value = processor.process(option);
				K key = keyProcessor.process(option);
				Tree<T> tree = new Tree<T>();
				tree.setNode(value);
				tree.setChildNodes(parse(options, keyProcessor, key, parentKeyProcessor, processor));
				list.add(tree);
			}
		}
		return list;
	}

	public static <S, K, V, E extends Throwable> List<Tree<Pair<K, V>>> parse(Collection<? extends S> sourceList,
			int depth, int maxDepth, Function<? super Pair<Integer, S>, ? extends Pair<K, V>, ? extends E> processor)
			throws E {
		if (sourceList == null) {
			return null;
		}

		if (depth >= maxDepth) {
			return null;
		}

		if (sourceList.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Temp<K, Pair<K, V>>, List<S>> map = new LinkedHashMap<>();
		for (S source : sourceList) {
			Pair<K, V> pair = processor.process(new Pair<Integer, S>(depth, source));
			if (pair == null) {
				continue;
			}

			Temp<K, Pair<K, V>> temp = new Temp<>();
			temp.setKey(pair.getKey());
			temp.setValue(pair);
			List<S> list = map.get(temp);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(source);
			map.put(temp, list);
		}

		List<Tree<Pair<K, V>>> list = new ArrayList<Tree<Pair<K, V>>>();
		for (Entry<Temp<K, Pair<K, V>>, List<S>> entry : map.entrySet()) {
			Tree<Pair<K, V>> tree = new Tree<Pair<K, V>>();
			tree.setNode(entry.getKey().getValue());
			tree.setChildNodes(parse(entry.getValue(), depth + 1, maxDepth, processor));
			list.add(tree);
		}
		return list;
	}

	private static class Temp<K, V> extends Pair<K, V> {
		private static final long serialVersionUID = 1L;

		@Override
		public int hashCode() {
			return getKey() == null ? 0 : getKey().hashCode();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof Temp) {
				return ObjectUtils.equals(((Temp) obj).getKey(), this.getKey());
			}
			return false;
		}
	}
}
