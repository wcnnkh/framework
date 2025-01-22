package io.basc.framework.beans;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.stractegy.DefaultTemplateTransformer;

public class ConfigurationPropertiesTransformer extends
		DefaultTemplateTransformer<Object, Value, Template<Object, ? extends Value>, Accessor, Template<Object, ? extends Accessor>, ConversionException> {
	@Override
	protected void doWrite(Template<Object, ? extends Value> template, Value source, Accessor target) {
		// TODO Auto-generated method stub
		super.doWrite(template, source, target);
	}
}
