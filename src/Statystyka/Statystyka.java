package Statystyka;

public class Statystyka {

    private int klienciNaSklepie;
    private int klienciPrzyKasach;
    private int obsluzeniKlienci;
    private int sumaCzasZakupow;
    private int sumaUprzywilejowanych;
    private int otwarteKasy;
    private int sumaCzasObslugi;

    public Statystyka() {
        klienciNaSklepie = 0;
        klienciPrzyKasach = 0;
        obsluzeniKlienci = 0;
        sumaCzasZakupow = 0;
        sumaUprzywilejowanych = 0;
        otwarteKasy = 0;
        sumaCzasObslugi = 0;
    }

    public void dodajPrzyKasach() {
        klienciPrzyKasach++;
    }

    public void odejmijPrzyKasach() {
        klienciPrzyKasach--;
    }

    public void dodajNaSklepie() {
        klienciNaSklepie++;
    }

    public void odejmijNaSklepie() {
        klienciNaSklepie--;
    }

    public void dodajObsluzeni() {
        obsluzeniKlienci++;
    }

    public int getKlienciNaSklepie() {
        return klienciNaSklepie;
    }

    public int getKlienciPrzyKasach() {
        return klienciPrzyKasach;
    }

    public int getObsluzeniKlienci() {
        return obsluzeniKlienci;
    }

    public double sredniCzasZakupow() {
        double sredniCzasZakupow = (double) sumaCzasZakupow / (klienciNaSklepie + klienciPrzyKasach + obsluzeniKlienci);
        return (double) Math.round(sredniCzasZakupow * 100) / 100;
    }

    public double procentUprzywilejowanych() {
        double procentUprzywilejowanych = sumaUprzywilejowanych * 100 / (double) (klienciNaSklepie + klienciPrzyKasach + obsluzeniKlienci);
        return (double) Math.round(procentUprzywilejowanych * 100) / 100;
    }

    public void dodajSumaCzasZakupow(int czasZakupow) {
        sumaCzasZakupow += czasZakupow;
    }

    public void dodajUprzywilejowany() {
        sumaUprzywilejowanych++;
    }

    public int getSumaUprzywilejowanych() {
        return sumaUprzywilejowanych;
    }

    public void dodajKase() {
        otwarteKasy++;
    }

    public void odejmijKase() {
        otwarteKasy--;
    }

    public int getOtwarteKasy() {
        return otwarteKasy;
    }

    public void dodajSumaCzasOblugi(int czasObslugi) {
        sumaCzasObslugi += czasObslugi;
    }

    public double sredniCzasObslugi() {
        return (double) Math.round(sumaCzasObslugi / (double) obsluzeniKlienci * 100) / 100;
    }
}