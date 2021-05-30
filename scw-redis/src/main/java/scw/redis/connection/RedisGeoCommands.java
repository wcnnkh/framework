package scw.redis.connection;

import java.util.List;

import scw.data.geo.Circle;
import scw.data.geo.Distance;
import scw.data.geo.Marker;
import scw.data.geo.Point;
import scw.util.comparator.OrderBy;

/**
 * https://redis.io/commands#geo
 * @author shuchaowen
 *
 */
public interface RedisGeoCommands<K, V> {
	/**
	 * Note: The XX and NX options are mutually exclusive.
	 * 
	 * @author shuchaowen
	 *
	 */
	static enum GeoaddOption {
		/**
		 * Only update elements that already exist. Never add elements.
		 */
		XX,
		/**
		 * Don't update already existing elements. Always add new elements.
		 */
		NX,
		/**
		 * Modify the return value from the number of new elements added, to the total
		 * number of elements changed (CH is an abbreviation of changed). Changed
		 * elements are new elements added and elements already existing for which the
		 * coordinates was updated. So elements specified in the command line having the
		 * same score as they had in the past are not counted. Note: normally, the
		 * return value of GEOADD only counts the number of new elements added.
		 */
		CH
	}

	/**
	 * https://redis.io/commands/geoadd
	 * 
	 * @param key
	 * @param option
	 * @param elements
	 * @return Integer reply, specifically:
	 * 
	 *         When used without optional arguments, the number of elements added to
	 *         the sorted set (excluding score updates). If the CH option is
	 *         specified, the number of elements that were changed (added or
	 *         updated).
	 */
	Long geoadd(K key, GeoaddOption option, @SuppressWarnings("unchecked") Marker<V>... elements);

