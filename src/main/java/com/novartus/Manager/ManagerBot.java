package com.novartus.Manager;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.toIntExact;

public class ManagerBot extends TelegramLongPollingBot {

     private String API_TOKEN ="123456:EnterYourToken_API";
     String BOT_NAME="YourBotName";
     final protected String ATLAS_URL = "mongodb+srv://Your_MongoDB_URL_FOR_JAVA_DRIVER"; //You can use your locl host too

    static long  found;
    Events events = new Events();

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage             message; // For All kind Text Messages
        ReplyKeyboardMarkup     keyboardMarkup;

        String user_first_name = update.getMessage().getChat().getFirstName();
        String user_last_name  = update.getMessage().getChat().getLastName();
        String user_username   = update.getMessage().getChat().getUserName();

        int  uid               = toIntExact(update.getMessage().getChat().getId());  //Long -> Integer
        long chat_id           = update.getMessage().getChatId();

        String greeting_text   = EmojiParser.parseToUnicode("Greeting Human. Welcome to EventManagerBot :smile:\n :alien:"); // Msg + Emoji
        String message_text    = update.getMessage().getText();
        String bot_reply       = "Unknown Command Please Try Again";

        String profile_details=("Your Profile Details: \nFirst Name: "+user_first_name+
                "\nLast Name: "+user_last_name+"\nUsername: "+user_username+
                "\nUserID: "+uid+"\nAccount Status: ");

