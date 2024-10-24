package io.basc.framework.redis;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;

import java.util.Optional;

public enum RedisValueEncodings implements RedisValueEncoding {
	/**
	 * Normal string encoding.
	 */
	RAW("raw"), //
	/**
	 * 64 bit signed interval String representing an integer.
	 */
	INT("int"), //
	/**
	 * Space saving representation for small lists, hashes and sorted sets.
	 */
	ZIPLIST("ziplist"), //
	/**
	 * Encoding for large lists.
	 */
	LINKEDLIST("linkedlist"), //
	/**
	 * Space saving representation for small sets that contain only integers.酶
	 */
	INTSET("intset"), //
	/**
	 * Encoding for large hashes.
	 */
	HASHTABLE("hashtable"), //
	/**
	 * Encoding for sorted sets of any size.
	 */
	SKIPLIST("skiplist"), //
	/**
	 * No encoding present due to non existing key.
	 */
	VACANT(null);

	private final @Nullable String raw;

	RedisValueEncodings(@Nullable String raw) {
		this.raw = raw;
	}

	@Override
	public String raw() {
		return raw;
	}

	@Nullable
	public static Optional<RedisValueEncoding> lookup(@Nullable String encoding) {
		for (RedisValueEncoding valueEncoding : values()) {
			if (ObjectUtils.equals(valueEncoding.raw(), encoding)) {
				return Optional.of(valueEncoding);
			}
		}
		return Optional.empty();
	}
}
