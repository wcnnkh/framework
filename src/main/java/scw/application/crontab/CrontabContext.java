package scw.application.crontab;

public interface CrontabContext {
	boolean begin();

	void end();

	void error(Throwable e);

	void completet();
}
