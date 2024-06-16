package io.basc.framework.mapper.io.template;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRecordImporter implements RecordImporter {
	@NonNull
	private RecordConverter converter = new DefaultRecordConverter();
}
