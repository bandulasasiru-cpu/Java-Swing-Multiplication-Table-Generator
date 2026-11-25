import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MultiplicationTable {

    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_BLACK = new Color(17, 24, 39);
    private static final Color MUTED_GRAY = new Color(75, 85, 99);
    private static final Color BORDER_GREEN = new Color(134, 239, 172);
    private static final Color PANEL_TINT = new Color(240, 253, 244);
    private static final Color BTN_GREEN = new Color(22, 163, 74);
    private static final Color BTN_GREEN_HOVER = new Color(21, 128, 61);
    private static final Color ERROR_RED = new Color(185, 28, 28);

    private static final String GREEN_BASE_NUM = "#15803D";
    private static final String GREEN_MULTIPLIER = "#16A34A";
    private static final String GREEN_SYMBOL = "#4ADE80";
    private static final String GREEN_RESULT = "#065F46";
    private static final String ROW_ALT_BG = "#F0FDF4";

    private static final Font FONT_TITLE = resolveFont("SansSerif", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = resolveFont("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_LABEL = resolveFont("SansSerif", Font.BOLD, 14);
    private static final Font FONT_INPUT = resolveFont("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_BUTTON = resolveFont("SansSerif", Font.BOLD, 14);
    private static final Font FONT_STATUS = resolveFont("SansSerif", Font.PLAIN, 13);

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0.######");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InputWindow().setVisible(true));
    }

    private static Font resolveFont(String family, int style, int size) {
        String[] preferred = {"Segoe UI", "Inter", "SF Pro Text", "Helvetica Neue", family};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] available = ge.getAvailableFontFamilyNames();
        for (String candidate : preferred) {
            for (String installed : available) {
                if (installed.equalsIgnoreCase(candidate)) {
                    return new Font(installed, style, size);
                }
            }
        }
        return new Font(family, style, size);
    }

    private static String formatNumber(double value) {
        if (value == 0.0) {
            return "0";
        }
        return NUMBER_FORMAT.format(value);
    }

    private static JButton createGreenButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isRollover() ? BTN_GREEN_HOVER : BTN_GREEN;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 42));
        return button;
    }

    private static class InputWindow extends JFrame {

        private final JTextField numberField;
        private final JLabel feedbackLabel;

        InputWindow() {
            super("Multiplication Table Generator");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(460, 300));
            setResizable(false);
            setSize(500, 320);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(0, 16));
            root.setBackground(BG_WHITE);
            root.setBorder(new EmptyBorder(28, 32, 28, 32));

            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(BG_WHITE);

            JLabel titleLabel = new JLabel("Multiplication Table Generator");
            titleLabel.setFont(FONT_TITLE);
            titleLabel.setForeground(TEXT_BLACK);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel subtitleLabel = new JLabel("Enter a number to generate its multiplication table from 0 to 100.");
            subtitleLabel.setFont(FONT_SUBTITLE);
            subtitleLabel.setForeground(MUTED_GRAY);
            subtitleLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
            subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            headerPanel.add(titleLabel);
            headerPanel.add(subtitleLabel);

            JPanel formPanel = new JPanel(new BorderLayout(12, 8));
            formPanel.setBackground(PANEL_TINT);
            formPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_GREEN, 1, true),
                    new EmptyBorder(18, 18, 18, 18)
            ));

            JLabel inputLabel = new JLabel("Number");
            inputLabel.setFont(FONT_LABEL);
            inputLabel.setForeground(TEXT_BLACK);

            JPanel inputRow = new JPanel(new BorderLayout(10, 0));
            inputRow.setOpaque(false);

            numberField = new JTextField();
            numberField.setFont(FONT_INPUT);
            numberField.setForeground(TEXT_BLACK);
            numberField.setBackground(BG_WHITE);
            numberField.setCaretColor(BTN_GREEN);
            numberField.setPreferredSize(new Dimension(0, 42));
            numberField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_GREEN, 2, true),
                    new EmptyBorder(6, 12, 6, 12)
            ));

            JButton goButton = createGreenButton("GO");
            Action submitAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleGo();
                }
            };
            goButton.addActionListener(submitAction);
            numberField.addActionListener(submitAction);

            inputRow.add(numberField, BorderLayout.CENTER);
            inputRow.add(goButton, BorderLayout.EAST);

            feedbackLabel = new JLabel(" ");
            feedbackLabel.setFont(FONT_STATUS);
            feedbackLabel.setForeground(ERROR_RED);

            formPanel.add(inputLabel, BorderLayout.NORTH);
            formPanel.add(inputRow, BorderLayout.CENTER);
            formPanel.add(feedbackLabel, BorderLayout.SOUTH);

            root.add(headerPanel, BorderLayout.NORTH);
            root.add(formPanel, BorderLayout.CENTER);

            setContentPane(root);
        }

        private void handleGo() {
            String raw = numberField.getText().trim();
            if (raw.isEmpty()) {
                showError("Please enter a valid number.");
                return;
            }

            double value;
            try {
                value = Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                showError("Invalid input. Please enter a numeric value (e.g., 2, -5, or 3.5).");
                return;
            }

            if (!Double.isFinite(value)) {
                showError("Please enter a finite number.");
                return;
            }

            feedbackLabel.setText(" ");
            new TableWindow(value).setVisible(true);
        }

        private void showError(String message) {
            feedbackLabel.setText(message);
            numberField.requestFocusInWindow();
            numberField.selectAll();
        }
    }

    private static class TableWindow extends JFrame {

        private static final Color GREEN_BASE_NUM = new Color(21, 128, 61);
        private static final Color GREEN_MULTIPLIER = new Color(22, 163, 74);
        private static final Color GREEN_SYMBOL = new Color(74, 222, 128);
        private static final Color GREEN_RESULT = new Color(6, 95, 70);
        private static final Color ROW_ALT_BG = new Color(240, 253, 244);

        TableWindow(double baseNumber) {
            super("Multiplication Table — " + formatNumber(baseNumber));
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setMinimumSize(new Dimension(480, 520));
            setSize(540, 660);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(0, 14));
            root.setBackground(BG_WHITE);
            root.setBorder(new EmptyBorder(24, 28, 24, 28));

            String formattedBase = formatNumber(baseNumber);

            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(BG_WHITE);

            JLabel headingLabel = new JLabel("Multiplication Table for " + formattedBase);
            headingLabel.setFont(FONT_TITLE);
            headingLabel.setForeground(TEXT_BLACK);
            headingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel subLabel = new JLabel("Showing results from 0 to 100");
            subLabel.setFont(FONT_SUBTITLE);
            subLabel.setForeground(MUTED_GRAY);
            subLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            headerPanel.add(headingLabel);
            headerPanel.add(subLabel);

            JTextPane tablePane = new JTextPane();
            tablePane.setEditable(false);
            tablePane.setBackground(BG_WHITE);
            tablePane.setBorder(new EmptyBorder(8, 12, 8, 12));
            populateTable(tablePane, baseNumber, formattedBase);
            tablePane.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(tablePane);
            scrollPane.setBackground(BG_WHITE);
            scrollPane.getViewport().setBackground(BG_WHITE);
            scrollPane.setBorder(new LineBorder(BORDER_GREEN, 1, true));
            scrollPane.getVerticalScrollBar().setUnitIncrement(18);

            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            footerPanel.setBackground(BG_WHITE);

            JButton closeButton = createGreenButton("Close");
            closeButton.addActionListener(e -> dispose());
            footerPanel.add(closeButton);

            root.add(headerPanel, BorderLayout.NORTH);
            root.add(scrollPane, BorderLayout.CENTER);
            root.add(footerPanel, BorderLayout.SOUTH);

            setContentPane(root);
        }

        private void populateTable(JTextPane pane, double baseNumber, String formattedBase) {
            StyledDocument doc = pane.getStyledDocument();
            Font baseFont = resolveFont("SansSerif", Font.BOLD, 16);
            String family = baseFont.getFamily();
            int size = baseFont.getSize();

            SimpleAttributeSet baseAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(baseAttr, family);
            StyleConstants.setFontSize(baseAttr, size);
            StyleConstants.setBold(baseAttr, true);
            StyleConstants.setForeground(baseAttr, GREEN_BASE_NUM);

            SimpleAttributeSet symbolAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(symbolAttr, family);
            StyleConstants.setFontSize(symbolAttr, size);
            StyleConstants.setBold(symbolAttr, true);
            StyleConstants.setForeground(symbolAttr, GREEN_SYMBOL);

            SimpleAttributeSet multAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(multAttr, family);
            StyleConstants.setFontSize(multAttr, size);
            StyleConstants.setBold(multAttr, true);
            StyleConstants.setForeground(multAttr, GREEN_MULTIPLIER);

            SimpleAttributeSet eqAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(eqAttr, family);
            StyleConstants.setFontSize(eqAttr, size);
            StyleConstants.setBold(eqAttr, true);
            StyleConstants.setForeground(eqAttr, TEXT_BLACK);

            SimpleAttributeSet resultAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(resultAttr, family);
            StyleConstants.setFontSize(resultAttr, size);
            StyleConstants.setBold(resultAttr, true);
            StyleConstants.setForeground(resultAttr, GREEN_RESULT);

            try {
                for (int i = 0; i <= 100; i++) {
                    double product = baseNumber * i;
                    SimpleAttributeSet lineAttr = new SimpleAttributeSet();
                    StyleConstants.setSpaceAbove(lineAttr, 4);
                    StyleConstants.setSpaceBelow(lineAttr, 4);
                    StyleConstants.setLeftIndent(lineAttr, 8);
                    if (i % 2 == 1) {
                        StyleConstants.setBackground(lineAttr, ROW_ALT_BG);
                    }

                    int start = doc.getLength();
                    doc.insertString(doc.getLength(), formattedBase, baseAttr);
                    doc.insertString(doc.getLength(), " \u00D7 ", symbolAttr);
                    doc.insertString(doc.getLength(), String.valueOf(i), multAttr);
                    doc.insertString(doc.getLength(), " = ", eqAttr);
                    doc.insertString(doc.getLength(), formatNumber(product) + "\n", resultAttr);
                    doc.setParagraphAttributes(start, doc.getLength() - start, lineAttr, false);
                }
            } catch (BadLocationException ex) {
                pane.setText("Unable to render multiplication table.");
            }
        }
    }
  }
