package music;

import graphics.G;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

public class Page extends Mass {
    public Margins margins = new Margins();
    public int sysGap;
    public G.HC pageTop;
    public Sys.List sysList;

    public Page(int y) {
        super("BACK");
        margins.top = y;
        pageTop = new G.HC(G.HC.ZERO, y);
        G.HC sysTop = new G.HC(pageTop, 0);
        sysList = new Sys.List();
        sysList.add(new Sys(this, sysTop));
        // add reactions
        addReaction(new Reaction("W-W"){ // add new staff to first system
            public int bid(Gesture g) {
                if (sysList.size() != 1) {return UC.noBid;}
                Sys sys = sysList.get(0);
                int y = g.vs.yM();
                if (y < sys.yBot() + UC.minStaffGap) {return UC.noBid;}
                return 1000;
            }

            public void act(Gesture g) {sysList.get(0).addNewStaff(g.vs.yM());}

        });

        addReaction(new Reaction("W-E"){ // add new system to page
            public int bid(Gesture g) {
                Sys lastSys = sysList.get(sysList.size() - 1);
                int y = g.vs.yM();

                if (y < lastSys.yBot() + UC.minSysGap) {return UC.noBid;}
                return 1000;
            }

            public void act(Gesture g) {addNewSys(g.vs.yM());}

        });
    }

    public void addNewSys(int y) { // called by page reaction, list is not empty
        int nSys = sysList.size();
        int sysHeight = sysList.get(0).height();
        if (nSys == 1) {sysGap = y - sysHeight - pageTop.v();}
        G.HC sysTop = new G.HC(pageTop, nSys * (sysHeight + sysGap));
        sysList.add(new Sys(this, sysTop));

    }

    public void show(Graphics g) {g.setColor(Color.black);}

    //--------------------Margins---------------------
    public static class Margins {
        private static int Mm = 50;
        public int top = Mm;
        public int bot = UC.screenHeight - Mm;
        public int left = Mm;
        public int right = UC.screenWidth - Mm;
    }
}
