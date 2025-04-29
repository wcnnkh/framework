package run.soeasy.framework.messaging;

import run.soeasy.framework.core.convert.Data;

public interface Entity<T> extends Message {
	Data<T> getBody();
}
