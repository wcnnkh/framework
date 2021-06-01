package scw.redis.core;

import java.io.Serializable;

import scw.convert.Converter;
import scw.util.comparator.Sort;

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
	
	public <T> GeoRadiusArgs<T> convert(Converter<K, T> convert){
		GeoRadiusArgs<T> geoRadiusArgs = new GeoRadiusArgs<T>();
		geoRadiusArgs.count = count;
		geoRadiusArgs.any = any;
		geoRadiusArgs.sort = sort;
		geoRadiusArgs.storeKey = convert.convert(storeKey);
		geoRadiusArgs.storeDistKey = convert.convert(storeDistKey);
		return geoRadiusArgs;
	}
}
