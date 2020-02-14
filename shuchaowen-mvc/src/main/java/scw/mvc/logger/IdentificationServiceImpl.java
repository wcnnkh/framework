package scw.mvc.logger;

import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.SimpleParameterConfig;
import scw.core.utils.StringUtils;
import scw.mvc.Action;
import scw.mvc.Channel;

public class IdentificationServiceImpl implements IdentificationService {
	private ParameterConfig identificationParameterConfig;

	public IdentificationServiceImpl(
			@ParameterName("mvc.logger.identification") @DefaultValue("uid") String identificationKey) {
		if (StringUtils.isNotEmpty(identificationKey)) {
			this.identificationParameterConfig = new SimpleParameterConfig(
					identificationKey, null, String.class, String.class);
		}
	}

	public String getIdentification(Action action, Channel channel) {
		return (String) channel.getParameter(identificationParameterConfig);
	}

}
