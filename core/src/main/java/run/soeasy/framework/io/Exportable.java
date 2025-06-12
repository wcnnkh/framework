package run.soeasy.framework.io;

import java.io.IOException;

/**
 * 可导出的
 * 
 * @author soeasy.run
 *
 */
@FunctionalInterface
public interface Exportable {
	/**
	 * 导出
	 * 
	 * @param target
	 * @throws IOException
	 */
	void export(Appendable target) throws IOException;
}
