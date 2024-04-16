package io.basc.framework.data.transfer;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;

public interface TransferListener {
	void invoke(Object row, TypeDescriptor rowTypeDescriptor) throws IOException;

	void doAfterAllAnalysed();
}
