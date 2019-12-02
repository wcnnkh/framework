package scw.logger;

import scw.core.UnsafeStringBuffer;

public class ConsoleLoggerFactory extends AbstractMyLoggerFactory {

	public void destroy() {
	}

	@Override
	public void log(Message message) {
		try {
			console(new UnsafeStringBuffer(), message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
