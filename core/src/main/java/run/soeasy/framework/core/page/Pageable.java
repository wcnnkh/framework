package run.soeasy.framework.core.page;

import java.util.function.Function;
import java.util.function.Predicate;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;

public interface Pageable<K, T> extends Page<K, T>, Browseable<K, T> {

	public static class ConvertiblePageable<M extends Pageable<SK, ST>, SK, ST, K, T>
			extends ConvertiblePage<M, SK, ST, K, T> implements Pageable<K, T> {
		private final Codec<SK, K> cursorIdCodec;

		public ConvertiblePageable(M source, Codec<SK, K> cursorIdCodec,
				Function<? super Elements<ST>, ? extends Elements<T>> elementsConverter) {
			super(source, Assert.requiredArgument(cursorIdCodec != null, "cursorIdCodec", cursorIdCodec)::encode,
					elementsConverter);
			this.cursorIdCodec = cursorIdCodec;
		}

		@Override
		public Pageable<K, T> jumpTo(K cursorId, long count) {
			SK targetCursorId = cursorId == null ? null : cursorIdCodec.decode(cursorId);
			Pageable<SK, ST> pages = source.jumpTo(targetCursorId);
			return new ConvertiblePageable<>(pages, cursorIdCodec, elementsConverter);
		}

	}

	public static class AllPage<S extends Pageable<K, T>, K, T> extends AllCursor<S, K, T> implements Page<K, T> {

		public AllPage(S source) {
			super(source);
		}

		@Override
		public long getTotal() {
			return source.getTotal();
		}

		@Override
		public long getPageSize() {
			// 优化父类实现
			long pageSize = source.getTotal();
			long mod = pageSize % source.getPageSize();
			if (mod != 0) {
				pageSize = pageSize - mod + source.getPageSize();
			}
			return pageSize;
		}
	}

	public static interface PageableWrapper<K, T, W extends Pageable<K, T>>
			extends BrowseableWrapper<K, T, W>, PageWrapper<K, T, W>, Pageable<K, T> {

		@Override
		default Page<K, T> shared() {
			return getSource().shared();
		}

		@Override
		default <TK, TT> Page<TK, TT> convert(Function<? super K, ? extends TK> cursorIdConverter,
				Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(cursorIdConverter, elementsConverter);
		}

		@Override
		default <TT> Pageable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(elementsConverter);
		}

		@Override
		default Elements<? extends Page<K, T>> pages() {
			return getSource().pages();
		}

		@Override
		default long getTotal() {
			return getSource().getTotal();
		}

		@Override
		default long getPageSize() {
			return getSource().getPageSize();
		}

		@Override
		default Pageable<K, T> jumpTo(K cursorId, long count) {
			return getSource().jumpTo(cursorId, count);
		}

		@Override
		default Pageable<K, T> jumpTo(K cursorId) {
			return getSource().jumpTo(cursorId);
		}

		@Override
		default Pageable<K, T> next() {
			return getSource().next();
		}

		@Override
		default <TK, TT> Page<TK, TT> map(Function<? super K, ? extends TK> keyMapper,
				Function<? super T, ? extends TT> valueMapper) {
			return getSource().map(keyMapper, valueMapper);
		}

		@Override
		default <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> valueMapper) {
			return getSource().map(valueMapper);
		}

		@Override
		default Page<K, T> all() {
			return getSource().all();
		}

		@Override
		default <TK, TT> Pageable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
				Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
			return getSource().convert(cursorIdCodec, elementsConverter);
		}

		@Override
		default Pageable<K, T> filter(Predicate<? super T> predicate) {
			return getSource().filter(predicate);
		}

		@Override
		default <TT> Pageable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
			return getSource().flatMap(mapper);
		}

		@Override
		default boolean hasNext() {
			return getSource().hasNext();
		}

		@Override
		default <TK, TT> Pageable<TK, TT> map(Codec<K, TK> cursorIdCodec,
				Function<? super T, ? extends TT> elementMapper) {
			return getSource().map(cursorIdCodec, elementMapper);
		}

		@Override
		default Paginations<T> toPaginations(long start, long limit) {
			return getSource().toPaginations(start, limit);
		}
	}

	@Override
	default Pageable<K, T> next() {
		return jumpTo(getNextCursorId());
	}

	default Pageable<K, T> jumpTo(K cursorId) {
		return jumpTo(cursorId, getPageSize());
	}

	/**
	 * 获取所有页
	 */
	default Elements<? extends Page<K, T>> pages() {
		return Elements.of(() -> new BrowseableIterator<>(this, (e) -> e.next()));
	}

	Pageable<K, T> jumpTo(K cursorId, long count);

	@Override
	default Page<K, T> all() {
		return new AllPage<>(this);
	}

	/**
	 * 这是极端情况下的处理，不推荐使用(性能低下)
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	default Paginations<T> toPaginations(long start, long limit) {
		Paginations<T> paginations = new Paginations<>(all().getElements());
		paginations.setTotal(paginations.getTotal());
		paginations.setCursorId(start);
		paginations.setPageSize(limit);
		return paginations;
	}

	/**
	 * 默认调用{@link #convert(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Pageable<K, T> filter(Predicate<? super T> predicate) {
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
	default <TT> Pageable<K, TT> map(Function<? super T, ? extends TT> elementMapper) {
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
	default <TK, TT> Pageable<TK, TT> map(Codec<K, TK> cursorIdCodec, Function<? super T, ? extends TT> elementMapper) {
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
	default <TT> Pageable<K, TT> flatMap(Function<? super T, ? extends Elements<TT>> mapper) {
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
	default <TT> Pageable<K, TT> convert(Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return convert(Codec.identity(), elementsConverter);
	}

	default <TK, TT> Pageable<TK, TT> convert(Codec<K, TK> cursorIdCodec,
			Function<? super Elements<T>, ? extends Elements<TT>> elementsConverter) {
		return new ConvertiblePageable<>(this, cursorIdCodec, elementsConverter);
	}
}
