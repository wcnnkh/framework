package scw.complete;

import java.io.Serializable;

public interface CompleteTask extends Serializable {
	Object process() throws Throwable;
}
