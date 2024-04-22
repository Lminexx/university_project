package org.example;

import org.example.Template.*;
import org.example.Base.*;
import org.example.Students.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
public class Habitat{
    private int width, height;
    private int update_male = 1000, update_female= 1000,UPDATE_INTERVAL = 1000;
    public int time;
    private Timer timer, timerMale, timerFemale, timerThreads;
    private JFrame frame;
    private JPanel panel;
    public static JLabel label, countStudentsText, countFemaleStudents;
    public static boolean timeLabel = true, simulationRunning = false, flagForInfo = false;
    private final ArraySing students;
    private Random random;
    private JButton buttonStart, buttonStop, buttonShowLife, buttonStopMoveMale, buttonStartMoveMale, buttonStartMoveFemale, buttonStopMoveFemale;
    public static JRadioButton showTime, hideTime;
    private double maleProbability = 0.6, femaleProbability = 0.4;
    private JCheckBox checkInfo;
    private int timeToDieFirst = 7, timeToDieSecond = 10;
    public MaleBaseAI maleBaseAI;
    public FemaleBaseAI femaleBaseAI;
    private MyConsole console;
    private JTextField inputTextFirst, inputTextSecond;
    private int malePriority= 1 , femalePriority =1;
    private JTextField inputTimeDieForFirst, inputTimeDieForSecond;
    private JComboBox<Integer> firstVib, secondVib, priorityMale, priorityFemale;
    private DateBase dateBase;

    public static void main(String[] args) {
        Habitat habitat = new Habitat(1000,700);
        habitat.loadConfiguration();
        Timer timer1 = new Timer(1000, e -> {
            habitat.saveConfiguration();
        });
        timer1.start();
    }

    public Habitat(int width, int height) {
        this.width = width;
        this.height = height;
        this.random = new Random();
        this.students = ArraySing.getInstance();
        this.console = new MyConsole();
        this.dateBase = new DateBase();
        initialize();

    }
    private void initialize() {
        initializeFrame();
        initializePanel();
        simulationMenu();
        controlPanel();
        keyBoardClick();
        startThreads();
        frame.setVisible(true);
    }

    private void initializeFrame(){
        frame = new JFrame();
        frame.setTitle("laboratornaya");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setLayout(new BorderLayout());
    }

