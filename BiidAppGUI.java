import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Creator: Bid class creates its own data
class Bid {
    private String bidderName;
    private double amount;

    public Bid(String bidderName, double amount) {
        this.bidderName = bidderName;
        this.amount = amount;
    }

    public String getBidderName() {
        return bidderName;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return bidderName + " - $" + amount;
    }
}

// High Cohesion: Responsible only for validation
class BidValidator {
    public boolean isValid(Bid bid) {
        return bid.getBidderName() != null && !bid.getBidderName().trim().isEmpty() && bid.getAmount() > 0;
    }
}

// Pure Fabrication: Handles storage logic
class BidRepository {
    private List<Bid> bids = new ArrayList<>();

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public List<Bid> getAllBids() {
        return bids;
    }
}

// Observer Pattern: Interface
interface BidObserver {
    void onBidPlaced(Bid bid);
}

// Controller: Handles app logic
class BidController {
    private BidValidator validator = new BidValidator();
    private BidRepository repository = new BidRepository();
    private List<BidObserver> observers = new ArrayList<>();

    public void addObserver(BidObserver observer) {
        observers.add(observer);
    }

    public void placeBid(String name, double amount) {
        Bid bid = new Bid(name.trim(), amount);
        if (validator.isValid(bid)) {
            repository.addBid(bid);
            for (BidObserver observer : observers) {
                observer.onBidPlaced(bid);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Bid!");
        }
    }
}

// GUI (Observer & Frontend)
public class BidAppGUI implements BidObserver {
    private JFrame frame;
    private JTextField nameField;
    private JTextField amountField;
    private JTextArea bidDisplay;
    private BidController controller;

    public BidAppGUI() {
        controller = new BidController();
        controller.addObserver(this);
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Bidding App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(5, 1));
        nameField = new JTextField();
        amountField = new JTextField();
        JButton submitBtn = new JButton("Place Bid");
        bidDisplay = new JTextArea();
        bidDisplay.setEditable(false);

        panel.add(new JLabel("Bidder Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Bid Amount:"));
        panel.add(amountField);
        panel.add(submitBtn);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(bidDisplay), BorderLayout.CENTER);

        submitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    controller.placeBid(name, amount);
                    nameField.setText("");
                    amountField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid amount.");
                }
            }
        });

        frame.setVisible(true);
    }

    // Observer update
    @Override
    public void onBidPlaced(Bid bid) {
        bidDisplay.append(bid.toString() + "\n");
    }

    public static void main(String[] args) {
        new BidAppGUI();
    }
}
