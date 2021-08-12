package scw.data.geo;

import scw.lang.Nullable;
import scw.util.comparator.Sort;
import scw.util.stream.Cursor;

/**
 * 基于位置的服务（Location Based Services，LBS）
 * 
 * @author shuchaowen
 *
 * @param <K>
 */
public interface Lbs<K> {
	/**
	 * 上报位置信息
	 * 
	 * @param marker
	 */
	void report(Marker<K> marker);

	@Nullable
	Marker<K> getMarker(K key);

	boolean remove(K key);

	boolean exists(K key);

	/**
	 * 查询附近的点
	 * 
	 * @return
	 */
	Cursor<Marker<K>> getNearbyMarkers(Point point, Distance radius, int count, Sort sort);
}