	/**
	 * https://redis.io/commands/geodist <br/>
	 * <br/>
	 * 
	 * @param key
	 * @param member1
	 * @param member2
	 * @param metric
	 * @return Bulk string reply, specifically:
	 * 
	 *         The command returns the distance as a double (represented as a
	 *         string) in the specified unit, or NULL if one or both the elements
	 *         are missing.
	 */
	Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit);

	/**
	 * https://redis.io/commands/geohash<br/>
	 * <br/>
	 * Return valid Geohash strings representing the position of one or more
	 * elements in a sorted set value representing a geospatial index (where
	 * elements were added using GEOADD).
	 * 
	 * Normally Redis represents positions of elements using a variation of the
	 * Geohash technique where positions are encoded using 52 bit integers. The
	 * encoding is also different compared to the standard because the initial min
	 * and max coordinates used during the encoding and decoding process are
	 * different. This command however returns a standard Geohash in the form of a
	 * string as described in the Wikipedia article and compatible with the
	 * geohash.org web site.
	 * 
	 * Geohash string properties The command returns 11 characters Geohash strings,
	 * so no precision is loss compared to the Redis internal 52 bit representation.
	 * The returned Geohashes have the following properties:
	 * 
	 * They can be shortened removing characters from the right. It will lose
	 * precision but will still point to the same area. It is possible to use them
	 * in geohash.org URLs such as http://geohash.org/<geohash-string>. This is an
	 * example of such URL. Strings with a similar prefix are nearby, but the
	 * contrary is not true, it is possible that strings with different prefixes are
	 * nearby too.
	 * 
	 * @param key
	 * @param members
	 * @return Array reply, specifically:
	 * 
	 *         The command returns an array where each element is the Geohash
	 *         corresponding to each member name passed as argument to the command.
	 */
	List<String> geohash(K key, V... members);

	/**
	 * https://redis.io/commands/geopos<br/>
	 * <br/>
	 * Return the positions (longitude,latitude) of all the specified members of the
	 * geospatial index represented by the sorted set at key.
	 * 
	 * Given a sorted set representing a geospatial index, populated using the
	 * GEOADD command, it is often useful to obtain back the coordinates of
	 * specified members. When the geospatial index is populated via GEOADD the
	 * coordinates are converted into a 52 bit geohash, so the coordinates returned
	 * may not be exactly the ones used in order to add the elements, but small
	 * errors may be introduced.
	 * 
	 * The command can accept a variable number of arguments so it always returns an
	 * array of positions even when a single element is specified.
	 * 
	 * @param key
	 * @param members
	 * @return Array reply, specifically:
	 * 
	 *         The command returns an array where each element is a two elements
	 *         array representing longitude and latitude (x,y) of each member name
	 *         passed as argument to the command.
	 * 
	 *         Non existing elements are reported as NULL elements of the array.
	 * 
	 * 
	 */
	List<Point> geopos(K key, V... members);

	static class GeoradiusStroage {
		private final String option;
		private final byte[] key;

		public GeoradiusStroage(String option, byte[] key) {
			this.option = option;
			this.key = key;
		}

		public String getOption() {
			return option;
		}

		public byte[] getKey() {
			return key;
		}
	}

	static class GeoResults extends Circle {
		private static final long serialVersionUID = 1L;
		private final byte[] key;
		private final byte[] hash;

		public GeoResults(byte[] key, Distance distance, Point point, byte[] hash) {
			super(point, distance);
			this.key = key;
			this.hash = hash;
		}

		public byte[] getKey() {
			return key;
		}

		public byte[] getHash() {
			return hash;
		}
	}

	static enum GeoResultWith {
		/**
		 * 还返回返回的项目到指定中心的距离。距离以与指定为命令的radius参数的单位相同的单位返回。
		 */
		WITHDIST,
		/**
		 * 还返回匹配项的经度，纬度坐标。
		 */
		WITHCOORD,
		/**
		 * 还以52位无符号整数的形式返回该项目的原始geohash编码的排序集得分。这仅对低级黑客或调试有用，否则对于一般用户来说就没什么用了。
		 */
		WITHHASH
	}

	/**
	 * https://redis.io/commands/georadius
	 * 
	 * @param key
	 * @param within
	 * @param count
	 * @param orderBy ASC: Sort returned items from the nearest to the farthest,
	 *                relative to the center. DESC: Sort returned items from the
	 *                farthest to the nearest, relative to the center.
	 * @param store   By default the command returns the items to the client. It is
	 *                possible to store the results with one of these options:
	 * 
	 *                STORE: Store the items in a sorted set populated with their
	 *                geospatial information. STOREDIST: Store the items in a sorted
	 *                set populated with their distance from the center as a
	 *                floating point number, in the same unit specified in the
	 *                radius.
	 * @param withs   WITHDIST: Also return the distance of the returned items from
	 *                the specified center. The distance is returned in the same
	 *                unit as the unit specified as the radius argument of the
	 *                command. WITHCOORD: Also return the longitude,latitude
	 *                coordinates of the matching items. WITHHASH: Also return the
	 *                raw geohash-encoded sorted set score of the item, in the form
	 *                of a 52 bit unsigned integer. This is only useful for low
	 *                level hacks or debugging and is otherwise of little interest
	 *                for the general user.
	 * @return Array reply, specifically:
	 * 
	 *         Without any WITH option specified, the command just returns a linear
	 *         array like ["New York","Milan","Paris"]. If WITHCOORD, WITHDIST or
	 *         WITHHASH options are specified, the command returns an array of
	 *         arrays, where each sub-array represents a single item. When
	 *         additional information is returned as an array of arrays for each
	 *         item, the first item in the sub-array is always the name of the
	 *         returned item. The other information is returned in the following
	 *         order as successive elements of the sub-array.
	 * 
	 *         The distance from the center as a floating point number, in the same
	 *         unit specified in the radius. The geohash integer. The coordinates as
	 *         a two items x,y array (longitude,latitude).
	 */
	List<GeoResults> georadius(byte[] key, Circle within, Integer count, OrderBy orderBy, GeoradiusStroage store,
			GeoResultWith... withs);

	/**
	 * https://redis.io/commands/georadiusbymember<br/>
	 * <br/>
	 * 
	 * @see #georadius(byte[], Circle, Integer, OrderBy, GeoradiusStroage,
	 *      GeoResultWith...)
	 * @param key
	 * @param member
	 * @param distance
	 * @param count
	 * @param orderBy
	 * @param store
	 * @param withs
	 * @return
	 */
	List<GeoResults> georadiusbymember(byte[] key, byte[] member, Distance distance, Integer count, OrderBy orderBy,
			GeoradiusStroage store, GeoResultWith... withs);
}
