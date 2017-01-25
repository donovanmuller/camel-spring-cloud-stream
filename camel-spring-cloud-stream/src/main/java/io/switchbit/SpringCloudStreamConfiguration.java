package io.switchbit;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.binding.MessageConverterConfigurer;
import org.springframework.cloud.stream.config.BinderFactoryConfiguration;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.cloud.stream.config.ChannelBindingAutoConfiguration;
import org.springframework.cloud.stream.converter.CompositeMessageConverterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@AutoConfigureAfter(ChannelBindingAutoConfiguration.class)
@Import({ BinderFactoryConfiguration.class, ChannelBindingAutoConfiguration.class,
		BindingServiceConfiguration.class })
public class SpringCloudStreamConfiguration {

	@Bean
	public BindingService bindingService(
			BindingServiceProperties bindingServiceProperties,
			BinderFactory binderFactory) {
		return new CamelBindingService(bindingServiceProperties, binderFactory);
	}

	/**
	 * Use the {@link BindingServiceProperties} reference from the {@link BindingService}.
	 * This ensures that the wrapped ({@link CamelConfigurableBindingServiceProperties})
	 * binding properties are used by messageConverterConfigurer.
	 */
	@Bean
	public MessageConverterConfigurer messageConverterConfigurer(
			BindingService bindingService,
			CompositeMessageConverterFactory compositeMessageConverterFactory) {
		return new MessageConverterConfigurer(
				bindingService.getBindingServiceProperties(),
				compositeMessageConverterFactory);
	}
}
