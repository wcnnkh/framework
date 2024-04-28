package io.basc.framework.mapper.transfer;

import java.io.IOException;

public interface Importer {
	void doRead(Exporter exporter) throws IOException;
}
