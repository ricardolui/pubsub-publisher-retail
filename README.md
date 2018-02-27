# PubSub Publisher example for Retail

This is a pubsub publisher for retail usecase on bigdata.

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


