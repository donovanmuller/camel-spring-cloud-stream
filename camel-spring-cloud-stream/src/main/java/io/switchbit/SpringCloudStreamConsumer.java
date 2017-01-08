package io.switchbit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.springframework.messaging.SubscribableChannel;

public class SpringCloudStreamConsumer extends DefaultConsumer {

	private final SpringCloudStreamEndpoint endpoint;

	public SpringCloudStreamConsumer(SpringCloudStreamEndpoint endpoint,
			Processor processor) {
		super(endpoint, processor);

		this.endpoint = endpoint;
	}

	@Override
	protected void doStart() throws Exception {
		SubscribableChannel bindingTarget = createInputBindingTarget();
		bindingTarget.subscribe(message -> {
			Exchange exchange = endpoint.createExchange();
			exchange.getIn().setHeaders(message.getHeaders());
			exchange.getIn().setBody(message.getPayload());
			try {
				getProcessor().process(exchange);
			}
			catch (Exception e) {
				log.error(String.format(
						"Could not process exchange for Spring Cloud Stream binding target '%s'",
						endpoint.getDestination()), e);
			}
		});

		endpoint.getBindingService().bindConsumer(bindingTarget,
				endpoint.getDestination());

		super.doStart();
	}

	/**
	 * Create a {@link SubscribableChannel} and register in the
	 * {@link org.springframework.context.ApplicationContext}
	 */
	private SubscribableChannel createInputBindingTarget() {
		SubscribableChannel channel = endpoint.getBindingTargetFactory()
				.createInputChannel(endpoint.getDestination());
		endpoint.getBeanFactory().registerSingleton(endpoint.getDestination(), channel);
		channel = (SubscribableChannel) endpoint.getBeanFactory().initializeBean(channel,
				endpoint.getDestination());
		return channel;
	}
}
