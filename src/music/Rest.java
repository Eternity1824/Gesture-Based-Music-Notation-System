package music;

import reaction.Gesture;
import reaction.Reaction;

import java.awt.*;

public class Rest extends Duration {
    public Staff staff;
    public Time time;
    public int line = 4; // 4th is the default place for 5 lines music notes
    public Rest(Staff staff, Time time) {
        this.staff = staff;
        this.time = time;

        addReaction(new Reaction("E-E") {
            @Override
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.noBid;}
                return Math.abs(y - Rest.this.staff.yOfLine(4));
            }

            @Override
            public void act(Gesture g) {
                Rest.this.incFlag();
            }
        });

        addReaction(new Reaction("W-W") {
            public int bid(Gesture g) {
                int y = g.vs.yM(), x1 = g.vs.xL(), x2 = g.vs.xH(), x = Rest.this.time.x;
                if (x1 > x || x2 < x) {return UC.noBid;}
                return Math.abs(y - Rest.this.staff.yOfLine(4));
            }

            public void act(Gesture g) {
                Rest.this.decFlag();
            }
        });

        addReaction(new Reaction("DOT") {
            public int bid(Gesture g) {
                int xR = Rest.this.time.x, yR = Rest.this.y();
                int x = g.vs.xM(), y = g.vs.yM();
                if (x < xR || x > xR + 40 || y < yR - 40 || y > yR + 40) {return UC.noBid;}
                return Math.abs(x - xR) + Math.abs(y - yR);
            }

            public void act(Gesture g) {
                Rest.this.cycleDot();
            }
        });
    }


    public int y() {return staff.yOfLine(line);}

    public static Glyph[] glyphs = {Glyph.REST_W, Glyph.REST_H, Glyph.REST_Q,
            Glyph.REST_1F, Glyph.REST_2F, Glyph.REST_3F, Glyph.REST_4F};

    @Override
    public void show(Graphics g) {
        int H = staff.fmt.H, y = y();
        Glyph glyph = glyphs[nFlag + 2]; // start at -2
        glyph.showAt(g, H, time.x, y);

        int off = UC.ArgDotOffset, sp = UC.ArgDotSpacing;
        for (int i = 0; i < nDot; i++) {
            g.fillOval(time.x + off+i*sp, y - 3*H/2, H*2/3, H*2/3);
        }
    }
}
