package j333.scripts.smithing;

import java.io.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;
import java.util.List;

public class RSSmithingForm
{
    /********* Static Properties *********/

    private final static String CONFIG_FILE_NAME = "RSSmithingConfig.ini";

    /********* Data Properties *********/

    private File storageDirectory;
    private RSSmithingFormEventHandler eventHandler;

    /********* GUI Properties *********/

    private JButton saveButton;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JTextArea logTextArea;
    private JTextField bankIdsTextField;
    private JTextField smelterNamesTextField;
    private JComboBox categoryComboBox;
    private JComboBox itemsComboBox;
    private JButton startButton;
    private JTextField runTimeTextField;
    private JTextField experiencePerHourTextField;
    private JTextField experienceGainedTextField;

    public RSSmithingForm(File storageDirectory)
    {
        this.storageDirectory = storageDirectory;
        this.setupProperties();
    }

    private void setupProperties()  {
        this.setupUI();
    }

    /********* Accessors *********/

    public void setEventHandler(RSSmithingFormEventHandler eventHandler) { this.eventHandler = eventHandler; }

    public List<Integer> getBankIds() { return this.getIntegerListFromString(this.bankIdsTextField.getText().trim()); }

    public List<String> getSmelterNames()
    {
        String[] components = this.smelterNamesTextField.getText().trim().split(",");
        return Arrays.asList(Arrays.stream(components).map(String::trim).toArray(String[]::new));
    }

    /********* Helpers *********/

    private File configFile() { return new File(this.storageDirectory, RSSmithingForm.CONFIG_FILE_NAME); }

    /********* Actions *********/

    public void setRunTime(String runTime) {
        this.runTimeTextField.setText(runTime);
    }

    public void setExperienceGained(String experienceGained) {
        this.experienceGainedTextField.setText(experienceGained);
    }

    public void setExperiencePerHour(String experiencePerHour) {
        this.experiencePerHourTextField.setText(experiencePerHour);
    }

    public void log(String message) {
        this.logTextArea.append("[" + LocalDateTime.now() + "] " + message + "\n");
    }

