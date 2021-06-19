import java.awt.*;
import javax.swing.*;

public class BalloonRacerFrame extends JFrame {
	private JLabel label = new JLabel();
	private static String DEFAULT = "pinwheelrun";
	private String purpose = "<" + DEFAULT + ">��";
	private GameManager game = new GameManager(this);
	private ControlPanel control = new ControlPanel(game);

	public BalloonRacerFrame() {
		super("Balloon Racer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.add(game.getScreen());
		c.add(control, BorderLayout.SOUTH);

		label.setBackground(Color.LIGHT_GRAY);
		label.setOpaque(true);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setPreferredSize(new Dimension(640, 30));
		c.add(label, BorderLayout.NORTH);

		setSize(640, 480);
		setResizable(false);
		setVisible(true);
		label.setText(getPurpose());
	}

	private String getPurpose() {
		String userInput = JOptionPane.showInputDialog(this, "���� ������ �����ּ���.\n��) ���ɸ޴� �� ���", "ǳ�� ���ְ� �����ϴ�!",
				JOptionPane.QUESTION_MESSAGE);
		if (userInput == null)
			userInput = DEFAULT;
		else if (userInput.length() == 0)
			userInput = DEFAULT;

		purpose = "<" + userInput + ">��";
		return new String("!!! " + purpose + " ǳ�� ���� !!!");
	}

	public void setLabel(String msg, Color color) {
		label.setBackground(color);
		if (msg != null) { // ��� ����� ��Ÿ���� ���
			control.tellRaceEnd();
			label.setFont(new Font(null, Font.ITALIC + Font.BOLD, 12));
			label.setText(purpose + " ǳ�� ������ ���ΰ��� " + msg);
		}
		else { // ��⸦ �����ϱ� �� �ؽ�Ʈ�� �ǵ����� ���
			label.setFont(null);
			label.setText("!!! " + purpose + " ǳ�� ���� !!!");
		}
	}

	public static void main(String[] args) {
		new BalloonRacerFrame();
	}
}
