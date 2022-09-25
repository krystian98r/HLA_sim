package Statystyka;

public class Statystyka {

    private int klienciNaSklepie;
    private int klienciPrzyKasach;
    private int obsluzeniKlienci;
    private double sredniCzasZakupow;
    private int sumaCzasZakupow;
    private int sumaUprzywilejowanych;
    private double procentUprzywilejowanych;
    private int otwarteKasy;

    public Statystyka() {
        klienciNaSklepie = 0;
        klienciPrzyKasach = 0;
        obsluzeniKlienci = 0;
        sredniCzasZakupow = 0;
        sumaCzasZakupow = 0;
        sumaUprzywilejowanych = 0;
        otwarteKasy = 0;
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

    public double getSredniCzasZakupow() {
        return (double) Math.round(sredniCzasZakupow * 100) / 100;
    }

    public double getProcentUprzywilejowanych() {
        zaktualizujProcentUprzywilejowanych();
        return (double) Math.round(procentUprzywilejowanych * 100) / 100;
    }

    public void dodajSumaCzasZakupow(int czasZakupow) {
        sumaCzasZakupow += czasZakupow;
        sredniCzasZakupow = (double) sumaCzasZakupow / (klienciNaSklepie + klienciPrzyKasach + obsluzeniKlienci);
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

    private void zaktualizujProcentUprzywilejowanych() {
        procentUprzywilejowanych = sumaUprzywilejowanych * 100 / (double) (klienciNaSklepie + klienciPrzyKasach + obsluzeniKlienci);
    }
}