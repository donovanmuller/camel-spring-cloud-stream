package io.switchbit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.HeaderMode;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.ChannelBindingServiceProperties;
import org.springframework.integration.expression.ValueExpression;

public class CamelConfigurableBindingServicePropertiesTest {

	@Test
	public void testHonourBindingPropertiesDefaultBehaviour() {
		ChannelBindingServiceProperties ChannelBindingServiceProperties = new ChannelBindingServiceProperties();
		ChannelBindingServiceProperties.setInstanceCount(2);
		ChannelBindingServiceProperties.setInstanceIndex(1);
		CamelConfigurableBindingServiceProperties properties = new CamelConfigurableBindingServiceProperties(
				ChannelBindingServiceProperties);
		properties.setCamelConfiguration(new SpringCloudStreamConfiguration(), "input");

		assertThat(properties.getInstanceCount()).isEqualTo(2);
		assertThat(properties.getInstanceIndex()).isEqualTo(1);
	}

	@Test
	public void testBindingPropertiesOverriddenByCamelConfiguration() {
		ChannelBindingServiceProperties ChannelBindingServiceProperties = new ChannelBindingServiceProperties();
		ChannelBindingServiceProperties.setInstanceCount(2);
		ChannelBindingServiceProperties.setInstanceIndex(1);
		ChannelBindingServiceProperties.setDefaultBinder("default");
		CamelConfigurableBindingServiceProperties properties = new CamelConfigurableBindingServiceProperties(
				ChannelBindingServiceProperties);
		SpringCloudStreamConfiguration camelConfiguration = new SpringCloudStreamConfiguration();
		camelConfiguration.setInstanceCount(1);
		camelConfiguration.setInstanceIndex(0);
		properties.setCamelConfiguration(camelConfiguration, "input");

		assertThat(properties.getInstanceCount()).isEqualTo(1);
		assertThat(properties.getInstanceIndex()).isEqualTo(0);
		assertThat(properties.getDefaultBinder()).isEqualTo("default");
	}

	@Test
	public void testConsumerPropertiesDefaultBehaviour() {
		ChannelBindingServiceProperties ChannelBindingServiceProperties = new ChannelBindingServiceProperties();
		Map<String, BindingProperties> bindingProperties = new HashMap<>();

		BindingProperties binding1Properties = new BindingProperties();
		binding1Properties.setContentType("application/json");
		ConsumerProperties binding1ConsumerProperties = new ConsumerProperties();
		binding1ConsumerProperties.setInstanceCount(1);
		binding1ConsumerProperties.setMaxAttempts(1);
		binding1Properties.setConsumer(binding1ConsumerProperties);
		bindingProperties.put("binding1", binding1Properties);

		BindingProperties binding2Properties = new BindingProperties();
		binding2Properties.setContentType("application/xml");
		ConsumerProperties binding2ConsumerProperties = new ConsumerProperties();
		binding2ConsumerProperties.setInstanceCount(2);
		binding2ConsumerProperties.setConcurrency(1);
		binding2Properties.setConsumer(binding2ConsumerProperties);
		bindingProperties.put("binding2", binding2Properties);

		ChannelBindingServiceProperties.setBindings(bindingProperties);

		CamelConfigurableBindingServiceProperties properties = new CamelConfigurableBindingServiceProperties(
				ChannelBindingServiceProperties);
		SpringCloudStreamConfiguration camelConfiguration = new SpringCloudStreamConfiguration();
		camelConfiguration.setInstanceIndex(1);
		camelConfiguration.setMaxAttempts(2);
		camelConfiguration.setConcurrency(2);
		properties.setCamelConfiguration(camelConfiguration, "input");

		binding1ConsumerProperties = properties.getConsumerProperties("binding1");

		assertThat(binding1ConsumerProperties.getInstanceCount()).isEqualTo(1);
		assertThat(binding1ConsumerProperties.getInstanceIndex()).isEqualTo(1);
		assertThat(binding1ConsumerProperties.getMaxAttempts()).isEqualTo(2);
		assertThat(properties.getBindingProperties("binding1").getContentType())
				.isEqualTo("application/json");

		binding2ConsumerProperties = properties.getConsumerProperties("binding2");

		assertThat(binding2ConsumerProperties.getInstanceCount()).isEqualTo(2);
		assertThat(binding1ConsumerProperties.getInstanceIndex()).isEqualTo(1);
		assertThat(binding2ConsumerProperties.getConcurrency()).isEqualTo(2);
		assertThat(properties.getBindingProperties("binding2").getContentType())
				.isEqualTo("application/xml");
	}

	@Test
	public void testProducerPropertiesDefaultBehaviour() {
		ChannelBindingServiceProperties ChannelBindingServiceProperties = new ChannelBindingServiceProperties();
		Map<String, BindingProperties> bindingProperties = new HashMap<>();

		BindingProperties binding1Properties = new BindingProperties();
		binding1Properties.setContentType("application/json");
		ProducerProperties binding1ProducerProperties = new ProducerProperties();
		binding1ProducerProperties.setPartitionCount(2);
		binding1ProducerProperties
				.setPartitionKeyExpression(new ValueExpression<>("payload"));
		binding1Properties.setProducer(binding1ProducerProperties);
		bindingProperties.put("binding1", binding1Properties);

		BindingProperties binding2Properties = new BindingProperties();
		binding2Properties.setContentType("application/xml");
		ProducerProperties binding2ProducerProperties = new ProducerProperties();
		binding2ProducerProperties.setHeaderMode(HeaderMode.raw);
		binding2Properties.setProducer(binding2ProducerProperties);
		bindingProperties.put("binding2", binding2Properties);

		ChannelBindingServiceProperties.setBindings(bindingProperties);

		CamelConfigurableBindingServiceProperties properties = new CamelConfigurableBindingServiceProperties(
				ChannelBindingServiceProperties);
		SpringCloudStreamConfiguration camelConfiguration = new SpringCloudStreamConfiguration();
		camelConfiguration.setRequiredGroups(new String[] { "group1" });
		camelConfiguration.setHeaderMode(HeaderMode.embeddedHeaders);
		properties.setCamelConfiguration(camelConfiguration, "input");

		binding1ProducerProperties = properties.getProducerProperties("binding1");

		assertThat(binding1ProducerProperties.getRequiredGroups()).containsOnly("group1");
		assertThat(binding1ProducerProperties.getPartitionCount()).isEqualTo(2);
		assertThat(binding1ProducerProperties.getPartitionKeyExpression()
				.getExpressionString()).isEqualTo("payload");
		assertThat(binding1ProducerProperties.getHeaderMode())
				.isEqualTo(HeaderMode.embeddedHeaders);
		assertThat(properties.getBindingProperties("binding1").getContentType())
				.isEqualTo("application/json");

		binding2ProducerProperties = properties.getProducerProperties("binding2");

		assertThat(binding2ProducerProperties.getRequiredGroups()).containsOnly("group1");
		assertThat(binding2ProducerProperties.getHeaderMode())
				.isEqualTo(HeaderMode.embeddedHeaders);
		assertThat(properties.getBindingProperties("binding2").getContentType())
				.isEqualTo("application/xml");
	}
}
