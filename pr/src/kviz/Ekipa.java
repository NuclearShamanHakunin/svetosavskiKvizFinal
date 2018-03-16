package kviz;

/**
 * Created by Marko on 07-Jan-18.
 *
 * Klasa koja se bavi ekipama
 *
 */
public class Ekipa {
    private String naziv;
    private int poeni;
    private int redJavljanja;
    private boolean javioSe;
    private static float[] bonusZaPrve = new float[5];
    private static float bonus = 0.5f;

    static {
        for(int i = 0 ; i < 5; i++){
            bonusZaPrve[i]=0;
        }
    }

    public Ekipa() {
        naziv="";
        poeni=0;
        redJavljanja=0;
        javioSe=false;
    }


    public Ekipa(String naziv, int poeni) {
        this.naziv = naziv;
        this.poeni = poeni;
        redJavljanja=0;
        javioSe=false;
    }

    public void setRedJavljanja(int redJavljanja) {
        this.redJavljanja = redJavljanja;
        javioSe=true;
    }

    public static float[] getBonusZaPrve() {
        return bonusZaPrve;
    }

    public int getRedJavljanja() {
        return redJavljanja;
    }

    public boolean DaLiSeJavio() {
        return javioSe;
    }

    public void setJavioSe(boolean javioSe) {
        this.javioSe = javioSe;
    }

    public void dodajPoene(int broj){
        poeni+=broj;
    }

    public static void OdjaviEkipe(Ekipa[] ekipe){
        for(int i = 0; i<ekipe.length;i++){
            ekipe[i].redJavljanja=0;
            ekipe[i].javioSe=false;
        }
    }

    public static void DodajPoenePrvoj(Ekipa[] ekipe, int br){
        for(int i = 0; i<5;i++){
            if(ekipe[i].redJavljanja==1){
                ekipe[i].poeni+=br;
                if(bonusZaPrve[i]==0 && br>0){  //dodavanje bonusa za one koji su se prvi javili
                    bonusZaPrve[i]+=bonus;
                    if(bonus>0){
                        bonus-=0.1f;    //svaki sledeci dobija manji bonus
                    }
                }
            }
        }
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public int getPoeni() {
        return poeni;
    }

    public void setPoeni(int poeni) {
        this.poeni = poeni;
    }

    @Override
    public String toString() {
        return "Екипа - " + naziv +
                " Поени - " + poeni;
    }

    public static void ProslediRedSledecoj(Ekipa[] ekipe) {
        for(int i = 0 ; i < 5 ; i++){
            if(ekipe[i].getRedJavljanja()>0){
                ekipe[i].setRedJavljanja(ekipe[i].getRedJavljanja()-1);
            }
        }
    }

    public static boolean NikoNijePrijavljen(Ekipa[] ekipe) {
        boolean prijavljeni = true;
        for(int i = 0 ; i < 5 ; i++){
            if(ekipe[i].getRedJavljanja()==1)
                prijavljeni=false;
        }
        return prijavljeni;
    }

    public static boolean SviSuPogresili(Ekipa[] ekipe){
        boolean pogresili = true;
        for(int i = 0 ; i < 5 ; i++){
            if(ekipe[i].getRedJavljanja()!=10)
                pogresili=false;
        }
        return pogresili;
    }

    public static void ProslediJavljanjeOstalima(Ekipa[] ekipe) {
        for(int i = 0 ; i < 5 ; i++){
            if(ekipe[i].getRedJavljanja()!=1 && ekipe[i].getRedJavljanja()<6){
                ekipe[i].setRedJavljanja(0);
                ekipe[i].setJavioSe(false);
            }else
                ekipe[i].setRedJavljanja(10);
        }
    }
}
