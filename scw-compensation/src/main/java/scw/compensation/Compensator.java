package scw.compensation;

import java.util.concurrent.Future;


public interface Compensator extends Runnable, Future<Object> {
}
