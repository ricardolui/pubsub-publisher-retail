#!/usr/bin/env bash
apt-get update
apt-get -y install git openjdk-8-jdk-headless maven
git clone https://github.com/ricardolui/pubsub-publisher-retail.git
cd pubsub-publisher-retail
mvn compile exec:java -Dexec.mainClass=com.google.ce.demos.dataflow.abandonedcarts.producer.PublisherPubSub \
-Dexec.args="--project=gricardo-brasil3 --topic=retailer-new-topic --messagesPerSecond=2000"