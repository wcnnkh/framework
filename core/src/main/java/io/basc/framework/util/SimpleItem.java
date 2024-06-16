package io.basc.framework.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SimpleItem extends SimpleNamed implements Item {
	private int positionIndex = -1;

	public SimpleItem(int positionIndex) {
		this.positionIndex = positionIndex;
	}
	
	public SimpleItem(Named named) {
		super(named);
	}

	public SimpleItem(Item item) {
		this((Named) item);
		this.positionIndex = item.getPositionIndex();
	}
}
