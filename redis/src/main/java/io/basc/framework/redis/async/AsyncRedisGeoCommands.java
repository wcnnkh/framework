package io.basc.framework.redis.async;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Metric;
import io.basc.framework.data.geo.Point;
import io.basc.framework.redis.GeoRadiusArgs;
import io.basc.framework.redis.GeoRadiusWith;
import io.basc.framework.redis.GeoResultWith;
import io.basc.framework.redis.GeoWithin;
import io.basc.framework.redis.GeoaddOption;
import io.basc.framework.redis.GeoradiusStroage;
import io.basc.framework.util.comparator.Sort;

/**
 * https://redis.io/commands#geo
 * 
 * @author shuchaowen
 *
 */
@SuppressWarnings("unchecked")
public interface AsyncRedisGeoCommands<K, V> {
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
	default Response<Long> geoadd(K key, V member, Point point) {
		return geoadd(key, null, member, point);
	}

	default Response<Long> geoadd(K key, GeoaddOption option, V member, Point point) {
		return geoadd(key, option, Collections.singletonMap(member, point));
	}

	Response<Long> geoadd(K key, GeoaddOption option, Map<V, Point> members);

	/**
	 * https://redis.io/commands/geodist <br/>
	 * <br/>
	 * 
	 * @return Bulk string reply, specifically:
	 * 
	 *         The command returns the distance as a double (represented as a
	 *         string) in the specified unit, or NULL if one or both the elements
	 *         are missing.
	 */
	Response<Double> geodist(K key, V member1, V member2, Metric metric);

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
	Response<List<String>> geohash(K key, V... members);

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
	Response<List<Point>> geopos(K key, V... members);

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

	Response<Collection<V>> georadius(K key, Circle within, GeoRadiusArgs<K> args);

	Response<List<GeoWithin<V>>> georadius(K key, Circle within, GeoRadiusWith with, GeoRadiusArgs<K> args);

	/**
	 * https://redis.io/commands/georadiusbymember<br/>
	 * <br/>
	 * 
	 * @see #georadius(byte[], Circle, Integer, Sort, GeoradiusStroage,
	 *      GeoResultWith...)
	 * @return
	 */
	Response<List<V>> georadiusbymember(K key, V member, Distance distance, GeoRadiusArgs<K> args);

	Response<List<GeoWithin<V>>> georadiusbymember(K key, V member, Distance distance, GeoRadiusWith with, GeoRadiusArgs<K> args);
}
