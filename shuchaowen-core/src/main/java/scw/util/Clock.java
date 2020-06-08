package scw.util;

public interface Clock {
	long currentTimeMillis();

	Clock SYSTEM = new Clock() {

		public long currentTimeMillis() {
			return System.currentTimeMillis();
		}
	};
}
