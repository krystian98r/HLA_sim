package Sklep;

import Klienci.Klient;

import java.util.ArrayList;

public class Sklep {
    private ArrayList<Klient> klienci;
    private static Sklep instance = null;

    public Sklep() {
    }

    static public Sklep getInstance()
    {
        if(instance==null) instance = new Sklep();
        return instance;
    }

    // Zwraca nr ostatniego klienta
    public Klient getOstatniKlient() {
        return klienci.get(klienci.size() - 1);
    }

    public void Wejdz() {
        klienci.add(new Klient(getOstatniKlient().getNr_klienta() + 1));
    }
}
