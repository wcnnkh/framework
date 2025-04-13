package run.soeasy.framework.core.io;

import java.io.IOException;

/**
 * 可打印的
 * 
 * @author shuchaowen
 *
 */
@FunctionalInterface
public interface Printable<T extends Appendable> {
	/**
	 * 打印到指定输出
	 * 
	 * @param target
	 * @throws IOException
	 */
	void print(T target) throws IOException;
}
