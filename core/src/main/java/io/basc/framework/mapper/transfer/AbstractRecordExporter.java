package io.basc.framework.mapper.transfer;

import io.basc.framework.beans.BeanUtils;
import io.basc.framework.mapper.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRecordExporter implements RecordExporter {
	@NonNull
	private ObjectMapper mapper = BeanUtils.getMapper();
}
