package music;
import graphics.G;
import graphics.WinApp;
import reaction.*;
import reaction.Shape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MusicEd extends WinApp {
//    public static Layer BACK = new Layer("BACK"), FORE = new Layer("FORE");
    static {
        new Layer("BACK");
        new Layer("NOTE");
        new Layer("FORE");
    }

    public static boolean training = false;
    public static I.Area curArea = Gesture.AREA;
    public static Page PAGE;

    public MusicEd() {
        super("Music Editor", UC.screenWidth, UC.screenHeight);


        Reaction.initialReactions.addReaction(new Reaction("W-W") {
            public int bid(Gesture g) {
                return 0;
            }

            public void act(Gesture g) {
                int y = g.vs.yM();
                PAGE = new Page(y);
                this.disable();
            }
        });
    }

//    public static int[] xPoly = {100, 200, 200, 100};
//    public static int[] yPoly = {50, 70, 80, 60};
//    public static Polygon poly = new Polygon(xPoly, yPoly, 4);


    public void paintComponent(Graphics g) {
        G.bgWhite(g);
        if (training) {
            Shape.TRAINER.show(g); return;
        }
        g.setColor(Color.BLACK);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
        g.drawString(Gesture.recognized, 900, 30);
        if (PAGE != null) {
            Glyph.CLEF_G.showAt(g, 8, 100, PAGE.margins.top + 4*8);
//            Glyph.HEAD_W.showAt(g, 8, 200, PAGE.margins.top + 4*8);
//            int H = 32;
//            Glyph.HEAD_Q.showAt(g, H, 200, PAGE.margins.top + 4*H);
//            g.setColor(Color.RED);
//            g.drawRect(200, PAGE.margins.top*3*H, 24*H/10, 2*H);

        }
//        Beam.setPoly(100, 100 + G.rnd(100), 200, 100 + G.rnd(100), 8);
//        g.fillPolygon(Beam.poly);
//        yPoly[3]++;
//        poly.ypoints[3]++;

//        testBeamStack(g);
    }

    public void testBeamStack(Graphics g) {
        int H = 8, x1 = 100, x2 = 200;
        Beam.setMasterBeam(x1, 100 + G.rnd(100), x2, G.rnd(100));
        g.drawLine(0, Beam.my1, 100, Beam.my1);
        Beam.drawBeamStack(g, 0, 1, x1, x2, H);
        g.setColor(Color.ORANGE);
        Beam.drawBeamStack(g, 1, 3, x1 + 10, x2 + 10, H);
    }

    public void trainButton(MouseEvent me) {
        if (me.getX() > UC.screenWidth - 40 && me.getY() < 40) {
            training = !training;
            curArea = training ? Shape.TRAINER : Gesture.AREA;
        }
    }

    public void mousePressed(MouseEvent me) {curArea.dn(me.getX(), me.getY()); repaint();}
    public void mouseDragged(MouseEvent me) {curArea.drag(me.getX(), me.getY()); repaint();}
    public void mouseReleased(MouseEvent me) {
        curArea.up(me.getX(), me.getY());
        trainButton(me);
        repaint();
    }
    public void keyTyped(KeyEvent ke) {if (training) {Shape.TRAINER.keyTyped(ke); repaint();}}

    public static void main(String[] args) {
        PANEL = new MusicEd();
        WinApp.launch();
    }
}
