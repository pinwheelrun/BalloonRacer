import java.awt.*;
import javax.swing.*;

public class Racer extends JPanel {
	private GameManager game = null;
	private Thread thread = null;
	private Balloon balloon = null;
	private JTextField name = new JTextField();
	private int lane;

	public Racer(GameManager game, int lane) {
		this.game = game;
		this.lane = lane;
		setPreferredSize(GameManager.SIZE);
		setLayout(new BorderLayout());

		balloon = new Balloon();
		add(balloon);

		name.setBackground(Color.WHITE);
		add(name, BorderLayout.SOUTH);

		thread = new Thread(balloon);
	}

	public void setRacerName(String name) { this.name.setText(name); }
	public String getRacerName() { return name.getText(); }
	public void setLane(int assign) { lane = assign; }
	public int getLane() { return lane; }
	public void setSpeed() { balloon.speed = game.setSpeed(); }
	
	public void ready() { thread.start(); }
	public void kill() { // ������ ���� ��� �ܺο��� ������ ������ ������Ű��
		if (thread.isAlive()) {
			thread.interrupt();
		}
	}

	private class Balloon extends JLabel implements Runnable {
		private Image img = GameManager.ICON.getImage();
		private final int ICON_HEIGHT = GameManager.ICON.getIconHeight();
		private final int WIDTH = (GameManager.TRACK.width - GameManager.ICON.getIconWidth()) / 2;
		private int currPosition = GameManager.START_LINE;
		private int speed;

		Balloon() {
			setPreferredSize(GameManager.TRACK);
			setBackground(GameManager.SCREEN);
			setOpaque(true);
			speed = game.setSpeed();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, WIDTH, currPosition, null);
		}

		@Override
		public void run() {
			try {
				waitForStart(); // ��� ��ư�� ������ ������ ���
				Racer.this.name.setEditable(false);
				game.saveName(lane, getRacerName());
				while (true) {
					Thread.sleep(100);
					currPosition -= speed;
					repaint();
					if (currPosition <= (-ICON_HEIGHT)) { // ȭ�鿡�� ǳ���� �������
						game.crossLine(Racer.this); // GameManager�� �˸��� ������ ����
						thread.interrupt();
					}
				}
			}
			catch (InterruptedException e) {}
		}

		private void waitForStart() throws InterruptedException {
			synchronized (game) {
				game.wait();
			}
		}
	}
}