    public void load()
    {
        this.setupUI();

        if (this.configFile().exists())
        {
            Properties config = this.getSavedConfig();
            this.loadConfig(config);
        }
        else { this.loadConfig(this.defaultConfig()); }

        JFrame frame = new JFrame("Configuration");
        frame.setContentPane(this.contentPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        this.startButton.addActionListener((e) -> this.eventHandler.formDidPressStartButton(this, this.startButton));

        this.categoryComboBox.addActionListener((e) ->
            this.eventHandler.formDidSelectSection(this, this.categoryComboBox.getSelectedIndex())
        );

        this.itemsComboBox.addActionListener((e) ->
                this.eventHandler.formDidSelectItemAt(this, this.itemsComboBox.getSelectedIndex())
        );

        this.saveButton.addActionListener((e) ->
        {
            try (FileOutputStream stream = new FileOutputStream(new File(this.storageDirectory, RSSmithingForm.CONFIG_FILE_NAME)))
            {
                Properties loadedConfig = this.getLoadedConfig();
                loadedConfig.store(stream, null);

            } catch (IOException ex) { ex.printStackTrace(); }
        });
    }

    public void loadTypes(List<String> items)
    {
        this.categoryComboBox.removeAllItems();
        items.forEach(i -> this.categoryComboBox.addItem(i));
    }

    public void loadItems(List<String> items)
    {
        this.itemsComboBox.removeAllItems();
        items.forEach(i -> this.itemsComboBox.addItem(i));
    }

    private Properties defaultConfig()
    {
        Properties config = new Properties();
        config.setProperty("bankIds", "79036");
        config.setProperty("smelterNames", "portable forge,furnace");
        return config;
    }

    private Properties getLoadedConfig()
    {
        Properties config = new Properties();
        config.setProperty("bankIds", this.bankIdsTextField.getText());
        config.setProperty("smelterNames", this.smelterNamesTextField.getText());
        return config;
    }

    private Properties getSavedConfig()
    {
        try (FileInputStream stream = new FileInputStream(this.configFile()))
        {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;

        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    private void loadConfig(Properties config)
    {
        this.bankIdsTextField.setText(config.get("bankIds").toString());
        this.smelterNamesTextField.setText(config.get("smelterNames").toString());
    }

    private List<Integer> getIntegerListFromString(String str)
    {
        String[] components = str.split(",");
        List<Integer> list = new ArrayList<>();

        for (String component : components) {
            list.add(Integer.parseInt(component.trim()));
        }

        return list;
    }

    /********* GUI Actions *********/

    private void setupUI()
    {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.setBackground(new Color(-12236470));
        contentPanel.setMaximumSize(new Dimension(178, 419));
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(-11512747));
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBackground(new Color(-10525852));
        tabbedPane.addTab("Configuration", panel1);
        final JLabel label1 = new JLabel();
        label1.setBackground(new Color(-984065));
        label1.setEnabled(true);
        label1.setFocusable(false);
        label1.setFont(new Font("Helvetica Neue", Font.BOLD, label1.getFont().getSize()));
        label1.setForeground(new Color(-1));
        label1.setHorizontalAlignment(0);
        label1.setHorizontalTextPosition(4);
        label1.setOpaque(false);
        label1.setText("Bank Ids");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(label1, gbc);
        bankIdsTextField = new JTextField();
        bankIdsTextField.setAlignmentX(0.0f);
        bankIdsTextField.setBackground(new Color(-13881809));
        bankIdsTextField.setCaretColor(new Color(-1));
        bankIdsTextField.setFont(new Font("Helvetica Neue", Font.PLAIN, bankIdsTextField.getFont().getSize()));
        bankIdsTextField.setForeground(new Color(-1));
        bankIdsTextField.setName("bankIds");
        bankIdsTextField.setText("");
        bankIdsTextField.setToolTipText("Comma seperated");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(bankIdsTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setBackground(new Color(-984065));
        label2.setFont(new Font("Helvetica Neue", Font.BOLD, label2.getFont().getSize()));
        label2.setForeground(new Color(-1));
        label2.setHorizontalAlignment(0);
        label2.setHorizontalTextPosition(4);
        label2.setText("Smelter Names");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(label2, gbc);
        smelterNamesTextField = new JTextField();
        smelterNamesTextField.setAlignmentX(0.0f);
        smelterNamesTextField.setBackground(new Color(-13881809));
        smelterNamesTextField.setCaretColor(new Color(-1));
        smelterNamesTextField.setFont(new Font("Helvetica Neue", Font.PLAIN, smelterNamesTextField.getFont().getSize()));
        smelterNamesTextField.setForeground(new Color(-1));
        smelterNamesTextField.setName("smelterNames");
        smelterNamesTextField.setToolTipText("Comma seperated");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(smelterNamesTextField, gbc);
        final JLabel label3 = new JLabel();
        label3.setBackground(new Color(-984065));
        label3.setEnabled(true);
        label3.setFocusable(false);
        label3.setFont(new Font("Helvetica Neue", Font.BOLD, label3.getFont().getSize()));
        label3.setForeground(new Color(-1));
        label3.setHorizontalAlignment(0);
        label3.setHorizontalTextPosition(4);
        label3.setOpaque(false);
        label3.setText("Category");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(label3, gbc);
        categoryComboBox = new JComboBox();
        categoryComboBox.setBackground(new Color(-1));
        categoryComboBox.setForeground(new Color(-16777216));
        categoryComboBox.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(categoryComboBox, gbc);
        final JLabel label4 = new JLabel();
        label4.setBackground(new Color(-984065));
        label4.setEnabled(true);
        label4.setFocusable(false);
        label4.setFont(new Font("Helvetica Neue", Font.BOLD, label4.getFont().getSize()));
        label4.setForeground(new Color(-1));
        label4.setHorizontalAlignment(0);
        label4.setHorizontalTextPosition(4);
        label4.setOpaque(false);
        label4.setText("Item");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(label4, gbc);
        itemsComboBox = new JComboBox();
        itemsComboBox.setBackground(new Color(-1));
        itemsComboBox.setForeground(new Color(-16777216));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel1.add(itemsComboBox, gbc);
        saveButton = new JButton();
        saveButton.setBackground(new Color(-1311233));
        saveButton.setFont(new Font("Helvetica Neue", Font.PLAIN, saveButton.getFont().getSize()));
        saveButton.setForeground(new Color(-16777216));
        saveButton.setText("Save");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 10);
        panel1.add(saveButton, gbc);
        startButton = new JButton();
        startButton.setBackground(new Color(-1311233));
        startButton.setFont(new Font("Helvetica Neue", Font.PLAIN, startButton.getFont().getSize()));
        startButton.setForeground(new Color(-16777216));
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 10, 10);
        panel1.add(startButton, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setBackground(new Color(-10525852));
        panel2.setEnabled(true);
        tabbedPane.addTab("Statistics", panel2);
        final JLabel label7 = new JLabel();
        label7.setFont(new Font("Helvetica Neue", Font.BOLD, label7.getFont().getSize()));
        label7.setForeground(new Color(-1));
        label7.setText("Run time");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel2.add(label7, gbc);
        runTimeTextField = new JTextField();
        runTimeTextField.setBackground(new Color(-13881809));
        runTimeTextField.setEditable(false);
        runTimeTextField.setEnabled(true);
        runTimeTextField.setFont(new Font("Helvetica Neue", runTimeTextField.getFont().getStyle(), runTimeTextField.getFont().getSize()));
        runTimeTextField.setForeground(new Color(-1));
        runTimeTextField.setText("--");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel2.add(runTimeTextField, gbc);
        final JLabel label8 = new JLabel();
        label8.setFont(new Font("Helvetica Neue", Font.BOLD, label8.getFont().getSize()));
        label8.setForeground(new Color(-1));
        label8.setText("Experience gained");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        panel2.add(label8, gbc);
        experienceGainedTextField = new JTextField();
        experienceGainedTextField.setBackground(new Color(-13881809));
        experienceGainedTextField.setEditable(false);
        experienceGainedTextField.setForeground(new Color(-1));
        experienceGainedTextField.setText("--");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel2.add(experienceGainedTextField, gbc);
        experiencePerHourTextField = new JTextField();
        experiencePerHourTextField.setBackground(new Color(-13881809));
        experiencePerHourTextField.setEditable(false);
        experiencePerHourTextField.setEnabled(true);
        experiencePerHourTextField.setFont(new Font("Helvetica Neue", experiencePerHourTextField.getFont().getStyle(), experiencePerHourTextField.getFont().getSize()));
        experiencePerHourTextField.setForeground(new Color(-1));
        experiencePerHourTextField.setText("--");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 10, 0, 10);
        panel2.add(experiencePerHourTextField, gbc);
        final JLabel label9 = new JLabel();
        label9.setFont(new Font("Helvetica Neue", Font.BOLD, label9.getFont().getSize()));
        label9.setForeground(new Color(-1));
        label9.setText("Experience per hour");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 10, 0, 10);
        panel2.add(label9, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel3.setBackground(new Color(-12236470));
        tabbedPane.addTab("Log", panel3);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel3.add(scrollPane1, BorderLayout.CENTER);
        logTextArea = new JTextArea();
        logTextArea.setBackground(new Color(-10525852));
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Helvetica Neue", logTextArea.getFont().getStyle(), logTextArea.getFont().getSize()));
        scrollPane1.setViewportView(logTextArea);
    }
}
