/*
 * Copyright 2013 Sławomir Śledź <slawomir.sledz@sof-tech.pl>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author Sławomir Śledź <slawomir.sledz@sof-tech.pl>
 * @since 1.0
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
