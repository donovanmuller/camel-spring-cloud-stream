package io.switchbit;

import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.springframework.cloud.stream.binder.HeaderMode;
import org.springframework.expression.Expression;

@UriParams
public class SpringCloudStreamEndpointConfiguration {

	@UriParam(description = "The consumer group of the channel. Applies only to inbound bindings.")
	private String group;

	@UriParam(description = "The content type of the channel.")
	private String contentType;

	@UriParam(description = "The binder used by this binding.")
	private String binder;

	@UriParam(description = "Consumer property: The concurrency of the inbound consumer.")
	private Integer concurrency;

	@UriParam(description = "Consumer property: Whether the consumer receives data from a partitioned producer.")
	private Boolean partitioned;

	@UriParam(description = "Consumer property: When set to raw, disables header parsing on input. "
			+ "Effective only for messaging middleware that does not support message headers natively "
			+ "and requires header embedding. Useful when inbound data is coming from "
			+ "outside Spring Cloud Stream applications.")
	private HeaderMode headerMode;

	@UriParam(description = "Consumer property: The number of attempts of re-processing an inbound message.")
	private Integer maxAttempts;

	@UriParam(description = "Consumer property: The backoff initial interval on retry.")
	private Integer backOffInitialInterval;

	@UriParam(description = "Consumer property: The maximum backoff interval.")
	private Integer backOffMaxInterval;

	@UriParam(description = "Consumer property: The backoff multiplier.")
	private Double backOffMultiplier;

	@UriParam(description = "Consumer property: When set to a value greater than equal to zero,"
			+ "allows customizing the instance index of this consumer "
			+ "(if different from spring.cloud.stream.instanceIndex). "
			+ "When set to a negative value, it will default to spring.cloud.stream.instanceIndex.")
	private Integer instanceIndex;

	@UriParam(description = "Consumer property: When set to a value greater than equal to zero, "
			+ "allows customizing the instance count of this consumer "
			+ "(if different from spring.cloud.stream.instanceCount). "
			+ "When set to a negative value, it will default to spring.cloud.stream.instanceCount.")
	private Integer instanceCount;

	@UriParam(description = "Producer property: A SpEL expression that determines how to partition outbound data."
			+ "If set, or if partitionKeyExtractorClass is set, "
			+ "outbound data on this channel will be partitioned, "
			+ "and partitionCount must be set to a value greater than 1 to be effective. ")
	private Expression partitionKeyExpression;

	@UriParam(description = "Producer property: A PartitionKeyExtractorStrategy implementation."
			+ "If set, or if partitionKeyExpression is set, outbound data on this channel will be partitioned,"
			+ "and partitionCount must be set to a value greater than 1 to be effective. "
			+ "The two options are mutually exclusive.")
	private Class<?> partitionKeyExtractorClass;

	@UriParam(description = "Producer property: A PartitionSelectorStrategy implementation."
			+ "Mutually exclusive with partitionSelectorExpression. "
			+ "If neither is set, the partition will be selected as the hashCode(key) % partitionCount,"
			+ "where key is computed via either partitionKeyExpression or partitionKeyExtractorClass.")
	private Class<?> partitionSelectorClass;

	@UriParam(description = "Producer property: A SpEL expression for customizing partition selection."
			+ "Mutually exclusive with partitionSelectorClass. If neither is set,"
			+ "the partition will be selected as the hashCode(key) % partitionCount,"
			+ "where key is computed via either partitionKeyExpression or partitionKeyExtractorClass.")
	private Expression partitionSelectorExpression;

	@UriParam(description = "Producer property: The number of target partitions for the data,"
			+ "if partitioning is enabled. Must be set to a value greater than 1 if the producer is partitioned."
			+ "On Kafka, interpreted as a hint;"
			+ "the larger of this and the partition count of the target topic is used instead.")
	private Integer partitionCount;

	@UriParam(description = "Producer property: A comma-separated list of groups to which the producer must"
			+ "ensure message delivery even if they start after it has been created"
			+ "(e.g., by pre-creating durable queues in RabbitMQ).")
	private String[] requiredGroups;

	@UriParam(description = "Only applicable to Spring Cloud Stream >= 1.2.0")
	private Boolean useNativeEncoding;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBinder() {
		return binder;
	}

	public void setBinder(String binder) {
		this.binder = binder;
	}

	public Integer getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(Integer concurrency) {
		this.concurrency = concurrency;
	}

	public Boolean getPartitioned() {
		return partitioned;
	}

	public void setPartitioned(Boolean partitioned) {
		this.partitioned = partitioned;
	}

	public Integer getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(Integer instanceCount) {
		this.instanceCount = instanceCount;
	}

	public Integer getInstanceIndex() {
		return instanceIndex;
	}

	public void setInstanceIndex(Integer instanceIndex) {
		this.instanceIndex = instanceIndex;
	}

	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public Integer getBackOffInitialInterval() {
		return backOffInitialInterval;
	}

	public void setBackOffInitialInterval(Integer backOffInitialInterval) {
		this.backOffInitialInterval = backOffInitialInterval;
	}

	public Integer getBackOffMaxInterval() {
		return backOffMaxInterval;
	}

	public void setBackOffMaxInterval(Integer backOffMaxInterval) {
		this.backOffMaxInterval = backOffMaxInterval;
	}

	public Double getBackOffMultiplier() {
		return backOffMultiplier;
	}

	public void setBackOffMultiplier(Double backOffMultiplier) {
		this.backOffMultiplier = backOffMultiplier;
	}

	public HeaderMode getHeaderMode() {
		return headerMode;
	}

	public void setHeaderMode(HeaderMode headerMode) {
		this.headerMode = headerMode;
	}

	public Expression getPartitionKeyExpression() {
		return partitionKeyExpression;
	}

	public void setPartitionKeyExpression(Expression partitionKeyExpression) {
		this.partitionKeyExpression = partitionKeyExpression;
	}

	public Class<?> getPartitionKeyExtractorClass() {
		return partitionKeyExtractorClass;
	}

	public void setPartitionKeyExtractorClass(Class<?> partitionKeyExtractorClass) {
		this.partitionKeyExtractorClass = partitionKeyExtractorClass;
	}

	public Class<?> getPartitionSelectorClass() {
		return partitionSelectorClass;
	}

	public void setPartitionSelectorClass(Class<?> partitionSelectorClass) {
		this.partitionSelectorClass = partitionSelectorClass;
	}

	public Expression getPartitionSelectorExpression() {
		return partitionSelectorExpression;
	}

	public void setPartitionSelectorExpression(Expression partitionSelectorExpression) {
		this.partitionSelectorExpression = partitionSelectorExpression;
	}

	public Integer getPartitionCount() {
		return partitionCount;
	}

	public void setPartitionCount(Integer partitionCount) {
		this.partitionCount = partitionCount;
	}

	public String[] getRequiredGroups() {
		return requiredGroups;
	}

	public void setRequiredGroups(String[] requiredGroups) {
		this.requiredGroups = requiredGroups;
	}

	public Boolean getUseNativeEncoding() {
		return useNativeEncoding;
	}

	public void setUseNativeEncoding(Boolean useNativeEncoding) {
		this.useNativeEncoding = useNativeEncoding;
	}
}
