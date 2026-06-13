import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PingPong extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private static final int PADDLE_WIDTH = 12;
    private static final int PADDLE_HEIGHT = 90;
    private static final int BALL_SIZE = 14;
    private static final int PADDLE_SPEED = 6;
    private static final int BALL_SPEED = 5;
    private static final double SPEED_INCREMENT = 0.15;
    private static final double MAX_SPEED = 3.0;

    private int leftY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int rightY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int ballX = WIDTH / 2 - BALL_SIZE / 2;
    private int ballY = HEIGHT / 2 - BALL_SIZE / 2;
    private int ballDX = BALL_SPEED;
    private int ballDY = BALL_SPEED;
    private double speedMultiplier = 1.0;

    private int leftScore = 0;
    private int rightScore = 0;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean wPressed = false;
    private boolean sPressed = false;

    private final Timer timer;

    public PingPong() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move left paddle (W/S)
        if (wPressed && leftY > 0) {
            leftY -= PADDLE_SPEED;
        }
        if (sPressed && leftY < HEIGHT - PADDLE_HEIGHT) {
            leftY += PADDLE_SPEED;
        }

        // Move right paddle (Up/Down)
        if (upPressed && rightY > 0) {
            rightY -= PADDLE_SPEED;
        }
        if (downPressed && rightY < HEIGHT - PADDLE_HEIGHT) {
            rightY += PADDLE_SPEED;
        }

        // Move ball with speed multiplier
        ballX += (int) Math.round(ballDX * speedMultiplier);
        ballY += (int) Math.round(ballDY * speedMultiplier);

        // Top/bottom wall bounce
        if (ballY <= 0 || ballY >= HEIGHT - BALL_SIZE) {
            ballDY = -ballDY;
        }

        // Left paddle collision
        if (ballX <= PADDLE_WIDTH + 20 && ballY + BALL_SIZE >= leftY && ballY <= leftY + PADDLE_HEIGHT && ballDX < 0) {
            ballDX = -ballDX;
            // Add slight angle based on where ball hits paddle
            int hitPos = (ballY + BALL_SIZE / 2) - (leftY + PADDLE_HEIGHT / 2);
            ballDY = hitPos / 10;
            // Increase speed
            speedMultiplier = Math.min(speedMultiplier + SPEED_INCREMENT, MAX_SPEED);
        }

        // Right paddle collision
        if (ballX + BALL_SIZE >= WIDTH - PADDLE_WIDTH - 20 && ballY + BALL_SIZE >= rightY && ballY <= rightY + PADDLE_HEIGHT && ballDX > 0) {
            ballDX = -ballDX;
            int hitPos = (ballY + BALL_SIZE / 2) - (rightY + PADDLE_HEIGHT / 2);
            ballDY = hitPos / 10;
            // Increase speed
            speedMultiplier = Math.min(speedMultiplier + SPEED_INCREMENT, MAX_SPEED);
        }

        // Score - ball goes past left edge
        if (ballX < 0) {
            rightScore++;
            resetBall(1);
        }

        // Score - ball goes past right edge
        if (ballX > WIDTH) {
            leftScore++;
            resetBall(-1);
        }

        repaint();
    }

    private void resetBall(int direction) {
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballDX = BALL_SPEED * direction;
        ballDY = BALL_SPEED * (Math.random() > 0.5 ? 1 : -1);
        speedMultiplier = 1.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        // Draw paddles
        g.fillRect(20, leftY, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH - 20, rightY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        // Draw center line
        for (int i = 0; i < HEIGHT; i += 20) {
            g.fillRect(WIDTH / 2 - 1, i, 2, 10);
        }

        // Draw scores
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.drawString(String.valueOf(leftScore), WIDTH / 4, 50);
        g.drawString(String.valueOf(rightScore), WIDTH * 3 / 4, 50);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wPressed = true;
            case KeyEvent.VK_S -> sPressed = true;
            case KeyEvent.VK_UP -> upPressed = true;
            case KeyEvent.VK_DOWN -> downPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> wPressed = false;
            case KeyEvent.VK_S -> sPressed = false;
            case KeyEvent.VK_UP -> upPressed = false;
            case KeyEvent.VK_DOWN -> downPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ping Pong");
        PingPong game = new PingPong();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}
