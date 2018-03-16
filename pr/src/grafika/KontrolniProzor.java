package grafika;

import kviz.*;
import kviz.Tajmer;
import misc.Pomocna;
import misc.UsbKonekcija;
import pitanja.Pitanje;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import static misc.globalnePromenljive.*;


/**
 * Created by Marko on 27-Dec-17.
 *
 * Kontrolni prozor je klasa u kojoj se tok kviza kontrolise. Postoje i opcije za pristup i promenu baze pitanja.
 *
 */
public class KontrolniProzor extends JFrame implements ActionListener{

    private static boolean pokrenutoPitanje = false;
    private static KvizProzor kp=null;
    private JTable tabelaPitanja,tabelaIzabranihPitanjaIgra1,tabelaIzabranihPitanjaIgra2,tabelaIzabranihPitanjaIgra3;
    private JButton pokreniKviz,sledecePitanje,prikazTacnogOdgovora,pokreniIgru1,pokreniIgru2,pokreniIgru3;
    private JPanel tab1,tab2;
    private static Vector<Vector> podaci,izabranaPitanjaIgra1,izabranaPitanjaIgra2,izabranaPitanjaIgra3;
    private Ekipa[] ekipe = new Ekipa[brojEkipa];
    private static JLabel trenutnoPitanjeLabela;
    private static JTextArea igra1,igra2,igra3,odgovori;
    private static HashMap<Integer,Boolean> predjenaPitanja = new HashMap<>(brojPitanja);
    private PrikazEkipa[] ekipeKontenjeri = new PrikazEkipa[brojEkipa]; //ovo su paneli u kojima su ekipe u kontrolnom prozoru
    private JLabel tajmer = new JLabel(); //TODO: skloniti t
    private static Tajmer kvizTajmer = new Tajmer(); //samo jedan sme da bude
    private static KontrolaJavljanja kontrolaJavljanja;

