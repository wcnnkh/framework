package io.basc.framework.core.execution.param;

import java.util.stream.IntStream;

import io.basc.framework.core.convert.Any;
import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class MainParameters implements Parameters {
	@NonNull
	private final String[] args;

	@Override
	public Elements<Parameter> getElements() {
		return Elements
				.of(() -> IntStream.range(0, args.length).mapToObj((index) -> new Arg(index, Any.of(args[index]))));
	}

	@Override
	public Elements<Object> getArgs() {
		return Elements.forArray((Object[]) args);
	}

}
