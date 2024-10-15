package reaction;

import graphics.G;
import graphics.WinApp;
import music.UC;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainerOld extends WinApp {
    public static String UNKNOW = " <- unknow currently";
    public static String ILLEGAL = " <- this name NOT legal";
    public static String KNOW = " <- known name";
    public static Shape.Prototype.List pList = null;

    public static String curName = "";
    public static String curState = ILLEGAL;

    public ShapeTrainerOld() {
        super("Shape Trainer", UC.screenWidth, UC.screenHeight);
    }

    public void setState() {
        curState = !Shape.DB.isLegal(curName) ? ILLEGAL : UNKNOW;
        if (curState == UNKNOW) {
            if (Shape.DB.isKnown(curName)) {
                curState = KNOW;
                pList = Shape.DB.get(curName).prototypes;
            } else {
                pList = null;
            }
        }
    }

    public void paintComponent(Graphics g) {
        G.bgWhite(g);
        g.setColor(Color.black);
        g.drawString(curName, 600, 30);
        g.drawString(curState, 700, 30);
        g.setColor(Color.RED);
        Ink.BUFFER.show(g);
        if (pList != null) {
            pList.show(g);
        }
    }

    public void mousePressed(MouseEvent me) {
        Ink.BUFFER.dn(me.getX(), me.getY());
        repaint();
    }

    public void mouseDragged(MouseEvent me) {
        Ink.BUFFER.drag(me.getX(), me.getY());
        repaint();
    }

    public void mouseReleased(MouseEvent me) {
        Ink ink = new Ink();
        Shape.DB.train(curName, ink.norm);
        setState();
        repaint();

        /*
        if (curState != ILLEGAL) {
            Ink ink = new Ink();
            Shape.Prototype proto;
            if (pList == null) {
                Shape s = new Shape(curName);
                Shape.DB.put(curName, s);
                pList = s.prototypes;
            }
            if (pList.bestDist(ink.norm) < UC.noMatchDist) {
                proto = Shape.Prototype.List.bestMatch;
                proto.blend(ink.norm);
            } else {
                proto = new Shape.Prototype();
                pList.add(proto);
            }
            setState();
        }
        repaint();

         */
    }

    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        System.out.println("type " + c);
        // 0x0D = carriage return (enter key), 0x0A = line feed char
        // white space are illegal
        curName = (c == ' ' || c == 0x0D || c == 0x0A) ? "" : curName + c;
        if (c == 0x0D || c == 0x0A) {
            Shape.DataBase.save();
        }
        setState();
        repaint();
    }

    public static void main(String[] args) {
        PANEL = new ShapeTrainerOld();
        WinApp.launch();
    }


}