    public KontrolniProzor(String title){
        super(title);
        setSize(1200,800);
        Pomocna.postaviPredjenaPitanja(predjenaPitanja);
        for(int i = 0;i<ekipe.length;i++){ekipe[i]=new Ekipa();} //da ne bude nullptr kada se pokrene prozor...
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("sts2.png")));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        napraviTabPristupBaziPitanja();
        napraviTabPostavke();

        JTabbedPane tabMeni = new JTabbedPane();
        tabMeni.addTab("Kontrola",tab1);
        tabMeni.addTab("Pristup bazi pitanja",tab2);
        getContentPane().add(tabMeni);

        setVisible(true);
    }

    /**
     *
     * Dodavanje elemenata u tab za pristup baze pitanja, na panel tab2
     *
     */
    private void napraviTabPristupBaziPitanja() {
        Vector<String> imenaKolona=new Vector<>();    //JTable prima imena kolona kao vektor
        String[] kolone = {"br.","Pitanje", "Odgovor 1", "Odgovor 2", "Odgovor 3", "Odgovor 4", "Odgovor 5","Tačan"};
        imenaKolona.addAll(Arrays.asList(kolone)); //ubacivanje kolona

        podaci = Pitanje.ucitajPitanja(); //funkcija za ucitavanje pitanja u vektor vektora
        izabranaPitanjaIgra1 = Pitanje.ucitajIgre(1);
        izabranaPitanjaIgra2 = Pitanje.ucitajIgre(2);
        izabranaPitanjaIgra3 = Pitanje.ucitajIgre(3);

        tab2 = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        tab2.setLayout(gridbag);
        GridBagConstraints gbc = new GridBagConstraints();  //granice za layout
        gbc.fill=GridBagConstraints.BOTH;
        gbc.weightx=1d; //da se popuni ceo prozor
        gbc.weighty=1d;
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridheight=2;

        JPanel tab2Glavni = new JPanel();   //tab gde se nalazi cela tabela sa pitanjima i dugmici za kontrolu pitanja
        gridbag.setConstraints(tab2Glavni,gbc);
        tab2.add(tab2Glavni);
        tab2Glavni.setLayout(new BorderLayout());

        tabelaPitanja = new JTable(new DefaultTableModel(podaci,imenaKolona));
        DefaultTableModel model=(DefaultTableModel)tabelaPitanja.getModel();    //uzimanje modela za kasnije koriscenje
        tabelaPitanja.setFillsViewportHeight(true);
        JScrollPane kontenjerZaTabelu = new JScrollPane(tabelaPitanja);tab2Glavni.add("Center",kontenjerZaTabelu); //skrol za tabelu
        tabelaPitanja.getColumnModel().getColumn(0).setMaxWidth(28);tabelaPitanja.getColumnModel().getColumn(0).setMinWidth(28);
        tabelaPitanja.getColumnModel().getColumn(1).setPreferredWidth(500); //sirina druge kolone
        tabelaPitanja.getColumnModel().getColumn(7).setMaxWidth(50);tabelaPitanja.getColumnModel().getColumn(7).setMinWidth(50);    //namestanje dimenzija


        JPanel panelZaDugmad= new JPanel();panelZaDugmad.setBorder(new EmptyBorder(10,10,10,10));
        panelZaDugmad.setLayout(new GridLayout(1,6,35,5));

        tab2Glavni.add("South",panelZaDugmad);
        JButton dodajPitanje=new JButton("Dodaj pitanje"); panelZaDugmad.add(dodajPitanje);
        dodajPitanje.addActionListener(e -> {
            model.addRow(new Object[]{tabelaPitanja.getRowCount()+1}); //ubacivanje jos jednog reda
            kontenjerZaTabelu.validate();   //mora da se pozove da bi dobio pravu vrednost u slecedoj liniji koda
            kontenjerZaTabelu.getVerticalScrollBar().setValue(kontenjerZaTabelu.getVerticalScrollBar().getMaximum());   //skrolujemo na dno, tako sto uzimamo skrolbar verikalni i njegovu maks vrednost
        });

        JButton obrisiPitanje = new JButton("Obriši pitanje"); panelZaDugmad.add(obrisiPitanje);
        obrisiPitanje.addActionListener(e -> {
            int izabrani_red=tabelaPitanja.getSelectedRow();
            if(izabrani_red==-1){   //da ne baca greske jvm kad nista nije izabrano...
                if(tabelaIzabranihPitanjaIgra1.getSelectedRow()!=-1){
                    DefaultTableModel tmodel=(DefaultTableModel)tabelaIzabranihPitanjaIgra1.getModel();
                    tmodel.removeRow(tabelaIzabranihPitanjaIgra1.getSelectedRow());
                }else if(tabelaIzabranihPitanjaIgra2.getSelectedRow()!=-1){
                    DefaultTableModel tmodel=(DefaultTableModel)tabelaIzabranihPitanjaIgra2.getModel();
                    tmodel.removeRow(tabelaIzabranihPitanjaIgra2.getSelectedRow());
                }else if(tabelaIzabranihPitanjaIgra3.getSelectedRow()!=-1){
                    DefaultTableModel tmodel=(DefaultTableModel)tabelaIzabranihPitanjaIgra3.getModel();
                    tmodel.removeRow(tabelaIzabranihPitanjaIgra3.getSelectedRow());
                }
            }
            else {
                model.removeRow(izabrani_red);
                for(int i = 0;i<tabelaPitanja.getRowCount();i++){
                    tabelaPitanja.setValueAt(i+1,i,0);
                }
            }
        });

        JButton sacuvajIzmene=new JButton("Sačuvaj izmene");panelZaDugmad.add(sacuvajIzmene);
        sacuvajIzmene.addActionListener(e -> {
        //todo: DA LI JE PAMENTO DA SE SVAKI PUT KADA SE SACUVA, CELA TABELA SACUVAVA U BAZU... naravno da nije pametno...
            //todo: Unutar ovoga bi trebalo i da se proveri sta se sme cuvati u bazu podataka!!
            //todo: update umesto sacuvavanja svega... uostalom nemam vremena za ovo sad, sledece godine
            Pitanje.azurirajBazuPitanja(tabelaPitanja);
        });

        //////////////// DONJI DEO PANELA


        JPanel tab2Sporedni = new JPanel(); //panel gde stoje izabrana pitanja
        gbc.gridheight=1;
        gbc.gridy=2;
        gridbag.setConstraints(tab2Sporedni,gbc);

        tab2Sporedni.setLayout(new BorderLayout());
        JPanel tab2SporedniTabele = new JPanel(new GridLayout(1,3,5,5));tab2Sporedni.add("Center",tab2SporedniTabele);
        JPanel tab2SporedniNazivi = new JPanel(new GridLayout(1,3,5,5));tab2Sporedni.add("North",tab2SporedniNazivi);
        tab2.add(tab2Sporedni);
        tab2SporedniNazivi.add(new JLabel("Igra 1",JLabel.CENTER));tab2SporedniNazivi.add(new JLabel("Igra 2",JLabel.CENTER));tab2SporedniNazivi.add(new JLabel("Igra 3",JLabel.CENTER));
        tabelaIzabranihPitanjaIgra1=new JTable(new DefaultTableModel(izabranaPitanjaIgra1,imenaKolona));tab2SporedniTabele.add(tabelaIzabranihPitanjaIgra1);
        tabelaIzabranihPitanjaIgra2=new JTable(new DefaultTableModel(izabranaPitanjaIgra2,imenaKolona));tab2SporedniTabele.add(tabelaIzabranihPitanjaIgra2);
        tabelaIzabranihPitanjaIgra3=new JTable(new DefaultTableModel(izabranaPitanjaIgra3,imenaKolona));tab2SporedniTabele.add(tabelaIzabranihPitanjaIgra3);

        Pomocna.postaviOsluskivaceFokusa(tabelaPitanja,tabelaIzabranihPitanjaIgra1,tabelaIzabranihPitanjaIgra2,tabelaIzabranihPitanjaIgra3);    //funkcija koja namesta fokuse

        Pomocna.prikaziSamoKolonu(tabelaIzabranihPitanjaIgra1,1);   //funckije koje namestaju koja kolona se prikazuje (uz nultu)
        Pomocna.prikaziSamoKolonu(tabelaIzabranihPitanjaIgra2,1);
        Pomocna.prikaziSamoKolonu(tabelaIzabranihPitanjaIgra3,1);


        JButton ubaciUPrvu = new JButton("Ubaci u igru 1");panelZaDugmad.add(ubaciUPrvu);
        Pomocna.postaviOsluskivacZaUbacivanjePitanja(ubaciUPrvu,tabelaPitanja,tabelaIzabranihPitanjaIgra1,tabelaIzabranihPitanjaIgra2,tabelaIzabranihPitanjaIgra3); //pomocna funkcija koja ubacuje osluskivac za ubacivanje pitanja

        JButton ubaciUDrugu = new JButton("Ubaci u igru 2");panelZaDugmad.add(ubaciUDrugu);
        Pomocna.postaviOsluskivacZaUbacivanjePitanja(ubaciUDrugu,tabelaPitanja,tabelaIzabranihPitanjaIgra2,tabelaIzabranihPitanjaIgra1,tabelaIzabranihPitanjaIgra3);

        JButton ubaciUTrecu = new JButton("Ubaci u igru 3");panelZaDugmad.add(ubaciUTrecu);
        Pomocna.postaviOsluskivacZaUbacivanjePitanja(ubaciUTrecu,tabelaPitanja,tabelaIzabranihPitanjaIgra3,tabelaIzabranihPitanjaIgra1,tabelaIzabranihPitanjaIgra2);

        JButton sacuvajIgre = new JButton("Sačuvaj igre");panelZaDugmad.add(sacuvajIgre);
        sacuvajIgre.addActionListener(e -> Pitanje.azurirajIgre(tabelaIzabranihPitanjaIgra1,tabelaIzabranihPitanjaIgra2,tabelaIzabranihPitanjaIgra3));

    }

    /**
     *
     * Dodavanje elemenata u glavni tab, na panel tab1
     *
     */
    private void napraviTabPostavke() {
        tab1 = new JPanel();
        tab1.setLayout(new GridLayout(5,1,0,10));

        //ovde pocinje kontrola aplikacije
        JPanel panelKontrola = new JPanel();panelKontrola.setLayout(new GridLayout(1,3));
        JPanel kontrolaIgara = new JPanel();panelKontrola.add(kontrolaIgara);
        JPanel tajmerPanel = new JPanel();panelKontrola.add(tajmerPanel);tajmerPanel.setLayout(new GridLayout(1,1));
        tajmer.setHorizontalAlignment(JLabel.CENTER);tajmer.setFont(new Font("Serif",Font.PLAIN,42));tajmerPanel.add(tajmer);
        JPanel kontrolaKonekcije = new JPanel();panelKontrola.add(kontrolaKonekcije);kontrolaKonekcije.setLayout(new GridLayout(2,3,5,50));
        pokreniKviz = new JButton("Pokreni kviz");kontrolaIgara.add(pokreniKviz);pokreniKviz.addActionListener(this);
        pokreniIgru1 = new JButton("igra1");kontrolaIgara.add(pokreniIgru1);pokreniIgru1.addActionListener(this);
        pokreniIgru2 = new JButton("igra2");kontrolaIgara.add(pokreniIgru2);pokreniIgru2.addActionListener(this);
        pokreniIgru3 = new JButton("igra3");kontrolaIgara.add(pokreniIgru3);pokreniIgru3.addActionListener(this);
        tab1.add(panelKontrola);
        JButton prikaziTabelu = new JButton("Prikaži tabelu");kontrolaIgara.add(prikaziTabelu);
        prikaziTabelu.addActionListener(e -> {
            if(kp!=null)
                kp.paliGasiTabeluRezultata(prikaziTabelu);
        });

        JComboBox<String>portList = new JComboBox<String>();kontrolaKonekcije.add(portList);
        UsbKonekcija.DodajPortoveDropdown(portList);
        JButton konektujArduino = new JButton("Poveži se");kontrolaKonekcije.add(konektujArduino);
        konektujArduino.addActionListener(e -> UsbKonekcija.KonektujArduino(portList,konektujArduino));
        JButton portRefresh = new JButton("Osvezi portove");kontrolaKonekcije.add(portRefresh);
        kontrolaKonekcije.add(new JLabel("")); kontrolaKonekcije.add(new JLabel(""));
        JCheckBox testMod = new JCheckBox("simulacija kviza");
        testMod.addActionListener(e -> KontrolaJavljanja.ToglujTestMod());
        kontrolaKonekcije.add(testMod);
        portRefresh.addActionListener(e -> UsbKonekcija.DodajPortoveDropdown(portList));


        //ovde pocinje podesavanje timova
        JLabel[] imenaEkipa = new JLabel[brojEkipa];
        JLabel[] bodoviEkipa = new JLabel[brojEkipa];
        JButton[] podesiIme = new JButton[brojEkipa];
        JTextField[] unesiIme = new JTextField[brojEkipa];
        JPanel[] panelDugmeZaBodove = new JPanel[brojEkipa];
        JTextField[] unesiPoene = new JTextField[brojEkipa];
        JButton[] podesiPoene = new JButton[brojEkipa];

        for(int i = 0; i< brojEkipa; i++){
            imenaEkipa[i]=new JLabel("Ekipa "+(i+1)+":");
            bodoviEkipa[i]=new JLabel("Bodovi: ");
            podesiIme[i]=new JButton("Unesi ime ekipe "+(i+1));
            unesiIme[i]= new JTextField();
            panelDugmeZaBodove[i]=new JPanel();
            int finalI = i;
            podesiIme[i].addActionListener(e -> {
                if(ekipe[finalI]==null)
                    ekipe[finalI]=new Ekipa(unesiIme[finalI].getText(),0);

                else if(!unesiIme[finalI].getText().equals(""))
                    ekipe[finalI].setNaziv(unesiIme[finalI].getText());

                if(kp!=null)
                    kp.getEkipeKontenjer(finalI).setImeEkipe(unesiIme[finalI].getText());   //ovo se menja samo ako je prozor kviza upaljen

                ekipeKontenjeri[finalI].setImeEkipe(unesiIme[finalI].getText());
                imenaEkipa[finalI].setText("Ekipa "+(finalI+1)+": "+unesiIme[finalI].getText());
            });
            unesiPoene[i]=new JTextField();
            unesiPoene[i].addKeyListener(new KeyAdapter() {//OVIM NE DAMO DA SE KUCA NISTA SEM CIFARA I MINUSA!
                @Override
                public void keyTyped(KeyEvent e) {  //OVO NE DA DA PISES KARAKTERE, SAMO BROJEVE
                    char vchar = e.getKeyChar();
                    if (!(Character.isDigit(vchar)) && vchar!=KeyEvent.VK_MINUS)
                        e.consume();
                }
            });
            podesiPoene[i]=new JButton("podesi");
            podesiPoene[i].addActionListener(e -> {
                if(ekipe[finalI]!=null && !unesiPoene[finalI].getText().equals("")){
                    ekipe[finalI].setPoeni(Integer.parseInt(unesiPoene[finalI].getText()));
                    bodoviEkipa[finalI].setText("Bodovi: "+ekipe[finalI].getPoeni());
                }
                if(kp!=null)
                    kp.getEkipeKontenjer(finalI).setBrPoena(Integer.parseInt(unesiPoene[finalI].getText()));

                ekipeKontenjeri[finalI].setBrPoena(Integer.parseInt(unesiPoene[finalI].getText()));
            });
        }

        JPanel podesavanjeTimova= new JPanel();podesavanjeTimova.setLayout(new GridLayout(5, brojEkipa,15,0));
        tab1.add(podesavanjeTimova);

        for(int i = 0;i<3;i++){
            for(int j = 0; j< brojEkipa; j++)
                podesavanjeTimova.add(imenaEkipa[j]);
            for(int j = 0; j< brojEkipa; j++)
                podesavanjeTimova.add(podesiIme[j]);
            for(int j = 0; j< brojEkipa; j++)
                podesavanjeTimova.add(unesiIme[j]);
            for(int j = 0; j< brojEkipa; j++)
                podesavanjeTimova.add(bodoviEkipa[j]);
            for(int j = 0; j< brojEkipa; j++){
                podesavanjeTimova.add(panelDugmeZaBodove[j]);
                panelDugmeZaBodove[j].setLayout(new GridLayout(1,2));
                panelDugmeZaBodove[j].add(unesiPoene[j]);
                panelDugmeZaBodove[j].add(podesiPoene[j]);
            }
        }

        //ovde pocinje kontrola kviza
        JPanel kontrolaKviza = new JPanel();kontrolaKviza.setLayout(new GridLayout(1,3,5,0));
        tab1.add(kontrolaKviza);
        JPanel kontrolaKvizaDugmici = new JPanel();kontrolaKviza.add(kontrolaKvizaDugmici);kontrolaKvizaDugmici.setLayout(new GridLayout(3,3));

        kontrolaKvizaDugmici.add(new JPanel());//dummy
        JButton tacanOdgovor = new JButton("Tačno");kontrolaKvizaDugmici.add(tacanOdgovor);tacanOdgovor.addActionListener(this);
        kontrolaKvizaDugmici.add(new JPanel());//dummy
        prikazTacnogOdgovora = new JButton("Tačan odgovor");kontrolaKvizaDugmici.add(prikazTacnogOdgovora);prikazTacnogOdgovora.addActionListener(this);
        JButton pokreniPitanje= new JButton("Pokreni pitanje");kontrolaKvizaDugmici.add(pokreniPitanje);pokreniPitanje.addActionListener(this);
        sledecePitanje = new JButton("Izaberi Pitanje");kontrolaKvizaDugmici.add(sledecePitanje);sledecePitanje.addActionListener(this);
        kontrolaKvizaDugmici.add(new JPanel());//dummy
        JButton netacanOdgovor = new JButton("Netačno");kontrolaKvizaDugmici.add(netacanOdgovor);netacanOdgovor.addActionListener(this);
        kontrolaKvizaDugmici.add(new JPanel());//dummy

        trenutnoPitanjeLabela = new JLabel("TRENUTNO PITANJE: 1");
        kontrolaKviza.add(trenutnoPitanjeLabela);

        odgovori = new JTextArea();odgovori.setEditable(false);
        kontrolaKviza.add(odgovori);


        //ovde pocinje prikaz pitanja
        JPanel prikazPitanja = new JPanel();
        prikazPitanja.setLayout(new GridLayout(1,3));
        tab1.add(prikazPitanja);
        igra1 = new JTextArea();
        igra2 = new JTextArea();
        igra3 = new JTextArea();
        Pomocna.popuniPoljePitanjima(igra1,izabranaPitanjaIgra1);prikazPitanja.add(igra1);
        Pomocna.popuniPoljePitanjima(igra2,izabranaPitanjaIgra2);prikazPitanja.add(igra2);
        Pomocna.popuniPoljePitanjima(igra3,izabranaPitanjaIgra3);prikazPitanja.add(igra3);


        //ovde pocinje prikaz informacija o timovima
        JPanel panelSaEkipama = new JPanel();
        panelSaEkipama.setLayout(new GridLayout(1,5,5,0));
        tab1.add(panelSaEkipama);

        JPanel[] ekipePaneli = new JPanel[brojEkipa];
        for(int i = 0; i < brojEkipa;i++){
            ekipePaneli[i]=new JPanel();
            panelSaEkipama.add(ekipePaneli[i]);
            ekipeKontenjeri[i] = new PrikazEkipa(ekipePaneli[i],ekipe[i]);
            ekipeKontenjeri[i].setBoja(Color.LIGHT_GRAY);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case "Pokreni kviz":
                if(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length<2){  //provera broja ekrana povezanih na racunar
                    JOptionPane.showMessageDialog(null,"Projektor u extended screen modu nije pronađen");
                }else{
                    kp = new KvizProzor(ekipe);
                    pokreniKviz.setEnabled(false);  //da ne bi mogao da palis koliko god hoces prozora
                    pokreniKviz.setText("Kviz pokrenut"); //da bi se znalo da je kviz pokrenut
                }
                break;
            case "igra1":
                if(kp!=null) {
                    pokreniIgru(1);
                }
                break;
            case "igra2":
                if(kp!=null) {
                    pokreniIgru(2);
                }
                break;
            case "igra3":
                if(kp!=null) {
                    pokreniIgru(3);
                }
                break;
            case "Pokreni pitanje":
                if(kp!=null) {
                        if(pokrenutaIgra==1 && !predjenaPitanja.get(trenutnoPitanje)){ //da ne mozes da otvaras pitanja opet!
                            oduzmiKontrolu();
                            kp.startujPitanje(Pitanje.konvertujIzVektora(izabranaPitanjaIgra1.get(trenutnoPitanje)));
                            predjenaPitanja.put(trenutnoPitanje,true);
                            Pomocna.oznaciTrenutnoPitanje(pokrenutaIgra,trenutnoPitanje,true,igra1,igra2,igra3,predjenaPitanja);
                            kvizTajmer = new Tajmer(tajmer,kp.getTajmer(),10,5);
                            kontrolaJavljanja = new KontrolaJavljanja(ekipe,ekipeKontenjeri,kp.getEkipeKontenjeri());
                            pokrenutoPitanje=true;
                            Timer timer = new Timer(15000, e1 -> {vratiKontrolu(); //u slucaju da se niko ne javi, da se vrati kontrola (ne mora se stiskati tacno/netacno bezveze
                                ((Timer)e1.getSource()).stop(); //gasi izvor tj. tajmer...
                            });
                            timer.start();
                        }
                        if(pokrenutaIgra==2 && !predjenaPitanja.get(trenutnoPitanje+10)){
                            oduzmiKontrolu();
                            kp.startujPitanje(Pitanje.konvertujIzVektora(izabranaPitanjaIgra2.get(trenutnoPitanje)));
                            predjenaPitanja.put(trenutnoPitanje+10,true);
                            Pomocna.oznaciTrenutnoPitanje(pokrenutaIgra,trenutnoPitanje,true,igra1,igra2,igra3,predjenaPitanja);
                            kvizTajmer = new Tajmer(tajmer,kp.getTajmer(),15,5);
                            kontrolaJavljanja = new KontrolaJavljanja(ekipe,ekipeKontenjeri,kp.getEkipeKontenjeri());
                            pokrenutoPitanje=true;
                            Timer timer = new Timer(16000, e1 -> {vratiKontrolu(); //u slucaju da se niko ne javi, da se vrati kontrola (ne mora se stiskati tacno/netacno bezveze
                                ((Timer)e1.getSource()).stop(); //gasi izvor tj. tajmer...
                            });
                            timer.start();
                        }
                        if(pokrenutaIgra==3 && !predjenaPitanja.get(trenutnoPitanje+20)) {
                            oduzmiKontrolu();
                            kp.startujPitanje(Pitanje.konvertujIzVektora(izabranaPitanjaIgra3.get(trenutnoPitanje)));
                            predjenaPitanja.put(trenutnoPitanje+20,true);
                            Pomocna.oznaciTrenutnoPitanje(pokrenutaIgra,trenutnoPitanje,true,igra1,igra2,igra3,predjenaPitanja);
                            kontrolaJavljanja = new KontrolaJavljanja(ekipe,ekipeKontenjeri,kp.getEkipeKontenjeri());
                            KontrolaJavljanja.ProzoviEkipuTrecaIgra();
                            kvizTajmer = new Tajmer(tajmer,kp.getTajmer(),-1,20);
                            pokrenutoPitanje=true;
                            KontrolaJavljanja.Ugasi();
                        }
                }
                break;
            case "Izaberi Pitanje":
                if(kp!=null && pokrenutaIgra!=0){
                    if(!Pomocna.SvaPitanjaPredjena(predjenaPitanja,pokrenutaIgra)) {
                        int tmp;
                        do {
                            tmp = (int) (Math.random() * 10);
                        } while (tmp == trenutnoPitanje || predjenaPitanja.get(tmp + (pokrenutaIgra * 10 - 10)));
                        trenutnoPitanje = tmp;
                        postaviTrenutnoPitanje(trenutnoPitanje);
                        sledecePitanje.setEnabled(false);
                        Ekipa.OdjaviEkipe(ekipe);
                        PrikazEkipa.refreshEkipe(ekipe, ekipeKontenjeri);
                        PrikazEkipa.refreshEkipe(ekipe, kp.getEkipeKontenjeri());
                    }
                }

                break;
            case "Tačan odgovor":
                if(kp!=null){
                    String tacanOdgovor;
                    int brTacanOdg;
                    switch (pokrenutaIgra){
                        case 1:
                            brTacanOdg =Integer.parseInt(izabranaPitanjaIgra1.get(trenutnoPitanje).get(7).toString());
                            tacanOdgovor = izabranaPitanjaIgra1.get(trenutnoPitanje).get(1+brTacanOdg).toString();
                            kp.getTekstTacnogOdgovora().setVisible(true);
                            kp.getTekstTacnogOdgovora().setText(tacanOdgovor);
                            break;
                        case 2:
                            brTacanOdg =Integer.parseInt(izabranaPitanjaIgra2.get(trenutnoPitanje).get(7).toString());
                            tacanOdgovor = izabranaPitanjaIgra2.get(trenutnoPitanje).get(1+brTacanOdg).toString();
                            kp.getTekstTacnogOdgovora().setVisible(true);
                            kp.getTekstTacnogOdgovora().setText(tacanOdgovor);
                            break;
                        case 3:
                            brTacanOdg =Integer.parseInt(izabranaPitanjaIgra3.get(trenutnoPitanje).get(7).toString());
                            tacanOdgovor = izabranaPitanjaIgra3.get(trenutnoPitanje).get(1+brTacanOdg).toString();
                            kp.getTekstTacnogOdgovora().setVisible(true);
                            kp.getTekstTacnogOdgovora().setText(tacanOdgovor);
                            break;
                    }

                }
                break;
            case "Tačno":
                if(kvizTajmer.getVreme()<1 && kp!=null) {
                    int brojPozitivnihBodova = 0;
                    switch (pokrenutaIgra) {
                        case 1:
                            brojPozitivnihBodova = 2;
                            break;
                        case 2:
                            brojPozitivnihBodova = 3;
                            break;
                        case 3:
                            brojPozitivnihBodova = 3;
                            break;
                    }
                    Ekipa.DodajPoenePrvoj(ekipe, brojPozitivnihBodova);
                    kvizTajmer.zaustaviTajmer();
                    Ekipa.OdjaviEkipe(ekipe);
                    PrikazEkipa.refreshEkipe(ekipe, ekipeKontenjeri);
                    PrikazEkipa.refreshEkipe(ekipe, kp.getEkipeKontenjeri());
                    vratiKontrolu();
                }
                break;
            case "Netačno":
                if(kvizTajmer.getVreme()<0 && kp!=null) {
                    int brojNegativnihBodova;
                    switch (pokrenutaIgra) {
                        case 1:
                            brojNegativnihBodova = -1;
                            Ekipa.DodajPoenePrvoj(ekipe, brojNegativnihBodova);
                            kvizTajmer.zaustaviTajmer();
                            Ekipa.OdjaviEkipe(ekipe);
                            PrikazEkipa.refreshEkipe(ekipe, ekipeKontenjeri);
                            PrikazEkipa.refreshEkipe(ekipe, kp.getEkipeKontenjeri());
                            vratiKontrolu();
                            break;
                        case 2:
                            brojNegativnihBodova = -1;
                            Ekipa.DodajPoenePrvoj(ekipe,brojNegativnihBodova);
                            Ekipa.ProslediRedSledecoj(ekipe);
                            kvizTajmer.zaustaviTajmer();
                            kvizTajmer = new Tajmer(tajmer,kp.getTajmer(),-1,5);
                            PrikazEkipa.refreshEkipe(ekipe, ekipeKontenjeri);
                            PrikazEkipa.refreshEkipe(ekipe, kp.getEkipeKontenjeri());
                            if(Ekipa.NikoNijePrijavljen(ekipe))
                                kvizTajmer.zaustaviTajmer();
                            vratiKontrolu();
                            break;
                        case 3:
                            kvizTajmer.zaustaviTajmer();
                            brojNegativnihBodova = -1;
                            Ekipa.DodajPoenePrvoj(ekipe,brojNegativnihBodova);
                            Ekipa.ProslediJavljanjeOstalima(ekipe);
                            PrikazEkipa.refreshEkipe(ekipe, ekipeKontenjeri);
                            PrikazEkipa.refreshEkipe(ekipe, kp.getEkipeKontenjeri());
                            if(!Ekipa.SviSuPogresili(ekipe)) {  //TODO: ovo zapravo valja
                                pokrenutoPitanje=true;
                                kvizTajmer = new Tajmer(tajmer, kp.getTajmer(), 5, 5);
                                oduzmiKontrolu();
                                Timer timer = new Timer(10000, e1 -> {vratiKontrolu();
                                    ((Timer)e1.getSource()).stop(); //gasi izvor tj. tajmer...
                                });
                                timer.start();
                                kontrolaJavljanja = new KontrolaJavljanja(ekipe, ekipeKontenjeri, kp.getEkipeKontenjeri());
                                sledecePitanje.setEnabled(false);
                            }
                            break;
                    }
                }
                break;
        }
    }


    private static void postaviTrenutnoPitanje(int pitanje){
        trenutnoPitanje=pitanje;
        trenutnoPitanjeLabela.setText("TRENUTNO PITANJE: "+(trenutnoPitanje+1));
        Pomocna.oznaciTrenutnoPitanje(pokrenutaIgra,trenutnoPitanje,false,igra1,igra2,igra3,predjenaPitanja);

        switch (pokrenutaIgra){
            case 1:
                Pomocna.popuniPoljeOdgovorima(odgovori,izabranaPitanjaIgra1,trenutnoPitanje);
                break;
            case 2:
                Pomocna.popuniPoljeOdgovorima(odgovori,izabranaPitanjaIgra2,trenutnoPitanje);
                break;
            case 3:
                Pomocna.popuniPoljeOdgovorima(odgovori,izabranaPitanjaIgra3,trenutnoPitanje);
                break;
            default:
                break;
        }
    }

    private void oduzmiKontrolu(){
        pokreniIgru1.setEnabled(false);
        pokreniIgru2.setEnabled(false);
        pokreniIgru3.setEnabled(false);
    }

    /**
     * metoda koja vraca kontrolu nad igrama i pitanjima kada se zavrsi pitanje
     */
    private void vratiKontrolu(){
        if(pokrenutoPitanje) {
            sledecePitanje.setEnabled(true);pokrenutoPitanje=false;
            pokreniIgru1.setEnabled(true);
            pokreniIgru2.setEnabled(true);
            pokreniIgru3.setEnabled(true);
        }
    }

    private void pokreniIgru(int brojIgre){
        pokrenutaIgra = brojIgre;
        if(!Pomocna.SvaPitanjaPredjena(predjenaPitanja,pokrenutaIgra)){
            kp.pokreniIgru(brojIgre);
            int tmp;
            do {
                tmp=(int)(Math.random() * 10);
            }  while (predjenaPitanja.get(tmp+(brojIgre*10-10)));   //da ne pokrece vec pokrenuta

            postaviTrenutnoPitanje(tmp);
            sledecePitanje.setEnabled(false);
        }
    }
}



