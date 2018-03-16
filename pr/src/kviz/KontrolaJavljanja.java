package kviz;

import grafika.PrikazEkipa;


import java.util.Date;
import java.util.HashMap;

import static misc.globalnePromenljive.brojEkipa;

/**
 * Created by Marko on 20-Jan-18.
 */
public class KontrolaJavljanja implements Runnable {
    private static Ekipa[] ekipe = new Ekipa[brojEkipa];
    private static volatile Boolean radi;
    private static Integer redJavljanja;
    private static boolean testMode=false;
    private static PrikazEkipa[] PrikazKontrolni,PrikazKvizProzor;
    private static Thread t;
    private static HashMap<Integer,Integer> prozvaneEkipe = new HashMap<>();


    static {
        for(int i = 0; i<brojEkipa;i++){
            prozvaneEkipe.put(i,0);
        }
    }

    public KontrolaJavljanja(Ekipa[] ekipe, PrikazEkipa[] PrikazKontrolni, PrikazEkipa[] PrikazKvizProzor) {
        KontrolaJavljanja.ekipe = ekipe;
        radi=false;
        redJavljanja=1;
        KontrolaJavljanja.PrikazKontrolni =PrikazKontrolni;
        KontrolaJavljanja.PrikazKvizProzor =PrikazKvizProzor;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        radi=true;
        while (radi){
            if(testMode)
                PrijaviEkipu((int)(Math.random()*5));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Ugasi() {
        KontrolaJavljanja.radi = false;
        redJavljanja=0;
        t.stop();   //TODO: IZ NEKOG RAZLOGA SAMO OVO HOCE DA UGASI TRHEAD?!!?!
    }

    public static void ToglujTestMod(){
        testMode=!testMode;
    }

    public static void ProzoviEkipuTrecaIgra(){
        int broj;

        do broj = (int) (Math.random() * 5);
        while (prozvaneEkipe.get(broj)>1);

        prozvaneEkipe.put(broj,prozvaneEkipe.get(broj)+1);
        ekipe[broj].setJavioSe(true);
        ekipe[broj].setRedJavljanja(1);
        PrikazKontrolni[broj].setRedJavljanja(redJavljanja);
        PrikazKvizProzor[broj].setRedJavljanja(redJavljanja);
    }

    public static void PrijaviEkipu(int redniBroj){
        if(!ekipe[redniBroj].DaLiSeJavio() && radi){
            ekipe[redniBroj].setRedJavljanja(redJavljanja);
            PrikazKontrolni[redniBroj].setRedJavljanja(redJavljanja);
            PrikazKvizProzor[redniBroj].setRedJavljanja(redJavljanja);
            redJavljanja++;
        }
    }
}
