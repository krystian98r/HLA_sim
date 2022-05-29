package Klienci;

import Sklep.SklepFederate;
import Sklep.SklepFederateAmbassador;
import hla.rti.RTIambassador;
import hla.rti1516e.FederateAmbassador;

import java.util.Random;

public class Klient {
    private int nr_klienta;
    private boolean robi_zakupy;
    private boolean uprzywilejowany;
    private int nr_kasy;
    private double czas_wejscia;
    private double czas_zakupow;
    private Random random;

    public Klient(int nr_klienta, double czas_wejscia) {
        this.random = new Random();
        this.nr_klienta = nr_klienta;
        this.robi_zakupy = true;
        this.uprzywilejowany = random.nextBoolean();
        this.czas_zakupow = random.nextInt(20) + 1;
        this.czas_wejscia = czas_wejscia;
    }

    void Zaplac() {

    }

    void Czekaj() {
        robi_zakupy = false;
        nr_kasy = 0;
    }

    public int getNr_klienta() {
        return this.nr_klienta;
    }

    public double getCzas_zakupow() {
        return this.czas_zakupow;
    }

    public double getCzas_wejscia() {
        return this.czas_wejscia;
    }
}
