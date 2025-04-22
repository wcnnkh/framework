package run.soeasy.framework.messaging;

import run.soeasy.framework.core.convert.value.Data;

public interface Entity<T> extends Message {
	Data<T> getBody();
}
