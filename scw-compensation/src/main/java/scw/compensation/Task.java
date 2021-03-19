package scw.compensation;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface Task extends Callable<Object>, Serializable {
}
