import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class TypingGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private ArrayList<FallingWord> words;
    private String userInput = "";
    private int score = 0;
    private boolean gameOver = false;
    private final String[] wordList = {
        "java", "swing", "keyboard", "block", "timer",
        "panel", "thread", "object", "class", "method"
    };
    private Random rand = new Random();

    public TypingGame() {
        setFocusable(true);
        setBackground(Color.BLACK);
        setFont(new Font("Consolas", Font.BOLD, 24));
        addKeyListener(this);

        words = new ArrayList<>();
        timer = new Timer(30, this); // 30ms = ~33 FPS
        timer.start();

        // Add first word
        words.add(new FallingWord(randomWord(), rand.nextInt(700), 0));
    }

    private String randomWord() {
        return wordList[rand.nextInt(wordList.length)];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        for (FallingWord word : words) {
            word.y += word.speed;

            if (word.y > getHeight() - 30) {
                gameOver = true;
            }
        }

        // Occasionally add new words
        if (rand.nextInt(100) < 2) {
            words.add(new FallingWord(randomWord(), rand.nextInt(700), 0));
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Consolas", Font.BOLD, 48));
            g.drawString("Game Over", 280, 250);
            g.drawString("Score: " + score, 320, 320);
            return;
        }

        g.setColor(Color.GREEN);
        g.drawString("Score: " + score, 10, 30);

        for (FallingWord word : words) {
            g.setColor(Color.WHITE);
            g.drawString(word.text, word.x, word.y);
        }

        // Show user input
        g.setColor(Color.CYAN);
        g.drawString(">> " + userInput, 10, getHeight() - 20);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (gameOver) return;

        char c = e.getKeyChar();
        if (Character.isLetterOrDigit(c)) {
            userInput += c;
        } else if (c == '\b' && userInput.length() > 0) {
            userInput = userInput.substring(0, userInput.length() - 1);
        } else if (c == '\n' || c == '\r') {
            checkUserInput();
        }
        repaint();
    }

    private void checkUserInput() {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).text.equals(userInput)) {
                words.remove(i);
                score++;
                userInput = "";
                return;
            }
        }
        userInput = ""; // clear input if no match
    }

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Typing Tetris Game");
        TypingGame gamePanel = new TypingGame();
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    
    static class FallingWord {
        String text;
        int x, y, speed;

        public FallingWord(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.speed = 1 + new Random().nextInt(3); // random speed 1-3
        }
    }
}