    private void initializePanel(){
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < students.getLinkedList().size(); i++) {
                    if(((double) time /1000  - students.getLinkedList().get(i).timeToBorn) >= students.getLinkedList().get(i).timeToDie){
                        students.clearObject(students.getLinkedList().get(i));
                        i--;
                    }else{
                        students.getLinkedList().get(i).draw(g);
                    }
                }
            }
        };
        label = new JLabel();
        label.setSize(100,100);
        label.setForeground(Color.green);
        countStudentsText = new JLabel();
        countStudentsText.setSize(150,150);
        countStudentsText.setForeground(Color.ORANGE);
        countFemaleStudents = new JLabel();
        countFemaleStudents.setSize(150,150);
        countFemaleStudents.setForeground(Color.red);
        panel.add(label);
        panel.add(countStudentsText);
        panel.add(countFemaleStudents);
        frame.add(panel, BorderLayout.CENTER);
        panel.requestFocusInWindow();
    }

    private void loadStudents(){
        JFileChooser jFileChooser = new JFileChooser();
        int value = jFileChooser.showOpenDialog(frame);
        stopSimulation();
        startSimulation();
        if (value == JFileChooser.APPROVE_OPTION) {
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(jFileChooser.getSelectedFile().getName()))){
                LinkedList<Student> linkedList;
                linkedList = (LinkedList<Student>) ois.readObject();
                students.setLinkedList(linkedList);
                for (Student student: students){
                    student.setTimeToBorn((double) time /1000);
                }
            } catch (ClassNotFoundException | IOException e) {
                System.out.println("error");
            }
        }
    }

    private void saveStudents(){
        JFileChooser jFileChooser = new JFileChooser();
        int value = jFileChooser.showSaveDialog(frame);
        if(value == JFileChooser.APPROVE_OPTION){
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(jFileChooser.getSelectedFile().getName()))) {
                oos.writeObject(students.getLinkedList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadConfiguration(){
        try(Scanner scanner = new Scanner(new File("C:\\Users\\sasha\\IdeaProjects\\lab67\\src\\main\\java\\org\\example\\filesTxt\\configuration.txt"))){
            while(scanner.hasNext()){
                setMaleProbability(Double.parseDouble(scanner.nextLine()));
                setInputTextFirst(scanner.nextLine());
                setFemaleProbability(Double.parseDouble(scanner.nextLine()));
                setInputTextSecond(scanner.nextLine());
                setTimeToDieFirst(Integer.parseInt(scanner.nextLine()));
                setTimeToDieSecond(Integer.parseInt(scanner.nextLine()));
                setMalePriority(Integer.parseInt(scanner.nextLine()));
                setFemalePriority(Integer.parseInt(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Метод loadConfiguration");
        }
    }

    private void saveConfiguration(){
        try(FileWriter fileWriter = new FileWriter(new File("C:\\Users\\sasha\\IdeaProjects\\lab67\\src\\main\\java\\org\\example\\filesTxt\\configuration.txt"))) {
            fileWriter.write(getMaleProbability() + "\n");
            fileWriter.write(getInputTextFirst()+ "\n");
            fileWriter.write(getFemaleProbability() + "\n");
            fileWriter.write(getInputTextSecond()+"\n");
            fileWriter.write(getTimeToDieFirst()+"\n");
            fileWriter.write(getTimeToDieSecond()+"\n");
            fileWriter.write(getMalePriority()+"\n");
            fileWriter.write(getFemalePriority()+"\n");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Метод saveConfiguration");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startThreads(){
        maleBaseAI = new MaleBaseAI();
        maleBaseAI.start();
        femaleBaseAI = new FemaleBaseAI();
        femaleBaseAI.start();

    }
    private void startButtonMethod(){
        startSimulation();
        buttonStart.setBackground(Color.GRAY);
        buttonStop.setBackground(Color.BLACK);
        simulationRunning = !simulationRunning;
    }
    private void stopButtonMethod(){
        stopSimulation();
        buttonStart.setBackground(Color.BLACK);
        buttonStop.setBackground(Color.GRAY);
        simulationRunning = !simulationRunning;
    }
    public void simulationMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        //меню бар старт.стоп
        JMenu simulation = new JMenu("File");
        JMenuItem start = new JMenuItem("Start");
        JMenuItem stop = new JMenuItem("Stop");
        JMenuItem show_info = new JMenuItem("Show info");
        JMenuItem exit = new JMenuItem("Exit");

        simulation.add(start);
        simulation.add(stop);
        simulation.add(show_info);
        simulation.add(exit);
        jMenuBar.add(simulation);
        //Меню бар для таймера
        JMenu timeMenu = new JMenu("Time");
        JMenuItem show_time = new JMenuItem("Show Time");
        JMenuItem hide_item = new JMenuItem("Hide Item");
        timeMenu.add(show_time);
        timeMenu.add(hide_item);
        jMenuBar.add(timeMenu);
        //Меню бар для сериализации
        JMenu serialMenu = new JMenu("Serialize");
        JMenuItem show_console = new JMenuItem("Console");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem save = new JMenuItem("Save");
        serialMenu.add(show_console);
        serialMenu.add(load);
        serialMenu.add(save);
        jMenuBar.add(serialMenu);
        //Меню бар для баз данных
        JMenu baseMenu = new JMenu("DateBase");
        JMenuItem save_base = new JMenuItem("Save base");
        JMenuItem load_base = new JMenuItem("Load base");
        JMenuItem clear_base = new JMenuItem("clear base");
        baseMenu.add(save_base);
        baseMenu.add(load_base);
        baseMenu.add(clear_base);
        jMenuBar.add(baseMenu);
        // Работа кнопки старт.
        start.addActionListener(e -> {
            if (!simulationRunning) {
                startButtonMethod();
            }
        });
        stop.addActionListener(e -> {
            if (simulationRunning) {
                stopButtonMethod();
            }
        });
        show_info.addActionListener(e -> {
            flagForInfo = true;
            checkInfo.setSelected(true);
        });
        exit.addActionListener(e -> System.exit(0));
        show_time.addActionListener(e -> {
            timeLabel = true;
            label.setVisible(true);
            showTime.setSelected(true);
        });
        hide_item.addActionListener(e -> {
            timeLabel = false;
            label.setVisible(false);
            hideTime.setSelected(true);
        });
        show_console.addActionListener(e->{
            console.showConsole();
        });
        save.addActionListener(e -> {
            saveStudents();
        });
        load.addActionListener(e -> {
            loadStudents();
        });
        save_base.addActionListener(e->{
            for (Student student : students.getLinkedList()){
                dateBase.insertTable(student);
            }
        });
        load_base.addActionListener(e -> {
            ResultSet result = dateBase.selectTable();
            try{
                while(result.next()){
                    int columns = result.getMetaData().getColumnCount();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 1; i <= columns; i++) {
                        stringBuilder.append(result.getString(i) + " ");
                    }
                    if(stringBuilder.toString().contains("F")){
                        String[] strings = stringBuilder.toString().split(" ");
                        students.addObj(new FemaleStudent(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), 40));
                    }
                    if(stringBuilder.toString().contains("M")){
                        String[] strings = stringBuilder.toString().split(" ");
                        students.addObj(new MaleStudent(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3])));
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        clear_base.addActionListener(e->{
            dateBase.clearTable();
        });


        simulation.setFocusable(false);
        timeMenu.setFocusable(false);
        serialMenu.setFocusable(false);
        baseMenu.setFocusable(false);
        frame.setJMenuBar(jMenuBar);
    }

    public void controlPanel(){
        JPanel controlpanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0,0, getWidth(), getHeight());
            }
        };
        controlpanel.setPreferredSize(new Dimension(300,frame.getHeight()));
        controlpanel.setLayout(new FlowLayout());
        //Создание объектов для контрольной панельки.
        buttonStart = new JButton("Start");
        buttonStop = new JButton("Stop");
        checkInfo = new JCheckBox("Show info");
        showTime = new JRadioButton("Showing time");
        hideTime = new JRadioButton("Hiding time");
        ButtonGroup groupRadio = new ButtonGroup();
        JLabel textForFirst = new JLabel("Шанс появления первого:");
        firstVib = new JComboBox<>();
        JLabel periodForFirst = new JLabel("Периодичность первого(сек):");
        inputTextFirst = new JFormattedTextField("1");
        JLabel textForSecond = new JLabel("Шанс появления второго:");
        secondVib = new JComboBox<>();
        JLabel periodForSecond = new JLabel("Периодичность второго(сек):");
        inputTextSecond = new JFormattedTextField("1");
        JLabel timeToDieTextForFirst = new JLabel("Время жизни для первого:");
        inputTimeDieForFirst = new JFormattedTextField("7");
        JLabel timeToDieTextForSecond = new JLabel("Время жизни для второго:");
        inputTimeDieForSecond = new JFormattedTextField("10");
        buttonShowLife = new JButton("Show life's users");
        buttonStartMoveMale = new JButton("Start male");
        buttonStopMoveMale = new JButton("Stop male");
        buttonStartMoveFemale = new JButton("Start female");
        buttonStopMoveFemale = new JButton("Stop female");
        JLabel priorityMaleText = new JLabel("Приоритет студента");
        priorityMale = new JComboBox<>();
        JLabel priorityFemaleText = new JLabel("Приоритет студентки");
        priorityFemale = new JComboBox<>();
        JLabel textB = new JLabel("B - start simulation");
        JLabel textE = new JLabel("E - stop simulation");
        JLabel textT = new JLabel("T - time on/off");

        //В выборку добавляю значения шанса
        for (int i = 10; i <= 100; i+=10) {
            firstVib.addItem(i);
            secondVib.addItem(i);
        }
        for (int i = 1; i <= 10; i++) {
            priorityMale.addItem(i);
            priorityFemale.addItem(i);
        }
        firstVib.setSelectedIndex(5);
        secondVib.setSelectedIndex(3);
        groupRadio.add(showTime);
        groupRadio.add(hideTime);
        showTime.setSelected(true);

        //Установил пустой цвет клика. Так симпатичнее имхо.
        buttonStart.setUI(new CustomButtonUI());
        buttonStop.setUI(new CustomButtonUI());
        buttonShowLife.setUI(new CustomButtonUI());
        //Нажатие кнопок, что делают и т.д.
        buttonStart.addActionListener(e -> {
            if(!simulationRunning){
                startSimulation();
                buttonStart.setBackground(Color.GRAY);
                buttonStop.setBackground(Color.BLACK);
                simulationRunning = !simulationRunning;
            }
        });

        buttonStop.addActionListener(e -> {
            if(simulationRunning){
                stopSimulation();
                buttonStart.setBackground(Color.BLACK);
                buttonStop.setBackground(Color.GRAY);
                simulationRunning = !simulationRunning;
            }
        });

        checkInfo.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                flagForInfo = true;
            }else{
                flagForInfo = false;
            }
        });

        showTime.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                timeLabel = true;
                label.setVisible(true);
            }
        });

        hideTime.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                timeLabel = false;
                label.setVisible(false);
            }
        });

        firstVib.addActionListener(e -> {
            int selected = (int) firstVib.getSelectedItem();
            maleProbability = selected/100.0;
        });
        secondVib.addActionListener(e -> {
            int selected = (int) secondVib.getSelectedItem();
            femaleProbability = selected/100.0;
        });

        priorityMale.addActionListener(e -> {
            int selected = (int) priorityMale.getSelectedItem();
            setPriorityMale(selected);
        });
        priorityFemale.addActionListener(e -> {
            int selected = (int) priorityFemale.getSelectedItem();
            setPriorityFemale(selected);
        });


        inputTextFirst.addActionListener(e -> {
            try {
                int newValue = Integer.parseInt(inputTextFirst.getText());
                if (newValue >= 0) {
                    timerMale.setDelay(newValue * 1000);
                    panel.requestFocusInWindow();
                    panel.setFocusable(true);
                    frame.setFocusable(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "Введите положительное значение", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите допустимое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputTextSecond.addActionListener(e -> {
            try {
                int newValue = Integer.parseInt(inputTextSecond.getText());
                if (newValue >= 0) {
                    timerFemale.setDelay(newValue * 1000);
                    panel.requestFocusInWindow();
                    panel.setFocusable(true);
                    frame.setFocusable(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "Введите положительное значение", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите допустимое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputTimeDieForFirst.addActionListener(e -> {
            try {
                int newValue = Integer.parseInt(inputTimeDieForFirst.getText());
                if (newValue >= 0) {
                    timeToDieFirst = newValue;
                    panel.requestFocusInWindow();
                    panel.setFocusable(true);
                    frame.setFocusable(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "Введите положительное значение", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите допустимое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        inputTimeDieForSecond.addActionListener(e -> {
            try {
                int newValue = Integer.parseInt(inputTimeDieForSecond.getText());
                if (newValue >= 0) {
                    timeToDieSecond = newValue;
                    panel.requestFocusInWindow();
                    panel.setFocusable(true);
                    frame.setFocusable(true);
                } else {
                    JOptionPane.showMessageDialog(frame, "Введите положительное значение", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Введите допустимое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonShowLife.addActionListener(e -> {
            StringBuilder sb  = new StringBuilder();
            for (Student student : students.getLinkedList()){
                sb.append(student.timeToBorn + " sec " + " " + student + "\n");
            }
            String message = sb.toString();
            JOptionPane.showOptionDialog(frame, message, "Живые студенты:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, new String[]{"Ок"}, "Ок");

        });
        buttonStopMoveMale.addActionListener(e -> stopMaleBase());
        buttonStartMoveMale.addActionListener(e -> enableMaleBase());
        buttonStopMoveFemale.addActionListener(e -> stopFemaleBase());
        buttonStartMoveFemale.addActionListener(e -> enableFemaleBase());
        //Убрал фокус с кнопок, а то не работали нажатия с клавы из-за этого.
        buttonStart.setFocusable(false);
        buttonStop.setFocusable(false);
        checkInfo.setFocusable(false);
        showTime.setFocusable(false);
        hideTime.setFocusable(false);
        firstVib.setFocusable(false);
        secondVib.setFocusable(false);
        buttonShowLife.setFocusable(false);
        buttonStartMoveMale.setFocusable(false);
        buttonStopMoveMale.setFocusable(false);
        buttonStartMoveFemale.setFocusable(false);
        buttonStopMoveFemale.setFocusable(false);
        priorityMale.setFocusable(false);
        priorityFemale.setFocusable(false);
        //Установка размеров
        buttonStart.setPreferredSize(new Dimension(300,50));
        buttonStop.setPreferredSize(new Dimension(300,50));
        checkInfo.setPreferredSize(new Dimension(90,20));
        showTime.setPreferredSize(new Dimension(90,20));
        hideTime.setPreferredSize(new Dimension(90,20));
        firstVib.setPreferredSize(new Dimension(50,30));
        inputTextFirst.setPreferredSize(new Dimension(290,40));
        secondVib.setPreferredSize(new Dimension(50,30));
        inputTextSecond.setPreferredSize(new Dimension(290,40));
        inputTimeDieForFirst.setPreferredSize(new Dimension(290,40));
        inputTimeDieForSecond.setPreferredSize(new Dimension(290,40));
        buttonShowLife.setPreferredSize(new Dimension(200,50));
        buttonStartMoveMale.setPreferredSize(new Dimension(120,30));
        buttonStopMoveMale.setPreferredSize(new Dimension(120,30));
        buttonStartMoveFemale.setPreferredSize(new Dimension(120,30));
        buttonStopMoveFemale.setPreferredSize(new Dimension(120,30));
        priorityMale.setPreferredSize(new Dimension(100,30));
        priorityFemale.setPreferredSize(new Dimension(100,30));
        //Установил задний фон и цвет текста. Получилось красиво :).
        buttonStart.setBackground(Color.BLACK);
        buttonStop.setBackground(Color.BLACK);
        buttonShowLife.setBackground(Color.BLACK);
        buttonStart.setForeground(Color.CYAN);
        buttonStop.setForeground(Color.CYAN);
        buttonShowLife.setForeground(Color.CYAN);
        buttonStartMoveMale.setBackground(Color.BLACK);
        buttonStartMoveMale.setForeground(Color.CYAN);
        buttonStopMoveMale.setBackground(Color.BLACK);
        buttonStopMoveMale.setForeground(Color.CYAN);
        buttonStartMoveFemale.setBackground(Color.BLACK);
        buttonStartMoveFemale.setForeground(Color.CYAN);
        buttonStopMoveFemale.setBackground(Color.BLACK);
        buttonStopMoveFemale.setForeground(Color.CYAN);

        textForFirst.setFont(new Font("Arial", Font.PLAIN, 17));
        periodForFirst.setFont(new Font("Arial",Font.PLAIN,15));
        inputTextFirst.setFont(new Font("Arial", Font.BOLD,15));
        textForSecond.setFont(new Font("Arial",Font.PLAIN,17));
        periodForSecond.setFont(new Font("Arial", Font.PLAIN,15));
        inputTextSecond.setFont(new Font("Arial", Font.BOLD,15));
        textB.setFont(new Font("Arial",Font.BOLD, 14));
        textE.setFont(new Font("Arial",Font.BOLD, 14));
        textT.setFont(new Font("Arial",Font.BOLD, 14));
        timeToDieTextForFirst.setFont(new Font("Arial",Font.BOLD,15));
        timeToDieTextForSecond.setFont(new Font("Arial",Font.BOLD, 15));
        priorityMaleText.setFont(new Font("Arial",Font.PLAIN,15));
        priorityFemaleText.setFont(new Font("Arial",Font.PLAIN,15));


        controlpanel.add(buttonStart);
        controlpanel.add(buttonStop);
        controlpanel.add(checkInfo);
        controlpanel.add(showTime);
        controlpanel.add(hideTime);
        controlpanel.add(textForFirst);
        controlpanel.add(firstVib);
        controlpanel.add(periodForFirst);
        controlpanel.add(inputTextFirst);
        controlpanel.add(textForSecond);
        controlpanel.add(secondVib);
        controlpanel.add(periodForSecond);
        controlpanel.add(inputTextSecond);
        controlpanel.add(timeToDieTextForFirst);
        controlpanel.add(inputTimeDieForFirst);
        controlpanel.add(timeToDieTextForSecond);
        controlpanel.add(inputTimeDieForSecond);
        controlpanel.add(buttonShowLife);
        controlpanel.add(buttonStartMoveMale);
        controlpanel.add(buttonStopMoveMale);
        controlpanel.add(buttonStartMoveFemale);
        controlpanel.add(buttonStopMoveFemale);
        controlpanel.add(priorityMaleText);
        controlpanel.add(priorityMale);
        controlpanel.add(priorityFemaleText);
        controlpanel.add(priorityFemale);
        controlpanel.add(textB);
        controlpanel.add(textE);
        controlpanel.add(textT);
        frame.add(controlpanel, BorderLayout.EAST);
    }

    public void startSimulation() {
        students.clear();
        startTimers();
        countStudentsText.setVisible(false);
        countFemaleStudents.setVisible(false);
    }
    public void startTimers(){
        timer = new Timer(1000, e -> {
            time+=UPDATE_INTERVAL;
            label.setText(String.format("Time %d seconds",time/1000));

        });
        timerMale = new Timer(update_male, e -> {
            updateMale();
            panel.repaint();
        });
        timerFemale = new Timer(update_female, e -> {
            updateFemale();
            panel.repaint();
        });
        timerThreads = new Timer(16, e -> {
            panel.repaint();
        });
        timer.start();
        timerMale.start();
        timerFemale.start();
        timerThreads.start();
    }

    public void stopSimulation() {
        int cntMale = 0;
        int cntFemale = 0;
        for (Student student : students.getLinkedList()){
            if(student instanceof MaleStudent){
                cntMale++;
            }
            if(student instanceof FemaleStudent){
                cntFemale++;
            }
        }
        frame.repaint();
        timer.stop();
        timerMale.stop();
        timerFemale.stop();
        timerThreads.stop();
        countStudentsText.setText("Количество студентов = " + cntMale);
        countFemaleStudents.setText("Количество студенток = " + cntFemale);

        countStudentsText.setVisible(true);
        countFemaleStudents.setVisible(true);
        if(flagForInfo){
            String message = "Количество студентов = " + cntMale + "\n" +
                    "Количество студенток = " + cntFemale + "\n" +
                    "Время выполнения: " + time/1000 + " секунд";
            JOptionPane.showOptionDialog(frame, message, "Хотите завершить работу?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, new String[]{"Ок", "Отмена"}, "Ок");

        }
        time = 0;
    }

    private void updateMale(){
        if (Math.random() < maleProbability) {
            students.addObj(new MaleStudent(random.nextInt(width), random.nextInt(height), time/1000, timeToDieFirst));
        }
    }
    private void updateFemale(){
        if (Math.random() < femaleProbability) {
            students.addObj(new FemaleStudent(random.nextInt(width), random.nextInt(height), time/1000, timeToDieSecond, 40));
        }
    }

    private void keyBoardClick() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            // Проверяем, находится ли фокус в текстовом поле
            if (!MyConsole.jTextField.isFocusOwner()) {
                // Обработка нажатий только если фокус не в текстовом поле
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (!simulationRunning) {
                        if (e.getKeyCode() == KeyEvent.VK_B) {
                            startButtonMethod();
                        }
                    } else {
                        if (e.getKeyCode() == KeyEvent.VK_E) {
                            stopButtonMethod();
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_T) {
                        timeLabel = !timeLabel;
                        label.setVisible(timeLabel);
                        if (showTime.isSelected()) {
                            hideTime.setSelected(true);
                        } else if (hideTime.isSelected()) {
                            showTime.setSelected(true);
                        }
                    }
                }
            }
            return false;
        });
    }

    public void stopMaleBase(){
        maleBaseAI.setSleep(true);
    }
    public void enableMaleBase(){
        maleBaseAI.setSleep(false);
        synchronized (MaleBaseAI.class){
            MaleBaseAI.class.notify();
        }
    }
    public void stopFemaleBase(){
        femaleBaseAI.setSleep(true);
    }
    public void enableFemaleBase(){
        femaleBaseAI.setSleep(false);
        synchronized (FemaleBaseAI.class){
            FemaleBaseAI.class.notify();
        }
    }
    public void setPriorityMale(int priority){
        malePriority = priority;
        maleBaseAI.getThread().setPriority(malePriority);
    }
    public void setPriorityFemale(int priority){
        femalePriority = priority;
        femaleBaseAI.getThread().setPriority(femalePriority);
    }


    public int getInputTextFirst() {
        return Integer.parseInt(inputTextFirst.getText());
    }

    public void setInputTextFirst(String text) {
        this.inputTextFirst.setText(text);
        this.update_male = Integer.parseInt(text)*1000;
    }

    public int getInputTextSecond() {
        return Integer.parseInt(inputTextSecond.getText());
    }

    public void setInputTextSecond(String text) {
        this.inputTextSecond.setText(text);
        this.update_female = Integer.parseInt(text)*1000;
    }

    public int getMalePriority() {
        return malePriority;
    }

    public void setMalePriority(int malePriority) {
        this.malePriority = malePriority;
        priorityMale.setSelectedIndex(malePriority-1);


    }

    public int getFemalePriority() {
        return femalePriority;
    }

    public void setFemalePriority(int femalePriority) {
        this.femalePriority = femalePriority;
        priorityFemale.setSelectedIndex(femalePriority-1);
    }

    public double getMaleProbability() {
        return maleProbability;
    }

    public void setMaleProbability(double maleProbability) {
        this.maleProbability = maleProbability;
        firstVib.setSelectedIndex((int) (maleProbability*10)-1);
    }

    public double getFemaleProbability() {
        return femaleProbability;
    }

    public void setFemaleProbability(double femaleProbability) {
        this.femaleProbability = femaleProbability;
        secondVib.setSelectedIndex((int) (femaleProbability*10)-1);
    }

    public int getTimeToDieFirst() {
        return timeToDieFirst;
    }

    public void setTimeToDieFirst(int timeToDieFirst) {
        this.timeToDieFirst = timeToDieFirst;
        inputTimeDieForFirst.setText(String.valueOf(timeToDieFirst));

    }

    public int getTimeToDieSecond() {
        return timeToDieSecond;
    }

    public void setTimeToDieSecond(int timeToDieSecond) {
        this.timeToDieSecond = timeToDieSecond;
        inputTimeDieForSecond.setText(String.valueOf(timeToDieSecond));
    }
}
