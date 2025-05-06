package run.soeasy.framework.messaging;

import run.soeasy.framework.core.convert.ConvertingData;

public interface Entity<T> extends Message {
	ConvertingData<T> getBody();
}
