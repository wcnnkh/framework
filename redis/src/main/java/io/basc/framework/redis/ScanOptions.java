package io.basc.framework.redis;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Processor;

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

	public <T, E extends Throwable> ScanOptions<T> convert(Processor<? super P, ? extends T, ? extends E> converter)
			throws E {
		return new ScanOptions<T>(count, converter.process(pattern));
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

		public ScanOptionsBuilder<P> count(long count) {
			this.count = count;
			return this;
		}

		public ScanOptionsBuilder<P> match(P pattern) {
			this.pattern = pattern;
			return this;
		}

		public ScanOptions<P> build() {
			return new ScanOptions<P>(count, pattern);
		}
	}
}
