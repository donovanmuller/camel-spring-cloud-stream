package io.switchbit;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.expression.Expression;

public class SpelExpressionTypeConverterTest extends CamelTestSupport {

	@Test
	public void testSpelConversion() {
		Expression expression = SpelExpressionTypeConverter
				.toSpelExpression("payload.id");

		assertNotNull(expression);
	}
}
