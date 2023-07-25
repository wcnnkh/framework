package io.basc.framework.data.geo;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.util.element.Elements;

/**
 * 基于位置的服务（Location Based Services，LBS）
 * 
 * @author wcnnkh
 *
 * @param <K> key的类型
 */
public interface Lbs<K> {
	void report(Marker<K> marker);

	@Nullable
	Marker<K> getMarker(K key);

	boolean remove(K key);

	boolean exists(K key);

	Elements<Marker<K>> getNearbyMarkers(Point point, Distance radius, int count, Sort sort);
}
