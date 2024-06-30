import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class HashTableView extends JComponent {
    private ArrayList<String>[] table;
    private String ak;
    private int ai;
    private int ap;
    private Timer at;
    private int as;
    private int cX, cY;
    private int tX, tY;
    private static final int A_S = 20;
    private static final int A_D = 20;
    private boolean movingToIndex;
    
    public HashTableView(ArrayList<String>[] table) {
        this.table = table;
        this.ak = null;
        this.ai = -1;
        this.ap = -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2d = (Graphics2D) g;

        int wt = 100;
        int height = 20;
        int distance = 5;

        for (int i = 0; i < table.length; i++) {
            int x = distance + 100;
            int y = i * (height + distance) + distance + 50;
            graphics2d.drawRect(x, y, wt, height);
            graphics2d.drawString(String.valueOf(i), x + wt/2, y + height / 2 + distance);
            graphics2d.setBackground(Color.BLACK);

            for (int j = 0; j < table[i].size(); j++) {
                int previousX = x;
                x += wt + distance;
                graphics2d.drawRect(x, y, wt, height);
                graphics2d.drawString(table[i].get(j), x + 20, y + height / 2 + distance);
                graphics2d.drawLine(previousX + wt, y + height / 2, x, y + height / 2);
            }
        
            if (!table[i].isEmpty()) {
                int previousX = x;
                x += wt + distance;
                graphics2d.drawLine(previousX + wt, y + height / 2, x, y + height / 2);
                graphics2d.drawLine(x +2, y + distance, x+3, y + height - distance);
                // Draw horizontal dashes
                for (int dashY = y + distance; dashY < y + height - distance; dashY += 5) {
                    graphics2d.drawLine(x +2 , dashY , x + 6, dashY + 2);
                    graphics2d.drawLine(x+2, dashY+3, x + 6, dashY +5);
                }
            }
        }

        if (ak != null) {
            graphics2d.setColor(Color.black);
            graphics2d.drawString(ak, cX + distance, cY + height / 2 + distance);
        }
    }

    public void startAnimation(String key, int index) {
        ak = key;
        ai = index;
        ap = table[index].size();
        as = 0;

        int height = 20;
        int distance = 5;
        int wt = 100;

        cX = distance + 120;
        cY = -height + 50;
        tX = distance + (wt + distance ) ;
        tY = index * (height + distance) + 15 +distance;
        movingToIndex = true;
        if (at != null && at.isRunning()) {
            at.stop();
        }

        at = new Timer(A_D, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (as < A_S-2) {
                    // Move towards index position
                    cX += (tX - cX +20) / (A_S - as + 1);
                    cY += (tY - cY +40) / (A_S -as+1);
                } else {
                    if (movingToIndex) {
                        movingToIndex = false;
                        as = 0;
                        tX = distance + (wt + distance)* (ap + 1);
                    } else {
                        cX += (tX - cX +100) / (A_S - as + 1);
                        if (as >= A_S) {
                            at.stop();
                            table[ai].add(ak);
                            ak = null;
                            repaint();
                        }
                    }
                }

                as++;
                repaint();
            }
        });

        at.start();
    }

    public void updateTable() {
        repaint();
    }
}
//********************************************************************************************************
public class HashTableGUI extends JFrame {
    private int tableSize;
    private ArrayList<String>[] table;
    private int count;
    private JTextField inputField;
    private JTextArea displayArea;
    private JLabel statusLabel;
    private HashTableView HashTableView;

    public HashTableGUI(int size) {
        this.tableSize = size;
        this.table = new ArrayList[this.tableSize];
        for (int i = 0; i < this.tableSize; i++) {
            this.table[i] = new ArrayList<>();
        }
        this.count = 0;
    }

    private int hashFunction(String key) {
        int hashValue = 0;
        for (int i = 0; i < key.length(); i++) {
            hashValue = (hashValue * 31 + key.charAt(i)) % this.tableSize;
        }
        return hashValue;
    }

    public void add(String key) {
        int index = hashFunction(key);
        if (!table[index].contains(key)) {
            HashTableView.startAnimation(key, index);
            count++;
            System.out.println("Added successfully: " + key);
        } else {
            System.out.println("Key already exists: " + key);
        }
    }

