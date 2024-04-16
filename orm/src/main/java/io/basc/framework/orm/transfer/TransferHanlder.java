package io.basc.framework.orm.transfer;

import java.io.IOException;

public interface TransferHanlder<T> {
	void handle(T data) throws IOException;
}
