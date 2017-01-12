package io.switchbit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.stream.Collectors;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.matcher.MessageQueueMatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = SpringCloudStreamComponentTest.class)
public class SpringCloudStreamComponentTest {

	@EndpointInject(uri = "mock:input")
	private MockEndpoint inputMockEndpoint;

	@Autowired
	private ProducerTemplate producerTemplate;

	@Autowired
	@Qualifier("input")
	private SubscribableChannel inputChannel;

	@Autowired
	@Qualifier("processorInput")
	private SubscribableChannel processorInputChannel;

	@Autowired
	private MessageCollector messageCollector;

	@Autowired
	private ApplicationContext context;

	@Test
	public void testConsumer() throws InterruptedException {
		inputMockEndpoint.expectedBodiesReceived("test");

		inputChannel.send(MessageBuilder.withPayload("test").build());

		inputMockEndpoint.assertIsSatisfied();
	}

	@Test
	public void testProducer() {
		producerTemplate.sendBody("direct:input", "test1");
		producerTemplate.sendBody("direct:input", "test2");

		assertThat(messageCollector
				.forChannel(context.getBean("output", SubscribableChannel.class)).stream()
				.map(message -> message.getPayload().toString())
				.collect(Collectors.toList())).contains("test1", "test2");
	}

	@Test
	public void testProducerWithHeaders() {
		producerTemplate.sendBodyAndHeader("direct:input", "test", "header", "value");

		Message<?> message = messageCollector
				.forChannel(context.getBean("output", SubscribableChannel.class)).peek();
		assertThat(message.getPayload()).isEqualTo("test");
		assertThat(message.getHeaders()).containsEntry("header", "value");
	}

	@Test
	public void testConsumerAndProducer() throws InterruptedException {
		processorInputChannel.send(MessageBuilder.withPayload("test").build());

		SubscribableChannel processorOutput = context.getBean("processorOutput",
				SubscribableChannel.class);
		Assert.assertThat(messageCollector.forChannel(processorOutput),
				MessageQueueMatcher.receivesPayloadThat(is("test")).immediately());
	}

	@TestConfiguration
	@Import(SpringCloudStreamConfiguration.class)
	public static class SpringCloudStreamTestConfig {

		@Bean
		public RoutesBuilder sourceRoute() {
			return new RouteBuilder() {
				public void configure() {
					from("scst:input").to("mock:input");
				}
			};
		}

		@Bean
		public RoutesBuilder processorRoute() {
			return new RouteBuilder() {
				public void configure() {
					from("scst:processorInput")
							.process(exchange -> assertThat(
									exchange.getIn().getBody().equals("test")))
							.to("scst:processorOutput");
				}
			};
		}

		@Bean
		public RoutesBuilder sinkRoute() {
			return new RouteBuilder() {
				public void configure() {
					from("direct:input").to("scst:output");
				}
			};
		}
	}
}
