package music;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass implements Comparable<Head>{
    public Staff staff;
    public int line;
    public Time time;
    public Glyph forcedGlyph; // in case we need to use a special head note
    public Stem stem = null;
    public boolean wrongSide = false;

    public Head(Staff staff, int x, int y) {
        super("NOTE");
        this.staff = staff;
        time = staff.sys.getTime(x);
//        int H = staff.fmt.H;
//        int top = staff.yTop() - H;
//        line = (y - top + H/2)/H - 1;
        time.heads.add(this);
        line = staff.lineOfY(y);
        System.out.println("line:"+line);

        addReaction(new Reaction("S-S") { // stem or unStem heads
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                int W = Head.this.w(), y = Head.this.y();
                if (y < y1 || y > y2) {return UC.noBid;}
                int hL = Head.this.time.x, hR = hL + W;
                if (x < hL - W || x > hR + W) {return UC.noBid;}
                if (x < hL + W/2) {return hL - x;}
                if (x > hR - W/2) {return x - hR;}
                return UC.noBid;
            }

            public void act(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                Staff staff = Head.this.staff;
                Time t = Head.this.time;
                int w = Head.this.w();
                boolean up = x > (t.x + w/2);
                if (Head.this.stem == null) {
//                    t.stemHeads(staff, up, y1, y2);
                    Stem.getStem(staff, t, y1, y2, up);
                } else {
                    t.unStemHeads(y1, y2);
                }
            }
        });

        addReaction(new Reaction("DOT") {
            public int bid(Gesture g) {
                int xH = x(), yH = y(), H = staff.fmt.H, W = Head.this.w();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xH || x > xH + 2*W || y < yH - H || y > yH*H) {return UC.noBid;}
                return Math.abs(xH + W - x) + Math.abs(yH - y);
            }

            public void act(Gesture g) {
                if (Head.this.stem != null) {
                    Head.this.stem.cycleDot();
                }
            }
        });
    }
    // width
    public int w() {return 24*staff.fmt.H/10;}

    public void show(Graphics g) {
//        g.setColor(wrongSide ? Color.GREEN : Color.BLUE);
//        if (stem != null && !stem.heads.isEmpty() && this == stem.firstHead()) {
//            g.setColor(Color.RED);
//        }
        int H = staff.fmt.H;
        (forcedGlyph != null ? forcedGlyph : normalGlyph()).showAt(g, H, x(), y());
        if (stem != null) {
            int off = UC.ArgDotOffset, sp = UC.ArgDotSpacing;
            for (int i = 0; i < stem.nDot; i++) {
                g.fillOval(time.x + off + i*sp, y() - 3*H/2, H*2/3, H*2/3);
            }
        }
    }

    public Glyph normalGlyph() {
        if (stem == null) {return Glyph.HEAD_Q;}
        if (stem.nFlag == -1) {return Glyph.HEAD_HALF;}
        if (stem.nFlag == -2) {return Glyph.HEAD_W;}
        return Glyph.HEAD_Q;
    }

    public int y() {return staff.yOfLine(line);}

    public int x() {
        int res = time.x;
        if (wrongSide) {res += (stem != null && stem.isUp) ? w() : -w();}
        return res;
    }

    // stub, since we need to delete all reference, not just here
    public void delete() {time.heads.remove(this);}

    public void unStem() {
        if (stem != null) {
            stem.heads.remove(this);
            if (stem.heads.size() == 0) {stem.deleteStem();}
            stem = null;
            wrongSide = false;
        }
    }

    public void joinStem(Stem s) {
        if (stem != null) {unStem();}
        s.heads.add(this);
        stem = s;
    }

    public int compareTo(Head h) {
        return (staff.iStaff != h.staff.iStaff) ? (staff.iStaff - h.staff.iStaff) : line - h.line;
    }

    //------------------------List-------------------------------
    public static class List extends ArrayList<Head> {}
}
