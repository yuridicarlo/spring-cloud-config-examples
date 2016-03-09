# spring-cloud-config-examples
The two Spring projects included here are a Spring Cloud Config Server and a Spring Cloud Config Client, as described in the [Spring Cloud Config Documentation](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)

The configuration of the two projects serves as working example of using the Spring Cloud Config Server to externalize a client's configuration (like application.properties), and enable changes to that configuration at runtime, without having to restart/reload the client service. The mechanism works by utilizing the [Spring Cloud Bus](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html#_push_notifications_and_spring_cloud_bus)

The two maven projects here are just barebones Spring Boot Apps. The cloud-config-server project is just as described [here in the Spring documentation](http://cloud.spring.io/spring-cloud-config/), with the addition of the `spring-cloud-config-monitor` dependency--the need for which is described below in the Notes section

The spring-cloud-config-client configuration file is externalized via the Spring Cloud Config Server, and lives in this git repository as file [spring-cloud-config-client.properties](https://github.com/ldojo/spring-cloud-config-examples/blob/master/spring-cloud-config-client.properties). As you'll find in the [Spring Cloud Config Documentation](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html), you can have different profiles/labels of this configuration as needed for your project (spring-cloud-config-client-production.properties, spring-cloud-config-client-development.properties, etc)

The spring-cloud-config-client Spring Boot app is a very simple app that does two things:

1) it has an endpoint /testProperty, which outputs the current `test.property` value as found in the [project's configuration](https://github.com/ldojo/spring-cloud-config-examples/blob/master/spring-cloud-config-client.properties)

2) it runs a background scheduled job that logs the value of `test.property` on an interval defined by the `test.property.cron` value in the [project's configuration](https://github.com/ldojo/spring-cloud-config-examples/blob/master/spring-cloud-config-client.properties)

By looking at the output of 1), or the output of 2) in the logs, you can verify that changing the [project's configuration](https://github.com/ldojo/spring-cloud-config-examples/blob/master/spring-cloud-config-client.properties) at runtime changes the value and interval used by the running service. 

The relevant code for 1) and 2) is in [RuntimePropertyChangeExample.java](https://github.com/ldojo/spring-cloud-config-examples/blob/master/cloud-config-client/src/main/java/com/example/RuntimePropertyChangeExample.java)

Spring Cloud services talk to each other over Spring Cloud Bus, which passes messages under the hood over RabbitMQ. For the spring-cloud-config-server and spring-cloud-config-client services to talk to each other over Spring Cloud Bus, there is a running RabbitMQ instance at this address 52.87.208.69. You'll find this address is configured in both the spring-cloud-config-server's [application.properties](https://github.com/ldojo/spring-cloud-config-examples/blob/master/cloud-config-server/src/main/resources/application.properties) file, as well as the spring-cloud-config-client's [bootstrap.properties](https://github.com/ldojo/spring-cloud-config-examples/blob/master/cloud-config-client/src/main/resources/bootstrap.properties) file. To use this mechanism in your own project, you'll want to have your own instance of RabbitMQ running, and point the Spring Cloud Config Server and the Spring Cloud Config Client to it in their configuration files similarly.


To run the example:

`cd cloud-config-client`

`mvn clean package`

make sure you have java 8 to run this

`java -jar target/spring-cloud-config-client-0.0.1-SNAPSHOT.jar`

After the application loads, you should see output like this:
```
Mon Mar 07 17:25:00 CST 2016 test.property value is : change me
Mon Mar 07 17:25:10 CST 2016 test.property value is : change me
Mon Mar 07 17:25:20 CST 2016 test.property value is : change me
Mon Mar 07 17:25:30 CST 2016 test.property value is : change me
```

As shown, the value of `test.property` is logged every interval as configured in `test.property.cron` in [spring-cloud-config-client.properties](https://github.com/ldojo/spring-cloud-config-examples/blob/master/spring-cloud-config-client.properties)

If you were now to change the `test.property` or `test.property.cron` values in  [spring-cloud-config-client.properties](https://github.com/ldojo/spring-cloud-config-examples/blob/master/spring-cloud-config-client.properties), you should see your changes reflect in the output of the application without a restart.


# summary

The two projects in this repo demonstrate how to externalize the configuration of a Spring Boot Application to a [Spring Cloud Config Server's](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) GIT repository. That Spring configuration of the app can then be changed at runtime without having to restart/reload it.

# Notes

The Push notification mechanism works by creating a [GitHub Webhook](https://developer.github.com/webhooks/) to notify the http://cloud-config-server-host/monitor URL whenever the configuration changes in GIT. You can read more about it here: [Push Notifications and Spring Cloud Bus.](http://projects.spring.io/spring-cloud/spring-cloud.html#_push_notifications_and_spring_cloud_bus)
You can set up a similar webhook in your GitHub repository which backs your Spring Cloud Config Server.

