package com.google.ce.demos.dataflow.abandonedcarts.producer;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.ce.demos.dataflow.abandonedcarts.common.AbonandonedCartsVariables;
import com.google.ce.demos.dataflow.abandonedcarts.common.PageView;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class PublisherPubSub {

    private final static Logger logger = Logger.getLogger(PublisherPubSub.class.getName());


    public static ListMultimap<String, String> parseCommandLine(
            String[] args, boolean strictParsing) {
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();
        for (String arg : args) {
            if (Strings.isNullOrEmpty(arg)) {
                continue;
            }
            try {
                checkArgument(arg.startsWith("--"),
                        "Argument '%s' does not begin with '--'", arg);
                int index = arg.indexOf("=");
                // Make sure that '=' isn't the first character after '--' or the last character
                checkArgument(index != 2,
                        "Argument '%s' starts with '--=', empty argument name not allowed", arg);
                if (index > 0) {
                    builder.put(arg.substring(2, index), arg.substring(index + 1, arg.length()));
                } else {
                    builder.put(arg.substring(2), "true");
                }
            } catch (IllegalArgumentException e) {
                if (strictParsing) {
                    throw e;
                } else {
                    logger.log(Level.INFO, String.format("Strict parsing is disabled, ignoring option '{}' because {}",
                            arg, e.getMessage()));
                }
            }
        }
        return builder.build();
    }


    public static void publishAbandonedCartMessage(long numberOfMessages) {


    }


    public static void publishMessage(int numberOfMessages, int customerIdInit, String myTopic, String myProject) {

        TopicName topicName = TopicName.of(myProject, myTopic);
        Publisher publisher = null;
        List<ApiFuture<String>> messageIdFutures = new ArrayList<ApiFuture<String>>();
        int abandoned = 0;
        int purchased = 0;
        int other = 0;
        int totalMessages = 0;

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();

//            // schedule publishing one message at a time : messages get automatically batched
            for (int i = 0; i < numberOfMessages; i++) {

                int customerId = numberOfMessages*customerIdInit + i;
                String userAgent = AbonandonedCartsVariables.BROWSERS[new Random().nextInt(4)];

                boolean isAbandoned = new Random().nextBoolean();

                DateTime dateTimeHelper = new DateTime(DateTimeZone.UTC);
                Date dateCart = dateTimeHelper.now().minusMinutes(15).toDate();

                if (isAbandoned) {
                    //Generate Abandoned Cart Scenario
                    //Visit cart and not checkout
                    addMessageToPubSub(customerId, AbonandonedCartsVariables.CART_PAGE, userAgent, publisher, messageIdFutures, dateCart);
                    addRandomPages(customerId, userAgent, publisher, messageIdFutures);
                    abandoned++;
                    totalMessages+=3;
                } else {

                    boolean isPurchased = new Random().nextBoolean();
                    if (isPurchased) {
                        addMessageToPubSub(customerId, AbonandonedCartsVariables.CART_PAGE, userAgent, publisher, messageIdFutures, dateTimeHelper.now().toDate());
                        addRandomPages(customerId, userAgent, publisher, messageIdFutures);
                        addMessageToPubSub(customerId, AbonandonedCartsVariables.CHECKOUT_PAGE, userAgent, publisher, messageIdFutures, dateTimeHelper.now().toDate());
                        purchased++;
                        totalMessages+=4;
                    } else {
                        addRandomPages(customerId, userAgent, publisher, messageIdFutures);
                        other++;
                        totalMessages+=2;
                    }
                }

            }

        } catch (Exception e) {

        } finally {

            try {
                // wait on any pending publish requests.
                List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();

                for (String messageId : messageIds) {
                    logger.log(Level.INFO, "published with message ID: " + messageId);
                }

                if (publisher != null) {
                    // When finished with the publisher, shutdown to free up resources.
                    publisher.shutdown();
                }
            } catch (Exception e) {

            }

            System.out.println(String.format("Final Status: Abandoned: %d, Purchased: %d, Other: %d -- Total Messages: %d", abandoned, purchased, other, totalMessages).toString());
        }

    }

    private static void addMessageToPubSub(int customerId, String currentPage, String userAgent, Publisher publisher, List<ApiFuture<String>> messageIdFutures, Date date) {
        PageView pageView = new PageView();
        pageView.setTimestamp(date);
        pageView.setCustomer("" + customerId);
        pageView.setPage(currentPage);
        pageView.setUseragent(userAgent);
        pageView.setTimestamp(new Date());
        if (pageView.getPage().equals(AbonandonedCartsVariables.CART_PAGE)) {
//            int numberOfItems = new Random().nextInt(5);
            int numberOfItems = 1;
            for (int j = 0; j < numberOfItems; j++) {
                pageView.getItems().add("" + new Random().nextInt(50));
            }
        }
        String jsonString = new Gson().toJson(pageView);
//        System.out.println(jsonString);
        ByteString data = ByteString.copyFromUtf8(jsonString);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
        // Once published, returns a server-assigned message id (unique within the topic)
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        messageIdFutures.add(messageIdFuture);
    }

    /**
     * Adds 3 Random pages
     * @param customerId
     * @param userAgent
     * @param publisher
     * @param messageIdFutures
     */
    private static void addRandomPages(int customerId, String userAgent, Publisher publisher, List<ApiFuture<String>> messageIdFutures) {
        for (int k = 0; k < 2; k++) {
            String currentPage = AbonandonedCartsVariables.PAGES[new Random().nextInt(3)];
            addMessageToPubSub(customerId, currentPage, userAgent, publisher, messageIdFutures, new Date());
        }
    }


    public static void main(String args[]) {


        System.out.println("Entered Application to Publish PubSub Messages to your Topic");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        ListMultimap<String, String> multiMap = PublisherPubSub.parseCommandLine(args, false);

//        for(int i = 0; i < args.length; i++) {
//            System.out.println(args[i]);
//        }

        List<String> topic = multiMap.get("topic");
        String myTopic = topic.get(0);

        List<String> project = multiMap.get("project");
        String myProject = project.get(0);


        List<String> mps = multiMap.get("messagesPerSecond");
        int messagesPerSecond = mps!=null? mps.size()>0? Integer.parseInt(mps.get(0)):100:100;


        //TODO: Implement autoscaling
        List<String> simAutoscaling = multiMap.get("simulateAutoscaling");
        boolean simulateAutoscaling = simAutoscaling!=null? Boolean.parseBoolean(simAutoscaling.get(0)): false;


        int customers = 0;
        while(true)
        {
            try
            {
                Thread.sleep(250);
                publishMessage(messagesPerSecond, customers, myTopic, myProject);
                customers++;
            }
            catch(Exception e)

            {

            }

        }

    }

}
