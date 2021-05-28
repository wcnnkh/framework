package scw.redis.connection;

import scw.lang.Nullable;

/**
 * Options to be used for with {@literal SCAN} commands.
 */
public class ScanOptions<P> {

	private final @Nullable Long count;
	private final @Nullable P pattern;

	private ScanOptions(@Nullable Long count, @Nullable P pattern) {
		this.count = count;
		this.pattern = pattern;
	}

	public static <P> ScanOptionsBuilder<P> scanOptions() {
		return new ScanOptionsBuilder<P>();
	}

	@Nullable
	public Long getCount() {
		return count;
	}

	@Nullable
	public P getPattern() {
		return pattern;
	}

	public String toOptionString() {
		String params = "";

		if (this.count != null) {
			params += (", 'count', " + count);
		}
		if (this.pattern != null) {
			params += (", 'match' , '" + this.pattern + "'");
		}

		return params;
	}

	public static class ScanOptionsBuilder<P> {

		private @Nullable Long count;
		private @Nullable P pattern;

		/**
		 * Returns the current {@link ScanOptionsBuilder} configured with the given
		 * {@code count}.
		 *
		 * @param count
		 * @return
		 */
		public ScanOptionsBuilder<P> count(long count) {
			this.count = count;
			return this;
		}

		/**
		 * Returns the current {@link ScanOptionsBuilder} configured with the given
		 * {@code pattern}.
		 *
		 * @param pattern
		 * @return
		 */
		public ScanOptionsBuilder<P> match(P pattern) {
			this.pattern = pattern;
			return this;
		}

		/**
		 * Builds a new {@link ScanOptions} objects.
		 *
		 * @return a new {@link ScanOptions} objects.
		 */
		public ScanOptions<P> build() {
			return new ScanOptions<P>(count, pattern);
		}
	}
}
