import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class DoubleWinGame extends JFrame {
    private int currentWin;
    private int roundsLeft = 4;
    private MainSlotMachine parent;
    private Image backgroundImage;
    private BackgroundMusic backgroundMusic;
    private Timer musicResumeTimer;

    public DoubleWinGame(int initialWin, MainSlotMachine parent) {
        this.currentWin = initialWin;
        this.parent = parent;
        this.backgroundMusic = new BackgroundMusic();
        this.backgroundImage = new ImageIcon("image/Double.png").getImage();

        //play music when the window opens
        backgroundMusic.play("image/drumscut.wav");

        //stop music when the window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMusicAndTimers();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                stopMusicAndTimers();
            }
        });

        setTitle("Double or Nothing");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //current Win + Stop button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.RED);
        topPanel.setPreferredSize(new Dimension(400, 50));

        JLabel infoLabel = new JLabel("Current Win: " + currentWin + " $", JLabel.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        infoLabel.setForeground(Color.BLACK);
        topPanel.add(infoLabel, BorderLayout.CENTER);

        JButton stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Arial", Font.BOLD, 20));
        stopButton.addActionListener(e -> stopDoubling());
        stopButton.setBackground(Color.LIGHT_GRAY);
        topPanel.add(stopButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //Red + Black button
        JPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);

        JButton redButton = new JButton("Red");
        redButton.setBounds(100, 160, 80, 90);
        redButton.addActionListener(e -> makeGuess("red", infoLabel));
        backgroundPanel.add(redButton);

        JButton blackButton = new JButton("Black");
        blackButton.setBounds(307, 160, 80, 90);
        blackButton.addActionListener(e -> makeGuess("black", infoLabel));
        backgroundPanel.add(blackButton);

        add(backgroundPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void makeGuess(String guess, JLabel infoLabel) {
        if (roundsLeft <= 0) {
            JOptionPane.showMessageDialog(this, "You reached the maximum number of doubles!");
            stopDoubling();
            return;
        }

        Random random = new Random();
        String result = random.nextBoolean() ? "red" : "black";

        //stop the music + cancel any active timers
        if (musicResumeTimer != null) {
            musicResumeTimer.stop();
        }
        backgroundMusic.stop();

        //resume after delay
        musicResumeTimer = new Timer(1000, e -> backgroundMusic.play("image/drumscut.wav"));
        musicResumeTimer.setRepeats(false);
        musicResumeTimer.start();

        if (guess.equals(result)) {
            currentWin *= 2;
            roundsLeft--;
            infoLabel.setText("Current Win: " + currentWin + " $");
        } else {
            JOptionPane.showMessageDialog(this, "Womp Womp! You lost!");
            currentWin = 0;
            stopDoubling();
        }
    }

    private void stopDoubling() {
        parent.updateBalanceAndWin(currentWin); //update main screen
        stopMusicAndTimers(); //music and timers stop if not stopped
        dispose(); //close window
    }

    private void stopMusicAndTimers() {
        if (musicResumeTimer != null) {
            musicResumeTimer.stop();
        }
        backgroundMusic.stop();
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
