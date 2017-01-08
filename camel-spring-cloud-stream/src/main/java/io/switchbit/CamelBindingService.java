package io.switchbit;

import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.binding.ChannelBindingService;
import org.springframework.cloud.stream.config.ChannelBindingServiceProperties;
import org.springframework.messaging.MessageChannel;

/**
 * A wrapper around {@link org.springframework.cloud.stream.binding.ChannelBindingService}
 * that allows {@link ChannelBindingServiceProperties} to be wrapped by
 * {@link CamelConfigurableBindingServiceProperties}, also providing a getter for the
 * wrapped {@link ChannelBindingServiceProperties} instance.
 */
public class CamelBindingService extends ChannelBindingService {

	public CamelBindingService(
			ChannelBindingServiceProperties ChannelBindingServiceProperties,
			BinderFactory<MessageChannel> binderFactory) {
		super(new CamelConfigurableBindingServiceProperties(
				ChannelBindingServiceProperties), binderFactory);
	}

	@Override
	public CamelConfigurableBindingServiceProperties getChannelBindingServiceProperties() {
		return (CamelConfigurableBindingServiceProperties) super.getChannelBindingServiceProperties();
	}
}
