package music;

import reaction.Mass;

import java.awt.*;

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
        mx2 = last().x();
        my2 = last().yBeanEnd();
    }

    public void show(Graphics g) {
        g.setColor(Color.black);
        drawBeamGroup(g);

    }

    public void drawBeamGroup(Graphics g) {
        setMasterBeam();
        Stem firstStem = first();
        int H = firstStem.staff.fmt.H;
        int sH = firstStem.isUp ? H: -H;
        int nPrev = 0;
        int nCur = first().nFlag, nNext = stems.get(1).nFlag;
        int pX; // location of previous stems
        int cX = firstStem.x(); // location of current stems
        int bX = cX + 3*H; // location of beam end
        if (nCur > nNext) {drawBeamStack(g, nNext, nCur, cX, bX, sH);} // draw beams first stems
        for (int cur = 1; cur < stems.size(); cur++) {
            Stem sCur = stems.get(cur);
            pX = cX;
            cX = sCur.x();
            nPrev = nCur;
            nCur = nNext;
            nNext = (cur < stems.size() - 1) ? stems.get(cur + 1).nFlag: 0;
            int nBack = Math.min(nPrev, nCur); // lines
            drawBeamStack(g, 0, nBack, pX, cX, sH);
            if (nCur > nPrev && nCur > nNext) { // test if we need beams lets
                if (nPrev < nNext) {
                    bX = cX + 3*H;
                    drawBeamStack(g, nNext, nCur, bX, cX, sH);
                } else {
                    bX = cX - 3*H;
                    drawBeamStack(g, nPrev, nCur, bX, cX, sH);
                }
            }
        }
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

    private static int[] points = {0, 0, 0, 0};
    public static Polygon poly = new Polygon(points, points, 4);

    public static void setPoly(int x1, int y1, int x2, int y2, int H) {
        int[] a = poly.xpoints;
        a[0] = x1; a[1] = x2; a[2] = x2; a[3] = x1;
        a = poly.ypoints;
        a[0] = y1; a[1] = y2; a[2] = y2 + H; a[3] = y1 + H;
    }

    public static void drawBeamStack(Graphics g, int n1, int n2, int x1, int x2, int H) {
        int y1 = yOfX(x1), y2 = yOfX(x2);
        for (int i = n1; i < n2; i++) {
            setPoly(x1, y1 + i*2*H, x2, y2 + i*2*H, H);
            g.fillPolygon(poly);
        }
    }

    public static boolean verticalLineCrossSegment(int x, int y1, int y2, int bX, int bY, int eX, int eY) {
        if (x < bX || x > eX) {return false;}
        int y = yOfX(x, bX, bY, eX, eY);
        if (y1 < y2) {return y1 < y && y < y2;}
        else {return y2 < y && y < y1;}
    }
}
