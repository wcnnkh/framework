package io.basc.framework.mapper.transfer;

import lombok.Getter;

@Getter
public abstract class AbstractRowExporter extends AbstractRecordExporter implements RowExporter {
	private final Titles titles = new Titles();
}
