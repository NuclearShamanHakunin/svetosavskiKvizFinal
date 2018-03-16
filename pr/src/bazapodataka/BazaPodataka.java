package bazapodataka;

/**
 * Created by Marko on 27-Dec-17.
 */

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Ova klasa omogucava rad sa bazom podata, sadrži osnovne metode za komunikaciju sa bazom.
 */
public class BazaPodataka {

    private java.sql.Connection conn;
    private static BazaPodataka instanca;

    /**
     * Konstruktor za konekciju ka bazi podataka. Ova metoda se povezuje na SQLite bazu.
     */
    private BazaPodataka()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = java.sql.DriverManager.getConnection("jdbc:sqlite:bazapitanja.db");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Doslo je do greske pri konekciji na bazu podataka");
        }
    }

    /**
     * Metoda koja vraca instancu baze podataka.
     *
     * @return instanca Instanca koja se vraca.
     */
    public static BazaPodataka getInstanca() {
        if (instanca == null)
            instanca = new BazaPodataka();
        return instanca;
    }

    /**
     * Metoda koja obavlja upite za UPIS/UPDATE/BRISANJE
     *
     * @param sql string za upit.
     * @return statement.executeUpdate(sql) - izvsavanje upita
     * @throws SQLException baca izuzetke vezane za rad sa SQL bazama podataka
     */
    public int iudQuerry(String sql) throws SQLException {
        java.sql.Statement statement = this.conn.createStatement();
        return statement.executeUpdate(sql);
    }

    /**
     * Metoda koja izvrsava upite za ucitavanje podataka iz baze.
     *
     * @param sql string za upit.
     * @return statement.executeQuery(sql) - izvsavanje upita
     * @throws SQLException baca izuzetke vezane za rad sa SQL bazama podataka
     */
    public ResultSet select(String sql) throws SQLException {
        java.sql.Statement statement = this.conn.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * Ova metoda vraca nov ID za odredjenu tabelu (najveci ID+1).
     *
     * @param tabela ime tabele za koju se vraca novi ID
     * @return nov_id ID koji metoda vraca
     */
    public static int newIdQuerry(String tabela) {
        int nov_id = 0;
        try {
            ResultSet rs = getInstanca().select("SELECT id FROM " + tabela + " ORDER BY id DESC LIMIT 1");
            if (!rs.next()) {
                return 0;
            }
            nov_id = rs.getInt("id") + 1;
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nov_id;
    }

    /**
     * Metoda koja proverava da li postoje podaci u zadatoj tabeli za zadati ID.
     *
     * @param tabela ime tabele u kojoj se traži ID
     * @param id vrednost ID koja se traži.
     *
     */
    public static void proveraZaID(String tabela, int id){
        try {
            if (!getInstanca().select("SELECT * FROM " + tabela + " where ID = '" + id + "'").next()) {
                JOptionPane.showMessageDialog(null,"NEMA PODATAKA");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Metoda koja proverava da li postoji odredjena tabela unutar baze podataka.
     *
     * @param imeTabele ime tabele koja se traži
     * @return tPostoji boolean vrednost koja odredjuje postojanje tabele.
     */
    public boolean tabelaPostoji(String imeTabele) throws SQLException {
        boolean tPostoji = false;
        ResultSet rs = this.conn.getMetaData().getTables(null, null, imeTabele, null);
        try {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if ((tName != null) && (tName.equals(imeTabele))) {
                    tPostoji = true;
                    break;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return tPostoji;
    }
}


