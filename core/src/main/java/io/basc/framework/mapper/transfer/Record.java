package io.basc.framework.mapper.transfer;

import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.execution.Parameters;
import lombok.Data;

@Data
public class Record {
	private final Parameters parameters;
	private final ReversibleConverter<? super Parameters, ? extends Object, ? extends RuntimeException> converter;
}
