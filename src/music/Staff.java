package music;

import graphics.G;
import reaction.Mass;

import java.awt.*;
import java.util.ArrayList;

public class Staff extends Mass {
    public Sys sys;
    public int iStaff;
    public G.HC staffTop;
    public Staff.Fmt fmt = Fmt.DEFAULT; // fmt format

    public Staff(Sys sys, int iStaff, G.HC staffTop) {
        super("BACK");
        this.sys = sys;
        this.iStaff = iStaff;
        this.staffTop = staffTop;

    }

    public int yTop() {return staffTop.v();}

    public int yOfLine(int line) {return yTop() + line + line * fmt.H;}

    public int yBot() {return yOfLine(2 * (fmt.nLines - 1));}

    public Staff copy(Sys newSys) {
        G.HC hc = new G.HC(newSys.staffs.sysTop, staffTop.dv);
        return new Staff(newSys, iStaff, hc);
    }

    public void show(Graphics g) {
        Page.Margins m = sys.page.margins;
        int x1 = m.left, x2 = m.right, y = yTop(), h = fmt.H * 2;
        for (int i = 0; i < fmt.nLines; i++) {
            g.drawLine(x1, y + i * h, x2, y + i * h);
        }
    }

    //-------------------Fmt------------------------
    public static class Fmt {
        public static Fmt DEFAULT = new Fmt(5, 8);
        public int nLines; // 5 lines on staff
        public int H; //this is half of the space between lines

        public Fmt(int nLines, int H) {
            this.nLines = nLines;
            this.H = H;
        }
    }
    //--------------------List------------------------
    public static class List extends ArrayList<Staff> {
        public G.HC sysTop;
        public List(G.HC sysTop) {
            this.sysTop = sysTop;
        }

        public int sysTop() {
            return sysTop.v();
        }
    }

}