    public void remove(String key) {
        int index = hashFunction(key);
        ArrayList<String> list = table[index];

        int lastIndex = -1;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(key)) {
                lastIndex = i;
                break;
            }
        }

        if (lastIndex != -1) {
            list.remove(lastIndex);
            count--;
            System.out.println("Removed successfully: " + key);
            HashTableView.updateTable();
        } else {
            System.out.println("Key not found: " + key);
        }
    }

    public int getHashTableSize() {
        return count;
    }

    public boolean contains(String key) {
        int index = hashFunction(key);
        if (table[index].contains(key)) {
            System.out.println("Key '" + key + "' found");
            return true;
        } else {
            System.out.println("Key '" + key + "' not found");
            return false;
        }
    }

    public String display() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableSize; i++) {
            sb.append(i).append(": ").append(table[i]).append("\n");
        }
        return sb.toString();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 30));
        button.setFocusPainted(false);
        button.setBackground(new Color(79, 127, 31));
        button.setForeground(Color.BLUE);
        return button;
    }

    public void createAndShowGUI() {
        setTitle("¤ HashTable ¤");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HashTableView = new HashTableView(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(HashTableView, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel inputStatusPanel = new JPanel();
        inputStatusPanel.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        inputPanel.setBackground(new Color(79, 127, 31));
        inputPanel.setForeground(Color.WHITE);
        JLabel inputLabel = new JLabel("Input:");
        inputField = new JTextField();
        inputField.setMargin(new Insets(10, 10, 10, 10));
        inputField.setBackground(Color.LIGHT_GRAY);
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputStatusPanel.add(inputPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setBorder(new EmptyBorder(10,10,10,10));
        displayArea = new JTextArea(5, 20);
        displayArea.setMargin(new Insets(10,10,10,10));
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        inputStatusPanel.add(statusLabel, BorderLayout.CENTER);
        inputStatusPanel.add(scrollPane, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3)); 
        JButton addButton = createStyledButton("Add ");
        addButton.setBounds(50, 50, 100, 100);
        
        JButton removeButton = createStyledButton("Remove");
        removeButton.setBounds(50, 50, 100, 100);
        
        JButton containsButton = createStyledButton("Contains");
        containsButton.setBounds(50, 50, 100, 100);
        
        JButton sizeButton = createStyledButton("Size");
        sizeButton.setBounds(50, 50, 100, 100);
       
        JButton displayButton = createStyledButton("Display");
        displayButton.setBounds(50, 50, 100, 100);
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        //buttonPanel.add(containsButton);
        buttonPanel.add(sizeButton);
        buttonPanel.add(displayButton);

        bottomPanel.add(inputStatusPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
//el actions
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = inputField.getText().trim();
                if (!key.isEmpty()) {
                    add(key);
                    statusLabel.setText(key +" Is Added");
                    inputField.setText("");
                } else {
                    statusLabel.setText("Invalid input : Give me an input !!");
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = inputField.getText().trim();
                if (!key.isEmpty()) {
                    remove(key);
                    statusLabel.setText(key+"Is Removed");
                    inputField.setText("");
                } else {
                    statusLabel.setText("Invalid input : Give me an input !!");
                }
            }
        });

        containsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = inputField.getText().trim();
                if (!key.isEmpty()) {
                    boolean result = contains(key);
                    statusLabel.setText("Contains " + key + ": " + result);
                    inputField.setText("");
                } else {
                    statusLabel.setText("Invalid input : Give me an input !!");
                }
            }
        });

        sizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size = getHashTableSize();
                statusLabel.setText("The Current Size Is: " + size);
            }
        });

        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String displayText = display();
                displayArea.setText(displayText);
            }
        });

        
        setLayout(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER); 
        add(bottomPanel, BorderLayout.SOUTH); 

        setVisible(true);
    }

    public static void main(String[] args) {
        HashTableGUI hashTableGUI = new HashTableGUI(5);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                hashTableGUI.createAndShowGUI();
            }
        });
    }
}
