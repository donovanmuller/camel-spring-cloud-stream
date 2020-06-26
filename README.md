# Camel Spring Cloud Stream [![Build Status](https://travis-ci.org/donovanmuller/camel-spring-cloud-stream.svg?branch=master)](https://travis-ci.org/donovanmuller/camel-spring-cloud-stream)

An Apache Camel component to add support for [Spring Cloud Stream](https://cloud.spring.io/spring-cloud-stream/).

## Quickstart

Add the [Bintray JCenter](https://bintray.com/bintray/jcenter)
repository in your Maven `pom.xml`

```xml
    ...
    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-donovanmuller-switchbit-public</id>
            <name>bintray</name>
            <url>http://dl.bintray.com/donovanmuller/switchbit-public</url>
        </repository>
    </repositories>
    ...
```

and then add the `camel-spring-cloud-stream-starter` dependency

```xml
    <dependency>
        <groupId>io.switchbit</groupId>
        <artifactId>camel-spring-cloud-stream-starter</artifactId>
        <version>0.13</version>
    </dependency>
```

next you can define Camel routes using the Camel Spring Cloud Stream endpoint.
The Spring Cloud Stream [programming model](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_programming_model)
comes with three predefined [interfaces](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#__literal_source_literal_literal_sink_literal_and_literal_processor_literal),
`Source`, `Processor` and `Sink`. The functionality of these interfaces can be replicated via the `scst` Camel endpoint.

### Source

```java
@Component
public class SourceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("twitter:timeline/home")
                .to("scst:output");
    }
}
```

in this example the [Camel Twitter](http://camel.apache.org/twitter.html)
component is used to stream statuses from a Twitter account. The individual status messages
are consumed by the `scst:output` producer endpoint, which leverages Spring Cloud Stream
to send the message out on the `ouput` channel, bound to a [binder](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_binders)
implementation.

### Processor

A `Processor` can be implemented as follows

```java
@Component
public class ProcessorRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("scst:input")
                .process(exchange -> ...)
                .to("scst:output");
    }
}
```

a message will be received via the `input` channel (from a binder implementation) via the `scst:input`
endpoint. From here normal processing can be handled via the multitude of [components](https://camel.apache.org/components.html)
and mechanisms available in Camel. Once processing has been performed, we can send the event out
on the `output` channel described by the `scst:output` Producer endpoint. This will then facilitate
sending the message over the binder implementation.

### Sink

Similarly, receiving a `Message` over a binder implementation, is as easy as

```java
@Component
public class SinkRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("scst:input")
                .log("${body}");
    }
}
```

in this example, the received message is simply logged.

## How it works

The Spring Cloud Stream [`@EnableBinding`](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_triggering_binding_via_literal_enablebinding_literal)
annotation kicks off the auto configuration steps that create and bind Spring Integration `MessageChannel`'s
to the corresponding annotated methods. The method parameters and return values are mapped
to both incoming and outgoing messages respectively, which are then sent over the channels.
These channels are in turn bound to binder implementations which facilitate sending/receiving messages
over the relevant messaging middleware (Kafka, RabbitMQ, etc.).

The Camel Spring Cloud Stream component does not rely on the auto configuration properties
that `@EnableBinding` initiates but rather creates and binds the `MessageChannel`'s
as well as invoking the binding mechanism. Essentially achieving the same outcome.

The input channel(s) are created and bound in [SpringCloudStreamConsumer](camel-spring-cloud-stream/src/main/java/io/switchbit/SpringCloudStreamConsumer.java)
when the consumer is started. The output channel(s) are lazily created and bound on processing the first `Exchange` in
[SpringCloudStreamProducer](camel-spring-cloud-stream/src/main/java/io/switchbit/SpringCloudStreamProducer.java).

## Configuration

Configuration of Spring Cloud Stream can be provided in two complimentary ways:

* via the current supported Spring Cloud Stream [configuration](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_configuration_options)
options
* as endpoint URI properties

All the Spring Cloud Stream configurations are supported as URI properties with the same name.
As an example, see below

```java
@Component
public class SourceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("twitter:timeline/home")
                .to("scst:output?spring.cloud.stream.bindings.output.contentType=application/json&spring.cloud.stream.bindings.output.destination=output-queue");
    }
}
```

## Sample application

See the repository below for a sample application that creates a stream with the following Spring Cloud Stream applications:

* Camel Twitter Source - Monitors a specific tweet for any retweets and creates an exchange for any new retweets
* Camel Tweet Processor - Renders a template, using the received message body, as the content of new tweet
* Camel Twitter Sink - Creates a new tweet on the configured users timeline with the content of the received message body

See the sample project here: https://github.com/donovanmuller/camel-twitter-stream

## More information

See the following blog post for more details: https://blog.switchbit.io/camel-spring-cloud-stream

## Known issues

* The Spring Boot Actuator `/metrics` endpoint does not currently include the created channel metrics - [#1](https://github.com/donovanmuller/camel-spring-cloud-stream/issues/1)
* Multiple destination values on the endpoint (`scdf:input1,input2`) is currently not supported
