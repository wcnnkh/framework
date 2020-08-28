package scw.logger;

public class ConsoleLoggerFactory extends AbstractConsoleLoggerFactory {

	public void destroy() {
	}

	@Override
	public void log(Message message) {
		try {
			console(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Appendable createAppendable() {
		return new StringBuilder();
	}
}
