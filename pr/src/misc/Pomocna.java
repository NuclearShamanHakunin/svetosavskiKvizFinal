package misc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Vector;

import static misc.globalnePromenljive.oznakeOdgovora;

/**
 * Created by Marko on 05-Jan-18.
 *
 * Pomocna klasa sa pomocnim metodama za rad aplikacije
 *
 */
public class Pomocna {

    /**
     * funkcija sakriva redove tabele sem nultog i prosledjenog
     *
     * @param tabela gde se sakriva
     * @param red koji se ne sakriva
     */
    public static void prikaziSamoKolonu(JTable tabela, int red){
        tabela.getColumnModel().getColumn(0).setMaxWidth(25);
        tabela.getColumnModel().getColumn(0).setMinWidth(25);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(25);
        tabela.getColumnModel().getColumn(0).setWidth(25);
        for(int i = 1;i<8;i++){
            if(i!=red){
                tabela.getColumnModel().getColumn(i).setMaxWidth(0);
                tabela.getColumnModel().getColumn(i).setMinWidth(0);
                tabela.getColumnModel().getColumn(i).setPreferredWidth(0);
                tabela.getColumnModel().getColumn(i).setWidth(0);
            }
        }
    }

    /**
     *
     * osluskivac na dugmetu koje ubacuje pitanja u neku tabelu (JTABLE)
     *
     * @param dugme dugme koje ubacuje
     * @param tabelaIzvor tabela iz koje se ubauje
     * @param tabelaOdrediste tabela u koju se ubacuje
     * @param drugaTabela1 druga tabela 1 - postoji radi provere kasnije u metodi
     * @param drugaTabela2 druga tabela 2
     */
    public static void postaviOsluskivacZaUbacivanjePitanja(JButton dugme,JTable tabelaIzvor,JTable tabelaOdrediste,JTable drugaTabela1,JTable drugaTabela2){
        dugme.addActionListener(e -> {
            if(tabelaOdrediste.getRowCount()<globalnePromenljive.brojPitanja){
                int izabraniRed = tabelaIzvor.getSelectedRow();
                if(izabraniRed!=-1){
                    DefaultTableModel modelt = (DefaultTableModel)tabelaOdrediste.getModel();
                    DefaultTableModel model=(DefaultTableModel)tabelaIzvor.getModel();
                    Vector red = (Vector)model.getDataVector().elementAt(izabraniRed);
                    if(!PitanjePostojiUIzabranim(red.firstElement(),tabelaOdrediste,drugaTabela1,drugaTabela2)){
                        modelt.addRow(red);
                    }
                }
            }
        });
    }

    /**
     * Proverava da li pitanje postoji u izabranim pitanjima (JTABLE)
     *
     * @param prviElement prvi element koji se ubacuje (ovde je to redni broj pitanja)...
     * @param tabelaOdrediste tabela u koju se ubacuje
     * @param drugaTabela1 druga tabela 1
     * @param drugaTabela2 druga tabela 2
     * @return postoji ili ne
     */
    private static boolean PitanjePostojiUIzabranim(Object prviElement,JTable tabelaOdrediste,JTable drugaTabela1,JTable drugaTabela2){
        boolean postoji = false;
        for(int i = 0; i<tabelaOdrediste.getRowCount();i++){
            if(prviElement.equals(tabelaOdrediste.getModel().getValueAt(i,0))){
                postoji=true;
                break;
            }
        }
        for(int i = 0; i<drugaTabela1.getRowCount();i++){
            if(prviElement.equals(drugaTabela1.getModel().getValueAt(i,0))){
                postoji=true;
                break;
            }
        }
        for(int i = 0; i<drugaTabela2.getRowCount();i++){
            if(prviElement.equals(drugaTabela2.getModel().getValueAt(i,0))){
                postoji=true;
                break;
            }
        }
        return postoji;
    }


