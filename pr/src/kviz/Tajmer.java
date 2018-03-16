package kviz;

import grafika.KontrolniProzor;
import grafika.KvizProzor;

import javax.swing.*;
import java.awt.*;


/**
 * Created by Marko on 19-Jan-18.
 */
public class Tajmer implements Runnable {
    private  JLabel prikazKvizProzor;
    private  JLabel prikazKontrolniProzor;
    private volatile int vremeZaJavljanje;
    private volatile int vremeZaOdgovor;

    public Tajmer(JLabel prikazKvizProzor, JLabel prikazKontrolniProzor, int vremeZaJavljanje, int vremeZaOdgovor) {
        this.prikazKvizProzor = prikazKvizProzor;
        this.prikazKontrolniProzor = prikazKontrolniProzor;
        this.vremeZaJavljanje = vremeZaJavljanje;
        this.vremeZaOdgovor=vremeZaOdgovor;
        Thread t = new Thread(this);
        t.start();
    }

    public Tajmer() {
        vremeZaJavljanje=0;
        vremeZaOdgovor=0;
        prikazKontrolniProzor=null;
        prikazKvizProzor=null;
    }

    public int getVreme() {
        return vremeZaJavljanje;
    }

    public void PostaviVremeZaOdgovor(int vremeZaOdgovor) {
        this.vremeZaOdgovor = vremeZaOdgovor;
    }

    @Override
    public void run() {
        int ukupnoVremeZaOdgovor=vremeZaOdgovor;
        int ukupnoVremeZaPrijavu=vremeZaJavljanje;

        while (vremeZaJavljanje>-1){
            prikazKontrolniProzor.setText(vremeZaJavljanje+"s");
            prikazKvizProzor.setText(vremeZaJavljanje+"s");
            KvizProzor.getPrikazTajmera().postaviPrikazTajmera(ukupnoVremeZaPrijavu,vremeZaJavljanje, Color.red);
            vremeZaJavljanje-=1;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        prikazKontrolniProzor.setText("");
        prikazKvizProzor.setText("");
        KontrolaJavljanja.Ugasi();

        while (vremeZaOdgovor>-1){
            prikazKontrolniProzor.setText(vremeZaOdgovor+"s");
            prikazKvizProzor.setText(vremeZaOdgovor+"s");
            KvizProzor.getPrikazTajmera().postaviPrikazTajmera(ukupnoVremeZaOdgovor,vremeZaOdgovor, Color.GREEN);
            vremeZaOdgovor-=1;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prikazKontrolniProzor.setText("");
            prikazKvizProzor.setText("");
        }
    }


    public void zaustaviTajmer(){
        vremeZaJavljanje=-1;
        vremeZaOdgovor=-1;
        KvizProzor.getPrikazTajmera().postaviPrikazTajmera(0,0, Color.GREEN);
        KontrolaJavljanja.Ugasi();

    }
}
