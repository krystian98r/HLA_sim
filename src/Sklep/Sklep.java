package Sklep;

import java.util.ArrayList;

public class Sklep {
    private ArrayList<Integer> klienci;

    public Sklep() {
    }

    // Zwraca nr ostatniego klienta
    int getOstatniKlient() {
        return klienci.get(klienci.size() - 1);
    }
}
