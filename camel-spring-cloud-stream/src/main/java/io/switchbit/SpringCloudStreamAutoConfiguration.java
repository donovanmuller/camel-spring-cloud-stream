package io.switchbit;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.binding.ChannelBindingService;
import org.springframework.cloud.stream.binding.MessageConverterConfigurer;
import org.springframework.cloud.stream.config.BinderFactoryConfiguration;
import org.springframework.cloud.stream.config.ChannelBindingAutoConfiguration;
import org.springframework.cloud.stream.config.ChannelBindingServiceConfiguration;
import org.springframework.cloud.stream.config.ChannelBindingServiceProperties;
import org.springframework.cloud.stream.converter.CompositeMessageConverterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.MessageChannel;

@Configuration
@AutoConfigureAfter(ChannelBindingAutoConfiguration.class)
@Import({ BinderFactoryConfiguration.class, ChannelBindingAutoConfiguration.class,
		ChannelBindingServiceConfiguration.class })
public class SpringCloudStreamAutoConfiguration {

	@Bean
	public ChannelBindingService bindingService(
			ChannelBindingServiceProperties bindingServiceProperties,
			BinderFactory<MessageChannel> binderFactory) {
		return new CamelBindingService(bindingServiceProperties, binderFactory);
	}

	/**
	 * Use the {@link ChannelBindingServiceProperties} reference from the
	 * {@link ChannelBindingService}. This ensures that the wrapped
	 * ({@link CamelConfigurableBindingServiceProperties}) binding properties are used by
	 * messageConverterConfigurer.
	 */
	@Bean
	public MessageConverterConfigurer messageConverterConfigurer(
			ChannelBindingService bindingService,
			CompositeMessageConverterFactory compositeMessageConverterFactory) {
		return new MessageConverterConfigurer(
				bindingService.getChannelBindingServiceProperties(),
				compositeMessageConverterFactory);
	}
}
