package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.StringWriter;
import java.util.Scanner;

public class MyConsole {
    private JTextArea jTextArea;
    public static JTextField jTextField;
    private JDialog jDialog;
    private Scanner scanner;

    public MyConsole() {
        jDialog = new JDialog();
        jTextArea = new JTextArea();
        jTextArea.setBackground(Color.BLACK);
        jTextArea.setForeground(Color.GREEN);

        jTextField = new JTextField(">");
        jTextField.setBackground(Color.BLACK);
        jTextField.setForeground(Color.WHITE);


        jTextArea.setEditable(false);
        jTextField.requestFocusInWindow();
        jDialog.add(jTextArea);
        jDialog.add(jTextField, BorderLayout.SOUTH);

        jTextField.addActionListener(e -> {
            scanner = new Scanner(jTextField.getText());
            String text = "";
            while(scanner.hasNext()){
                text = scanner.nextLine();
                inputText(text);
                jTextField.setText(">");
            }
        });
        jDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        jDialog.setSize(600,400);
        jDialog.setLocationRelativeTo(null);
    }

    public void inputText(String text){
        if(text.equals(">Показывать время симуляции")){
            Habitat.timeLabel = true;
            Habitat.label.setVisible(true);
            Habitat.showTime.setSelected(true);
        }else if(text.equals(">Скрывать время симуляции")){
            Habitat.timeLabel = false;
            Habitat.label.setVisible(false);
            Habitat.hideTime.setSelected(true);
        }else{
//            jTextArea.append("Такой комманды не существует.\nСуществует 2 команды:\n" +
//                    "Показывать время симуляции\nСкрывать время симуляции\n");
            StringWriter stringWriter = new StringWriter();
            stringWriter.write("Такой комманды не существует.\nСуществует 2 команды:\nПоказывать время симуляции\nСкрывать время симуляции\n");
            jTextArea.append(stringWriter.toString());

        }
    }
    public void showConsole(){
        jDialog.setVisible(true);
    }
}

