package scw.util;

/**
 * 这只是一个抽象的状态，并没有实际实现
 * 
 * @author shuchaowen
 * @param <T>
 * @see DefaultStatus
 */
public interface Status<T> extends Supplier<T> {
	boolean isActive();
}
