package io.basc.framework.redis;

import java.io.Serializable;

import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.function.Processor;

public class GeoRadiusArgs<K> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer count;
	private boolean any;
	private Sort sort;
	private K storeKey;
	private K storeDistKey;

	public Integer getCount() {
		return count;
	}

	public Sort getSort() {
		return sort;
	}

	public K getStoreKey() {
		return storeKey;
	}

	public K getStoreDistKey() {
		return storeDistKey;
	}

	public boolean isAny() {
		return any;
	}

	public GeoRadiusArgs<K> count(Integer count) {
		return count(count, false);
	}

	public GeoRadiusArgs<K> count(int count, boolean any) {
		this.count = count;
		this.any = any;
		return this;
	}

	public GeoRadiusArgs<K> sort(Sort sort) {
		this.sort = sort;
		return this;
	}

	public GeoRadiusArgs<K> asc() {
		return sort(Sort.ASC);
	}

	public GeoRadiusArgs<K> desc() {
		return sort(Sort.DESC);
	}

	public GeoRadiusArgs<K> storeKey(K storeKey) {
		this.storeKey = storeKey;
		return this;
	}

	public GeoRadiusArgs<K> storeDistKey(K storeDistKey) {
		this.storeDistKey = storeDistKey;
		return this;
	}

	public <T, E extends Throwable> GeoRadiusArgs<T> convert(Processor<? super K, ? extends T, ? extends E> convert)
			throws E {
		GeoRadiusArgs<T> geoRadiusArgs = new GeoRadiusArgs<T>();
		geoRadiusArgs.count = count;
		geoRadiusArgs.any = any;
		geoRadiusArgs.sort = sort;
		geoRadiusArgs.storeKey = convert.process(storeKey);
		geoRadiusArgs.storeDistKey = convert.process(storeDistKey);
		return geoRadiusArgs;
	}
}
