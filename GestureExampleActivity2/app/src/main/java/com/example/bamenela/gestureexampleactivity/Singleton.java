package com.example.bamenela.gestureexampleactivity;

import java.util.ArrayList;

class Singleton {
    // On crée une list permetant de stocké ce que l'appareil nous retourne comme chemin
    public ArrayList<String> listImageMemory = new ArrayList<>();
    private static final Singleton ourInstance = new Singleton();

    static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
    }
}
