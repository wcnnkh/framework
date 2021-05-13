package scw.search.location;

import java.util.List;

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
	
	/**
	 * 查询附近的点
	 * @param queryNearby
	 * @return
	 */
	List<Marker> getNearbyMarkers(QueryNearby queryNearby);
}
