package aplikacija;

import grafika.KontrolniProzor;

/**
 * Created by Marko on 27-Dec-17.
 *
 * Ovo je main klasa, koja pokrece aplikaciju za kviz. aplikacija se sastoji od dva prozora, jednog kontrolnog i
 * drugog kviz prozora, koji je napravljen da se prikazuje u fullscreenu preko projektora.
 *
 */
public class Kviz {
    public static void main(String[] args) {
        KontrolniProzor p = new KontrolniProzor("Svetosavski kviz - Kontrolni prozor");
    }
}
