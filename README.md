# PubSub Publisher example for Retail

This is a pubsub publisher for retail usecase on bigdata.


## Set up your environment

### Tools

1.  Install [Java 8](https://java.com/fr/download/)
1.  Install Maven (for
    [windows](https://maven.apache.org/guides/getting-started/windows-prerequisites.html),
    [mac](http://tostring.me/151/installing-maven-on-os-x/),
    [linux](http://maven.apache.org/install.html))
1.  Install Google [Cloud SDK](https://cloud.google.com/sdk/)
1.  Install an IDE such as [Eclipse](https://eclipse.org/downloads/) or
    [IntelliJ](https://www.jetbrains.com/idea/download/) (optional)
1.  To test your installation, open a terminal window and type:

    ```shell
    $ java -version
    $ mvn --version
    $ gcloud --version
    ```

### Google Cloud

1.  Go to https://cloud.google.com/console.****
1.  Enable billing and create a project.
1.  Enable Google PubSub API


## Getting Started

How to run the code, currently only project, topic and messages per second are supported variables

```
mvn compile exec:java \
         -Dexec.mainClass=com.google.ce.demos.dataflow.abandonedcarts.producer.PublisherPubSub \
         -Dexec.args="--project=YOUR_PROJECT \
         --topic=YOUR_TOPIC \
         --messagesPerSecond=MSGS_PER_SECOND \
         --simulateAutoscaling=false
```


## License

This project is licensed under the MIT License


mvn compile exec:java \
 -Dexec.mainClass=com.google.ce.demos.dataflow.abandonedcarts.producer.PublisherPubSub \
 -Dexec.args="--project=gricardo-brasil3 \
              --topic=retailer-new-topic \
	      --messagesPerSecond=10 \
	      --simulateAutoscaling=false"