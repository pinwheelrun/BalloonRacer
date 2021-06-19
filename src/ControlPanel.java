import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {
	private GameManager game = null;
	private JComboBox<String> numberBox = null;
	JRadioButton winner = new JRadioButton("우승 가려내기", true);
	JRadioButton loser = new JRadioButton("꼴찌 가려내기");
	JButton button = new JButton("출발");

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
		add(new JLabel("  참가자"), layout);

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
				if (src.getText().equals("출발")) {
					raceOn = true;
					holdCrowds();
					game.go();
					src.setText("새 게임");
				}
				else { // 버튼 내용이 "새 게임"인 경우
					game.maintainStadium();
					String currBox = (String) numberBox.getSelectedItem();
					int currValue;
					if (currBox.equals("..."))
						currValue = getUserInput();
					else
						currValue = Integer.parseInt(currBox);
					game.recruitRacer(currValue);
					game.onYourMark();
					src.setText("출발");
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
				"경주에 몇 명이 참여하나요?\n최대 " + GameManager.MAX_RACER + "명까지만 가능합니다.", "참가 인원", JOptionPane.QUESTION_MESSAGE);

		try {
			value = Integer.parseInt(userInput);
			if (value > GameManager.MAX_RACER || value < GameManager.MIN_RACER) {
				JOptionPane.showMessageDialog(null, "가능한 참가 인원은 2~16명입니다.", "오류", JOptionPane.ERROR_MESSAGE);
				numberBox.setSelectedIndex(GameManager.DEFAULT);
				value = Integer.parseInt(numberBox.getItemAt(GameManager.DEFAULT));
			}
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "올바르지 않은 입력입니다.", "오류", JOptionPane.ERROR_MESSAGE);
			numberBox.setSelectedIndex(GameManager.DEFAULT);
			value = Integer.parseInt(numberBox.getItemAt(GameManager.DEFAULT));
		}

		return value;
	}

	private class ComboBoxListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			button.setText("출발");
			game.maintainStadium();
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String src = (String) e.getItem();
				int num;

				try {
					num = Integer.parseInt(src);
				}
				catch (NumberFormatException ex) { // 사용자가 "..."를 선택한 경우
					num = getUserInput();
				}
				game.recruitRacer(num);
				game.onYourMark();
			}
		}
	}
}
