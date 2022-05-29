package Sklep;

import Klienci.Klient;

import java.util.ArrayList;
import java.util.Random;

public class Sklep {
    private ArrayList<Klient> klienci;
    private Random random;
    private static Sklep instance = null;
    private double minCzasMiedzyKlientami = 1;
    private double maxCzasMiedzyKlientami = 10;


    public Sklep() {
        random = new Random();
    }

    static public Sklep getInstance() {
        if (instance == null) instance = new Sklep();
        return instance;
    }

    // Zwraca nr ostatniego klienta
    public Klient getOstatniKlient() {
        return klienci.get(klienci.size() - 1);
    }

    public Klient getKlient(int nr_klienta) {
        return klienci.get(nr_klienta);
    }

    public void Wejdz() {
        klienci.add(new Klient(getOstatniKlient().getNr_klienta() + 1));
    }

    public double getCzasWejsciaKolejnego() {
        return minCzasMiedzyKlientami + (maxCzasMiedzyKlientami - minCzasMiedzyKlientami) * random.nextDouble();
    }
}
