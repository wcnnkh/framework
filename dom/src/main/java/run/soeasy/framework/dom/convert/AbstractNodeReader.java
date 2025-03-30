package run.soeasy.framework.dom.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.DefaultConversionService;

@Getter
@Setter
public abstract class AbstractNodeReader implements NodeReader, ConversionServiceAware, NodeReaderAware {
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();
	private NodeReader nodeWriter = this;
}
