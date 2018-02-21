package com.google.ce.demos.dataflow.abandonedcarts.producer;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.ce.demos.dataflow.abandonedcarts.common.AbonandonedCartsVariables;
import com.google.ce.demos.dataflow.abandonedcarts.common.PageView;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.*;

public class PublisherPubSub {

    /**
     * Replace this variables with your own.
     */
    public static String PROJECT_NAME="PROJECTID";
    public static String TOPIC = "TOPIC";


    public static void publishAbandonedCartMessage(long numberOfMessages) {


    }


    public static void publishMessage(int numberOfMessages, int customerIdInit) {

        TopicName topicName = TopicName.of(PROJECT_NAME, TOPIC);
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
                    System.out.println("published with message ID: " + messageId);
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
        System.out.println(jsonString);
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


//        System.out.println(Calendar.getInstance(TimeZone.));
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        int customers = 0;
        while(true)
        {
            try
            {
                Thread.sleep(1000);
                publishMessage(10, customers);
                customers++;

            }
            catch(Exception e)

            {

            }

        }



    }

}
