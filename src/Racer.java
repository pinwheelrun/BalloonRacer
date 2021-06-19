import javax.swing.*;
import java.awt.*;

/////////////주석, 코드정리
///////// name필드를 다른클래스로 빼는게나을까?(새 게임을 만들면 지워지는현상)
//////// 출발 버튼을 누르는 순간 name들을 String[]로 manager에 저장한뒤(배열길이16, 초기화""),
//////// 생성과정에서 lane인덱스를 이용해 name을 불러올수있을것같아. i<numOfRacer.length
//////// lane인덱스 정리필요. this.lane=lane으로 저장하고, 출력시에만 +1하는게 나을것같아.
/////// icon과 img는 manager클래스에 넣는게 더 나을지도. (HEIGHT도 static으로 ICON_HEIGHT)
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

	public void kill() { // 게임이 끝난 경우 외부에서 강제로 스레드 중지시키기
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
			/////////// WIDTH값도 GameManager.TRACK.width 사용해 위로 빼기
			final int WIDTH = (this.getWidth() - icon.getIconWidth()) / 2;
			g.drawImage(img, WIDTH, currPosition, null);
		}

		@Override
		public void run() {
			try {
				waitForStart(); // 출발 버튼이 눌리기 전까지 대기
				Racer.this.name.setEditable(false);
				while (true) {
					Thread.sleep(100);
					currPosition -= speed;
					repaint();
					if (currPosition <= (-HEIGHT)) { // 화면에서 풍선이 사라지면
						game.crossLine(Racer.this); // GameManager에 알리고 스레드 종료
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
