package scw.redis.connection;

import scw.lang.Nullable;

public interface RedisValueEncoding {
	@Nullable
	String raw();

	/**
	 * Get the {@link RedisValueEncoding} for given {@code encoding}.
	 *
	 * @param encoding can be {@literal null}.
	 * @return never {@literal null}.
	 */
	static RedisValueEncoding of(@Nullable String encoding) {
		return RedisValueEncodings.lookup(encoding).orElse(() -> encoding);
	}
}
