package io.basc.framework.mapper.io.template;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRecordExporter implements RecordExporter {
	private RecordConverter converter = new DefaultRecordConverter();
}
