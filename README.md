# Spring Boot NATS Example

This examples demonstrates how to use the [Java NATS client](https://github.com/cloudfoundry-community/java-nats)
with [Spring Boot](https://github.com/SpringSource/spring-boot) by providing a NATS to web gateway. This allows
you watch all published NATS messages in your browser.

To run this example, you will need Java, Maven, and Ruby installed.

## Install NATS

To use this example, you will need to have NATS installed. To install NATS simply run (assuming you have Ruby installed
with the proper permissions to install Ruby Gems):

```
gem install nats
```

To test your NATS server, make sure you can run:

```
nats-server
```

## Install Java NATS Client

The support for using Spring's Java config in the Java NATS client hasn't yet been pushed up to Maven Central, so you
will need to check out the Java NATS client and compile it.

```
git clone https://github.com/cloudfoundry-community/java-nats.git
cd java-nats
mvn install
```

## Compile and run Spring Boot NATS Example

Now you need the code for this example. Simply clone it and compile it.

```
git clone https://github.com/mheath/spring-boot-nats-example
cd spring-boot-nats-example
mvn package
```

Now to run the example, you don't need an application server or any kind of servlet container. We're using Spring Boot
so you can simply run:

```
java -jar target/spring-boot-nats-example-0.1-SNAPSHOT.jar
```

You should see some logs indicating the the example has started.

To make sure the example is working, open another terminal and run:

```
nats-pub test "This is a test."
```

You should see in the example terminal that it received a NATS message.

Now point your browser to 'http://localhost:8080' and publish more NATS messages doing:

```
nats-pub spring "Spring Boo rocks."
nats-pub some.other.subject "Cloud Foundry makes me happy."
nats-pub heath "Mike Heath is my hero."
```

You should see each of these NATS messages appear in your browser.
