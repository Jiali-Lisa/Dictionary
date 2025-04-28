/**
 *
 * This is the GUI, using Swing
 * It is responsible for showing interface for users
 * get input from search bar and show result
 * include command instruction/format/example
 * Given Name: Jiali
 * Surname: Ying
 * Student ID: 1346717
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class DictionaryGUI {
    private JTextField textField;
    private JLabel resultLabel;

    public void createGUI(DictionaryClient client){
        // create a frame， with title "Dictionary"
        JFrame frame = new JFrame("Dictionary");
        frame.setSize(1200, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // spacing around components
        gbc.insets = new Insets(30, 10, 0, 10);
        // components expand to fill available horizontal space
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        // add a title
        JLabel title = new JLabel("Dictionary", SwingConstants.CENTER);
        title.setFont(new Font("Times Roman", Font.BOLD, 24));
        // Positions the title at row
        gbc.gridy = 1;
        // takes only its own height
        // Does not take extra vertical space
        gbc.weighty = 0;
        mainPanel.add(title, gbc);

        // searchPanel for both search bar and button
        // Centers the components and sets a 20-pixel gap between them
        JPanel searchPanel = new JPanel(new GridBagLayout());
        // left and right space
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 70, 30, 70));
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(10, 10, 10, 10);

        // Places the search box and button in row 3
        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        mainPanel.add(searchPanel, gbc);

        // search bar
        textField = new JTextField(20);
        // initial size
        textField.setPreferredSize(new Dimension(200, 40));
        searchGbc.gridx = 0;
        searchGbc.weightx = 0.7;
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(textField, searchGbc);

        // search button
        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchGbc.gridx = 1;
        searchGbc.weightx = 0;
        searchGbc.fill = GridBagConstraints.NONE;
        searchPanel.add(searchButton, searchGbc);

        resultLabel = new JLabel("Result: ");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 70, 20, 70);
        mainPanel.add(resultLabel, gbc);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = textField.getText().trim();
                if (!searchText.isEmpty()) {
                    client.queryWord(searchText);
                } else {
                    resultLabel.setText("Result: Please enter a command.");
                }
            }
        });

        //available commands example
        JLabel instructionsLabel = new JLabel("<html>" +
                "<b>Supported Commands:</b><br><br>" +
                "1. <b>Query the meaning(s)</b> → <span style='color:blue;'>QUERY word</span> <br>" +
                "   Example: <i>QUERY apple</i><br><br>" +
                "2. <b>Add a new word and meaning(s)</b> → <span style='color:green;'>ADD word [meaning(s)]</span> <br>" +
                "   Example: <i>ADD grape [\"purple fruit\"]</i><br>" +
                "   Example: <i>ADD grape [\"purple fruit\", \"sweet\"]</i><br>" +
                "   If meaning includes \" \", replace with \' \'</i><br><br>" +
                "3. <b>Remove an existing word</b> → <span style='color:orange;'>REMOVE word</span> <br>" +
                "   Example: <i>REMOVE apple</i><br>" +
                "   If meaning includes \" \", replace with \' \'</i><br><br>" +
                "4. <b>Add additional meaning to an existing word</b> → <span style='color:purple;'>ADDITIONAL word [meaning]</span> <br>" +
                "   Example: <i>ADDITIONAL apple [\"red fruit\"]</i><br>" +
                "   If meaning includes \" \", replace with \' \'</i><br><br>" +
                "5. <b>Update existing meaning of a word</b> → <span style='color:red;'>UPDATE word [one existing meaning] [one new meaning]</span> <br>" +
                "   Example: <i>UPDATE apple [\"fruit\"] [\"red fruit\"]</i><br>" +
                "   If meaning includes \" \", replace with \' \'</i><br><br>" +
                "</html>"
        );
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // empty space at the bottom
        gbc.gridy = 6;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(instructionsLabel, gbc);
//        frame.add(mainPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // display window
        frame.setVisible(true);

    }
    // update the result from server response
    public void updateResult(String result) {
        resultLabel.setText("<html>Result: <br>" + result.replace(";", "<br>") + "</html>");
    }
}
