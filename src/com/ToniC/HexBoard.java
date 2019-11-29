package com.ToniC;

/**
 *
 * @author Usuari
 */
import edu.upc.epsevg.prop.hex.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class HexBoard extends MouseAdapter {

    private JFrame mainFrame;

    private BufferedImage image = null;
    private int r;
    private int b;
    private double x, y;
    private int n;
    private double h;
    private double dx;
    private double dy;
    private IPlayer players[];
    private HexGameStatus status;

    private int curPlayerIdx = 0;
    private GameEstatus gameEstatus;
    private JControlsPanel controlPanel;
    private JPanel boardPanel;
    private int baseX;
    private int baseY;

    private int getCurrentPlayerColor() {
        return curPlayerIdx == 0 ? 1 : -1;
    }

    private IPlayer getCurrentPlayer() {
        return players[curPlayerIdx];
    }

    private boolean isCurrentPlayerAuto() {
        return getCurrentPlayer() instanceof IAuto;
    }

    private enum GameEstatus {
        INIT,
        PLAYING,
        END_GAME
    }

    public HexBoard() {

        initComponents();

    }

    public HexBoard(HexGameStatus status, IPlayer player1, IPlayer player2) {
        this.status = status;
        this.n = status.getSize();
        this.players = new IPlayer[2];
        this.players[0] = player1;
        this.players[1] = player2;

        this.gameEstatus = GameEstatus.INIT;
        this.curPlayerIdx = 0;
        initComponents();
        showCurrentStatus();
    }

    private void showCurrentStatus() {
        switch (gameEstatus) {
            case INIT: {
                controlPanel.setThinking(false);
                controlPanel.setPlayer1Name(players[0].getName());
                controlPanel.setPlayer2Name(players[1].getName());
                String clicToStart = "Click START !";
                controlPanel.setPlayer1Message(clicToStart);
                controlPanel.setPlayer2Message(clicToStart);
                controlPanel.setButtonText("Start the game");
                controlPanel.setButtonEnabled(true);
            }
            break;
            case END_GAME: {
                controlPanel.setThinking(false);
                String win = "YOU WIN ! :-D ";
                String lose = "You lose :_(";
                if (curPlayerIdx == 0) {
                    controlPanel.setPlayer1Message(win);
                    controlPanel.setPlayer2Message(lose);
                } else {
                    controlPanel.setPlayer2Message(win);
                    controlPanel.setPlayer1Message(lose);
                }
                controlPanel.setButtonText("Another game?");
                controlPanel.setButtonEnabled(true);
            }
            break;
            case PLAYING: {
                controlPanel.setThinking(false);
                String waiting = "Waiting....";
                String yourTurn = isCurrentPlayerAuto() ? "Thinking..." : "Your Turn. Move please.";
                if (curPlayerIdx == 0) {
                    controlPanel.setPlayer1Message(yourTurn);
                    controlPanel.setPlayer2Message(waiting);
                } else {
                    controlPanel.setPlayer2Message(yourTurn);
                    controlPanel.setPlayer1Message(waiting);
                }
                controlPanel.setButtonText("Stop");
                controlPanel.setButtonEnabled(!isCurrentPlayerAuto());
            }
            break;
        }
    }

    void OnStartClicked() {
        status = new HexGameStatus(n);
        boardPanel.repaint();
        curPlayerIdx = 0;
        if (gameEstatus == GameEstatus.PLAYING) { //wish to STOP
            gameEstatus = GameEstatus.INIT;
            showCurrentStatus();
        } else if (gameEstatus == GameEstatus.INIT || gameEstatus == GameEstatus.END_GAME) {
            gameEstatus = GameEstatus.PLAYING;
            showCurrentStatus();
            startTurn();
        }

    }

    private void startTurn() {
        if (isCurrentPlayerAuto()) {
            this.controlPanel.setThinking(true);
            (new Mover()).execute();
            //(new Mover()).doInBackground();
        } else {

        }
    }

    private void endTurn() {
        if (status.isGameOver()) {
            gameEstatus = GameEstatus.END_GAME;

            showCurrentStatus();
        } else {
            curPlayerIdx = (curPlayerIdx + 1) % 2;
            showCurrentStatus();
            startTurn();
        }
    }

    class Mover extends SwingWorker<Point, Object> {

        Mover() {

        }

        @Override
        public Point doInBackground() {
            try {
                return getCurrentPlayer().move(status, getCurrentPlayerColor());
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                System.out.println( sw.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                HexBoard.this.controlPanel.setThinking(false);
                status.placeStone(get(), getCurrentPlayerColor());
//                System.out.println(">" + status.toString());
                boardPanel.repaint();
                endTurn();
            } catch (Exception ignore) {
            }
        }

    }

    private void drawHexa(Graphics2D g, Point p, int radius) {
        drawHexa(g, p, radius, false, null);
    }

    private void drawHexa(Graphics2D g, Point p, int radius, boolean fill, Color c) {

        Polygon pol = new Polygon();
        double a = 0, da = 2 * Math.PI / 6;
        for (int s = 0; s < 6; s++, a += da) {
            pol.addPoint((int) (p.x + radius * Math.sin(a)), (int) (p.y + radius * Math.cos(a)));
        }
        if (!fill) {
            g.setColor(new Color(0, 0, 0, 40));
            g.setStroke(new BasicStroke(7));
            g.drawPolygon(pol);
            g.setColor(new Color(0, 0, 0, 255));
            g.setStroke(new BasicStroke(2));
            g.drawPolygon(pol);
        } else {
            g.setColor(c);
            g.fillPolygon(pol);

        }

    }

    private void buildHexaLine(Graphics2D g, int xPoints[], int yPoints[], int base, Point p, int radius, int min, int max, Color c) {

        g.setColor(c);
        g.setStroke(new BasicStroke(5));
        double da = 2 * Math.PI / 6;
        double a = min * da;

        int i = 0;
        for (int s = min; s < max; s++, a += da, i++) {
            xPoints[base + i] = (int) (p.x + radius * Math.sin(a));
            yPoints[base + i] = (int) (p.y + radius * Math.cos(a));
        }
    }

    private void initComponents() {
        try {
            image = ImageIO.read(getClass().getResource("/resources/back_light.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame();
        //mainMap.addComponentListener(this);
        //mainMap.setResizable(false);

        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        r = 38; // radius external grid
        b = 8; // borders
        x = 0;
        y = 0;

        h = r * Math.sin(2 * Math.PI / 6);
        dx = 2 * h;
        dy = r + r * Math.sin(Math.PI / 6);//h / 2;

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Color blackColor = new Color(45, 72, 106, 255);
                Color whiteColor = new Color(255, 255, 255, 255);
                Color backColor = new Color(241, 200, 134, 255);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

                //g.setColor(backColor);
                //g.fillRect(0, 0, getWidth(), getHeight());
                baseX = (int) ((getWidth() - ((n - 1) * dx + (n - 1) * h)) / 2);
                baseY = (int) ((getHeight() - (n - 1) * dy) / 2);

                if (status.isGameOver()) {
                    ArrayList<Point> solPoints = status.getSolution();
                    for (Point pPos : solPoints) {
                        Point p = getCoord(baseX, baseY, pPos.y, pPos.x);
                        drawHexa(g2d, p, r - b, true, new Color(241, 0, 0, 255));//curPlayerIdx==1? whiteColor:blackColor);
                    }
                }

                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        Point p = getCoord(baseX, baseY, i, j);
                        drawHexa(g2d, p, r - b);
                        int color = status.getPos(j, i);
                        if (color != 0) {
                            paintStone(g2d, color == 1, p.x, p.y, r - b - 8);
                        }
                    }
                }

                //-------------------------------------------------------------------
                int xPoints[] = new int[n * 2 + 3];
                int yPoints[] = new int[n * 2 + 3];

                Point p = getCoord(baseX, baseY, 0, 0);
                p.x -= h;
                p.y -= (Math.cos(Math.PI / 3)) * r;

                p.x -= h;
                p.y -= r * (1 - Math.cos(Math.PI / 3));

                double ddy = (1 - Math.cos(Math.PI / 3)) * r;
                double ddx = dx / 2;
                double xx = p.x, yy = p.y;
                for (int j = 0; j <= 2 * n + 1; j++) {
                    xPoints[j] = (int) xx;
                    yPoints[j] = (int) (yy + (j % 2 == 1 ? ddy : 0));
                    xx += ddx;
                }
                xPoints[xPoints.length - 2] = xPoints[0];
                yPoints[yPoints.length - 2] = yPoints[0];

                g2d.setStroke(new BasicStroke(7));
                g.setColor(blackColor);
                g.fillPolygon(xPoints, yPoints, 2 * n + 2);

                xx = p.x;
                yy = p.y;
//                xx-=h;
//                yy -= r * (1-Math.cos(Math.PI/3));
                int j = 0;
                for (; j < 2 * n + 1; j++) {
                    xPoints[j] = (int) xx;
                    yPoints[j] = (int) yy;
                    if (j % 2 == 0) {
                        xx += h;
                        yy += r * (1 - Math.cos(Math.PI / 3));
                    } else {
                        yy += r * 2 * Math.sin(Math.PI / 6);
                    }
                }
                xPoints[j] = xPoints[0];
                yPoints[j] = yPoints[0];

                g2d.setStroke(new BasicStroke(7));
                g.setColor(whiteColor);
                g.fillPolygon(xPoints, yPoints, 2 * n + 2);

                p = getCoord(baseX, baseY, n - 1, 0);
                //p.x -=h;
                p.y += r;//(Math.cos(Math.PI/3))*r;
                //double ddy = (1-Math.cos(Math.PI/3))*r;
                //double ddx = dx/2;
                xx = p.x;
                yy = p.y;
                for (j = 0; j <= 2 * n; j++) {
                    xPoints[j] = (int) xx;
                    yPoints[j] = (int) (yy - (j % 2 == 1 ? ddy : 0));
                    xx += ddx;
                }
                xPoints[j] = xPoints[0];
                yPoints[j] = yPoints[0];
                g.setColor(blackColor);
                g.fillPolygon(xPoints, yPoints, 2 * n + 1);

                p = getCoord(baseX, baseY, 0, n - 1);
                p.x += h;
                p.y -= (Math.cos(Math.PI / 3)) * r;
                xx = p.x;
                yy = p.y;
                for (j = 0; j <= 2 * n + 1; j++) {
                    xPoints[j] = (int) xx;
                    yPoints[j] = (int) (yy);
                    if (j % 2 == 1) {
                        xx += h;
                        yy += r * (1 - Math.cos(Math.PI / 3));
                    } else {
                        yy += r * 2 * Math.sin(Math.PI / 6);
                    }
                }
                xPoints[j] = xPoints[0];
                yPoints[j] = yPoints[0];
                g2d.setStroke(new BasicStroke(7));
                g.setColor(whiteColor);
                g.fillPolygon(xPoints, yPoints, 2 * n + 1);

            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension((int) (n * dx + (n - 1) * h) + 200, (int) (n * dy + 200));
            }
        };

        boardPanel.addMouseListener(this);

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        controlPanel = new JControlsPanel(this);
        controlPanel.setThinking(true);
        mainPane.add(controlPanel, BorderLayout.WEST);
        mainPane.add(boardPanel, BorderLayout.CENTER);

        Dimension dB = boardPanel.getPreferredSize();
        Dimension dP = controlPanel.getMinimumSize();
        Dimension d = new Dimension(dB.width + dP.width, dB.height);
        mainFrame.setMinimumSize(d);
        mainFrame.add(mainPane);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int y = (int) Math.round((me.getY() - baseY) / dy);
        int x = 0;
        if (y >= 0 && y < n) {
            x = (int) Math.round(((me.getX() - baseX) - y * h) / dx);
            if (x >= 0 && x < n) {
                if (gameEstatus == GameEstatus.PLAYING && !isCurrentPlayerAuto()) {
                    status.placeStone(new Point(x, y), getCurrentPlayerColor());
//                    System.out.println(">" + status.toString());
                    boardPanel.repaint();
                    endTurn();
                }
            }
        }

    }

    public Point getCoord(int baseX, int baseY, int i, int j) {
        int x = (int) (baseX + i * h + j * dx);
        int y = (int) (baseY + i * dy);
        return new Point(x, y);
    }

    protected void paintStone(Graphics2D g2, boolean isWhite, int x, int y, int radius) {

        x -= radius;
        y -= radius;
        int size = radius * 2;

        // Retains the previous state
        Paint oldPaint = g2.getPaint();

        // Fills the circle with solid blue color
        //g2.setColor(new Color(0x0153CC));
        int backColor = isWhite ? 0xFFFFFF : 0x333333;
        g2.setColor(new Color(backColor));
        g2.fillOval(x, y, size - 1, size - 1);
        g2.setColor(new Color(0x000000));
        g2.drawOval(x, y, size - 1, size - 1);

        // Adds shadows at the top
        Paint p;
        p = new GradientPaint(x, y, new Color(0.0f, 0.0f, 0.0f, 0.4f),
                x, y + size, new Color(0.0f, 0.0f, 0.0f, 0.0f));
        g2.setPaint(p);
        g2.fillOval(x, y, size - 1, size - 1);

        // Adds highlights at the bottom 
        {
            //Color i =isWhite? new Color(1.0f, 1.0f, 1.0f, 0.0f);
            //Color f = new Color(1.0f, 1.0f, 1.0f, 0.4f); 
            Color i = isWhite ? new Color(160, 160, 160, 127) : new Color(1.0f, 1.0f, 1.0f, 0.0f);
            Color f = isWhite ? new Color(0.0f, 0.0f, 0.0f, 0.1f) : new Color(1.0f, 1.0f, 1.0f, 0.4f);

            p = new GradientPaint(x, y, i,
                    x, y + size, f);
            g2.setPaint(p);
            g2.fillOval(x, y, size - 1, size - 1);
        }
        // Creates dark edges for 3D effect
        //Color i = new Color(6, 76, 160, 127);
        //Color f = new Color(0.0f, 0.0f, 0.0f, 0.8f); 
        {
            Color i = isWhite ? new Color(250, 250, 250, 127) : new Color(6, 76, 160, 127);
            Color f = isWhite ? new Color(0.0f, 0.0f, 0.0f, 0.2f) : new Color(0.0f, 0.0f, 0.0f, 0.8f);
            p = new RadialGradientPaint(new Point2D.Double(x + size / 2.0,
                    y + size / 2.0), size / 2.0f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{i,
                        f});
            g2.setPaint(p);
            g2.fillOval(x, y, size - 1, size - 1);
        }

        // Adds oval specular highlight at the top left
        p = new RadialGradientPaint(new Point2D.Double(x + size / 2.0,
                y + size / 2.0), size / 1.4f,
                new Point2D.Double(45.0, 25.0),
                new float[]{0.0f, 0.5f},
                new Color[]{new Color(1.0f, 1.0f, 1.0f, 0.4f),
                    new Color(1.0f, 1.0f, 1.0f, 0.0f)},
                RadialGradientPaint.CycleMethod.NO_CYCLE);
        g2.setPaint(p);
        g2.fillOval(x, y, size - 1, size - 1);

        // Restores the previous state
        g2.setPaint(oldPaint);

    }

}
