package misc;

import com.fazecast.jSerialComm.SerialPort;
import kviz.KontrolaJavljanja;

import javax.swing.*;
import java.util.Scanner;


/**
 * Created by Marko on 07-Jan-18.
 *
 *
 *
 */
public class UsbKonekcija {

    private static SerialPort izabranPort;
    private static int ser1,ser2;

    public static void DodajPortoveDropdown(JComboBox<String> meni){
        SerialPort[] sviPortovi = SerialPort.getCommPorts();
        meni.removeAllItems();
        for(int i=0;i<sviPortovi.length;i++)
            meni.addItem(sviPortovi[i].getSystemPortName());
    }

    public static void KonektujArduino(JComboBox<String> meni, JButton konektuj){
        if(konektuj.getText().equals("Poveži se")){
            izabranPort = SerialPort.getCommPort(meni.getSelectedItem().toString());
            izabranPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            if(izabranPort.openPort()){
                konektuj.setText("Prekini vezu");
                meni.setEnabled(false);
            }

            Thread thread = new Thread(() -> {
                Scanner scanner = new Scanner(izabranPort.getInputStream());
                while (scanner.hasNextInt()) {  //cekamo end of file, on nece doci...
                    try {
                        ser1 = scanner.nextInt();
                        ser2 = scanner.nextInt();
                        if(ser1==ser2){
                            System.out.println("dugme br: "+ser1);
                            KontrolaJavljanja.PrijaviEkipu(ser2-1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                scanner.close();
            });
            thread.start();
        }else {
            izabranPort.closePort();
            meni.setEnabled(true);
            konektuj.setText("Poveži se");
        }
    }

}


