package io.basc.framework.jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.Aggregate;
import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.InsertPosition;
import io.basc.framework.redis.InterArgs;
import io.basc.framework.redis.MovePosition;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.redis.ScoreOption;
import io.basc.framework.redis.SetOption;
import io.basc.framework.redis.Tuple;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.comparator.Sort;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.args.BitOP;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.args.ListPosition;
import redis.clients.jedis.params.BitPosParams;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.MigrateParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.XClaimParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZParams;
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

	public static BitOP toBitOP(io.basc.framework.redis.BitOP bitOP) {
		switch (bitOP) {
		case AND:
			return BitOP.AND;
		case NOT:
			return BitOP.NOT;
		case OR:
			return BitOP.OR;
		case XOR:
			return BitOP.XOR;
		default:
			return null;
		}
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

	public static MigrateParams toMigrateParams(io.basc.framework.redis.MigrateParams params) {
		MigrateParams option = new MigrateParams();
		if (params.isCopy()) {
			option.copy();
		}

		if (params.isReplace()) {
			option.replace();
		}

		if (params.getUsername() != null) {
			option.auth2(params.getUsername(), params.getPassword());
		} else {
			option.auth(params.getPassword());
		}
		return option;
	}

	public static RestoreParams toRestoreParams(io.basc.framework.redis.RestoreParams option) {
		if (option == null) {
			return null;
		}
		RestoreParams params = new RestoreParams();
		if (option.isReplace()) {
			params.replace();
		}

		if (option.isAbsTtl()) {
			params.absTtl();
		}

		if (option.getIdleTime() != null) {
			params.idleTime(option.getIdleTime());
		}

		if (option.getFrequency() != null) {
			params.frequency(option.getFrequency());
		}
		return params;
	}

	public static ListDirection toListDirection(MovePosition position) {
		return position == MovePosition.LEFT ? ListDirection.LEFT : ListDirection.RIGHT;
	}

	public static ListPosition toListPosition(InsertPosition position) {
		if (position == null) {
			return null;
		}
		return position == InsertPosition.AFTER ? ListPosition.AFTER : ListPosition.BEFORE;
	}

	public static SetParams toSetParams(ExpireOption option, long time, SetOption setOption) {
		SetParams params = new SetParams();
		if (option != null) {
			switch (option) {
			case EX:
				params.ex(time);
				break;
			case EXAT:
				params.exAt(time);
				break;
			case PX:
				params.px(time);
				break;
			case PXAT:
				params.pxAt(time);
			default:
				break;
			}
		}

		if (setOption != null) {
			switch (setOption) {
			case NX:
				params.nx();
				break;
			case XX:
				params.xx();
				break;
			default:
				break;
			}
		}
		return params;
	}

	public static GetExParams toGetExParams(ExpireOption option, long time) {
		GetExParams params = new GetExParams();
		switch (option) {
		case EX:
			params.ex(time);
			break;
		case EXAT:
			params.exAt(time);
			break;
		case PX:
			params.px(time);
			break;
		case PXAT:
			params.pxAt(time);
		case PERSIST:
			params.persist();
			break;
		default:
			break;
		}
		return params;
	}

	public static BitPosParams toBitPosParams(Long start, Long end) {
		if (start == null && end == null) {
			return null;
		}

		if (start == null) {
			return new BitPosParams(0, end);
		}
		if (end == null) {
			return new BitPosParams(start);
		}

		return new BitPosParams(start, end);
	}

	public static ZParams toZParams(InterArgs args) {
		ZParams params = new ZParams();
		if (args != null) {
			double[] weights = args.getWeights();
			if (weights != null) {
				params.weights(weights);
			}

			Aggregate aggregate = args.getAggregate();
			if (aggregate != null) {
				switch (aggregate) {
				case MAX:
					params.aggregate(redis.clients.jedis.params.ZParams.Aggregate.MAX);
					break;
				case MIN:
					params.aggregate(redis.clients.jedis.params.ZParams.Aggregate.MIN);
					break;
				case SUM:
					params.aggregate(redis.clients.jedis.params.ZParams.Aggregate.SUM);
					break;
				default:
					break;
				}
			}
		}
		return params;
	}

	public static Collection<Tuple<byte[]>> toTuples(Collection<redis.clients.jedis.resps.Tuple> tuples) {
		if (CollectionUtils.isEmpty(tuples)) {
			return Collections.emptyList();
		}
		List<Tuple<byte[]>> list = new ArrayList<Tuple<byte[]>>(tuples.size());
		for (redis.clients.jedis.resps.Tuple tuple : tuples) {
			list.add(new Tuple<byte[]>(tuple.getBinaryElement(), tuple.getScore()));
		}
		return list;
	}
}
