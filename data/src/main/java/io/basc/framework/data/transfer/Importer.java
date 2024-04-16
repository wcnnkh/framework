package io.basc.framework.data.transfer;

import java.io.IOException;

public interface Importer {
	void doRead(TransferListener listener) throws IOException;
}
