package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.util.CollectionUtils;

@FunctionalInterface
public interface ExportProcessor<S> {
	void process(Iterator<? extends S> source, File target) throws IOException;

	default void process(Collection<? extends S> source, File target) throws IOException {
		if (CollectionUtils.isEmpty(source)) {
			return;
		}

		process(source.iterator(), target);
	}
}
