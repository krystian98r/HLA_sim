package Kasa;

import Klienci.Klient;

import java.util.*;

public class Kasa {
    private final int nrKasy;
    private final int maxDlugosc = 5;
    private final int maxCzasObslugi = 8;
    private final int minCzasObslugi = 2;
    private int dlugoscKolejki;
    private int czasObslugi;
    private double nastepnaObsluga;
    private Random rand = new Random();
    //    private ArrayList<Integer> kolejka;
    private Deque<Integer> kolejka;


    public Kasa(int nrKasy) {
        this.nrKasy = nrKasy;
        this.dlugoscKolejki = 0;
        this.czasObslugi = rand.nextInt(maxCzasObslugi - minCzasObslugi) + minCzasObslugi; // rand.nextInt(maxVal-minVal) + minVal
        this.nastepnaObsluga = -1;
        this.kolejka = new ArrayDeque<>();
        System.out.println("\u001B[32mOtwarto kasę nr " + nrKasy + "\nCzas obługi: " + czasObslugi + "\nMax kolejka: " + maxDlugosc + "\u001B[0m");
    }

    public void czekaj(int nrKlienta, double federateTime, boolean uprzywilejowany) {
        if (kolejka.isEmpty() && federateTime != 0) setNastepnaObsluga(federateTime);
        if (uprzywilejowany) {
            kolejka.offerFirst(nrKlienta);
            setNastepnaObsluga(federateTime);
            System.out.println("\033[41m" + "Klient nr " + nrKlienta + " wchodzi na początek kolejki przy kasie nr " + nrKasy + "; Długość kolejki: " + getDlugoscKolejki() + "\033[0m");
        } else {
            kolejka.add(nrKlienta);
            System.out.println("\033[41m" + "Klient nr " + nrKlienta + " podchodzi do kasy nr " + nrKasy + "; Długość kolejki: " + getDlugoscKolejki() + "\033[0m");
        }
    }

    public int obsluz(int nrKasy) {
        int nrKlienta = kolejka.peek();
        this.kolejka.remove();
        System.out.println("\033[42m" + "Klient nr " + nrKlienta + " został obsłużony przy kasie nr " + nrKasy + "; Długość kolejki: " + getDlugoscKolejki() + "\033[0m");
        return nrKlienta;
    }

    public int getNrKasy() {
        return nrKasy;
    }

    public int getDlugoscKolejki() {
        return kolejka.size();
    }

    public double getNastepnaObsluga() {
        return nastepnaObsluga;
    }

    public void setNastepnaObsluga(double federateTime) {
        this.nastepnaObsluga = federateTime + czasObslugi;
    }

    public boolean getDostepnosc() {
        return getDlugoscKolejki() < maxDlugosc;
    }
}