package scw.data.geo;

import java.util.List;

import scw.lang.Nullable;

public interface MarkerService<K> {
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
	 * @param queryNearby
	 * @return
	 */
	List<Marker<K>> getNearbyMarkers(QueryNearby queryNearby);
}
