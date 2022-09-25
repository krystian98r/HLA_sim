package Klienci;

import java.util.Random;
import java.util.UUID;

public class Klient {
    private final int maxCzasZakupow = 12;
    private final int minCzasZakupow = 2;
    private final int nrKlienta;
    private boolean uprzywilejowany;
    private int nrKasy;
    private double czasWejscia;
    private int czasZakupow;

    private Random random = new Random();


    public Klient(int nrKlienta, double czasWejscia) {
        this.czasWejscia = czasWejscia;
        this.czasZakupow = random.nextInt(maxCzasZakupow - minCzasZakupow) + minCzasZakupow;
        this.uprzywilejowany = random.nextInt(10) == 0; // bound/100 chance to get
        this.nrKlienta = nrKlienta;

        System.out.println("\u001B[34mNOWY KLIENT\nNr klienta: " + nrKlienta + "\nCzas robienia zakupów: " + czasZakupow);
        if (uprzywilejowany) System.out.println("Uprzywilejowany: TAK\n");
        else System.out.println("Uprzywilejowany: NIE");
        System.out.println("\u001B[0m");
    }

    public Klient(int nrKlienta) {
        this.nrKlienta = nrKlienta;
    }

    public int zaplac() {
        System.out.println("\033[42m" + "Klient o nr " + nrKlienta + " zapłacił i wychodzi ze sklepu." + "\033[0m");
        return this.nrKlienta;
    }

    public int getNrKasy() {
        return this.nrKasy;
    }

    public int czekaj(int nrKasy) {
        this.nrKasy = nrKasy;
        System.out.println("\033[41m" + "Klient o nr " + nrKlienta + " podchodzi do kasy nr " + nrKasy + "\033[0m");
        return this.nrKlienta;
    }

    public int getNrKlienta() {
        return nrKlienta;
    }

    public double getCzasWejscia() {
        return czasWejscia;
    }

    public int getCzasZakupow() {
        return czasZakupow;
    }

    public boolean isUprzywilejowany() {
        return uprzywilejowany;
    }

    public void setUprzywilejowany(boolean uprzywilejowany) {
        this.uprzywilejowany = uprzywilejowany;
    }

    public void setNrKasy(int nrKasy) {
        this.nrKasy = nrKasy;
    }

    public void setCzasZakupow(int czasZakupow) {
        this.czasZakupow = czasZakupow;
    }
}