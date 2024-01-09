package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.execution.ParameterExtractors;

public class AutowireParameterExtractors extends ParameterExtractors<BeanFactory>
		implements AutowireParameterExtractor {
	private static volatile AutowireParameterExtractors defaults;

	public static AutowireParameterExtractors defaults() {
		if (defaults == null) {
			synchronized (AutowireParameterExtractors.class) {
				if (defaults == null) {
					defaults = new AutowireParameterExtractors();
					defaults.registers(SPI.getServices(AutowireParameterExtractor.class));
				}
			}
		}
		return defaults;
	}
}
