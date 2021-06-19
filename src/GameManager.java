import javax.swing.*;
import java.awt.*;
import java.util.Vector;

////// �ּ� �ޱ�(�ٸ� �ҽ��鵵)
////// synchronized�� �ڵ� ����
public class GameManager {
	// ���� public ���
	public static final int MAX_RACER = 16;
	public static final int MIN_RACER = 2;
	public static final int SPEED_SEED = 30; // ������ ���� �ӵ����� ����
	public static final int START_LINE = 320;
	public static final int DEFAULT = 4; // �޺��ڽ� �������� �����ϴ� ����Ʈ �ε���
	public static final Dimension TRACK = new Dimension(60, START_LINE);
	public static final Dimension SIZE = new Dimension(60, START_LINE + 50);
	public static final Color RESULT = new Color(169, 109, 199);
	public static final Color SCREEN = new Color(227, 251, 210);

	// ������ ����
	private BalloonRacerFrame frame = null;
	private JPanel screen = new JPanel();

	// ���� ���� ������
	private int numOfRacer = MIN_RACER + DEFAULT; // ���� �⺻��
	private Vector<Racer> racer = new Vector<Racer>();
	private Vector<Integer> speedVector = new Vector<Integer>();
	private int veteran; // ������ ���ָ� ���� racer�� ��

	// ���/���� ������ ���� ������
	private boolean pickWinner = true;
	private int goal = 0;
	private int loserFlag = 0;
	private Racer chosen = null;

	public GameManager(BalloonRacerFrame frame) {
		this.frame = frame;
		screen.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 4));
		screen.setBackground(SCREEN);
	}

	public void maintainStadium() { // �� ������ ���� �ʱ�ȭ
		screen.removeAll();
		screen.revalidate();
		screen.repaint();

		goal = 0;
		loserFlag = 0;
		chosen = null;
	}

	public void onYourMark() { // screen ����
		for (int i = 0; i < numOfRacer; i++)
			screen.add(racer.get(i));

		screen.revalidate();
		screen.repaint();
		frame.setLabel(null, Color.LIGHT_GRAY);
		frame.pack();

		if (!pickWinner) {
			for (int i = 0; i < numOfRacer; i++)
				loserFlag += i;
		}
	}

	private void getSet() {
		veteran = numOfRacer; // "�� ����"�� ������ �� �޺��ڽ��� �ǵ���� ���� �����ϱ� ����
		for (int i = 0; i < numOfRacer; i++)
			racer.get(i).ready();

		try { // �� �ڵ� ���� �ٷ� notifyAll()�� ȣ���ϸ� ������ racer�� ����� �ʴ� ������ ����
			Thread.sleep(100);
		}
		catch (InterruptedException e) {}
	}

	public void go() { // ���� ���� ����
		getSet();
		synchronized (this) {
			this.notifyAll();
		}
	}

	public void recruitRacer(int userSelect) {
		int currSize = racer.size();
		int diff = userSelect - currSize;
		if (diff > 0) {
			for (int i = 0; i < diff; i++)
				racer.add(new Racer(this, currSize + i));
		}
		numOfRacer = userSelect;
	}

	public void fireRacer() {
		for (int i = 0; i < veteran; i++) // ���̽��� �����ߴ� racer�鸸 �����
			racer.removeElementAt(0);
		for (int i = 0; i < racer.size(); i++)
			racer.get(i).setLane(i); // ��� ���� �����ڵ鿡�� ���� ��ȣ�� �ٽ� 0���� �ο�

		// GameManager.setSpeed()�� ������ ���� �� ���� speed�� �̾Ƴ��Ƿ�,
		// ���� ������ �������� ���� ���� ���� �� speed�� ���� ����
		speedVector.removeAllElements();
		for (int i = 0; i < racer.size(); i++) {
			racer.get(i).setSpeed();
		}
	}

	////////////// �Լ����� �����ϱ�
	public JPanel getScreen() { return screen; }
	public void setRaceRule(boolean pick) { pickWinner = pick; }

	public int setSpeed() {
		int speed;
		do {
			speed = (int) (Math.random() * SPEED_SEED) + 1;
		} while (isAlreadyExist(speed));
		speedVector.add(speed);
		return speed;
	}

	private boolean isAlreadyExist(int n) {
		for (int i = 0; i < speedVector.size(); i++) {
			int v = speedVector.get(i);
			if (v == n)
				return true;
		}
		return false;
	}

	synchronized public void crossLine(Racer racer) {
		goal++;

		if (pickWinner) {
			if (goal == 1)
				chosen = racer;
			else
				return;
		}
		else { // ��� �������� ���
			int index = racer.getLane() - 1;
			loserFlag -= index;
			if (numOfRacer - goal == 1)
				chosen = this.racer.get(loserFlag);
			else
				return;
		}

		String name = chosen.getRacerName();
		if (name.length() == 0)
			name = "(����)";

		frame.setLabel(chosen.getLane() + "�� ���� " + name + "���Դϴ�!!", RESULT);
		endRace();
	}

	private void endRace() {
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {}
		for (int i = 0; i < veteran; i++)
			racer.get(i).kill();
		fireRacer();
	}
}