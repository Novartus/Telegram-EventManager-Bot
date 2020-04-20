package com.novartus;
import com.novartus.Manager.ManagerBot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class StartBot {
    public static void main(String args[]){
        System.out.println("Hello Human");

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi(); //Instantiate Telegram Bots API

        try {                                   // Register Bot
            botsApi.registerBot(new ManagerBot());
        } catch (TelegramApiException e) {
                e.printStackTrace();
        }
        clearScreen();;
        System.out.println("Started Bot");
    }
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
