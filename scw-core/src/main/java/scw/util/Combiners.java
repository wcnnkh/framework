package scw.util;

import java.util.List;
import java.util.Properties;

public final class Combiners {
	private Combiners() {
		throw new RuntimeException();
	}

	public static final Combiner<Properties> PROPERTIES = new Combiner<Properties>() {

		@Override
		public Properties combine(List<Properties> list) {
			Properties properties = new Properties();
			for (Properties p : list) {
				if (p == null) {
					continue;
				}
				properties.putAll(p);
			}
			return properties;
		}
	};
}
