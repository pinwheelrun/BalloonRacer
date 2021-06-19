import java.awt.*;
import javax.swing.*;

public class BalloonRacerFrame extends JFrame {
	private JLabel label = new JLabel();
	private String purpose = null;
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
		label.setText(getPurpose());

		setSize(640, 480);
		setResizable(false);
		setVisible(true);
	}

	private String getPurpose() {
		String userInput = JOptionPane.showInputDialog(this, "참가 목적을 정해주세요.\n예) 점심메뉴 고를 사람", "풍선 경주가 열립니다!",
				JOptionPane.QUESTION_MESSAGE);
		if (userInput.length() == 0)
			userInput = "pinwheelrun";
		purpose = "<" + userInput + ">배";
		return new String("!!! " + purpose + " 풍선 경주 !!!");
	}

	public void setLabel(String msg, Color color) {
		label.setBackground(color);
		if (msg != null) { // 경기 결과를 나타내는 경우
			control.tellRaceEnd();
			label.setFont(new Font(null, Font.ITALIC + Font.BOLD, 12));
			label.setText(purpose + " 풍선 경주의 주인공은 " + msg);
		}
		else { // 경기를 시작하기 전 텍스트로 되돌리는 경우
			label.setFont(null);
			label.setText("!!! " + purpose + " 풍선 경주 !!!");
		}
	}

	public static void main(String[] args) {
		new BalloonRacerFrame();
	}
}
