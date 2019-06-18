package scw.application.crontab;

public class DefaultCrontabContext implements CrontabContext {

	public boolean begin() {
		return true;
	}

	public void end() {
	}

	public void error(Throwable e) {
	}

	public void completet() {
	}

}
