package scw.data.geo;

import java.util.List;

import scw.lang.Nullable;
import scw.util.comparator.Sort;

public interface MarkerManager<K> {
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
	List<Marker<K>> getNearbyMarkers(Point point, Distance radius, int count, Sort sort);
}
