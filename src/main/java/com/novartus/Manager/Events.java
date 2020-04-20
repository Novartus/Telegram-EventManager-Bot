package com.novartus.Manager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Events{
    private String API_TOKEN ="123456:EnterYourToken_API";

    String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    String Event_Name;

    final private String ATLAS_URL = "mongodb+srv://Your_MongoDB_URL_FOR_JAVA_DRIVER"; //You can use your locl host too
    String name,details,venue,timing;
    long found;

    protected String event_details(String event,String user_first_name,String user_last_name,String user_username,int uid) {

        switch (event){

            case "Event 1":
                name ="Event1";
                details="This is the a event 1 description";
                venue="1 Gujarat,India";
                timing="@24:00 HRS";
                Event_Name = name;
                break;

            case "Event 2":
                name ="Event 2";
                details="This is the a event 2 description";
                venue="2 Gujarat,India";
                timing="@24:00 HRS";
                Event_Name = name;
                break;

            case "Event 3":
                name ="Event 3";
                details="This is the a event 3 description";
                venue="3 Gujarat,India";
                timing="@24:00 HRS";
                Event_Name = name;
                break;

            case "Event 4":
                name ="Event 4";
                details="This is the a event 4 description";
                venue="4 Gujarat,India";
                timing="@24:00 HRS";
                Event_Name = name;
                break;

            case "Event 5":
                name ="Event 5";
                details="This is the a event 5 description";
                venue="5 Gujarat,India";
                timing="@24:00 HRS";
                Event_Name = name;
                break;

            case "Event 6":
                name ="Event 6";
                details="This is the a event 6 description";
                venue="6 Gujarat,India";
                timing="@24:00 HRS";
                Event_Name = name;
                break;

            default:
                System.out.println("No Event Found :(");
                break;
        }
        event_db(user_first_name,user_last_name,uid,user_username,Event_Name);
        return (" Name: "+name+"\n Details: "+details+"\n Venue: "+venue+"\n Timing: "+timing + "\n Registration Confirmed");

    }
     void event_check(long uid) {
        MongoClientURI connectionString = new MongoClientURI(ATLAS_URL);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("db_eventmanager");
        for (int i =1 ;i<=6;i++){
            int k =i;
            String event_number= ("Event "+Integer.toString(k));
            String msg= ("Event "+Integer.toString(k)+" Registered");
            MongoCollection<Document> collection = database.getCollection(event_number);
            found = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));
            if (found != 0) {
              //  System.out.println("User Registered.");
                urlString = String.format(urlString, API_TOKEN, uid, msg);
                found =0;
                try {   // Sending  message object to user
                    URL url = new URL(urlString);
                    URLConnection conn = url.openConnection();
                    InputStream is = new BufferedInputStream(conn.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    msg= null;
                }
            }
        }
         mongoClient.close();

     }

     String event_db(String first_name, String last_name, int uid, String username,String Event_Name) {
        MongoClientURI connectionString = new MongoClientURI(ATLAS_URL);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("db_eventmanager");
        MongoCollection<Document> collection = database.getCollection(Event_Name);
        long found = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));
        if (found == 0) {
            Document doc = new Document("first_name", first_name)
                    .append("last_name", last_name)
                    .append("username", username)
                    .append("uid", uid);
                //  .append("event",Event_Name);

            collection.insertOne(doc);
            mongoClient.close();
        //  System.out.println("Event Added In Your Account.");
        }
        return "event_add";
    }

}
