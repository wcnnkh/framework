package io.basc.framework.core.execution.param;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class ParameterMatchingResults implements Comparable<ParameterMatchingResults> {
	@NonNull
	private Elements<ParameterMatched> elements = Elements.empty();
	private boolean successful;

	@Override
	public int compareTo(ParameterMatchingResults o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Elements<Object> getParameters() {
		return elements.filter((e) -> e.exists() && e.isSuccessful()).map((e) -> e.getValue());
	}
}
