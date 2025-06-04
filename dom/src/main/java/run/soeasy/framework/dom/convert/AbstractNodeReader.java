package run.soeasy.framework.dom.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.SystemConversionService;

@Getter
@Setter
public abstract class AbstractNodeReader implements NodeReader, ConversionServiceAware, NodeReaderAware {
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();
	private NodeReader nodeWriter = this;
}