        // Message contains Text
        if (update.hasMessage() && update.getMessage().hasText()) {

            switch (message_text){
                case "/info":
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("/start      : The Bot will greet you." +
                                    "\n/profile   : It will show you your Profile Keyboard." +
                                    "\n/event_list : It will show you list of available events." +
                                    "\n/event_keyboard : Just touch on 'Event Name' and you will get confirmation message." +
                                    "\n/hide       : To hide your open keyboard.");
                    break;

                case "/event_list":
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("1. Event 1" +
                                    "\n 2. Event 2" +
                                    "\n 3. Event 3" +
                                    "\n 4. Event 4" +
                                    "\n 5. Event 5" +
                                    "\n 6. Event 6" +
                                    "\n /event_keyboard to Register !");
                    break;

                case "/start":
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText(greeting_text);
                    bot_reply = "greeting_text";
                    break;

                case "/profile":
                    message = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText("Here is your keyboard");
                    bot_reply = "Here is your keyboard";
                    // Create ReplyKeyboardMarkup object
                    keyboardMarkup = new ReplyKeyboardMarkup();
                    // Create the keyboard (list of keyboard rows)
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    // Create a keyboard row
                    KeyboardRow row = new KeyboardRow();
                    // Set each button, you can also use KeyboardButton objects if you need something else than text
                    row.add("About Dev");
                    row.add("New Registration");
                    row.add("Check Registration");
                    // Add the first row to the keyboard
                    keyboard.add(row);
                    // Create another keyboard row

                    row = new KeyboardRow();
                    // Set each button for the second line
                    row.add("My Events");
                    row.add("View My Profile Details");
                    row.add("Delete Profile");
                    // Add the second row to the keyboard
                    keyboard.add(row);

                    // Set the keyboard to the markup
                    keyboardMarkup.setKeyboard(keyboard);

                    // Add it to the message
                    message.setReplyMarkup(keyboardMarkup);
                    break;

                case "About Dev":
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("www.github.com/Novartus");
                    bot_reply="About Dev";
                    break;

                case "New Registration":
                    registration_db(user_first_name, user_last_name, uid, user_username);
                    if (found == 0) {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("Registration Complete");
                        bot_reply = "New Registration";
                    } else {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("Already Registered");
                        bot_reply = "Already Registered";
                    }
                    break;

                case "Check Registration":
                    registration_db_check( uid);
                    if (found == 0){
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText("No Registration Found");
                        bot_reply = "No Registration Found";
                    }
                    else{message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("You are a Registered user");
                        bot_reply = "You are a Registered user";
                    }
                    break;

                case "My Events":
                   events.event_check(uid);
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Those are registered events");
                    bot_reply="Registered Events are given";
                    break;

                case "View My Profile Details":
                    registration_db_check(uid);
                    if (found == 0) {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText(profile_details+"Not Registered");

                    } else {
                        message = new SendMessage()
                                .setChatId(chat_id)
                                .setText(profile_details+"Registered");
                    }
                    bot_reply = "View My Profile Details";
                    break;

                case "Delete Profile":
                    registration_db_delete(uid);
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Registration Deleted");
                    bot_reply="Registration Deleted";
                    break;

                case "/hide":
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Keyboard Hidden");
                    ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
                    message.setReplyMarkup(keyboardRemove);
                    bot_reply="Keyboard Hidden";
                    break;

                case "/event_keyboard":
                    message = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText("Here is your Event Registration Keyboard");
                    bot_reply = "Here is your Event keyboard";
                    keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard1= new ArrayList<>();
                    KeyboardRow row1 = new KeyboardRow();
                    row1.add("Event 1");
                    row1.add("Event 2");
                    row1.add("Event 3");
                    keyboard1.add(row1);
                    row1 = new KeyboardRow();

                    row1.add("Event 4");
                    row1.add("Event 5");
                    row1.add("Event 6");
                    keyboard1.add(row1);

                    keyboardMarkup.setKeyboard(keyboard1);
                    message.setReplyMarkup(keyboardMarkup);
                    break;

                case "Event 1":
                case "Event 2":
                case "Event 3":
                case "Event 4":
                case "Event 5":
                case "Event 6":
                {
                    message =new SendMessage()
                            .setChatId(chat_id)
                            .setText(events.event_details(message_text,user_first_name,user_last_name,user_username,uid));
                    bot_reply="Event Registration";
                }
                break;

                default:
                    message = new SendMessage()
                            .setChatId(chat_id)
                            .setText("Unknown Command Please Try Again");
                    bot_reply="Unknown Command Please Try Again";
                    break;
            }

            try {   // Sending  message object to user
                this.execute(message);
                log(user_first_name, user_last_name, uid,user_username,message_text,bot_reply);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return API_TOKEN;
    }

    private void log(String first_name, String last_name, Integer uid, String username, String txt, String bot_answer) {
        System.out.println("\n----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (uid = " + uid + ") (Username=@"+username+")\n Text : " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
        System.out.println("----------------------------");
    }

    // DataBase Connectivity
    private String registration_db(String first_name, String last_name, int uid, String username) {
        MongoClientURI connectionString = new MongoClientURI(ATLAS_URL);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("db_eventmanager");
        MongoCollection<Document> collection = database.getCollection("users");
        long found = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));
        if (found == 0) {
            Document doc = new Document("first_name", first_name)
                    .append("last_name", last_name)
                    .append("username", username)
                    .append("uid", uid);

            collection.insertOne(doc);
            mongoClient.close();
            System.out.println("New User Detected Added in Database.");
            return "new_user";
        } else {
            System.out.println("User is already exists in database.");
            mongoClient.close();
            return "existing_user";
        }
    }

    private String registration_db_check(int uid) {
        MongoClientURI connectionString = new MongoClientURI(ATLAS_URL);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("db_eventmanager");
        MongoCollection<Document> collection = database.getCollection("users");
        found = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));
        if (found == 0) {
            System.out.println("User is not Registered.");
            mongoClient.close();
        }
        else{
            System.out.println("User is Registered.");
        }
        return "existing_user";
    }

    private String registration_db_delete(int uid) {
        MongoClientURI connectionString = new MongoClientURI(ATLAS_URL);
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("db_eventmanager");
        MongoCollection<Document> collection = database.getCollection("users");

        long delete = collection.countDocuments(Document.parse("{ uid : " + uid + "}"));

        if ((delete != 0)){
            collection.deleteOne(new Document("uid", uid));
            System.out.println("User Deleted From Database.");
            return "Deleted";

        } else {
            System.out.println("Can't Delete Unregistered User.");
            return "Can't Delete";
        }
    }

}
