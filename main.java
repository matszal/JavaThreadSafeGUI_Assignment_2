package com.company;
import javax.swing.*;


public class main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                GUI gui = new GUI();
                gui.createAndShowGUI();
            }
        });
    }

}
