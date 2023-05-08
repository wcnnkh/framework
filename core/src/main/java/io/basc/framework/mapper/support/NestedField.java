package io.basc.framework.mapper.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.Setter;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NestedField implements Field {
	private final Field parent;
	private final Field current;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Elements<? extends Getter> getGetters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Elements<? extends Setter> getSetters() {
		// TODO Auto-generated method stub
		return null;
	}

}
