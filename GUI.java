package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

public class GUI extends Component implements ActionListener
{
    SwingWorker<Void, String> worker;
    SwingWorker<Void, String> zipWorker;

    Logger LOGGER = Logger.getLogger(GUI.class .getName());

    //Global variables
    StringBuilder sb, sb2;
    BufferedReader br;
    String  line = "", fullPath = "", outputPath = "/home/mateusz/Desktop/", outputName="Compressed.zip", end=".zip", fileName="", test="";
    File file;
    JScrollPane area;
    JPanel txtPanel, totalGUI;
    JButton choose, clear, zipFile;
    JTextArea storyArea, t;
    JFileChooser fc;
    long startTime;



    public static void createAndShowGUI()
    {

        JFrame frame = new JFrame("Assignment 1");

        //Create and set up the content pane
        GUI pane = new GUI();
        frame.setContentPane(pane.createContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(560, 400);
        frame.setVisible(true);
    }

    public JPanel createContentPane()
    {
        fc = new JFileChooser();
        totalGUI = new JPanel();

        storyArea = new JTextArea();
        storyArea.setEditable(false);
        storyArea.setLineWrap(true);
        storyArea.setWrapStyleWord(true);

        area = new JScrollPane(storyArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        area.setPreferredSize(new Dimension(500,350));


        totalGUI.add(area);

        txtPanel = new JPanel();
        //txtPanel.setBackground(Color.BLACK);
        txtPanel.setLayout(null);
        txtPanel.setLocation(385, 70);
        txtPanel.setSize(120, 20);
        totalGUI.add(txtPanel);

        t = new JTextArea();
        //t.setLocation(425, 100);
        //t.setSize(100, 20);
        t.setEditable(false);
        //t.setLineWrap(true);
        //t.setWrapStyleWord(true);
        //t.setHorizontalAlignment(0);
        totalGUI.add(t);

        //BUTTONS

        choose = new JButton("Choose File");
        //choose.setLocation(20, 10);
        choose.setSize(160, 50);
        choose.addActionListener(this);
        totalGUI.add(choose);

        zipFile = new JButton("ZIP file");
        zipFile.setSize(160, 50);
        zipFile.addActionListener(this);
        totalGUI.add(zipFile);

        clear = new JButton("clear");
        //clear.setLocation(380, 10);
        clear.setSize(160, 50);
        clear.addActionListener(this);
        totalGUI.add(clear);

        totalGUI.setOpaque(true);
        return totalGUI;
    }

    //@Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == choose)
        {
            LOGGER.info("Button pressed select file!");
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                startWorker();
            }
        }
        else if (e.getSource() == clear)
        {
            LOGGER.info("ALL fields cleared");
            t.setText("");
            storyArea.setText("");

        }
        else if (e.getSource() == zipFile)
        {
            startZipWorker();
        }
    }
    private void startWorker()
    {
        worker = new SwingWorker<Void, String>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                startTime = System.currentTimeMillis();
                file = fc.getSelectedFile();

                fullPath = file.getCanonicalPath();
                fileName = file.getName();

                try
                {
                    LOGGER.setLevel(Level.INFO);
                    LOGGER.info("Reading File");

                    sb = new StringBuilder();
                    br = new BufferedReader(new FileReader(file));
                    line = br.readLine();
                    while(line != null)
                    {

                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();

                    }
                    publish(sb.toString());

                    br.close();
                    //float timeTaken = millis/10;

                    LOGGER.info("Finished Reading displayed! ");

                }
                catch (Exception ee)
                {
                    ee.printStackTrace();
                }
                return null;
            }
            @Override
            protected void process(List<String> list)
            {
                for(String end : list)
                {
                    storyArea.append(end+"\n");
                }
                t.setText(""+(System.currentTimeMillis()-startTime)/1000.0);
            }
        };
        worker.execute();
    }
    private void startZipWorker()
    {
        zipWorker = new SwingWorker<Void, String>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                byte[] buffer = new byte[1024];
                float fsize =file.length();
                fsize = fsize/(1024*1024);
                String fileSizeFormattedString = String.format("%.2f", fsize);
                LOGGER.info("Compressing selected file");
                try
                {
                    FileOutputStream fos = new FileOutputStream(outputPath+outputName);
                    System.out.println("Output to:  " +outputPath+outputName);
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    ZipEntry ze = new ZipEntry(fileName);
                    String entryName = ze.getName();
                    zos.putNextEntry(ze);
                    FileInputStream in = new FileInputStream(fullPath);

                    int len;
                    while ((len = in.read(buffer))>0)
                    {
                        zos.write(buffer, 0 , len);
                    }

                    in.close();
                    zos.closeEntry();
                    zos.close();

                    float compressedSize = ze.getCompressedSize();
                    compressedSize = compressedSize/(1024*1024);
                    String compressedSizeFormattedString = String.format("%.2f", compressedSize);
                    String percentageratioFormattedString = String.format("%.2f", ((fsize-compressedSize)/fsize)*100);

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss z");
                    Date date = new Date();

                    sb2 = new StringBuilder();
                    sb2.append("Original size in Mb is: ");
                    sb2.append(fileSizeFormattedString);
                    sb2.append(System.lineSeparator());
                    sb2.append("Compressed size in Mb is: ");
                    sb2.append(compressedSizeFormattedString);
                    sb2.append(System.lineSeparator());
                    sb2.append("Percentage ratio is: ");
                    sb2.append(percentageratioFormattedString+"%");
                    sb2.append(System.lineSeparator());
                    sb2.append("Date created ");
                    sb2.append(dateFormat.format(date));
                    publish(sb2.toString());

                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                }
                return null;
            }
            @Override
            protected void process(List<String> list)
            {
                for (String end: list)
                {
                    storyArea.append(end+"\n");
                }
                LOGGER.info("File compressing done!");
            }
        };
        zipWorker.execute();
    }
}

