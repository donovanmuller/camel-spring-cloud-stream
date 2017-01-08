package io.switchbit;

import java.beans.FeatureDescriptor;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.ChannelBindingServiceProperties;

/**
 * A wrapper around
 * {@link org.springframework.cloud.stream.config.ChannelBindingServiceProperties} that
 * allows the properties provided in the endpoint URI (see
 * {@link SpringCloudStreamConfiguration}) to override the properties provided via Spring
 * Boot configuration.
 */
public class CamelConfigurableBindingServiceProperties
		extends ChannelBindingServiceProperties {

	private SpringCloudStreamConfiguration camelConfiguration;

	public CamelConfigurableBindingServiceProperties(
			ChannelBindingServiceProperties bindingServiceProperties) {
		BeanUtils.copyProperties(bindingServiceProperties, this);
	}

	@Override
	public ConsumerProperties getConsumerProperties(String inputBindingName) {
		ConsumerProperties consumerProperties = super.getConsumerProperties(
				inputBindingName);
		copyNonNullProperties(camelConfiguration, consumerProperties);

		return consumerProperties;
	}

	@Override
	public ProducerProperties getProducerProperties(String outputBindingName) {
		ProducerProperties producerProperties = super.getProducerProperties(
				outputBindingName);
		copyNonNullProperties(camelConfiguration, producerProperties);

		return producerProperties;
	}

	public void setCamelConfiguration(SpringCloudStreamConfiguration camelConfiguration,
			String bindingName) {
		this.camelConfiguration = camelConfiguration;
		copyNonNullProperties(camelConfiguration, this);

		BindingProperties bindingProperties = mergeBindingProperties(bindingName);
		mergeAndAddConsumerProperties(bindingName, bindingProperties);
		mergeAndAddProducerProperties(bindingName, bindingProperties);
	}

	private BindingProperties mergeBindingProperties(String bindingName) {
		BindingProperties bindingProperties = Optional
				.ofNullable(this.getBindingProperties(bindingName))
				.orElse(new BindingProperties());

		copyNonNullProperties(this.camelConfiguration, bindingProperties);
		return bindingProperties;
	}

	private void mergeAndAddConsumerProperties(String bindingName,
			BindingProperties bindingProperties) {
		ConsumerProperties consumerProperties = Optional
				.ofNullable(bindingProperties.getConsumer())
				.orElse(new ConsumerProperties());

		copyNonNullProperties(this.camelConfiguration, consumerProperties);

		bindingProperties.setConsumer(consumerProperties);
		this.getBindings().put(bindingName, bindingProperties);
	}

	private void mergeAndAddProducerProperties(String bindingName,
			BindingProperties bindingProperties) {
		ProducerProperties producerProperties = Optional
				.ofNullable(bindingProperties.getProducer())
				.orElse(new ProducerProperties());

		copyNonNullProperties(this.camelConfiguration, producerProperties);

		bindingProperties.setProducer(producerProperties);
		this.getBindings().put(bindingName, bindingProperties);
	}

	private void copyNonNullProperties(Object source, Object target) {
		BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
	}

	/**
	 * Builds an array of properties that should be ignored by
	 * {@link BeanUtils#copyProperties} because their values are null.
	 * <p>
	 * Credit to these SO answers:
	 * <ul>
	 * <li>http://stackoverflow.com/a/19739041/2408961</li>
	 * <li>http://stackoverflow.com/a/32066155/2408961</li>
	 * </ul>
	 */
	private String[] getNullPropertyNames(Object source) {
		final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
		return Stream.of(wrappedSource.getPropertyDescriptors())
				.map(FeatureDescriptor::getName).filter(propertyName -> wrappedSource
						.getPropertyValue(propertyName) == null)
				.toArray(String[]::new);
	}
}