    /**
     *
     *  funkcija gde se implementiraju osluskivaci fokusa, kliknes na jednu tabelu, polja sa ostalih tabela se deselectuju
     *  polja moraju ostati selectovana kada tabela gubi fokus da bi mogli skupiti stvari preko dugmeta!
     *
     * @param t1 glavna tabela sa pitanjima
     * @param t2 tabela sa igrom 2
     * @param t3 3
     * @param t4 4
     */
    public static void postaviOsluskivaceFokusa(JTable t1,JTable t2,JTable t3,JTable t4){
        JTable[] tabele = new JTable[4];
        tabele[0]=t1;tabele[1]=t2;tabele[2]=t3;tabele[3]=t4;

        for(int j = 0;j<4;j++){
            int finalJ = j;
            FocusListener f = new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    for(int i = 0; i<4;i++){
                        if(i!= finalJ)
                            tabele[i].clearSelection();
                    }

                }
            };
            tabele[j].addFocusListener(f);
        }

    }

    /**
     *
     * postavlja pocetne vrednosti hashmapu sa predjenim pitanjima na false (nisu predjena)
     *
     * @param mapa hashmapa u koju postavlja
     */
    public static void postaviPredjenaPitanja(HashMap<Integer,Boolean> mapa){
        for(int i = 0; i<30;i++){
            mapa.put(i,false);
        }
    }

    /**
     *
     * popunjava textarea sa pitanjima odredjene igre
     *
     * @param ta text area koji se popunjava
     * @param pitanja vektor vektora koji sadrzi informacije o izabranim pitanjima za igru
     */
    public static void popuniPoljePitanjima(JTextArea ta, Vector<Vector>pitanja){
        String textZaPolje = "";
        String noviRed = "\n";

        for(int i = 0;i<globalnePromenljive.brojPitanja;i++){
            textZaPolje+=pitanja.get(i).get(1).toString()+noviRed;
        }
        ta.setText(textZaPolje);
        Font font = ta.getFont();
        float size = font.getSize() - 3f;
        ta.setFont( font.deriveFont(size) );
        ta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ta.setEditable(false);
    }

    public static void popuniPoljeOdgovorima(JTextArea ta, Vector<Vector>pitanja, int trenutnoPitanje){
        String textZaPolje = "";
        String noviRed = "\n";
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);

        int tacnoPitanje = Integer.parseInt(pitanja.get(trenutnoPitanje).get(7).toString());

        for(int j = 0; j < 5; j++){
            String odgovor = pitanja.get(trenutnoPitanje).get(j+2).toString();
            if(!odgovor.equals(""))
                textZaPolje+=oznakeOdgovora[j]+odgovor+noviRed;
        }

        ta.setText(textZaPolje);

        try {
            int pocetakReda = ta.getLineStartOffset(tacnoPitanje-1);
            int krajReda = ta.getLineEndOffset(tacnoPitanje-1);
            ta.getHighlighter().addHighlight(pocetakReda,krajReda,painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static boolean SvaPitanjaPredjena(HashMap<Integer,Boolean>pitanja,int igra){
        boolean predjena=true;
        for(int i = (igra*10-10);i<igra*10;i++){
            if(!pitanja.get(i)){
                predjena=false;
                break;
            }
        }
        return predjena;
    }

    /**
     *
     * metoda koja oznacava koja su pitanja trenutno izabrana-zutom bojom, trenutno aktivna-zelenom bojom.
     * pitanja koja su vec bila pitana su oznacena crvenom bojom
     *
     * @param pokrenutaIgra igra koja je pokrenuta
     * @param redniBrojPitanja broj pitanja koje je oznaceno
     * @param pokrenuto booean, da li je pitanje pokrenuto
     * @param p1 text area igre1
     * @param p2 text area igre2
     * @param p3 text area igre3
     * @param mapa hashmapa sa informacijama o tome koje su igre bile pokrenute
     */
    public static void oznaciTrenutnoPitanje(int pokrenutaIgra,int redniBrojPitanja, boolean pokrenuto,JTextArea p1,JTextArea p2, JTextArea p3,HashMap<Integer,Boolean> mapa){
        //ternarni zato sto zasto da ne
        Highlighter.HighlightPainter painter = pokrenuto? new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN) : new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        Highlighter.HighlightPainter predjednoPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
        int pocetakReda;
        int krajReda;

        p1.getHighlighter().removeAllHighlights();
        p2.getHighlighter().removeAllHighlights();
        p3.getHighlighter().removeAllHighlights();

        try {
            for(int i = 0; i<10;i++){
                if(mapa.get(i)){
                    pocetakReda = p1.getLineStartOffset(i);
                    krajReda = p1.getLineEndOffset(i);
                    if((i==redniBrojPitanja && pokrenutaIgra==1))
                        p1.getHighlighter().addHighlight(pocetakReda, krajReda, painter);
                    else
                        p1.getHighlighter().addHighlight(pocetakReda, krajReda,predjednoPainter  );
                }
            }
            for(int i = 10;i<20;i++){
                if(mapa.get(i)){
                    pocetakReda = p2.getLineStartOffset(i-10);
                    krajReda = p2.getLineEndOffset(i-10);
                    if(((i-10)==(redniBrojPitanja) && pokrenutaIgra==2))
                        p2.getHighlighter().addHighlight(pocetakReda, krajReda, painter);
                    else
                        p2.getHighlighter().addHighlight(pocetakReda, krajReda,predjednoPainter  );
                }
            }
            for(int i = 20;i<30;i++){
                if(mapa.get(i)){
                    pocetakReda = p3.getLineStartOffset(i-20);
                    krajReda = p3.getLineEndOffset(i-20);
                    if(((i-20)==redniBrojPitanja && pokrenutaIgra==3))
                        p3.getHighlighter().addHighlight(pocetakReda, krajReda, painter);
                    else
                        p3.getHighlighter().addHighlight(pocetakReda, krajReda,predjednoPainter  );
                }
            }

           switch (pokrenutaIgra){
                case 1:
                    pocetakReda = p1.getLineStartOffset(redniBrojPitanja);
                    krajReda = p1.getLineEndOffset(redniBrojPitanja);
                    p1.getHighlighter().addHighlight(pocetakReda, krajReda, painter);
                    break;
                case 2:
                    pocetakReda = p2.getLineStartOffset(redniBrojPitanja);
                    krajReda = p2.getLineEndOffset(redniBrojPitanja);
                    p2.getHighlighter().addHighlight(pocetakReda, krajReda, painter);
                    break;
                case 3:
                    pocetakReda = p3.getLineStartOffset(redniBrojPitanja);
                    krajReda = p3.getLineEndOffset(redniBrojPitanja);
                    p3.getHighlighter().addHighlight(pocetakReda, krajReda, painter);
                    break;
                default:
                    break;
            }
        }catch (BadLocationException ex){
            ex.printStackTrace();
        }

    }

}
