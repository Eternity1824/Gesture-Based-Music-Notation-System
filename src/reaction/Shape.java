package reaction;

import music.I;
import music.UC;
import graphics.G;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class Shape implements Serializable {
    public static Shape.DataBase DB = Shape.DataBase.load();
    public static Shape DOT = DB.get("DOT");
    public static Trainer TRAINER = new Trainer();
    // LIST will always be up to date with DB, supported by language feature
    public static Collection<Shape> List = DB.values();
    public Prototype.List prototypes = new Prototype.List();

    public String name;
    public Shape(String name) {
        this.name = name;
    }

    public static Shape recognize(Ink ink) { // note cannot return null
        if (ink.vs.size.x < UC.dotThresHold && ink.vs.size.y < UC.dotThresHold) {
            return DOT;
        }
        Shape bestMatch = null;
        int bestSoFar = UC.noMatchDist;
        for (Shape s : List) {
            int d = s.prototypes.bestDist(ink.norm);
            if (d < bestSoFar) {
                bestMatch = s;
                bestSoFar = d;
            }
        }
        return bestMatch;
    }

    //-----------------------Database---------------------------
    public static class DataBase extends TreeMap<String, Shape> implements Serializable {
        private static String fileName = UC.shapeDataBasefilename;

        private DataBase() {
            super();
            String dot = "DOT";
            put(dot, new Shape(dot));
        }

        private Shape forceGet(String name) {
            if (!DB.containsKey(name)) {
                DB.put(name, new Shape(name));
            }
            return DB.get(name);
        }

        public void train(String name, Ink.Norm norm) {
            if (isLegal(name)) {
                forceGet(name).prototypes.train(norm);
            }
        }

        public static DataBase load() {
            DataBase res;
            try {
                System.out.println("Loading " + fileName);
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
                res = (Shape.DataBase) ois.readObject();
                System.out.println("successfully loaded " + res.keySet());
                ois.close();
            } catch (Exception e) {
                System.out.println("failed to load " + fileName);
                System.out.println("caught " + e);
                res = new DataBase();
            }
            return res;
        }

        public static void save() {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
                oos.writeObject(DB);
                System.out.println("successfully saved " + fileName);
                oos.close();
            } catch (Exception e) {
                System.out.println("failed to save " + fileName);
                System.out.println("caught " + e);
            }
        }

        public boolean isKnown(String name) {return containsKey(name);}

        public boolean unKnown(String name) {return !containsKey(name);}

        public boolean isLegal(String name) {return !name.equals("") && ! name.equals("DOT");}
    }




    //------------------Prototype--------------------represent one way of drawing a shape
    public static class Prototype extends Ink.Norm implements Serializable{
        public int nBlend;
        public void blend(Ink.Norm norm) {
            blend(norm, nBlend);
            nBlend++;
        }

        //---------------List-----------------------
        public static class List extends ArrayList<Prototype> implements I.Show, Serializable{
            // set as side effect of bestDist()
            public static Prototype bestMatch;
            public int bestDist(Ink.Norm norm) {
                bestMatch = null;
                int bestSoFar = UC.noMatchDist;
                for (Prototype p : this) {
                    int d = p.dist(norm);
                    if (d < bestSoFar) {
                        bestMatch = p;
                        bestSoFar = d;
                    }
                }
                return bestSoFar;
            }
            public void train (Ink.Norm norm) {
                if (bestDist(norm) < UC.noMatchDist) {
                    bestMatch.blend(norm);
                } else {
                    add(new Shape.Prototype());
                }
            }
            private static int m = 10, w = 40, showBoxHeight = m + w;
            private static G.VS showBox = new G.VS(m, m, w, w);
            @Override
            public void show(Graphics g) {
                g.setColor(Color.orange);
                for (int i = 0; i < this.size(); i++) {
                    Prototype p = this.get(i);
                    int x = m + i * (m + w);
                    showBox.loc.set(x, m);
                    p.drawAt(g, showBox);
                    g.drawString("" + p.nBlend, x, 20);
                }
            }
        }
    }
    //-------------------------Trainer-------------------------------
    public static class Trainer implements I.Show, I.Area {
        public static String UNKNOW = " <- unknow currently";
        public static String ILLEGAL = " <- this name NOT legal";
        public static String KNOW = " <- known name";
        public static Shape.Prototype.List pList = null;
        public static String curName = "";
        public static String curState = ILLEGAL;
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
        private boolean removePrototype(int x, int y) {
            int H = Prototype.List.showBoxHeight;
            if (y < H) {
                int ndx = x / H;
                Prototype.List pList = TRAINER.pList;
                if (pList != null && ndx < pList.size()) {
                    pList.remove(ndx);
                    Ink.BUFFER.clear();
                    return true;
                }
            }
            return false;
        }
        private Trainer() {}
        public void show(Graphics g) {
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
        public boolean hit(int x, int y) {return true;}
        public void dn(int x, int y) {Ink.BUFFER.dn(x, y);}
        public void drag(int x, int y) {Ink.BUFFER.drag(x, y);}
        public void up(int x, int y) {
            if (removePrototype(x, y)) {return;}
            Ink.BUFFER.up(x, y);
            Ink ink = new Ink();
            Shape.DB.train(curName, ink.norm);
            setState();
        }
        public void keyTyped(KeyEvent ke) {
            char c = ke.getKeyChar();
            if (c == 0x0D || c == 0x0A) {Shape.DB.save();}
            curName = (c == ' ' || c == 0x0D || c == 0x0A)? "": curName + c;
            setState();
        }
    }
}
