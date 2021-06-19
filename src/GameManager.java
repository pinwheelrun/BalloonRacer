import java.awt.*;
import javax.swing.*;
import java.util.Vector;

public class GameManager {
	// 각종 public 상수
	public static final int MAX_RACER = 16;
	public static final int MIN_RACER = 2;
	public static final int SPEED_SEED = 30; // 가능한 랜덤 속도값의 개수
	public static final int START_LINE = 320;
	public static final int DEFAULT = 4; // 콤보박스 아이템이 선택하는 디폴트 인덱스
	public static final Dimension TRACK = new Dimension(60, START_LINE);
	public static final Dimension SIZE = new Dimension(60, START_LINE + 50);
	public static final Color RESULT = new Color(169, 109, 199);
	public static final Color SCREEN = new Color(227, 251, 210);
	public static final ImageIcon ICON = new ImageIcon("images\\balloons_color.png");

	// 생성자 관련
	private BalloonRacerFrame frame = null;
	private JPanel screen = new JPanel();

	// 경주 관련 변수들
	private int numOfRacer = MIN_RACER + DEFAULT; // 임의 기본값
	private Vector<Racer> racer = new Vector<Racer>();
	private Vector<Integer> speedVector = new Vector<Integer>();
	private int veteran; // 실제로 경주를 끝낸 racer의 수
	private String[] names = new String[MAX_RACER];

	// 우승/꼴찌 가리기 관련 변수들
	private boolean pickWinner = true;
	private int goal = 0;
	private int loserIndex = 0;
	private Racer chosen = null;

	public GameManager(BalloonRacerFrame frame) {
		this.frame = frame;
		screen.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 4));
		screen.setBackground(SCREEN);
		for (int i = 0; i < names.length; i++)
			names[i] = new String("");
	}

	public JPanel getScreen() { return screen; }
	public void setRaceRule(boolean pick) { pickWinner = pick; }
	public void saveName(int lane, String name) { names[lane] = name; }

	public void maintainStadium() { // 새 게임을 위한 초기화
		screen.removeAll();
		screen.revalidate();
		screen.repaint();
		goal = 0;
		loserIndex = 0;
		chosen = null;
	}

	public void onYourMark() { // screen 변경
		for (int i = 0; i < numOfRacer; i++) {
			Racer r = racer.get(i);
			screen.add(r);
			r.setRacerName(names[i]); // 기존에 입력한 레인 이름 가져오기
		}
		screen.revalidate();
		screen.repaint();

		frame.setLabel(null, Color.LIGHT_GRAY);
		frame.pack();
		if (!pickWinner)
			loserPicker();
	}

	public void loserPicker() {
		for (int i = 0; i < numOfRacer; i++)
			loserIndex += i;
	}

	private void getSet() {
		veteran = numOfRacer; // "새 게임"을 누르기 전 콤보박스를 건드렸을 때를 무시하기 위해
		for (int i = 0; i < numOfRacer; i++)
			racer.get(i).ready();

		try { // 이 코드 없이 바로 notifyAll()을 호출하면 마지막 racer가 깨어나지 않는 문제가 있음
			Thread.sleep(100);
		}
		catch (InterruptedException e) {}
	}

	public void go() { // 실제 게임 진행
		getSet();
		synchronized (this) {
			this.notifyAll();
		}
	}

	public void recruitRacer(int userSelect) { // Vector에 경주를 기다리는 Racer들의 대기열 만들기
		int currSize = racer.size();
		int diff = userSelect - currSize;
		if (diff > 0) {
			for (int i = 0; i < diff; i++)
				racer.add(new Racer(this, currSize + i));
		}
		numOfRacer = userSelect;
	}

	public void fireRacer() {
		for (int i = 0; i < veteran; i++) // 레이스에 참가했던 racer들만 지우기
			racer.removeElementAt(0);
		for (int i = 0; i < racer.size(); i++)
			racer.get(i).setLane(i); // 대기 중인 참가자들에게 레인 번호를 다시 0부터 부여

		// GameManager.setSpeed()는 이전에 뽑힌 적 없는 speed만 뽑아내므로,
		// 다음 게임의 공정성을 위해 전부 지운 후 speed를 새로 공급
		speedVector.removeAllElements();
		for (int i = 0; i < racer.size(); i++) {
			racer.get(i).setSpeed();
		}
	}

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
		else { // 꼴찌를 가려내는 경우
			loserIndex -= racer.getLane();
			if (numOfRacer - goal == 1)
				chosen = this.racer.get(loserIndex);
			else
				return;
		}

		// 승자 또는 꼴찌가 가려내진 경우
		String name = chosen.getRacerName();
		if (name.length() == 0)
			name = "(무명)";

		frame.setLabel((chosen.getLane() + 1) + "번 레인 " + name + "님입니다!!", RESULT);
		clearance();
	}

	private void clearance() { // 게임 마무리 작업
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {}

		for (int i = 0; i < veteran; i++)
			racer.get(i).kill();
		fireRacer();
	}
}
