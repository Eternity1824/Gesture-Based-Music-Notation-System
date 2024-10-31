package music;

import reaction.Mass;

public class Beam extends Mass {
    public Stem.List stems = new Stem.List();
    public Beam(Stem first, Stem last) {
        super("NOTE");
        addStem(first);
        addStem(last);

    }
    public Stem first(){return stems.get(0);}
    public Stem last(){return stems.get(stems.size()-1);}

    public void deleteBeam(){
        for (Stem s : stems) {
            s.beam = null;
        }
        deleteMass();
    }

    public void addStem(Stem stem) {
        if (stem.beam == null) {
            stems.add(stem);
            stem.beam = this;
            stem.nFlag = 1;
            stems.sort();
        }
    }

    public void setMasterBeam() {
        mx1 = first().x();
        my1 = first().yBeanEnd();
        mx1 = last().x();
        my1 = last().yBeanEnd();
    }

    public static int yOfX(int x, int x1, int y1, int x2, int y2) {
        int dy = y2 - y1;
        int dx = x2 - x1;
        return (x - x1)*dy/dx + y1;
    }

    public static int mx1, my1, mx2, my2; // coordinates for master beam
    public static int yOfX(int x) {
        int dx = mx2 - mx1;
        int dy = my2 - my1;
        return (x - mx1)*dy/dx + my1;
    }

    public static void setMasterBeam(int x1, int y1, int x2, int y2) { mx1 = x1; my1 = y1; mx2 = x2; my2 = y2;}

}
