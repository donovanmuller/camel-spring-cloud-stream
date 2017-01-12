package io.switchbit;

import org.apache.camel.CamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SpringCloudStreamConfiguration.class)
public class SpringCloudStreamAutoConfiguration {

	@Bean
	public SpringCloudStreamComponent springCloudStreamComponent(
			CamelContext camelContext) {
		return new SpringCloudStreamComponent(camelContext);
	}
}
