package run.soeasy.framework.net;

import run.soeasy.framework.core.convert.Data;

public interface Entity<T> extends Message {
	Data<T> getBody();
}
