package io.switchbit;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.SubscribableChannel;

public class SpringCloudStreamProducer extends DefaultProducer {

	private SpringCloudStreamEndpoint endpoint;

	public SpringCloudStreamProducer(SpringCloudStreamEndpoint endpoint) {
		super(endpoint);

		this.endpoint = endpoint;
	}

	public void process(Exchange exchange) throws Exception {
		SubscribableChannel channel;

		// reuse the existing binding target
		if (endpoint.getBeanFactory().containsBean(endpoint.getDestination())) {
			channel = endpoint.getBeanFactory().getBean(endpoint.getDestination(),
					SubscribableChannel.class);
		}
		else {
			channel = createOutputBindingTarget();
			endpoint.getBindingService().bindProducer(channel, endpoint.getDestination());
		}

		Message message = exchange.getIn();
		if (message.getBody() != null) {
			channel.send(MessageBuilder.withPayload(message.getBody())
					.copyHeadersIfAbsent(message.getHeaders()).build());
		}
		else {
			log.warn("Message body is null, ignoring");
		}
	}

	/**
	 * Create a {@link SubscribableChannel} and register in the
	 * {@link org.springframework.context.ApplicationContext}
	 */
	private SubscribableChannel createOutputBindingTarget() {
		SubscribableChannel channel = (SubscribableChannel) endpoint
				.getBindingTargetFactory().createOutput(endpoint.getDestination());
		endpoint.getBeanFactory().registerSingleton(endpoint.getDestination(), channel);
		channel = (SubscribableChannel) endpoint.getBeanFactory().initializeBean(channel,
				endpoint.getDestination());
		return channel;
	}

}
