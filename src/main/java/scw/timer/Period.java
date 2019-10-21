package scw.timer;

import java.util.concurrent.TimeUnit;

public interface Period {
	long getPeriod(TimeUnit timeUnit);
}
