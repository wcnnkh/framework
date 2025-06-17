package run.soeasy.framework.dom.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.support.SystemConversionService;

@Getter
@Setter
public abstract class AbstractNodeWriter implements NodeWriter, ConverterAware, NodeWriterAware {
	@NonNull
	private Converter converter = SystemConversionService.getInstance();
	@NonNull
	private NodeWriter nodeWriter = this;
}
