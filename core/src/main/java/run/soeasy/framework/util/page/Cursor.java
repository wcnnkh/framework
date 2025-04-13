package run.soeasy.framework.util.page;

import java.util.function.Function;
import java.util.function.Predicate;

import run.soeasy.framework.lang.Wrapper;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.collection.Elements;

public interface Cursor<K, T> {

	public static class ConvertibleCursor<M extends Cursor<SK, ST>, SK, ST, K, T> implements Cursor<K, T> {
		protected final M source;
		protected final Function<? super SK, ? extends K> cursorIdConverter;
		protected final Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter;

		public ConvertibleCursor(M source, Function<? super SK, ? extends K> cursorIdConverter,
				Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
			Assert.requiredArgument(source != null, "source");
			Assert.requiredArgument(cursorIdConverter != null, "cursorIdConverter");
			Assert.requiredArgument(elementsConverter != null, "elementsConverter");
			this.source = source;
			this.cursorIdConverter = cursorIdConverter;
			this.elementsConverter = elementsConverter;
		}

		@Override
		public K getCursorId() {
			SK value = source.getCursorId();
			return value == null ? null : cursorIdConverter.apply(value);
		}

		@Override
		public K getNextCursorId() {
			SK value = source.getNextCursorId();
			return value == null ? null : cursorIdConverter.apply(value);
		}

		@Override
		public boolean hasNext() {
			return source.hasNext();
		}

		@Override
		public Elements<T> getElements() {
			return elementsConverter.apply(source.getElements());
		}
	}

	public static interface CursorWrapper<K, T, W extends Cursor<K, T>> extends Cursor<K, T>, Wrapper<W> {

		@Override
		default K getCursorId() {
			return getSource().getCursorId();
		}

		@Override
		default Elements<T> getElements() {
			return getSource().getElements();
		}

		@Override
		default boolean hasNext() {
			return getSource().hasNext();
		}

		@Override
		default <TK, TT> Cursor<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
				Function<? super T, ? extends TT> valueMapper) {
			return getSource().map(keyMapper, valueMapper);
		}

		@Override
		default <TT> Cursor<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
			return getSource().map(valueMapper);
		}

		@Override
		default Cursor<K, T> shared() {
			return getSource().shared();
		}

		@Override
		default K getNextCursorId() {
			return getSource().getNextCursorId();
		}

		@Override
		default <TT> Cursor<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(elementsConverter);
		}

		@Override
		default <TK, TT> Cursor<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
				Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(cursorIdConverter, elementsConverter);
		}

		@Override
		default Cursor<K, T> filter(Predicate<? super T> predicate) {
			return getSource().filter(predicate);
		}

		@Override
		default <TT> Cursor<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
			return getSource().flatMap(mapper);
		}
	}

	/**
	 * 获取当前页的使用的开始游标
	 * 
	 * @return
	 */
	K getCursorId();

	/**
	 * 获取下一页的开始游标id
	 * 
	 * @return
	 */
	K getNextCursorId();

	Elements<T> getElements();

	/**
	 * 是否还有更多数据
	 * 
	 * @return
	 */
	default boolean hasNext() {
		return getNextCursorId() != null;
	}

	default Cursor<K, T> shared() {
		return new SharedCursor<>(this);
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Cursor<K, T> filter(Predicate<? super T> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return convert((elements) -> elements.filter(predicate));
	}

	/**
	 * 默认调用{@link #map(Function, Function)}
	 * 
	 * @param <TT>
	 * @param elementMapper
	 * @return
	 */
	default <TT> Cursor<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
		return map(Function.identity(), elementMapper);
	}

	/**
	 * 默认调用{@link #convert(Function, Function)}
	 * 
	 * @param <TK>
	 * @param <TT>
	 * @param cursorIdMapper
	 * @param elementMapper
	 * @return
	 */
	default <TK, TT> Cursor<TK, TT> map(Function<? super K, ? extends TK> cursorIdMapper,
			Function<? super T, ? extends TT> elementMapper) {
		Assert.requiredArgument(elementMapper != null, "elementMapper");
		return convert(cursorIdMapper, (elements) -> elements.map(elementMapper));
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param <TT>
	 * @param mapper
	 * @return
	 */
	default <TT> Cursor<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
		Assert.requiredArgument(mapper != null, "mapper");
		return convert((elements) -> elements.flatMap(mapper));
	}

	/**
	 * 默认调用{@link #convert(Function, Function)}
	 * 
	 * @param <TT>
	 * @param elementsConverter
	 * @return
	 */
	default <TT> Cursor<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Function.identity(), elementsConverter);
	}

	default <TK, TT> Cursor<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertibleCursor<>(this, cursorIdConverter, elementsConverter);
	}
}