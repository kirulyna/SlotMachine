import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.*;


public class MainSlotMachine extends JFrame {
    private Image backgroundImage;
    private int balance;
    private int bet = 10; //default cuc
    private int lastWin = 0; //default cuc
    private JLabel balanceLabel;
    private JLabel winLabel;
    private JTextField betField;

    private Image[] slotImages; //slot simbol images
    private int[][] slotPositions; //positions for each of the 3 columns
    private boolean isSpinning = false;
    private int[] currentSlotIndices;
    private int ROWS = 3;
    private int SYMBOL_HEIGHT = 200;
    private BackgroundMusic backgroundMusic;

    public MainSlotMachine(int initialBalance) {
        this.balance = initialBalance;
        this.slotPositions = new int[3][ROWS + 1];
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //bg music
        backgroundMusic = new BackgroundMusic();
        backgroundMusic.play("image/SlotMachineMain1.wav"); //<-- Jazz chill
        //backgroundMusic.play("image/aparate.wav"); //<-- Manele
        backgroundMusic.setVolume(-20.0f);

        //background image load
        try {
            backgroundImage = ImageIO.read(new File("image/SlotMachineInterface.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //slot images load
        slotImages = new Image[6];
        for (int i = 0; i < 6; i++) {
            try {
                slotImages[i] = ImageIO.read(new File("image/" + getSymbolName(i) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //initialize slot positions with random symbols
        slotPositions = new int[3][ROWS];
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < ROWS; j++) {
                slotPositions[i][j] = rand.nextInt(6); // Random symbol index
            }
        }

        //setup main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                //bacckground
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

                //images to sett
                int startX = 850;
                int startY = 235;
                int columnWidth = 200;
                int rowHeight = SYMBOL_HEIGHT - 40;
                int spacing = 140;
                int visibleHeight = rowHeight * 5; //visible space

                //draw simbols
                for (int col = 0; col < 3; col++) {
                    for (int row = 0; row < ROWS; row++) {
                        int symbolIndex = slotPositions[col][(row + ROWS) % ROWS];
                        int x = startX + col * (columnWidth + spacing); // X poz
                        int y = startY + (row * rowHeight); // Y poz

                        g.drawImage(slotImages[symbolIndex], x, y, columnWidth, rowHeight, null);
                    }
                }
            }
        };
        mainPanel.setLayout(null);

        //balance label
        balanceLabel = new JLabel("Balance: " + balance + " $");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel.setBounds(1000, 110, 300, 30);
        mainPanel.add(balanceLabel);

        //last win label
        winLabel = new JLabel("Last Win: " + lastWin + " $");
        winLabel.setFont(new Font("Arial", Font.BOLD, 24));
        winLabel.setBounds(1500, 110, 300, 30);
        mainPanel.add(winLabel);

        //bet input field
        betField = new JTextField(String.valueOf(bet), 5);
        betField.setBackground(Color.yellow);
        betField.setFont(new Font("Arial", Font.PLAIN, 22));
        betField.setBounds(835, 860, 250, 20);
        mainPanel.add(betField);

        //start button
        JButton startButton = new JButton("START");
        startButton.setBackground(Color.yellow);
        startButton.setFont(new Font("Arial", Font.BOLD, 28));
        startButton.setBounds(1190, 830, 200, 60);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        mainPanel.add(startButton);

        //double or nothing button
        JButton doubleButton = new JButton("Double Win");
        doubleButton.setBackground(Color.yellow);
        doubleButton.setFont(new Font("Arial", Font.BOLD, 24));
        doubleButton.setBounds(1490, 848, 250, 40);
        doubleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastWin == -1) {
                    JOptionPane.showMessageDialog(MainSlotMachine.this, "Nothing * Nothing is still Nothing");
                    return;
                }
                new DoubleWinGame(lastWin, MainSlotMachine.this);
            }
        });
        mainPanel.add(doubleButton);

