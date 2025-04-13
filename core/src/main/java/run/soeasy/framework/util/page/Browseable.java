package run.soeasy.framework.util.page;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.codec.Codec;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.function.Functions;

public interface Browseable<K, T> extends Cursor<K, T> {

	public static final class BrowseableIterator<E extends Browseable<?, ?>> implements Iterator<E> {
		private E pageables;
		private Supplier<E> current;
		private final Function<? super E, ? extends E> next;

		public BrowseableIterator(E pageables, Function<? super E, ? extends E> next) {
			this.pageables = pageables;
			this.current = Functions.forValue(pageables);
			this.next = next;
		}

		@Override
		public boolean hasNext() {
			if (current != null) {
				return true;
			}

			return pageables.hasNext();
		}

		@Override
		public E next() {
			if (current != null) {
				try {
					return current.get();
				} finally {
					current = null;
				}
			} else {
				this.pageables = next.apply(this.pageables);
				return pageables;
			}
		}
	}

	public static class ConvertibleBrowseable<M extends Browseable<SK, ST>, SK, ST, K, T>
			extends ConvertibleCursor<M, SK, ST, K, T> implements Browseable<K, T> {
		protected final Codec<SK, K> cursorIdCodec;

		public ConvertibleBrowseable(M source, Codec<SK, K> cursorIdCodec,
				Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
			super(source, Assert.requiredArgument(cursorIdCodec != null, "elementsConverter", cursorIdCodec)::encode,
					elementsConverter);
			this.cursorIdCodec = cursorIdCodec;
		}

		@Override
		public Browseable<K, T> jumpTo(K cursorId) {
			SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
			Browseable<SK, ST> pageables = source.jumpTo(targetCursorId);
			return new ConvertibleBrowseable<>(pageables, cursorIdCodec, elementsConverter);
		}

	}

	public static class AllCursor<S extends Browseable<K, T>, K, T> implements Cursor<K, T> {
		protected final S source;

		public AllCursor(S source) {
			this.source = source;
		}

		@Override
		public K getCursorId() {
			return source.getCursorId();
		}

		@Override
		public K getNextCursorId() {
			return null;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Elements<T> getElements() {
			return source.pages().flatMap((e) -> e.getElements());
		}
	}

	public static interface BrowseableWrapper<K, T, W extends Browseable<K, T>>
			extends CursorWrapper<K, T, W>, Browseable<K, T> {

		default Cursor<K, T> shared() {
			return getSource().shared();
		}

		@Override
		default Browseable<K, T> jumpTo(K cursorId) {
			return getSource().jumpTo(cursorId);
		}

		@Override
		default Browseable<K, T> next() {
			return getSource().next();
		}

		@Override
		default Cursor<K, T> all() {
			return getSource().all();
		}

		@Override
		default Elements<? extends Cursor<K, T>> pages() {
			return getSource().pages();
		}

		@Override
		default <TK, TT> Browseable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
				Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(cursorIdCodec, elementsConverter);
		}

		@Override
		default <TT> Browseable<K, TT> convert(
				Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(elementsConverter);
		}

		@Override
		default Browseable<K, T> filter(Predicate<? super T> predicate) {
			return getSource().filter(predicate);
		}

		@Override
		default <TT> Browseable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
			return getSource().flatMap(mapper);
		}

		@Override
		default boolean hasNext() {
			return getSource().hasNext();
		}

		@Override
		default <TK, TT> Browseable<TK, TT> map(Codec<K, TK> cursorIdCodec,
				Function<? super T, ? extends TT> elementMapper) {
			return getSource().map(cursorIdCodec, elementMapper);
		}

		@Override
		default <TT> Browseable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
			return getSource().map(valueMapper);
		}

	}

	Browseable<K, T> jumpTo(K cursorId);

	default Browseable<K, T> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("cursorId=" + getCursorId() + ", nextCursorId=" + getNextCursorId());
		}
		return jumpTo(getNextCursorId());
	}

	default Elements<? extends Cursor<K, T>> pages() {
		return Elements.of(() -> new BrowseableIterator<>(this, (e) -> e.next()));
	}

	default Cursor<K, T> all() {
		return new AllCursor<>(this);
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Browseable<K, T> filter(Predicate<? super T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((elements) -> elements.filter(predicate));
	}

	/**
	 * 默认调用{@link #map(Codec, Function)}
	 * 
	 * @param <TT>
	 * @param elementMapper
	 * @return
	 */
	default <TT> Browseable<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Codec.identity(), elementMapper);
	}

	/**
	 * 默认调用{@link #convert(Function, Function)}
	 * 
	 * @param <TK>
	 * @param <TT>
	 * @param cursorIdCodec
	 * @param elementMapper
	 * @return
	 */
	default <TK, TT> Browseable<TK, TT> map(Codec<K, TK> cursorIdCodec,
			Function<? super T, ? extends TT> elementMapper) {
		Assert.requiredArgument(elementMapper != null, "elementMapper");
		return convert(cursorIdCodec, (elements) -> elements.map(elementMapper));
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param <TT>
	 * @param mapper
	 * @return
	 */
	default <TT> Browseable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((elements) -> elements.flatMap(mapper));
	}

	/**
	 * 默认调用{@link #convert(Codec, Function)}
	 * 
	 * @param <TT>
	 * @param elementsConverter
	 * @return
	 */
	default <TT> Browseable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Codec.identity(), elementsConverter);
	}

	default <TK, TT> Browseable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertibleBrowseable<>(this, cursorIdCodec, elementsConverter);
	}
}