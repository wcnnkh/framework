package io.basc.framework.redis;

public enum GeoResultWith {
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