package scw.search.location;

import java.util.List;

import scw.lang.Nullable;

/**
 * 位置服务
 * @author shuchaowen
 *
 */
public interface LocationService {
	/**
	 * 上报位置信息
	 * @param marker
	 */
	void report(Marker marker);
	
	@Nullable
	Marker getMarker(String id);
	
	boolean remove(String id);
	
	boolean exists(String id);
	
	/**
	 * 查询附近的点
	 * @param queryNearby
	 * @return
	 */
	List<Marker> getNearbyMarkers(QueryNearby queryNearby);
}
