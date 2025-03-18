import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SlotMachineStart extends JFrame {
    private JTextField inputField;
    private JButton submitButton;
    private JButton startButton;
    private Image backgroundImage;
    private Image[] numberImages;  //images

    public SlotMachineStart() {
        setTitle("Lucky Slot Machine");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  //kozep

        //background set
        try {
            backgroundImage = ImageIO.read(new File("image/SlotMachineStart.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //number images load (0-9)
        numberImages = new Image[10];
        for (int i = 0; i < 10; i++) {
            try {
                numberImages[i] = ImageIO.read(new File("image/" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //bg + numbers paint
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

                String input = inputField.getText();
                if (!input.isEmpty()) {
                    try {
                        int number = Integer.parseInt(input);
                        if (number >= 0 && number <= 9999) {
                            //numbers put in to stack v mi az
                            char[] digits = input.toCharArray();

                            //start koordinatak
                            int totalDigits = 4; //max 4 numbers
                            int digitWidth = 50;
                            int spacing = 70; //space between nr
                            int startX = 600 - (totalDigits * digitWidth + (totalDigits - 1) * spacing); //jobbra igazit
                            int y = 210; //Y koord

                            int position = digits.length - 1; //utolso szamjegy poz
                            for (int i = totalDigits - 1; i >= 0; i--) {
                                if (position >= 0) {
                                    int digit = Character.getNumericValue(digits[position]);
                                    if (digit >= 0 && digit <= 9 && numberImages[digit] != null) {
                                        g.drawImage(numberImages[digit], startX + i * (digitWidth + spacing), y, digitWidth, digitWidth, null);
                                    }
                                    position--;
                                }
                            }

                            //for (int i = 0; i < digits.length; i++) {
                            //    int digit = Character.getNumericValue(digits[i]);
                            //    if (digit >= 0 && digit <= 9 && numberImages[digit] != null) {
                            //        g.drawImage(numberImages[digit], startX + i * 123, y, 50, 50, null);
                            //    }
                            //}
                        }
                    } catch (NumberFormatException e) {
                        //nem tortenik semmi ha nem megfelelo bemenet
                    }
                }
            }
        };

        mainPanel.setLayout(null);

        //szovegmezo + gombok
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.red);
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setPreferredSize(new Dimension(800, 40));

        JLabel label = new JLabel("Enter balance (0 to 9999):");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        inputPanel.add(label);

        inputField = new JTextField(10);
        inputField.setBackground(Color.yellow);
        inputField.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPanel.add(inputField);

        submitButton = new JButton("Submit");
        submitButton.setBackground(Color.YELLOW);
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                try {
                    int balance = Integer.parseInt(input); //change into number
                    if (balance >= 0 && balance <= 9999) {
                        mainPanel.repaint();

                        //if there is a sum then we can go ahead with the game
                        startButton.setEnabled(true); //aprove to the "Let's go Gambling!" button
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a valid amount between 0 and 9999.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter a number.");
                }
            }
        });
        inputPanel.add(submitButton);

        startButton = new JButton("Let's go Gambling!");
        startButton.setBackground(Color.yellow);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                try {
                    int balance = Integer.parseInt(input);
                    if (balance >= 0 && balance <= 9999) {
                        new MainSlotMachine(balance); //balance share to next window
                        dispose(); //dispose the current window
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a valid amount between 0 and 9999.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter a number.");
                }
            }
        });
        inputPanel.add(startButton);

        //elrendezes
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
}
