package io.basc.framework.jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.redis.ScoreOption;
import io.basc.framework.redis.SetOption;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.XClaimParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.resps.GeoRadiusResponse;

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

	public static Map<byte[], GeoCoordinate> toMemberCoordinateMap(Map<byte[], Point> members) {
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

	public static GeoUnit toGeoUnit(Metric metric) {
		if (metric == null) {
			return GeoUnit.M;
		}

		String name = metric.getAbbreviation();
		if (StringUtils.isEmpty(name)) {
			return GeoUnit.M;
		}
		return GeoUnit.valueOf(name.toUpperCase());
	}

	public static GeoRadiusParam toGeoRadiusParam(GeoRadiusWith with, GeoRadiusArgs<byte[]> args) {
		GeoRadiusParam param = new GeoRadiusParam();
		Integer count = args.getCount();
		if (count != null) {
			param.count(args.getCount());
		}

		Sort sort = args.getSort();
		if (sort != null) {
			switch (sort) {
			case ASC:
				param.sortAscending();
				break;
			case DESC:
				param.sortDescending();
				break;
			default:
				break;
			}
		}

		if (with != null) {
			if (with.isWithCoord()) {
				param.withCoord();
			}

			if (with.isWithDist()) {
				param.withCoord();
			}

			if (with.isWithHash()) {
				param.withHash();
			}
		}
		return param;
	}

	public static List<byte[]> toGeoMembers(List<GeoRadiusResponse> list) {
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		List<byte[]> members = new ArrayList<byte[]>();
		for (GeoRadiusResponse radiusResponse : list) {
			members.add(radiusResponse.getMember());
		}
		return members;
	}

	public static List<GeoWithin<byte[]>> toGeoWithins(List<GeoRadiusResponse> list) {
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}

		List<GeoWithin<byte[]>> members = new ArrayList<GeoWithin<byte[]>>();
		for (GeoRadiusResponse radiusResponse : list) {
			GeoWithin<byte[]> geoWithin = new GeoWithin<byte[]>(radiusResponse.getMember(),
					radiusResponse.getDistance(), null, toPoint(radiusResponse.getCoordinate()));
			members.add(geoWithin);
		}
		return members;
	}

	public static Point toPoint(GeoCoordinate coordinate) {
		if (coordinate == null) {
			return null;
		}
		return new Point(coordinate.getLongitude(), coordinate.getLatitude());
	}

	public static ZAddParams toZAddParams(SetOption setOption, ScoreOption scoreOption, boolean changed) {
		ZAddParams params = new ZAddParams();
		if (setOption != null) {
			switch (setOption) {
			case NX:
				params.nx();
				break;
			case XX:
				params.xx();

			default:
				break;
			}
		}

		if (scoreOption != null) {
			switch (scoreOption) {
			case GT:
				params.gt();
				break;
			case LT:
				params.lt();
			default:
				break;
			}
		}

		if (changed) {
			params.ch();
		}
		return params;
	}

	public static Boolean parseBoolean(String response) {
		return response == null ? null : "OK".equalsIgnoreCase(response);
	}

	public static List<Point> toPoints(List<GeoCoordinate> list) {
		List<Point> points = new ArrayList<Point>();
		for (GeoCoordinate geo : list) {
			points.add(new Point(geo.getLatitude(), geo.getLongitude()));
		}
		return points;
	}
}
