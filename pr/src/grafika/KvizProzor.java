package grafika;

import kviz.Ekipa;
import misc.globalnePromenljive;
import pitanja.Pitanje;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static misc.globalnePromenljive.brojEkipa;
import static misc.globalnePromenljive.oznakeOdgovora;
import static misc.globalnePromenljive.pokrenutaIgra;


/**
 * Created by Marko on 27-Dec-17.
 *
 * Kviz prozor je klasa u kojoj se prikazuje tok kviza ucesnicima. Postoje tri igre, koje se prikazuju na odvojenim panelima.
 * Ostvaren je preko CardLayouta.
 *
 */
public class KvizProzor extends JFrame {
    private static GraphicsDevice ekran2 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1];     //uzimanje drugog ekrana
    private JTextArea tekstPitanja = new JTextArea("");
    private JLabel[] tekstoviOdgovora = new JLabel[5];
    private JLabel tekstTacnogOdgovora = new JLabel();
    private PrikazEkipa[] ekipeKontenjer = new PrikazEkipa[brojEkipa];
    private JLabel[] tabelaEkipe = new JLabel[5];
    private JLabel tajmer = new JLabel();
    private JPanel p,tabelaRezultata;
    private static PrikazTajmera prikazTajmera = new PrikazTajmera();
    public static PrikazTajmera getPrikazTajmera() {
        return prikazTajmera;
    }
    private Ekipa[] ekipe;

    /**
     *
     * konstruktor u kome se pravi prozor gde se nalazi kviz, fullscreen prozor je na sekundarnom ekranu (projektoru)
     */
    public KvizProzor(Ekipa[] ekipe){

        setUndecorated(true);   // da nema window okvira...
        ekran2.setFullScreenWindow(this);   //u kom prozoru je fullscreen ekran
        this.ekipe=ekipe;
        getContentPane().setLayout(new CardLayout());
        tabelaRezultata = new JPanel();tabelaRezultata.setLayout(new GridLayout(6,1,50,10));tabelaRezultata.setBorder(new EmptyBorder(20,50,50,50));
        JLabel naslovTabeleRezultata = new JLabel("ТАБЕЛА РЕЗУЛТАТА: ");naslovTabeleRezultata.setFont(new Font("Arial",Font.BOLD,48));tabelaRezultata.add(naslovTabeleRezultata);
        for(int i = 0 ; i < tabelaEkipe.length;i++){
            tabelaEkipe[i]=new JLabel();
            tabelaEkipe[i].setHorizontalAlignment(JLabel.CENTER);
            tabelaEkipe[i].setOpaque(true);tabelaEkipe[i].setBackground(Color.WHITE);
            tabelaEkipe[i].setFont(new Font("Arial",Font.PLAIN,48));
            tabelaEkipe[i].setBorder(BorderFactory.createMatteBorder(4,4,4,4,Color.BLACK));
            tabelaRezultata.add(tabelaEkipe[i]);
        }
        p = new JPanel();
        p.setLayout(null);
        add(p);
        add(tabelaRezultata);

        //prikazi ekipa kao klasa;
        JPanel[] prikaziEkipa = new JPanel[brojEkipa];
        for(int i = 0; i<brojEkipa; i++){
            prikaziEkipa[i] = new JPanel();
            ekipeKontenjer[i]=new PrikazEkipa(prikaziEkipa[i], ekipe[i]);
        }

        tekstPitanja.setEditable(false);tekstPitanja.setLineWrap(true);tekstPitanja.setWrapStyleWord(true);
        p.add(tekstPitanja);
        for(int i = 0;i<5;i++){
            tekstoviOdgovora[i]=new JLabel("");
            p.add(tekstoviOdgovora[i]);
            tekstoviOdgovora[i].setOpaque(true);
            tekstoviOdgovora[i].setBackground(Color.WHITE);
        }



        //odavde se namesta layout i mozda ne bi bilo lose da je odvojena metoda
        Rectangle r = getBounds(); //pravimo svoj layout ovako
        int h =r.height;
        int w =r.width;
        tekstPitanja.setBounds(w/20,h/10,w*9/10,h/7);
        tekstPitanja.setFont(new Font("Arial", Font.PLAIN,42));
        tekstPitanja.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));

        for(int i = 0;i<5;i++){
            double top = h*(0.35+i*0.05);
            double left = w*0.1;
            tekstoviOdgovora[i].setBounds((int)left,(int)top,(int)(w*0.8),h/16);
            tekstoviOdgovora[i].setFont(new Font("Arial",Font.PLAIN,26));
            tekstoviOdgovora[i].setBorder(BorderFactory.createMatteBorder(0,2,0,2,Color.BLACK));
        }
        tekstoviOdgovora[0].setBorder(BorderFactory.createMatteBorder(2,2,0,2,Color.BLACK));
        tekstoviOdgovora[4].setBorder(BorderFactory.createMatteBorder(0,2,2,2,Color.BLACK));

        tekstTacnogOdgovora.setBounds((int)(w*0.1),(int)(h*0.65),(int)(w*0.8),h/11);
        tekstTacnogOdgovora.setOpaque(true);tekstTacnogOdgovora.setBackground(Color.GREEN);
        tekstTacnogOdgovora.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
        tekstTacnogOdgovora.setFont(new Font("Arial",Font.PLAIN,36));
        tekstTacnogOdgovora.setHorizontalAlignment(JLabel.CENTER);
        p.add(tekstTacnogOdgovora);
        tekstTacnogOdgovora.setVisible(false);

        for(int i = 0;i<brojEkipa;i++){
            double top = h*0.8;
            double left = w*(0.01+i*0.20);
            prikaziEkipa[i].setLocation((int)left,(int)top);
            prikaziEkipa[i].setSize((int)(w*0.18),w/10);
            p.add(prikaziEkipa[i]);
        }


        p.add(prikazTajmera);prikazTajmera.setBounds(w/20,h/10+h/7,w*9/10,h/25);
        tajmer.setOpaque(true);tajmer.setBackground(Color.WHITE);tajmer.setHorizontalAlignment(JLabel.CENTER);
        tajmer.setFont(new Font("Arial",Font.PLAIN,28));
        tajmer.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
        p.add(tajmer);tajmer.setBounds(10,h/10,w/26,h/15);

        setVisible(true);
    }


    public PrikazEkipa[] getEkipeKontenjeri() {
        return ekipeKontenjer;
    }

    public JLabel getTajmer() {
        return tajmer;
    }

    /**
     *
     * Funkcija koja na osnovu parametra broj_igre pokrece izabranu igru (iz kontrolnog prozora)
     *
     * @param broj_igre int koji bira igru
     */
    public void pokreniIgru(int broj_igre){ //mora preko ref zato sto je static
        switch (broj_igre){
            case 1:
                for(int i = 0;i<5;i++){
                    if(!tekstoviOdgovora[i].isVisible())
                        tekstoviOdgovora[i].setVisible(true);
                }
                break;
            case 2:
                for(int i = 0;i<5;i++){
                    tekstoviOdgovora[i].setVisible(false);
                }
                break;
            case 3:
                for(int i = 0;i<5;i++){
                    tekstoviOdgovora[i].setVisible(false);
                }
                break;
            default:
                break;
        }
    }

    public PrikazEkipa getEkipeKontenjer(int i) {
        return ekipeKontenjer[i];
    }

    public JLabel getTekstTacnogOdgovora() {
        return tekstTacnogOdgovora;
    }

    public void paliGasiTabeluRezultata(JButton dugme){
        if(p.isVisible()){
            p.setVisible(false);
            tabelaRezultata.setVisible(true);
            dugme.setText("Prikaži kviz");
            String[] imeEkipa = new String[5];
            float[] poeniEkipa = new float[5];
            for(int i = 0 ; i < 5 ; i++){
                poeniEkipa[i]=ekipe[i].getPoeni();
                imeEkipa[i]=ekipe[i].getNaziv();
            }

            float[] bonusZaPrve = Ekipa.getBonusZaPrve();
            for(int i = 0 ; i < 5 ; i++){
                poeniEkipa[i]+=bonusZaPrve[i];
            }

            float temp;
            String tString;
            for (int i = 1; i < poeniEkipa.length; i++) {
                for(int j = i ; j > 0 ; j--){
                    if(poeniEkipa[j] > poeniEkipa[j-1]){
                        temp = poeniEkipa[j];
                        poeniEkipa[j] = poeniEkipa[j-1];
                        poeniEkipa[j-1] = temp;
                        tString=imeEkipa[j];
                        imeEkipa[j] = imeEkipa[j-1];
                        imeEkipa[j-1]=tString;
                    }
                }
            }

            for(int i = 0 ; i < 5 ; i++){
                tabelaEkipe[i].setText(globalnePromenljive.oznakeMesta[i]+" место: "+ "Екипа - " + imeEkipa[i] + "       Поени - " + (int)poeniEkipa[i]);
            }

        }else {
            p.setVisible(true);
            tabelaRezultata.setVisible(false);
            dugme.setText("Prikaži tabelu");
        }
    }

    public void startujPitanje(Pitanje p){
        ArrayList<String> odgovori = p.getOdgovori();
        tekstTacnogOdgovora.setVisible(false);
        switch (pokrenutaIgra){
            case 1:
                tekstPitanja.setText(p.getTekstPitanja());
                for(int i = 0; i<odgovori.size();i++){
                    if(!odgovori.get(i).equals(""))
                        tekstoviOdgovora[i].setText(oznakeOdgovora[i]+odgovori.get(i));
                    else {
                        tekstoviOdgovora[i].setText("");
                    }
                }
                break;
            case 2:
                tekstPitanja.setText(p.getTekstPitanja());
                for(int i = 0; i<odgovori.size();i++)
                    tekstoviOdgovora[i].setText("");
                break;
            case 3:
                tekstPitanja.setText(p.getTekstPitanja());
                for(int i = 0; i<odgovori.size();i++)
                    tekstoviOdgovora[i].setText("");
                break;
            default:
        }
    }
}
