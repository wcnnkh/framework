package io.basc.framework.data.transfer;

import java.io.IOException;

import io.basc.framework.io.WritableResource;

public interface Exporter {
	void doWrite(WritableResource target) throws IOException;
}
