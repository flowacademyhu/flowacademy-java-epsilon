package hu.flowacademy.epsilon._02_serialization;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.*;

public class SerializeSwing {
    public static void main(String[] args) {
        var frame = new JFrame("Swing Serialize demo");
        frame.setSize(400, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.add(new TextArea("Some text here", 4, 40));
        var button = new JButton("Save");
        button.addActionListener(e -> {
            var fc = new JFileChooser();
            if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try(var out = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile()))) {
                    out.writeObject(frame.getContentPane());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        frame.add(button);
        frame.setVisible(true);
    }
}
