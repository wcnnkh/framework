package run.soeasy.framework.dom.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.DefaultConversionService;

@Getter
@Setter
public abstract class AbstractNodeWriter implements NodeWriter, ConversionServiceAware, NodeWriterAware {
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();
	@NonNull
	private NodeWriter nodeWriter = this;
}
