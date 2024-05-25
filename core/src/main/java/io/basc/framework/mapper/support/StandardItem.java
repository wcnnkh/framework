package io.basc.framework.mapper.support;

import io.basc.framework.mapper.property.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardItem extends StandardNamed implements Item {
	private int positionIndex = -1;

	@Override
	public boolean equals(Object o) {
		if (o instanceof Item) {
			Item item = (Item) o;
			if (item.getPositionIndex() == getPositionIndex()) {
				return true;
			}
		}
		return super.equals(o);
	}
}
