package run.soeasy.framework.core.invoke;

import java.util.Arrays;
import java.util.Iterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Args implements ParameterSource {
	@NonNull
	private final ParameterAccessor[] array;

	@Override
	public Iterator<ParameterAccessor> iterator() {
		return Arrays.asList(array).iterator();
	}

}
