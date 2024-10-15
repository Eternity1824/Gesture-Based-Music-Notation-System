package sandbox;

import graphics.G;
import graphics.WinApp;
import music.UC;
import reaction.Ink;
import reaction.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PaintInk extends WinApp {
    public static Ink.List inkList = new Ink.List();
    public static Shape.Prototype.List pList = new Shape.Prototype.List();
    public static String recognize = "";

    public PaintInk() {
        super("PaintInk", UC.screenWidth, UC.screenHeight);
        //TEST: inkList.add(new Ink());
    }

    public void paintComponent(Graphics g) {
        G.bgWhite(g);
        //g.setColor(Color.RED); g.fillRect(100, 100, 100, 100);
        g.setColor(Color.black);
        inkList.show(g);
        Ink.BUFFER.show(g);
        g.drawString("points: " + Ink.BUFFER.n, 600, 30);
        if (inkList.size() > 1) {
            int last = inkList.size() - 1;
            int dist = inkList.get(last).norm.dist(inkList.get(last - 1).norm);
            g.setColor(dist > UC.noMatchDist ? Color.RED : Color.BLACK);
            g.drawString("dist: " + dist, 600, 60);
        }
        pList.show(g);
        g.drawString(recognize, 700, 40);
    }

    public void mousePressed(MouseEvent me) {Ink.BUFFER.dn(me.getX(), me.getY()); repaint();}

    public void mouseDragged(MouseEvent me) {Ink.BUFFER.drag(me.getX(), me.getY()); repaint();}

    public void mouseReleased(MouseEvent me) {
        Ink.BUFFER.up(me.getX(), me.getY());
        Ink ink = new Ink();
        Shape s = Shape.recognize(ink); // can fail
        recognize = "recognize: " + (s != null ? s.name: "unrecognized");
        inkList.add(ink);
        Shape.Prototype proto;
        if (pList.bestDist(ink.norm) < UC.noMatchDist) {
            proto = Shape.Prototype.List.bestMatch;
            proto.blend(ink.norm);
        } else {
            proto = new Shape.Prototype();
            pList.add(proto);
        }
        ink.norm = proto;
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new PaintInk();
        WinApp.launch();
    }
}
