/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.gui;

import javax.swing.JFrame;

/**
 *
 * @author ssledz
 */
public class SimpleRunner {
    
    public static void main(String[] args) {
        
        
        JFrame frame = new JFrame("Simple Runner");
        SimpleRunnerController src = new SimpleRunnerController(new SimpleRunnerView(), frame);
        frame.setContentPane(src.getView());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
    
}
