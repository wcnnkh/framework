package scw.io;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface WriteReplaceInterface extends Serializable {
	public static final String WRITE_REPLACE_METHOD = "writeReplace";
	
	Object writeReplace() throws ObjectStreamException;
}
