package scw.application.crontab;

public interface CrontabContextFactory {
	CrontabContext getContext(String name);
}
