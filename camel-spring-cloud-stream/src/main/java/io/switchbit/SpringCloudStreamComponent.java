package io.switchbit;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.stream.binding.BindableChannelFactory;

public class SpringCloudStreamComponent extends UriEndpointComponent
		implements BeanFactoryAware {

	private ConfigurableListableBeanFactory beanFactory;

	@Autowired
	private CamelBindingService bindingService;

	@Autowired
	private BindableChannelFactory bindingTargetFactory;

	public SpringCloudStreamComponent() {
		super(SpringCloudStreamEndpoint.class);
	}

	public SpringCloudStreamComponent(CamelContext context) {
		super(context, SpringCloudStreamEndpoint.class);
	}

	@Override
	protected Endpoint createEndpoint(String uri, String remaining,
			Map<String, Object> parameters) throws Exception {
		Endpoint endpoint = new SpringCloudStreamEndpoint(uri, remaining, this,
				createConfiguration(parameters), beanFactory, bindingService,
				bindingTargetFactory);

		return endpoint;
	}

	private SpringCloudStreamConfiguration createConfiguration(
			Map<String, Object> parameters) throws Exception {
		SpringCloudStreamConfiguration configuration = new SpringCloudStreamConfiguration();
		setProperties(configuration, parameters);

		return configuration;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}
}
