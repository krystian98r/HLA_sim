package Sklep;

import Klienci.Klient;

import java.util.ArrayList;
import java.util.Random;

public class Sklep {
    private ArrayList<Klient> klienci;
    private Random random;
    private int nastepnyNrKlienta;
    private static Sklep instance = null;
    private int maxCzasMiedzyKlientami;


    public Sklep() {
        this.random = new Random();
        this.maxCzasMiedzyKlientami = 10;
        this.nastepnyNrKlienta = 0;
        this.klienci = new ArrayList<Klient>();
    }

    static public Sklep getInstance() {
        if (instance == null) instance = new Sklep();
        return instance;
    }

    public Klient getKlient(int nr_klienta) {
        return klienci.get(nr_klienta);
    }

    public void Wejdz(double czasSymulacji) {
        klienci.add(new Klient(nastepnyNrKlienta, czasSymulacji));
        System.out.println("Wszedl nowy klient [" + nastepnyNrKlienta + "]");
        nastepnyNrKlienta++;
    }

    public double getCzasWejsciaKolejnego() {
        return random.nextInt(maxCzasMiedzyKlientami) + 1;
    }

    public ArrayList<Klient> getKlienci() {
        return klienci;
    }
}

