package io.basc.framework.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.util.CollectionUtils;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.XClaimParams;

public final class JedisUtils {
	private JedisUtils() {
	}

	public static ScanParams toScanParams(ScanOptions<byte[]> options) {
		ScanParams scanParams = new ScanParams();
		if (options != null) {
			scanParams.match(options.getPattern());
			if (options.getCount() != null) {
				scanParams.count(options.getCount().intValue());
			}
		}
		return scanParams;
	}
	
	public static XClaimParams toXClaimParams(ClaimArgs args) {
		XClaimParams params = new XClaimParams();
		if (args != null) {
			if (args.getIdle() != null) {
				params.idle(args.getIdle());
			}

			if (args.getTime() != null) {
				params.time(args.getTime());
			}

			if (args.getRetryCount() != null) {
				params.retryCount(args.getRetryCount());
			}

			if (args.isForce()) {
				params.force();
			}
		}
		return params;
	}
	
	public static GeoAddParams toGeoAddParams(GeoaddOption option) {
		GeoAddParams params = new GeoAddParams();
		switch (option) {
		case CH:
			params.ch();
			break;
		case NX:
			params.nx();
			break;
		case XX:
			params.xx();
			break;
		default:
			break;
		}
		return params;
	}
	
	public static Map<byte[], GeoCoordinate> toMemberCoordinateMap(Map<byte[], Point> members){
		Map<byte[], GeoCoordinate> memberCoordinateMap = new HashMap<byte[], GeoCoordinate>(members.size());
		for (Entry<byte[], Point> entry : members.entrySet()) {
			GeoCoordinate coordinate = new GeoCoordinate(entry.getValue().getX(), entry.getValue().getY());
			memberCoordinateMap.put(entry.getKey(), coordinate);
		}
		return memberCoordinateMap;
	}
	
	public static byte[][] toPairsArgs(Map<byte[], byte[]> pairs) {
		if (CollectionUtils.isEmpty(pairs)) {
			return new byte[0][0];
		}
		List<byte[]> args = new ArrayList<byte[]>();
		for (Entry<byte[], byte[]> entry : pairs.entrySet()) {
			args.add(entry.getKey());
			args.add(entry.getValue());
		}
		return args.toArray(new byte[0][0]);
	}
}
