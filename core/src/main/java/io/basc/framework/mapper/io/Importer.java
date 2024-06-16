package io.basc.framework.mapper.io;

import java.io.IOException;

public interface Importer {

	void doRead(Exporter exporter) throws IOException;

}
