import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceShooter extends JPanel implements ActionListener, KeyListener {

    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    Timer timer;
    Player player;
    ArrayList<Bullet> bullets;
    ArrayList<Enemy> enemies;
    Random random;

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    JButton restartButton;

    public SpaceShooter() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        player = new Player(WIDTH / 2 - 25, HEIGHT - 80);

        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        random = new Random();

        restartButton = new JButton("Restart");
        restartButton.setBounds(340, 320, 120, 40);
        restartButton.setVisible(false);

        restartButton.addActionListener(e -> restartGame());

        add(restartButton);

        timer = new Timer(20, this);
        timer.start();
    }

    private void restartGame() {
        score = 0;
        lives = 3;
        bullets.clear();
        enemies.clear();
        player = new Player(WIDTH / 2 - 25, HEIGHT - 80);
        gameOver = false;
        restartButton.setVisible(false);
        timer.start();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        player.draw(g);

        for (Bullet b : bullets)
            b.draw(g);

        for (Enemy e : enemies)
            e.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("Lives: " + lives, 20, 60);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", 250, 250);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (gameOver)
            return;

        if (random.nextInt(40) == 0) {
            enemies.add(new Enemy(random.nextInt(WIDTH - 30), 0));
        }

        for (Bullet bullet : bullets)
            bullet.move();

        for (Enemy enemy : enemies) {
            enemy.move();

            if (enemy.y > HEIGHT) {
                enemy.y = -100;
                lives--;

                if (lives <= 0) {
                    gameOver = true;
                    timer.stop();
                    restartButton.setVisible(true);
                }
            }
        }

        ArrayList<Bullet> removeBullets = new ArrayList<>();
        ArrayList<Enemy> removeEnemies = new ArrayList<>();

        for (Bullet bullet : bullets) {
            for (Enemy enemy : enemies) {

                if (bullet.getBounds().intersects(enemy.getBounds())) {

                    removeBullets.add(bullet);
                    removeEnemies.add(enemy);
                    score += 10;
                }
            }
        }

        bullets.removeAll(removeBullets);
        enemies.removeAll(removeEnemies);

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (gameOver)
            return;

        switch (e.getKeyCode()) {

            case KeyEvent.VK_LEFT:
                if (player.x > 0)
                    player.x -= player.speed;
                break;

            case KeyEvent.VK_RIGHT:
                if (player.x < WIDTH - player.width)
                    player.x += player.speed;
                break;

            case KeyEvent.VK_SPACE:
                bullets.add(
                        new Bullet(
                                player.x + player.width / 2 - 2,
                                player.y));
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    class Player {
        int x, y;
        int width = 50;
        int height = 30;
        int speed = 12;

        Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void draw(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, width, height);
        }
    }

    class Bullet {
        int x, y;
        int width = 5;
        int height = 15;
        int speed = 10;

        Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void move() {
            y -= speed;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        void draw(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }

    class Enemy {
        int x, y;
        int size = 30;
        int speed = 4;

        Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void move() {
            y += speed;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, size, size);
        }

        void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillOval(x, y, size, size);
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Space Shooter");

        SpaceShooter game = new SpaceShooter();

        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        game.requestFocusInWindow();
    }
}