package grafika;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marko on 20-Jan-18. klasa koja crta tajmer!
 */
public class PrikazTajmera extends JPanel {


    private void paintComponent(Graphics g,int sirina ,Color novaBoja){
        g.setColor(novaBoja);
        g.clearRect(0,0,getWidth(),getHeight());
        g.fillRect(0,0,sirina,20);
    }

    public void postaviPrikazTajmera(int vremeUkupno,int vremeTrenutno,Color novaBoja){
        float odnos = (float)vremeTrenutno/(float)vremeUkupno;
        float sirina=odnos*this.getWidth();
        this.paintComponent(getGraphics(),(int)sirina,novaBoja);
    }

}
