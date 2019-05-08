package hu.flowacademy.epsilon._02_serialization;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.*;

public class DeserializeSwing {
    public static void main(String[] args) {
        Enum x;
        var frame = new JFrame("Swing Deserialize demo");
        frame.setSize(400, 400);
        var button = new JButton("Load");
        button.setSize(100, 50);
        button.addActionListener(e -> {
            var fc = new JFileChooser();
            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try(var in = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) {
                    var contentPane = (Container)in.readObject();
                    frame.setContentPane(contentPane);
                    frame.repaint();
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        frame.add(button);
        frame.setVisible(true);
    }
}
