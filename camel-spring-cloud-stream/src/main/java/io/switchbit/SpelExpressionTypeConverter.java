package io.switchbit;

import org.apache.camel.Converter;
import org.springframework.cloud.stream.config.SpelExpressionConverterConfiguration;
import org.springframework.expression.Expression;

/**
 * Type converter for
 * {@link io.switchbit.SpringCloudStreamConfiguration#partitionKeyExpression}.
 */
@Converter
public class SpelExpressionTypeConverter {

	private SpelExpressionTypeConverter() {
	}

	@Converter
	public static Expression toSpelExpression(String expression) {
		return new SpelExpressionConverterConfiguration.SpelConverter()
				.convert(expression);
	}

	@Converter
	public static String toString(Expression expression) {
		return expression.getExpressionString();
	}
}
