package com.google.ce.demos.dataflow.abandonedcarts.bq;

import com.google.cloud.bigquery.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BqInsert {


    public static void insertRows() {

        String projectName = "MyPROJECTID";
        String datasetName = "dataflow";
        String tableName = "abandoned_carts2018_02_14";


        BigQueryOptions.Builder optionsBuilder = BigQueryOptions.newBuilder().setProjectId(projectName);
        BigQuery bigquery = optionsBuilder.build().getService();


        TableId tableId = TableId.of(datasetName, tableName);
        // Values of the row to insert
        Map<String, Object> rowContent = new HashMap<String, Object>();
        rowContent.put("timestamp", System.currentTimeMillis());
        // Bytes are passed in base64
        rowContent.put("useragent", "chrome");
        rowContent.put("customer", "12312314555");
//        // Records are passed as a map
        ArrayList<Map<String, Object>> arrayRecords = new ArrayList<Map<String, Object>>();
        Map<String, Object> recordsContent01 = new HashMap<String, Object>();
        recordsContent01.put("item", "0001");
        Map<String, Object> recordsContent02 = new HashMap<String, Object>();
        recordsContent02.put("item", "0002");
        Map<String, Object> recordsContent03 = new HashMap<String, Object>();
        recordsContent03.put("item", "00012");
        arrayRecords.add(recordsContent01);
        arrayRecords.add(recordsContent02);
        arrayRecords.add(recordsContent03);
        rowContent.put("items", arrayRecords);

        InsertAllResponse response = bigquery.insertAll(InsertAllRequest.newBuilder(tableId)
                .addRow("152312341", rowContent)
                // More rows can be added in the same RPC by invoking .addRow() on the builder
                .build());
        if (response.hasErrors()) {
            System.out.println("Has Errors");
            // If any of the insertions failed, this lets you inspect the errors
            for (Map.Entry<Long, List<BigQueryError>> entry : response.getInsertErrors().entrySet()) {
                // inspect row error
                System.out.println(entry.toString());
            }
        }

    }

    public static void main(String[] args) {

        insertRows();


    }
}
