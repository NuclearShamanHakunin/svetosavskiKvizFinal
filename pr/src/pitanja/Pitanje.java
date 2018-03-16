package pitanja;

import bazapodataka.BazaPodataka;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import static misc.globalnePromenljive.brojPitanja;

/**
 * Created by Marko on 27-Dec-17.
 *
 * Klasa koja definise pitanja...
 *
 */
public class Pitanje {
    private int id;
    private String tekstPitanja;
    private ArrayList<String> odgovori;
    private int redniBrojTacnogOdg;

    private Pitanje(int id, String tekstPitanja, ArrayList<String> odgovori, int redniBrojTacnogOdg) {
        this.id = id;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.redniBrojTacnogOdg = redniBrojTacnogOdg;
    }

    /**
     *
     * funkcija koja za zadati ResultSet formira objekat tipa Pitanje
     * @param rs result set koji dobijamo upitom
     * @return tPitanje, vracamo pitanje koje smo formirali
     */
    private static Pitanje formirajZaResultSet(java.sql.ResultSet rs){
        Pitanje tPitanje = null;
        try {
            int id=rs.getInt("id");
            String tekstPitanja = rs.getString("pitanje");
            String odgovori_niz[] = rs.getString("odgovori").split(";");
            int redniBrojTacnog = rs.getInt("redniBrojTacnog");
            ArrayList<String>odgovori= new ArrayList<String>(Arrays.asList(odgovori_niz)); //pretvaranje niza u listu
            tPitanje = new Pitanje(id,tekstPitanja,odgovori,redniBrojTacnog);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"GRESKA PRI PRISTUPU BAZI!!"+"\n"+e);
        }
        return tPitanje;
    }

    /**
     *
     * metoda koja ubacuje pitanje u bazu podataka
     *
     * @return vraca upit ili nulu prilikom greske
     */
    public int ubaciPitanje(){
        String odgovori_formatirano="";
        for(String s : odgovori){
            odgovori_formatirano += ";" + s;
        }
        odgovori_formatirano=odgovori_formatirano.substring(1);
        String sql = "INSERT INTO pitanja(id,pitanje,odgovori,redniBrojTacnog) VALUES ("+id+",'"+tekstPitanja+"','"+odgovori_formatirano+"',"+redniBrojTacnogOdg+")";

        try {
            return BazaPodataka.getInstanca().iudQuerry(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"GRESKA PRI PRISTUPU BAZI!!"+"\n"+e);
            return 0;
        }

    }

    private int uzmiNoviID() {
        return BazaPodataka.newIdQuerry("pitanja");
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public int getRedniBrojTacnogOdg() {
        return redniBrojTacnogOdg;
    }

    /**
     *
     * metoda koja pretvara objekat tipa pitanje u vektor
     *
     * @return v
     */
    private Vector konvertujUVektor(){
        Vector v = new Vector();


        v.addElement(id+1);
        v.addElement(tekstPitanja);
        for(int i = 0 ; i<5 ; i++){
            if(odgovori.size()>i){
                v.addElement(odgovori.get(i));
            }else {
                v.addElement("");
            }
        }
        v.addElement(redniBrojTacnogOdg);

        return v;
    }

    /**
     *
     * Metoda koja iz vektora pravi objekat klase pitanje
     *
     * @param v
     * @return
     */
    public static Pitanje konvertujIzVektora(Vector v){
        Pitanje p;
        ArrayList<String> odgovori = new ArrayList<>();
        for(int i = 2;i<7;i++){
            odgovori.add(v.get(i).toString());
            if(v.get(i).toString()==null)
                break;
        }


        p=new Pitanje(Integer.parseInt(v.get(0).toString()),v.get(1).toString(),odgovori,Integer.parseInt(v.get(7).toString()));

        return p;
    }


    /**
     *
     * metoda koja vraca vektor vektora napravljenih od objekta pitanje
     *
     * @return nizPitanja
     */
    public static Vector<Vector> ucitajPitanja(){
        Vector<Vector> nizPitanja = new Vector<>();
        String sql = "SELECT * FROM pitanja";

        try{
            ResultSet rs = BazaPodataka.getInstanca().select(sql);
            while (rs.next()){
                Pitanje tp = Pitanje.formirajZaResultSet(rs);
                Vector v = tp.konvertujUVektor();
                nizPitanja.addElement(v);
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"GRESKA PRI PRISTUPU BAZI!!"+"\n"+e);
        }

        return nizPitanja;
    }

    /**
     *
     * metoda koja ucitava igre iz baze podataka
     *
     * @param brIgre igra koju ucitava
     * @return niz vektora koje vraca, koji se dalje pretvaraju u pitanja
     */
    public static Vector<Vector> ucitajIgre(int brIgre){
        Vector<Vector> nizPitanja = new Vector<>();
        String sql = "SELECT igra"+brIgre+" FROM igre";

        try {
            ResultSet rs = BazaPodataka.getInstanca().select(sql);
            while (rs.next()){
                int id = rs.getInt("igra"+brIgre);
                if(rs.wasNull())
                    continue;
                ResultSet rs2 = BazaPodataka.getInstanca().select("SELECT * FROM pitanja WHERE id = "+id);
                Pitanje tp = Pitanje.formirajZaResultSet(rs2);
                Vector v = tp.konvertujUVektor();
                nizPitanja.addElement(v);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nizPitanja;
    }

    /**
     * metoda koja brise odredjenu tabelu u bazi podataka
     *
     * @param tabela koja se brise
     */
    private static void obrisiBazu(String tabela){
        String sql = "DELETE FROM "+tabela;
        try {
            BazaPodataka.getInstanca().iudQuerry(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"GRESKA PRI PRISTUPU BAZI!!"+"\n"+e);
        }
    }

    /**
     *
     * metoda koja azurira bazu podataka, tako sto obrise sve i iz tabele ucita ponov.
     * na ovaj nacin se sve promenjeno u tabeli odjednom ucita kada se stisne dugme sacuvaj...
     *
     * @param tabela koja se brise
     */
    public static void azurirajBazuPitanja(JTable tabela){
        String sql = "INSERT INTO pitanja(id,pitanje,odgovori,redniBrojTacnog) VALUES ";
        obrisiBazu("pitanja");
        DefaultTableModel dtm = (DefaultTableModel) tabela.getModel();
        int nRow = dtm.getRowCount();
        for (int i = 0 ; i < nRow ; i++){
            ArrayList<String>tempOdgovori=new ArrayList<>();    //u ovom nizu stringova cuvamo odgovore
            for(int j = 2;j<7;j++){//od 2 do 6 idu ponudjeni odgovori...
                if(dtm.getValueAt(i,j)!=null){
                    String s = dtm.getValueAt(i,j).toString();
                    tempOdgovori.add(s);
                }
            }
            Pitanje p = new Pitanje(Integer.parseInt(dtm.getValueAt(i,0).toString())-1,dtm.getValueAt(i,1).toString(),tempOdgovori,Integer.parseInt(dtm.getValueAt(i,7).toString()));
            //uzimamo podatke iz tabele i pravimo objekat, mora se cast iz objekta prvo sa tostring pa u parseInt kad nam treba integer!

            sql+=p.napraviUpit();
            sql+=",";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql+=";";

        izvrsiUpitPitanja(sql); //pravimo veliki upit
    }

    /**
     * cuva igre u bazu podataka
     *
     * podaci:
     * @param tabela1 igra1
     * @param tabela2 igra2
     * @param tabela3 igra3
     */
    public static void azurirajIgre(JTable tabela1,JTable tabela2,JTable tabela3){
        String sql = "INSERT INTO igre(id,igra1,igra2,igra3) VALUES ";
        DefaultTableModel dtm1,dtm2,dtm3;
        int nRow1,nRow2,nRow3;
        ArrayList<Integer>i1,i2,i3;
        obrisiBazu("igre");
        dtm1 = (DefaultTableModel)tabela1.getModel();dtm2 = (DefaultTableModel)tabela2.getModel();dtm3 = (DefaultTableModel)tabela3.getModel();
        nRow1=dtm1.getRowCount();nRow2=dtm2.getRowCount();nRow3=dtm3.getRowCount();
        i1=ucitajPitanjaUListu(nRow1,dtm1);i2=ucitajPitanjaUListu(nRow2,dtm2);i3=ucitajPitanjaUListu(nRow3,dtm3);

        for(int i = 0; i< brojPitanja; i++){
            String s="("+i+",";
            if(i>i1.size()-1){
                s+="null,";
            }else {
                s+=(i1.get(i)-1)+",";//redni br je veci od id u bazi, pa moramo smanjiti
            }
            if(i>i2.size()-1){
                s+="null,";
            }else {
                s+=(i2.get(i)-1)+",";
            }
            if(i>i3.size()-1){
                s+="null";
            }else {
                s+=(i3.get(i)-1);
            }
            s+="),";
            sql+=s;
        }
        sql = sql.substring(0, sql.length() - 1);
        sql+=";";izvrsiUpitPitanja(sql);
    }

    /**
     *
     * pomocna funkcija ucitava iz tablemodela u listu koju vraca
     *
     * @param nRow broj kolona
     * @param dtm table model gde su podaci
     * @return
     */
    private static ArrayList<Integer> ucitajPitanjaUListu(int nRow,DefaultTableModel dtm){
        ArrayList<Integer>lista=new ArrayList<>();
        for(int i = 0; i<nRow;i++){
            if(dtm.getValueAt(i,0)!=null){
                lista.add((Integer) dtm.getValueAt(i,0));
            }
        }
        return lista;
    }

    /**
     * pravi upit od vise redova od objekata
     * @return (u zagradi)
     */
    private String napraviUpit(){
        String sql;
        String odgovori_formatirano="";
        for(String s : odgovori){
            odgovori_formatirano += ";" + s;
        }
        odgovori_formatirano=odgovori_formatirano.substring(1);
        sql ="("+id+",'"+tekstPitanja+"','"+odgovori_formatirano+"',"+redniBrojTacnogOdg+")";

        return sql;
    }

    /**
     * izvrsava upit
     *
     * @param sql upit
     * @return odgovor
     */
    private static int izvrsiUpitPitanja(String sql){
        try {
            return BazaPodataka.getInstanca().iudQuerry(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"GRESKA PRI PRISTUPU BAZI!!"+"\n"+e);
            return 0;
        }
    }
}
