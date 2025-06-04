package run.soeasy.framework.core.io;

import java.io.IOException;

/**
 * 可导出的
 * 
 * @author soeasy.run
 *
 */
@FunctionalInterface
public interface Exportable {
	void export(Appendable target) throws IOException;
}
