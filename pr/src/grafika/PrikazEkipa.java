package grafika;

import kviz.Ekipa;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marko on 19-Jan-18.
 */
public class PrikazEkipa{
    private JLabel imeEkipe;
    private JLabel brPoena;
    private JLabel redJavljanja;
    private JPanel panel;

    public PrikazEkipa(JPanel panel, Ekipa ekipa) {
        this.panel=panel;
        panel.setLayout(new GridLayout(3,2));
        imeEkipe = new JLabel(ekipa.getNaziv());
        brPoena = new JLabel(ekipa.getPoeni()+"");
        redJavljanja= new JLabel();

        panel.add(new JLabel("EKIPA: ",SwingConstants.CENTER));
        imeEkipe.setFont(new Font("Serif",Font.BOLD,28));panel.add(imeEkipe);
        panel.add(new JLabel("POENI:",SwingConstants.CENTER));
        brPoena.setFont(new Font("Serif",Font.BOLD,32));panel.add(brPoena);
        panel.add(new JLabel("RED: ",SwingConstants.CENTER));
        redJavljanja.setFont(new Font("Serif",Font.BOLD,32));panel.add(redJavljanja);
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBorder(BorderFactory.createMatteBorder(4,4,4,4,Color.BLACK));
    }

    public static void refreshEkipe(Ekipa[] ekipe,PrikazEkipa[] prikazEkipa){
        for(int i = 0 ; i < 5; i++){
           prikazEkipa[i].refresh(ekipe[i]);
        }
    }

    private void refresh(Ekipa e){
        imeEkipe.setText(e.getNaziv());
        brPoena.setText(e.getPoeni()+"");
        if(e.getRedJavljanja()==10) {
            redJavljanja.setText("NE");
            panel.setBackground(Color.RED);
        }
        else if(e.getRedJavljanja()!=0){
            redJavljanja.setText(e.getRedJavljanja()+"");
            panel.setBackground(Color.lightGray);
        }
        else{
            redJavljanja.setText("");
            panel.setBackground(Color.lightGray);
        }
        if(e.getRedJavljanja()==1){
            redJavljanja.setText(e.getRedJavljanja()+"");
            panel.setBackground(Color.GREEN);
        }

    }


    public void setImeEkipe(String imeEkipe) {
        this.imeEkipe.setText(imeEkipe);
    }

    public void setBrPoena(int brPoena) {
        this.brPoena.setText(brPoena+"");
    }

    public void setRedJavljanja(int redJavljanja) {
        if(redJavljanja==1)
            panel.setBackground(Color.GREEN);
        this.redJavljanja.setText(redJavljanja+"");
    }

    public void setBoja(Color c){
        panel.setBackground(c);
    }
}