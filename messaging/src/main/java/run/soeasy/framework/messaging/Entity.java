package run.soeasy.framework.messaging;

import run.soeasy.framework.core.convert.value.TypedData;

public interface Entity<T> extends Message {
	TypedData<T> getBody();
}
