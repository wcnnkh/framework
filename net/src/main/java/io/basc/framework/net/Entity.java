package io.basc.framework.net;

import io.basc.framework.core.convert.Data;

public interface Entity<T> extends Message {
	Data<T> getBody();
}
