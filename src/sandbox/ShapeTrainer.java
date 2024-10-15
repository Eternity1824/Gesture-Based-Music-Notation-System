package sandbox;

import graphics.WinApp;
import music.UC;
import reaction.Shape;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShapeTrainer extends WinApp {
    public ShapeTrainer() {super("ShapeTrainer", UC.screenWidth, UC.screenHeight);}
    public void paintComponent(Graphics g) {Shape.TRAINER.show(g);}
    public void mousePressed(MouseEvent me) {Shape.TRAINER.dn(me.getX(), me.getY()); repaint();}
    public void mouseReleased(MouseEvent me) {Shape.TRAINER.up(me.getX(), me.getY()); repaint();}
    public void mouseDragged(MouseEvent me) {Shape.TRAINER.drag(me.getX(), me.getY()); repaint();}
    public void keyTyped(KeyEvent ke) {Shape.TRAINER.keyTyped(ke); repaint();}

    public static void main(String[] args) {
        PANEL = new ShapeTrainer();
        WinApp.launch();
    }
}
