import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LibrarySystem1 {
    private static final Color NAVY = new Color(24, 37, 65);
    private static final Color BLUE = new Color(63, 111, 220);
    private static final Color BG = new Color(245, 247, 251);
    private static final Color TEXT = new Color(43, 52, 69);

    private final List<Book> books = new ArrayList<>();
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private JLabel totalValue, availableValue, borrowedValue;
    private JTextField searchField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySystem1().showLoadingScreen());
    }

    private void showLoadingScreen() {
        JFrame loading = new JFrame("LibrarySystem1");
        loading.setUndecorated(true);
        loading.setSize(470, 250);
        loading.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(NAVY);
        panel.setBorder(new EmptyBorder(28, 32, 26, 32));

        JLabel title = new JLabel("LibrarySystem1", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 29));

        JLabel subtitle = new JLabel("Your library, organized.", SwingConstants.CENTER);
        subtitle.setForeground(new Color(205, 216, 239));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JProgressBar progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        progress.setForeground(new Color(104, 158, 255));
        progress.setBackground(new Color(47, 63, 94));
        progress.setBorderPainted(false);

        JLabel status = new JLabel("Starting library system...", SwingConstants.CENTER);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 7));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(progress, BorderLayout.CENTER);
        panel.add(status, BorderLayout.SOUTH);
        loading.setContentPane(panel);
        loading.setVisible(true);

        String[] messages = {
            "Preparing dashboard...", "Loading book catalog...",
            "Setting up library tools...", "Almost ready..."
        };
        Timer timer = new Timer(35, null);
        timer.addActionListener(e -> {
            int value = progress.getValue() + 2;
            progress.setValue(value);
            status.setText(messages[Math.min(value / 26, messages.length - 1)]);
            if (value >= 100) {
                timer.stop();
                loading.dispose();
                seedBooks();
                createMainWindow();
            }
        });
        timer.start();
    }

    private void seedBooks() {
        books.add(new Book("B001", "Noli Me Tangere", "Jose Rizal", false));
        books.add(new Book("B002", "El Filibusterismo", "Jose Rizal", true));
        books.add(new Book("B003", "Java: A Beginner's Guide", "Herbert Schildt", false));
        books.add(new Book("B004", "The Little Prince", "Antoine de Saint-Exupery", false));
        books.add(new Book("B005", "Introduction to Algorithms", "Thomas H. Cormen", true));
    }

    private void createMainWindow() {
        frame = new JFrame("LibrarySystem1 | Library Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 680);
        frame.setMinimumSize(new Dimension(850, 560));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(215, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(22, 14, 18, 14));

        JLabel brand = new JLabel("  LibrarySystem1");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 19));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(8));

        JLabel sub = new JLabel("  LIBRARY MANAGEMENT");
        sub.setForeground(new Color(164, 181, 216));
        sub.setFont(new Font("SansSerif", Font.BOLD, 10));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sub);
        sidebar.add(Box.createVerticalStrut(32));

        addNavButton(sidebar, "Dashboard", "dashboard");
        addNavButton(sidebar, "Books Catalog", "books");
        addNavButton(sidebar, "Borrow / Return", "books");
        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("<html><font color='#A4B5D8'>LibrarySystem1</font><br>"
                + "<font color='#7185AD'>Java Swing Edition</font></html>");
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(footer);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);
        contentPanel.add(createDashboard(), "dashboard");
        contentPanel.add(createBooksPage(), "books");

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);
        refreshData();
        frame.setVisible(true);
    }

    private void addNavButton(JPanel sidebar, String label, String card) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(185, 43));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(card.equals("dashboard") && label.equals("Dashboard")
                ? BLUE : NAVY);
        button.setBorder(new EmptyBorder(8, 14, 8, 8));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, card);
            if (card.equals("books")) refreshData();
        });
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(7));
    }

    private JPanel createDashboard() {
        JPanel page = new JPanel(new BorderLayout(18, 18));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(TEXT);
        JLabel description = new JLabel("Welcome back! Here's your library overview.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(new Color(112, 122, 141));

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 5));
        top.setOpaque(false);
        top.add(heading);
        top.add(description);

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        totalValue = new JLabel("0");
        availableValue = new JLabel("0");
        borrowedValue = new JLabel("0");
        stats.add(statCard("TOTAL BOOKS", totalValue, BLUE));
        stats.add(statCard("AVAILABLE", availableValue, new Color(35, 157, 111)));
        stats.add(statCard("BORROWED", borrowedValue, new Color(220, 145, 45)));

        JPanel quick = new JPanel(new BorderLayout(10, 10));
        quick.setBackground(Color.WHITE);
        quick.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 233, 241)),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel quickTitle = new JLabel("Quick actions");
        quickTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel quickText = new JLabel("<html>Manage your collection, add new titles, or record a book loan.</html>");
        quickText.setForeground(new Color(112, 122, 141));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setOpaque(false);
        JButton catalog = primaryButton("Open Books Catalog");
        catalog.addActionListener(e -> cardLayout.show(contentPanel, "books"));
        JButton add = secondaryButton("+ Add a Book");
        add.addActionListener(e -> { cardLayout.show(contentPanel, "books"); showAddBookDialog(); });
        actions.add(catalog);
        actions.add(add);

        JPanel quickTop = new JPanel(new GridLayout(2, 1, 0, 8));
        quickTop.setOpaque(false);
        quickTop.add(quickTitle);
        quickTop.add(quickText);
        quick.add(quickTop, BorderLayout.NORTH);
        quick.add(actions, BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout(0, 18));
        middle.setOpaque(false);
        middle.add(stats, BorderLayout.NORTH);
        middle.add(quick, BorderLayout.CENTER);

        page.add(top, BorderLayout.NORTH);
        page.add(middle, BorderLayout.CENTER);
        return page;
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(229, 233, 241)),
                        new EmptyBorder(17, 16, 17, 16))));
        JLabel label = new JLabel(title);
        label.setForeground(new Color(112, 122, 141));
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        value.setForeground(TEXT);
        value.setFont(new Font("SansSerif", Font.BOLD, 30));
        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBooksPage() {
        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel("Books Catalog");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(TEXT);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Search by ID, title, or author");
        JButton search = primaryButton("Search");
        search.addActionListener(e -> filterTable());
        JButton reset = secondaryButton("Show All");
        reset.addActionListener(e -> { searchField.setText(""); refreshData(); });

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(searchField);
        searchBar.add(search);
        searchBar.add(reset);

        JPanel header = new JPanel(new BorderLayout(10, 12));
        header.setOpaque(false);
        header.add(heading, BorderLayout.NORTH);
        header.add(searchBar, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Title", "Author", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(32);
        bookTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bookTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        bookTable.getTableHeader().setBackground(new Color(235, 239, 247));
        bookTable.setSelectionBackground(new Color(218, 229, 255));
        bookTable.setGridColor(new Color(232, 236, 243));
        JScrollPane scroll = new JScrollPane(bookTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(225, 230, 239)));

        JButton add = primaryButton("+ Add Book");
        add.addActionListener(e -> showAddBookDialog());
        JButton borrow = secondaryButton("Borrow Selected");
        borrow.addActionListener(e -> changeBookStatus(true));
        JButton returnBook = secondaryButton("Return Selected");
        returnBook.addActionListener(e -> changeBookStatus(false));
        JButton remove = secondaryButton("Remove Selected");
        remove.addActionListener(e -> removeSelectedBook());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(borrow);
        actions.add(returnBook);
        actions.add(remove);

        page.add(header, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);
        page.add(actions, BorderLayout.SOUTH);
        return page;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 223, 235)),
                new EmptyBorder(8, 12, 8, 12)));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return button;
    }

    private void refreshData() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
            for (Book book : books) addBookRow(book);
        }
        if (totalValue != null) {
            int borrowed = 0;
            for (Book book : books) if (book.borrowed) borrowed++;
            totalValue.setText(String.valueOf(books.size()));
            borrowedValue.setText(String.valueOf(borrowed));
            availableValue.setText(String.valueOf(books.size() - borrowed));
        }
    }

    private void addBookRow(Book book) {
        tableModel.addRow(new Object[]{book.id, book.title, book.author,
                book.borrowed ? "Borrowed" : "Available"});
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Book book : books) {
            if (book.id.toLowerCase().contains(query)
                    || book.title.toLowerCase().contains(query)
                    || book.author.toLowerCase().contains(query)) {
                addBookRow(book);
            }
        }
    }

    private void showAddBookDialog() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Book ID:"));
        form.add(idField);
        form.add(new JLabel("Book title:"));
        form.add(titleField);
        form.add(new JLabel("Author:"));
        form.add(authorField);

        int result = JOptionPane.showConfirmDialog(frame, form, "Add a Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.",
                    "Missing information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Book book : books) {
            if (book.id.equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(frame, "That Book ID already exists.",
                        "Duplicate ID", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        books.add(new Book(id, title, author, false));
        saveBooks();
        refreshData();
        JOptionPane.showMessageDialog(frame, "Book added successfully.");
    }

    private Book getSelectedBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Please select a book from the table.",
                    "No book selected", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        String id = String.valueOf(tableModel.getValueAt(row, 0));
        for (Book book : books) if (book.id.equals(id)) return book;
        return null;
    }

    private void changeBookStatus(boolean borrow) {
        Book book = getSelectedBook();
        if (book == null) return;
        if (borrow && book.borrowed) {
            JOptionPane.showMessageDialog(frame, "This book is already borrowed.");
            return;
        }
        if (!borrow && !book.borrowed) {
            JOptionPane.showMessageDialog(frame, "This book is already marked available.");
            return;
        }
        book.borrowed = borrow;
        saveBooks();
        refreshData();
        JOptionPane.showMessageDialog(frame, borrow
                ? "Book marked as borrowed." : "Book returned successfully.");
    }

    private void removeSelectedBook() {
        Book book = getSelectedBook();
        if (book == null) return;
        int answer = JOptionPane.showConfirmDialog(frame,
                "Remove \"" + book.title + "\" from the catalog?",
                "Confirm removal", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            books.remove(book);
            saveBooks();
            refreshData();
        }
    }

    private void saveBooks() {
    List<String> lines = new ArrayList<>();

    for (Book book : books) {
        lines.add(
            book.id + "|" +
            book.title + "|" +
            book.author + "|" +
            book.borrowed
        );
    }

    try {
        Files.write(Path.of("book.txt"), lines);
    } catch (IOException e) {
        JOptionPane.showMessageDialog(
            frame,
            "Unable to save books:\n" + e.getMessage(),
            "Save Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

    private static class Book {
        final String id;
        final String title;
        final String author;
        boolean borrowed;

        Book(String id, String title, String author, boolean borrowed) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.borrowed = borrowed;
        }
    }
}