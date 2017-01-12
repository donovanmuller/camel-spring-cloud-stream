package io.switchbit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.binding.ChannelBindingService;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.expression.Expression;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = SpringCloudStreamComponentPropertiesTest.class, properties = {
		"spring.cloud.stream.bindings.propsInput.contentType=application/json",
		"spring.cloud.stream.bindings.propsInput.consumer.maxAttempts=2",
		"spring.cloud.stream.bindings.propsOutput.contentType=application/json",
		"spring.cloud.stream.bindings.propsOutput.producer.partitionCount=3" })
public class SpringCloudStreamComponentPropertiesTest {

	@EndpointInject(uri = "mock:input")
	private MockEndpoint inputMockEndpoint;

	@Autowired
	private ProducerTemplate producerTemplate;

	@Autowired
	@Qualifier("propsInput")
	private SubscribableChannel inputChannel;

	@Autowired
	private MessageCollector messageCollector;

	@Autowired
	private ChannelBindingService bindingService;

	@Autowired
	private ApplicationContext context;

	@Test
	public void testConsumer() throws InterruptedException {
		inputMockEndpoint.expectedBodiesReceived("{\"name\": \"test\"}");
		inputMockEndpoint.expectedHeaderReceived("contentType", "text/plain");

		inputChannel.send(MessageBuilder.withPayload("{\"name\": \"test\"}").build());

		inputMockEndpoint.assertIsSatisfied();

		ConsumerProperties consumerProperties = bindingService
				.getChannelBindingServiceProperties().getConsumerProperties("propsInput");
		assertThat(bindingService.getChannelBindingServiceProperties()
				.getBindingDestination("propsInput")).isEqualTo("propsInput");
		assertThat(consumerProperties.getInstanceIndex()).isEqualTo(1);
		assertThat(consumerProperties.getMaxAttempts()).isEqualTo(1);
	}

	@Test
	public void testProducer() throws InterruptedException {
		producerTemplate.sendBody("direct:input", "test");

		ProducerProperties producerProperties = bindingService
				.getChannelBindingServiceProperties()
				.getProducerProperties("propsOutput");
		assertThat(bindingService.getChannelBindingServiceProperties()
				.getBindingDestination("propsOutput")).isEqualTo("propsOutput");
		assertThat(producerProperties.getPartitionCount()).isEqualTo(2);
		assertThat(producerProperties.getPartitionKeyExpression())
				.isInstanceOf(Expression.class);

		assertThat(messageCollector
				.forChannel(context.getBean("propsOutput", SubscribableChannel.class))
				.peek().getHeaders()).contains(entry("partition", 0))
						.contains(entry("contentType", "application/json"));
	}

	@TestConfiguration
	@Import(SpringCloudStreamConfiguration.class)
	public static class SpringCloudStreamTestConfig {

		@Bean
		public RoutesBuilder inputRoute() {
			return new RouteBuilder() {
				public void configure() {
					from("scst:propsInput?instanceIndex=1&contentType=text/plain&maxAttempts=1")
							.to("mock:input");
				}
			};
		}

		@Bean
		public RoutesBuilder outputRoute() {
			return new RouteBuilder() {
				public void configure() {
					from("direct:input").to(
							"scst:propsOutput?partitionCount=2&partitionKeyExpression=payload");
				}
			};
		}
	}
}
