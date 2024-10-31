package music;

import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Stem extends Duration implements Comparable<Stem> {
    @Override
    public int compareTo(Stem s) {
        return x() - s.x();
    }

    public Staff staff;
    public Head.List heads = new Head.List();
    public boolean isUp = true;
    public Beam beam = null;

    public Stem(Staff staff, boolean up){
        this.staff = staff;
        isUp = up;

        addReaction(new Reaction("E-E") {
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 +y2)/2);
            }

            public void act(Gesture g) {
                Stem.this.incFlag();
            }
        });

        addReaction(new Reaction("W-W") {
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.heads.get(0).time.x;
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 +y2)/2);
            }

            public void act(Gesture g) {
                Stem.this.decFlag();
            }
        });
    }

    @Override
    public void show(Graphics g) {
        if (nFlag >= -1 && !heads.isEmpty()) {
            int x = x(), H = staff.fmt.H, yH = yFirstHead(), yB = yBeanEnd();
            g.drawLine(x, yH, x, yB);
            if (nFlag > 0) {
                if (nFlag == 1) {(isUp ? Glyph.FLAG1D : Glyph.FLAG1U).showAt(g, H, x(), yBeanEnd());}
                if (nFlag == 2) {(isUp ? Glyph.FLAG2D : Glyph.FLAG2U).showAt(g, H, x(), yBeanEnd());}
                if (nFlag == 3) {(isUp ? Glyph.FLAG3D : Glyph.FLAG3U).showAt(g, H, x(), yBeanEnd());}
                if (nFlag == 4) {(isUp ? Glyph.FLAG4D : Glyph.FLAG4U).showAt(g, H, x(), yBeanEnd());}
            }
        }
    }

    public Head firstHead() {return heads.get(isUp? heads.size() - 1: 0);}
    public Head lastHead() {return heads.get(isUp? 0: heads.size() - 1);}

    public int yLo() {return isUp? yBeanEnd() : yFirstHead();}
    public int yHi() {return isUp? yFirstHead() : yBeanEnd();}

    public int yFirstHead() {
        Head h = firstHead();
        return h.staff.yOfLine(h.line);
    }

    public int yBeanEnd() {
        if (isInternalStem()) {beam.setMasterBeam(); return Beam.yOfX(x());}
        Head h = lastHead();
        int line = h.line;
        line += isUp? -7: 7; // default one octave
        int flagIncrement = nFlag > 2 ? 2*(nFlag - 2) : 0;
        line += isUp ? -flagIncrement: flagIncrement;
        if ((isUp && line > 4) || (!isUp && line < 4)) {line = 4;} // hit center line if possible
        return h.staff.yOfLine(line);
    }

    public boolean isInternalStem() {
        if (beam == null) {return false;}
        if (this == beam.first() || this == beam.last()) {return false;}
        return true;
    }

    public int x() {
        Head h = firstHead();
        return h.time.x + (isUp? h.w() : 0);
    }

    public void deleteStem() {
        staff.sys.stems.remove(this);
        deleteMass();
    }

    public void setWrongSide() {
        Collections.sort(heads);
        int i, last, next;
        if (isUp) {i = heads.size() - 1; last = 0; next = -1;} else {i = 0; last = heads.size() - 1; next = 1;}
        Head ph = heads.get(i);
        ph.wrongSide = false;
        while (i != last) {
            i += next;
            Head nh = heads.get(i);
            nh.wrongSide = (ph.staff == nh.staff && (Math.abs(nh.line - ph.line) <= 1) && !ph.wrongSide);
            ph = nh;
        }
    }
    //----------------------List---------------------
    public static class List extends ArrayList<Stem> {
        public int yMin = 1000000, yMax = -1000000;
        public void addStem(Stem s) {
            add(s);
            if (s.yLo() < yMin) {yMin = s.yLo();}
            if (s.yHi() > yMax) {yMax = s.yHi();}
        }

        public boolean fastReject(int y) {return y > yMax || y < yMin;}
        public void sort() {
            Collections.sort(this);
        }
    }
}
