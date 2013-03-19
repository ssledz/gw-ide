/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *
 * @author ssledz
 */
public class JPasswordOptionPane {

    public static String showInputDialog(Component parentComponent, String message, String title, int optionType) {

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message);
        JPasswordField pass = new JPasswordField(10);
        panel.add(label, BorderLayout.NORTH);
        panel.add(pass, BorderLayout.SOUTH);

        String[] options = new String[]{"OK", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, panel, title,
                JOptionPane.NO_OPTION, optionType,
                null, options, options[1]);
        if (option == 0) {
            return new String(pass.getPassword());
        }

        return null;


    }
}
