package org.interactunes.screensaver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainWindow {

    private final JFrame window;

    public MainWindow() {
        window = new JFrame("InteracTunes Screen Saver");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JButton button = createButton();
        panel.add(button);
        window.add(panel, BorderLayout.CENTER);

        /*
        JPanel panel = new JPanel(new GridLayout(4, 5, 10, 10));
        for (int i = 0; i < 20; i++) {
            JButton button = new JButton("Button " + i);
            panel.add(button);
        }
        window.add(panel, BorderLayout.CENTER);
        */

        /*
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(Color.PINK);
        for (int i = 0; i < 5; i++) {
            JButton button = new JButton("Button " + i);
            panel.add(button);
        }
        window.add(panel, BorderLayout.NORTH);
        */

        /*
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(10);
        borderLayout.setVgap(10);
        window.setLayout(borderLayout);
        window.setLocationRelativeTo(null);

        window.add(new JButton("NORTH"), BorderLayout.NORTH);
        window.add(new JButton("SOUTH"), BorderLayout.SOUTH);
        window.add(new JButton("EAST"), BorderLayout.EAST);
        window.add(new JButton("WEST"), BorderLayout.WEST);
        */

        /*
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(Color.RED);

        Button button1 = new Button("Click me!");
        button1.addActionListener(e -> System.out.println("Button clicked!"));
        panel.add(button1);

        Button button2 = new Button("Click me too!");
        button2.addActionListener(e -> System.out.println("Button 2 clicked!"));
        panel.add(button2);

        panel.setPreferredSize(new Dimension(250, 250));

        window.add(panel, BorderLayout.WEST);
        */
    }

    public void show() {
        // window.pack();
        window.setVisible(true);
    }

    private JButton createButton() {
        JButton button = new JButton("Click me!");
        ImageIcon printIcon = new ImageIcon("src/main/resources/print.png");
        button.setIcon(printIcon);
        button.setIconTextGap(10);
        button.setMnemonic(KeyEvent.VK_C);
        button.setToolTipText("Click this button to print something!");
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setMargin(new Insets(10, 20, 10, 20));
        button.addActionListener(e -> System.out.println("Button clicked!"));
        // button.setEnabled(false);
        // button.setVerticalTextPosition(SwingConstants.BOTTOM);
        // button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(200, 100));
        return button;
    }
}