        add(mainPanel);
        setVisible(true);
    }

    private void startGame() {
        try {
            int currentBet = Integer.parseInt(betField.getText());
            if (currentBet > balance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!");
                return;
            }

            balance -= currentBet;
            balanceLabel.setText("Balance: " + balance + " $");

            //slot initialized with random symbols
            Random rand = new Random();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < ROWS; j++) {
                    slotPositions[i][j] = rand.nextInt(6); //random symbol index
                }
            }

            isSpinning = true;
            backgroundMusic = new BackgroundMusic();
            backgroundMusic.play("image/rollsoundcut2.wav"); //<--  porgetesi cuc

            Timer timer = new Timer(70, new ActionListener() {
                int steps = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    steps++;
                    for (int col = 0; col < 3; col++) {
                        for (int row = 0; row < ROWS; row++) {
                            slotPositions[col][row] = (slotPositions[col][row] + 1) % 6;//symbols goes downward
                        }
                    }
                    repaint();

                    if (steps >= 55) { //end animation after 70 steps
                        ((Timer) e.getSource()).stop();
                        isSpinning = false;

                        //stop music
                        backgroundMusic.stop();

                        //middle row is the final result
                        currentSlotIndices = new int[3];
                        for (int col = 0; col < 3; col++) {
                            currentSlotIndices[col] = slotPositions[col][1]; //center row
                        }
                        repaint();

                        //calculate winnings
                        double winnings = calculateWinnings();
                        lastWin = (int) (winnings * currentBet);
                        balance += lastWin;

                        //sound play if win
                        if (lastWin > 0) {
                            backgroundMusic = new BackgroundMusic();
                            backgroundMusic.playOnce("image/WinSound.wav"); //win sound
                        }

                        balanceLabel.setText("Balance: " + balance + " $");
                        winLabel.setText("Last Win: " + lastWin + " $");
                        //backgroundMusic.stop();
                    }
                }
            });
            timer.start();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid bet amount!");
        }
    }

    //kombinaciok nyereshez
    private double calculateWinnings() {
        int s1 = currentSlotIndices[0];
        int s2 = currentSlotIndices[1];
        int s3 = currentSlotIndices[2];

        if (s1 == 5 && s2 == 5 && s3 == 5) {
            return 100.0;
        } else if (s1 == 2 && s2 == 2 && s3 == 2) {
            return 50.0;
        } else if (s1 == 3 && s2 == 3 && s3 == 3) {
            return 30.0;
        } else if (s1 == 0 && s2 == 0 && s3 == 0) {
            return 10.0;
        } else if (s1 == 4 && s2 == 4 && s3 == 4) {
            return 8.0;
        } else if (s1 == 1 && s2 == 1 && s3 == 1) {
            return 6.66;
        } else if (s1 == 2 && s2 == 5 && s3 == 5) {
            return 5.00;
        } else if (s1 == 2 && s2 == 2 && s3 == 5) {
            return 4.20;
        } else if (s1 == 2 && s2 == 5 && s3 == 2) {
            return 4.20;
        } else if (s1 == 2 && s2 == 3 && s3 == 3) {
            return 3.60;
        } else if (s1 == 2 && s2 == 0 && s3 == 0) {
            return 2.20;
        } else if (s1 == 2 && s2 == 4 && s3 == 4) {
            return 1.80;
        } else if (s1 == 2 && s2 == 1 && s3 == 1) {
            return 1.50;
        } else if (s1 == 5 && s2 == 5 && s3 == 2) {
            return 5.00;
        } else if (s1 == 5 && s2 == 2 && s3 == 2) {
            return 4.20;
        } else if (s1 == 3 && s2 == 3 && s3 == 2) {
            return 3.60;
        } else if (s1 == 0 && s2 == 0 && s3 == 2) {
            return 2.20;
        } else if (s1 == 4 && s2 == 4 && s3 == 2) {
            return 1.80;
        } else if (s1 == 1 && s2 == 1 && s3 == 2) {
            return 1.50;
        } else if (s1 == 3 && s2 == 2 && s3 == 3) {
            return 3.60;
        } else if (s1 == 0 && s2 == 2 && s3 == 0) {
            return 2.20;
        } else if (s1 == 4 && s2 == 2 && s3 == 4) {
            return 1.80;
        } else if (s1 == 1 && s2 == 2 && s3 == 1) {
            return 1.50;
        } else {
            return 0.0; //No win
        }
    }

    //assign numbers to the images
    private String getSymbolName(int i) {
        return switch (i) {
            case 0 -> "banan";
            case 1 -> "chery";
            case 2 -> "daimond";
            case 3 -> "dinye";
            case 4 -> "korte";
            case 5 -> "septar";
            default -> "banan";
        };
    }

    //update panels
    public void updateBalanceAndWin(int lastWin) {
        this.lastWin = lastWin;
        balance += lastWin;

        winLabel.setText("Last Win: " + lastWin + " $");
        balanceLabel.setText("Balance: " + balance + " $");
    }
}
