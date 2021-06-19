import javax.swing.*;
import java.awt.*;

/////////////�ּ�, �ڵ�����
///////// name�ʵ带 �ٸ�Ŭ������ ���°Գ�����?(�� ������ ����� ������������)
//////// ��� ��ư�� ������ ���� name���� String[]�� manager�� �����ѵ�(�迭����16, �ʱ�ȭ""),
//////// ������������ lane�ε����� �̿��� name�� �ҷ��ü������Ͱ���. i<numOfRacer.length
//////// lane�ε��� �����ʿ�. this.lane=lane���� �����ϰ�, ��½ÿ��� +1�ϴ°� �����Ͱ���.
/////// icon�� img�� managerŬ������ �ִ°� �� ��������. (HEIGHT�� static���� ICON_HEIGHT)
public class Racer extends JPanel {
	private GameManager game = null;
	private Thread thread = null;
	private JTextField name = new JTextField();
	private int lane;
	private Balloon balloon = null;

	public Racer(GameManager game, int lane) {
		this.game = game;
		this.lane = lane + 1;
		setPreferredSize(GameManager.SIZE);
		setLayout(new BorderLayout());

		balloon = new Balloon();
		add(balloon);

		name.setBackground(Color.WHITE);
		add(name, BorderLayout.SOUTH);

		thread = new Thread(balloon);
	}

	public String getRacerName() { return name.getText(); }
	public int getLane() { return lane; }
	public void setSpeed() { balloon.speed = game.setSpeed(); }
	public void setLane(int assign) { lane = assign + 1; }
	public void ready() { thread.start(); }

	public void kill() { // ������ ���� ��� �ܺο��� ������ ������ ������Ű��
		if (thread.isAlive()) {
			thread.interrupt();
		}
	}

	private class Balloon extends JLabel implements Runnable {
		private ImageIcon icon = new ImageIcon("images\\balloons_color.png");
		private Image img = icon.getImage();
		private final int HEIGHT = icon.getIconHeight();
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
			/////////// WIDTH���� GameManager.TRACK.width ����� ���� ����
			final int WIDTH = (this.getWidth() - icon.getIconWidth()) / 2;
			g.drawImage(img, WIDTH, currPosition, null);
		}

		@Override
		public void run() {
			try {
				waitForStart(); // ��� ��ư�� ������ ������ ���
				Racer.this.name.setEditable(false);
				while (true) {
					Thread.sleep(100);
					currPosition -= speed;
					repaint();
					if (currPosition <= (-HEIGHT)) { // ȭ�鿡�� ǳ���� �������
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
