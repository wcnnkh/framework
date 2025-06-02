package run.soeasy.framework.dom.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.SystemConversionService;

@Getter
@Setter
public abstract class AbstractNodeWriter implements NodeWriter, ConversionServiceAware, NodeWriterAware {
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();
	@NonNull
	private NodeWriter nodeWriter = this;
}
