package io.switchbit;

import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.config.BindingServiceProperties;

/**
 * A wrapper around {@link org.springframework.cloud.stream.binding.BindingService} that
 * allows {@link BindingServiceProperties} to be wrapped by
 * {@link CamelConfigurableBindingServiceProperties}, also providing a getter for the
 * wrapped {@link BindingServiceProperties} instance.
 */
public class CamelBindingService extends BindingService {

	public CamelBindingService(BindingServiceProperties BindingServiceProperties,
			BinderFactory binderFactory) {
		super(new CamelConfigurableBindingServiceProperties(BindingServiceProperties),
				binderFactory);
	}

	@Override
	public CamelConfigurableBindingServiceProperties getBindingServiceProperties() {
		return (CamelConfigurableBindingServiceProperties) super.getBindingServiceProperties();
	}
}
