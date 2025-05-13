package run.soeasy.framework.messaging;

import run.soeasy.framework.core.convert.value.ConvertingData;

public interface Entity<T> extends Message {
	ConvertingData<T> getBody();
}
