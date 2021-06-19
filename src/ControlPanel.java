import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
	private GameManager game = null;
	private JComboBox<String> numberBox = null;
	JRadioButton winner = new JRadioButton("��� ��������", true);
	JRadioButton loser = new JRadioButton("���� ��������");
	JButton button = new JButton("���");

	private final boolean WINNER = true;
	private final boolean LOSER = false;
	private boolean raceOn = false;

	public ControlPanel(GameManager game) {
		this.game = game;
		createComboBox();
		setPreferredSize(new Dimension(640, 32));

		setLayout(new GridBagLayout());
		GridBagConstraints layout = new GridBagConstraints();

		layout.anchor = GridBagConstraints.LINE_START;
		layout.weightx = 0.1;
		layout.gridx = 0;
		add(new JLabel("  ������"), layout);

		layout.weightx = 0.7;
		layout.gridx++;
		add(numberBox, layout);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(winner);
		buttonGroup.add(loser);
		winner.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					game.setRaceRule(WINNER);
				}
			}
		});
		loser.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					game.setRaceRule(LOSER);
					game.loserPicker();
				}
			}
		});

		layout.anchor = GridBagConstraints.CENTER;
		layout.weightx = 0.2;
		layout.gridx++;
		add(winner, layout);
		layout.gridx++;
		add(loser, layout);

		layout.anchor = GridBagConstraints.LINE_END;
		layout.weightx = 0.7;
		layout.gridx++;
		add(button, layout);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (raceOn)
					return;

				JButton src = (JButton) e.getSource();
				if (src.getText().equals("���")) {
					raceOn = true;
					holdCrowds();
					game.go();
					src.setText("�� ����");
				}
				else { // ��ư ������ "�� ����"�� ���
					game.maintainStadium();
					String currBox = (String) numberBox.getSelectedItem();
					int currValue;
					if (currBox.equals("..."))
						currValue = getUserInput();
					else
						currValue = Integer.parseInt(currBox);
					game.recruitRacer(currValue);
					game.onYourMark();
					src.setText("���");
				}
			}
		});
	}

	private void holdCrowds() {
		numberBox.setEnabled(false);
		winner.setEnabled(false);
		loser.setEnabled(false);
	}

	public void tellRaceEnd() {
		raceOn = false;
		numberBox.setEnabled(true);
		winner.setEnabled(true);
		loser.setEnabled(true);
	}

	private void createComboBox() {
		numberBox = new JComboBox<String>();
		for (int i = 2; i <= 9; i++) {
			numberBox.addItem(Integer.toString(i));
		}
		numberBox.addItem("...");
		numberBox.addItemListener(new ComboBoxListener());
		numberBox.setSelectedIndex(GameManager.DEFAULT);
		game.onYourMark();
	}

	private int getUserInput() {
		int value;
		String userInput = JOptionPane.showInputDialog(null,
				"���ֿ� �� ���� �����ϳ���?\n�ִ� " + GameManager.MAX_RACER + "������� �����մϴ�.", "���� �ο�", JOptionPane.QUESTION_MESSAGE);

		try {
			value = Integer.parseInt(userInput);
			if (value > GameManager.MAX_RACER || value < GameManager.MIN_RACER) {
				JOptionPane.showMessageDialog(null, "������ ���� �ο��� 2~16���Դϴ�.", "����", JOptionPane.ERROR_MESSAGE);
				numberBox.setSelectedIndex(GameManager.DEFAULT);
				value = Integer.parseInt(numberBox.getItemAt(GameManager.DEFAULT));
			}
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "�ùٸ��� ���� �Է��Դϴ�.", "����", JOptionPane.ERROR_MESSAGE);
			numberBox.setSelectedIndex(GameManager.DEFAULT);
			value = Integer.parseInt(numberBox.getItemAt(GameManager.DEFAULT));
		}

		return value;
	}

	private class ComboBoxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			button.setText("���");
			game.maintainStadium();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String src = (String) e.getItem();
				int num;

				try {
					num = Integer.parseInt(src);
				}
				catch (NumberFormatException ex) { // ����ڰ� "..."�� ������ ���
					num = getUserInput();
				}
				game.recruitRacer(num);
				game.onYourMark();
			}
		}
	}
}
