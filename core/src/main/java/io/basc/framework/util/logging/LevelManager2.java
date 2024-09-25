package io.basc.framework.util.logging;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import lombok.NonNull;

public class LevelManager2 extends LevelEditor {

	public LevelManager2(@NonNull Publisher<? super Elements<ChangeEvent<String>>> publisher) {
		super(publisher);
	}
}
