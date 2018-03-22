#!/usr/bin/env bash
PROJECT=YOUR_PROJECT
TOPIC=YOUR_TOPIC
apt-get update
apt-get -y install git openjdk-8-jdk-headless maven
curl -sSO "https://dl.google.com/cloudagents/install-logging-agent.sh"
bash install-logging-agent.sh
git clone https://github.com/ricardolui/pubsub-publisher-retail.git
cd pubsub-publisher-retail
mvn compile exec:java -Dexec.mainClass=com.google.ce.demos.dataflow.abandonedcarts.producer.PublisherPubSub \
-Dexec.args="--project=gricardo-brasil3 --topic=b2w --messagesPerSecond=3000" &