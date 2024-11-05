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

    public Stem(Staff staff, Head.List heads, boolean up){
        this.staff = staff;
        isUp = up;
        for (Head h : heads) {h.unStem(); h.stem = this;}
        this.heads = heads;
        staff.sys.stems.addStem(this);
        setWrongSide();

        addReaction(new Reaction("E-E") {
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.x();
                if (x1 > xS || x2 < xS) {return UC.noBid;}
                int y1 = Stem.this.yLo(), y2 = Stem.this.yHi();
                if (y < y1 || y > y2) {return UC.noBid;}
                return Math.abs(y - (y1 +y2)/2) + 60; // allow sys "E-E" underbid this
            }

            public void act(Gesture g) {
                Stem.this.incFlag();
            }
        });

        addReaction(new Reaction("W-W") {
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH();
                int xS = Stem.this.x();
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
    // factory method
    public static Stem getStem(Staff staff, Time time, int y1, int y2, boolean up){
        Head.List heads = new Head.List();
        for (Head h : time.heads) {
            int yH = h.y();
            if (yH > y1 && yH < y2) {heads.add(h);}
        }
        if (heads.isEmpty()) {return null;} // no stem return if no heads
        Beam b = internalStems(staff.sys, time.x, y1, y2); // possibly this is internal stem in beams group
        Stem result = new Stem(staff, heads, up);
        if (b != null) {b.addStem(result); result.nFlag = 1; return result;}
        return result;
    }

    @Override
    public void show(Graphics g) {
        if (nFlag >= -1 && !heads.isEmpty()) {
            int x = x(), H = staff.fmt.H, yH = yFirstHead(), yB = yBeanEnd();
            g.drawLine(x, yH, x, yB);
            if (nFlag >= 0 && beam == null) {
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

    public static Beam internalStems(Sys sys, int x, int y1, int y2) {
        for (Stem s: sys.stems) {
            if (s.beam != null && s.x() < x && s.yLo() < y2 && s.yHi() > y1) {
                int bX = s.beam.first().x(), bY = s.beam.first().yBeanEnd();
                int eX = s.beam.last().x(), eY = s.beam.last().yBeanEnd();
                if (Beam.verticalLineCrossSegment(x, y1, y2, bX, bY, eX, eY)) {return s.beam;}
            }
        }
        return null;
    }
    //----------------------List---------------------
    public static class List extends ArrayList<Stem> {
        public int yMin = 1000000, yMax = -1000000;
        public void addStem(Stem s) {
            add(s);
            if (s.yLo() < yMin) {yMin = s.yLo();}
            if (s.yHi() > yMax) {yMax = s.yHi();}
        }

        public boolean fastReject(int y) {System.out.println("y:"+y+"ymax: "+yMax+"ymin:"+yMin);return y > yMax || y < yMin;}

        public ArrayList<Stem> allIntersecters(int x1, int y1, int x2, int y2) {
            ArrayList<Stem> res = new ArrayList<>();
            for (Stem s: this) {
                if (Beam.verticalLineCrossSegment(s.x(), s.yLo(), s.yHi(), x1, y1, x2, y2)) {
                    res.add(s);
                }
            }
            return res;
        }

        public void sort() {
            Collections.sort(this);
        }
    }
}
