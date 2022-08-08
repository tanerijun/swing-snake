import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int WINDOW_WIDTH = 600;
    static final int WINDOW_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int MAX_UNITS = WINDOW_WIDTH * WINDOW_HEIGHT / UNIT_SIZE; // how many units can fit in the game
    static final int DELAY = 75; // speed of the game. The higher, the slower the game is

    // These arrays hold the coordinates for the body parts of the snake
    final int[] x = new int[MAX_UNITS];
    final int[] y = new int[MAX_UNITS];

    int snakeJoints = 6; // initial snake size
    int score;
    int foodX;
    int foodY;
    char direction = 'R'; // U, R, D, L
    boolean gameIsRunning = false;
    Timer timer;
    Random random;

    Color lineColor = Color.GRAY;
    Color foodColor = Color.ORANGE;
    Color snakeHeadColor = Color.decode("#013f28");
    Color snakeBodyColor = Color.decode("#013330");

    GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new SnakeKeyAdapter());
        startGame();
    }

    public void startGame() {
        spawnFood();
        gameIsRunning = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!gameIsRunning) {
            gameOver(g);
            return;
        }

        // draw grid
        g.setColor(lineColor);
        for (int i = 0; i < WINDOW_HEIGHT / UNIT_SIZE; i++) {
            if (i == 0) continue;
            drawDashedLine(g,i * UNIT_SIZE, 0, i * UNIT_SIZE, WINDOW_HEIGHT); // vertical
            drawDashedLine(g, 0, i * UNIT_SIZE, WINDOW_WIDTH, i * UNIT_SIZE); // horizontal
        }

        // draw food
        g.setColor(foodColor);
        g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

        // draw snake
        for (int i = 0; i < snakeJoints; i++) {
            if (i == 0)
                g.setColor(snakeHeadColor);
            else
                g.setColor(snakeBodyColor);

            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }

        // draw score
        drawCenteredText(g, "Score: " + score, 35, (int) (WINDOW_HEIGHT * 0.1));
    }

    public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2d = (Graphics2D) g.create(); // make a copy of the Graphics instance

        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
        g2d.setStroke(dashed);

        g2d.drawLine(x1, y1, x2, y2);

        g2d.dispose(); // delete the copy
    }

    public void move() {
        // Shift each joint by 1 coordinate
        for (int i = snakeJoints; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
        }
    }

    public void spawnFood() {
        foodX = random.nextInt(WINDOW_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(WINDOW_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void checkFood() {
        // check if head collide with food
        if ((x[0] == foodX) && (y[0] == foodY)) {
            snakeJoints++;
            score++;
            spawnFood();
        }
    }

    public void checkCollisions() {
        // check if head collide with body
        for (int i = snakeJoints; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                gameIsRunning = false;
                break;
            }
        }

        // check if head collide with game borders
        if ((x[0] <  0) || x[0] > WINDOW_WIDTH || y[0] < 0 || y[0] > WINDOW_HEIGHT) {
            gameIsRunning = false;
        }
    }

    public void gameOver(Graphics g) {
        drawCenteredText(g, "Score: " + score, 35, (int) (WINDOW_HEIGHT * 0.1));
        drawCenteredText(g, "Game Over", 75, WINDOW_HEIGHT / 2);
    }

    public void drawCenteredText(Graphics g, String text, int size, int yCoordinate) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, size));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(text, (WINDOW_WIDTH - metrics.stringWidth(text)) / 2, yCoordinate);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameIsRunning) {
            move();
            checkFood();
            checkCollisions();
        }

        repaint();
    }

    public class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    // Prevent 180deg turn
                    if (direction != 'R') direction = 'L';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') direction = 'U';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') direction = 'R';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') direction = 'D';
                }
            }
        }
    }
}
