package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.util.Assert;

public class DefaultExporter<E> implements Exporter {
	private final Supplier<Stream<? extends E>> streamSupplier;
	private final ExportProcessor<E> processor;

	public DefaultExporter(ExportProcessor<E> processor, Supplier<Stream<? extends E>> streamSupplier) {
		Assert.requiredArgument(processor != null, "processor");
		Assert.requiredArgument(streamSupplier != null, "streamSupplier");
		this.processor = processor;
		this.streamSupplier = streamSupplier;
	}

	@Override
	public void export(File target) throws IOException {
		Stream<? extends E> stream = streamSupplier.get();
		try {
			processor.process(stream.iterator(), target);
		} finally {
			stream.close();
		}
	}

}
