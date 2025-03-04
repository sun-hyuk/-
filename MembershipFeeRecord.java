package Project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Project.BoardGamePageAWT.BackgroundPanel;

import java.sql.*;

public class MembershipFeeRecord extends JFrame implements ActionListener {

	JButton backButton, homeButton, notificationButton, paymentButton, myPageButton;
	JPanel topPanel, topRightPanel, buttonGroup, bottomMenu, titlePanel, separatorContainer, dividerPanel,
			feeRecordPanel;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
	private JLabel totalAssetLabel;

	// ← 추가된 필드: 이전 프레임 및 위치 정보
	private JFrame previousFrame;
	private Point previousLocation;

	// 커스텀 배경 패널 클래스 추가
	class BackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			// 안티앨리어싱 설정
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			// 기본 배경색 설정
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, w, h);

			// 보드게임 패턴 그리기
			drawGamePattern(g2d, w, h);

			// 반투명 오버레이 추가
			g2d.setColor(new Color(255, 255, 255, 180));
			g2d.fillRect(0, 0, w, h);
		}

		private void drawGamePattern(Graphics2D g2d, int width, int height) {
			int patternSize = 150;
			for (int x = 25; x < width; x += patternSize) {
				for (int y = 0; y < height; y += patternSize) {
					// 주사위 그리기
					drawDice(g2d, x + 20, y + 20);

					// 체스말 그리기
					if (((x - 25) + y) % (patternSize * 2) == 0) {
						drawChessPiece(g2d, x + 80, y + 40);
					}

					// 보드게임 말 그리기
					if (((x - 25) + y) % (patternSize * 2) == patternSize) {
						drawGamePiece(g2d, x + 40, y + 80);
					}
				}
			}
		}

		private void drawDice(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(220, 220, 220));
			g2d.fillRoundRect(x, y, 40, 40, 10, 10);
			g2d.setColor(new Color(180, 180, 180));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRoundRect(x, y, 40, 40, 10, 10);

			// 주사위 점 그리기
			g2d.setColor(new Color(150, 150, 150));
			int dotSize = 6;
			// 중앙 점
			g2d.fillOval(x + 17, y + 17, dotSize, dotSize);
			// 모서리 점들
			g2d.fillOval(x + 8, y + 8, dotSize, dotSize);
			g2d.fillOval(x + 26, y + 8, dotSize, dotSize);
			g2d.fillOval(x + 8, y + 26, dotSize, dotSize);
			g2d.fillOval(x + 26, y + 26, dotSize, dotSize);
		}

		private void drawChessPiece(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(200, 200, 200));
			// 체스말 (폰) 실루엣
			int[] xPoints = { x, x + 20, x + 16, x + 12, x + 8, x + 4 };
			int[] yPoints = { y + 30, y + 30, y + 20, y + 10, y + 20, y + 30 };
			g2d.fillPolygon(xPoints, yPoints, 6);
			g2d.setColor(new Color(180, 180, 180));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 6);
		}

		private void drawGamePiece(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(210, 210, 210));
			// 미플 형태의 게임말
			int[] xPoints = { x, x + 20, x + 40, x + 30, x + 10 };
			int[] yPoints = { y + 30, y, y + 30, y + 45, y + 45 };
			g2d.fillPolygon(xPoints, yPoints, 5);
			g2d.setColor(new Color(180, 180, 180));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 5);
		}
	}

	// 기본 생성자
	public MembershipFeeRecord() {
		this(null, null);
	}

	// 이전 프레임과 위치 정보를 받는 생성자
	public MembershipFeeRecord(JFrame previousFrame, Point previousLocation) {
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;

		setTitle("회비 내역");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		// getContentPane().setBackground(Color.WHITE);

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// 상단 패널
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		// 뒤로가기 버튼
		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		// 제목 (단순히 표시만, 클릭 시 이동 없음)
		titleLabel = new JLabel("회비내역", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		// 뒤로가기 버튼과 제목을 감싸는 패널
		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);
		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃(텍스트 버튼)
		logoutLabel = new JLabel("로그아웃");
		logoutLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				dispose(); // 현재 창 닫기
				new LoginAWT(); // 로그인 페이지로 이동
			}
		});

		// 메뉴(텍스트 버튼)
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, MembershipFeeRecord.this); // 팝업 메뉴 호출
			}
		});

		spaceLabel = new JLabel("  "); // 공백 추가

		// 로그아웃, 메뉴 버튼 그룹화
		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 버튼 그룹 패널
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60)); // 오른쪽에서 60px 띄우기
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		// topRightPanel을 먼저 생성하여 NullPointerException 방지
		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false);
		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 경계를 구분하는 1px 높이의 밝은 회색 선
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true); // JLabel을 배경색으로 채움
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2)); // 높이 1px

		// 선을 감싸는 패널 (가운데 정렬)
		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);
		topPanel.add(separatorContainer, BorderLayout.SOUTH); // topPanel 내부에 추가

		add(topPanel, BorderLayout.NORTH);

		// 회비 내역 패널 추가
		feeRecordPanel = new JPanel();
		feeRecordPanel.setLayout(new BoxLayout(feeRecordPanel, BoxLayout.Y_AXIS)); // 수직으로 배치
		feeRecordPanel.setOpaque(false);
		feeRecordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// totalAssetLabel을 중앙에 배치하기 위해 FlowLayout.CENTER 사용
		JPanel totalAssetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		totalAssetPanel.setOpaque(false);
		totalAssetPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 115));
		feeRecordPanel.add(totalAssetPanel);

		// 총 자산과 항목 사이에 구분선 추가 (폭 500, 높이 1)
		feeRecordPanel.add(createDivider(500, 1, 0, 5));
		add(feeRecordPanel, BorderLayout.CENTER);

		// 하단 메뉴
		bottomMenu = new JPanel(new GridLayout(1, 4, 5, 5));
		bottomMenu.setBorder(BorderFactory.createEmptyBorder(30, 70, 15, 70));
		bottomMenu.setOpaque(false);
		homeButton = new JButton("홈");
		notificationButton = new JButton("알림");
		paymentButton = new JButton("회비내역");
		myPageButton = new JButton("마이페이지");

		// 버튼 스타일 적용
		Font menuFont = new Font("SansSerif", Font.BOLD, 16);
		JButton[] buttons = { homeButton, notificationButton, paymentButton, myPageButton };

		for (JButton button : buttons) {
			button.setFont(menuFont);
			button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양
			button.setFocusPainted(false); // 버튼 클릭 후 테두리 제거
			button.setContentAreaFilled(false); // 버튼 배경 제거
			button.setBorderPainted(false); // 버튼 테두리 제거
			button.addActionListener(this);
			bottomMenu.add(button); // 패널에 추가
		}

		// 창에 모든 패널 추가
		add(bottomMenu, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);

		updateTotalAssets(); // 화면이 초기화될 때 총 자산 업데이트
	}

	// 위쪽과 아래쪽 gap을 모두 받는 구분선(divider)을 생성하는 메소드
	public JPanel createDivider(int width, int height, int topGap, int bottomGap) {
		JPanel divider = new JPanel();
		divider.setBackground(Color.LIGHT_GRAY);
		divider.setMaximumSize(new Dimension(width, height));
		divider.setPreferredSize(new Dimension(width, height));
		divider.setAlignmentX(Component.CENTER_ALIGNMENT);
		divider.setBorder(BorderFactory.createEmptyBorder(topGap, 0, bottomGap, 0));
		return divider;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton || e.getSource() == homeButton) {
			dispose();
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MainPageAWT(null, null);
			}
		} else if (e.getSource() == notificationButton) {
			Point currentLocation = this.getLocation();
			new NotificationPageAWT(this, currentLocation);
			this.dispose();
		} else if (e.getSource() == paymentButton) {
			Point currentLocation = this.getLocation();
			new MembershipFeeRecord(this, currentLocation);
			this.dispose();
		} else if (e.getSource() == myPageButton) {
			Point currentLocation = this.getLocation();
			new MyPageAWT(this, currentLocation);
			this.dispose();
		}
	}

	public void updateTotalAssets() {
		FeeMgr feeMgr = new FeeMgr();
		FeeRecordBean feeRecord = feeMgr.getFeeRecords();

		int totalDeposit = feeRecord.getTotalDeposit();
		int totalExpenditure = feeRecord.getTotalExpenditure();
		int totalAssets = totalDeposit - totalExpenditure;

		// 숫자에 천 단위 구분기호 추가
		String formattedTotalAssets = String.format("%,d", totalAssets);

		feeRecordPanel.removeAll(); // 기존 라벨들 제거
		// 총 회비내역 갱신
		addTotalFeeItem("총 회비내역", formattedTotalAssets + "원");
		feeRecordPanel.add(createDivider(500, 1, 0, 5));

		// 최신 입금 내역 추가
		int latestDeposit = feeRecord.getLatestDepositAmount();
		String formattedDeposit = String.format("%,d", latestDeposit);
		addFeeItem("최신 입금", "+" + formattedDeposit + " 원", Color.BLUE);
		feeRecordPanel.add(createDivider(500, 1, 0, 5));

		// 최신 지출 내역 추가
		int latestExpenditure = feeRecord.getLatestExpenditureAmount();
		String formattedExpenditure = String.format("%,d", latestExpenditure);
		String latestExpenditureNote = feeRecord.getLatestExpenditureNote();
		addFeeItem("지출 내역 (" + latestExpenditureNote + ")", "-" + formattedExpenditure + " 원", Color.RED);
		feeRecordPanel.add(createDivider(500, 1, 0, 5));

		feeRecordPanel.revalidate();
		feeRecordPanel.repaint();
	}

	// 항목 하나를 생성하는 헬퍼 메소드 (왼쪽: 항목명, 오른쪽: 금액)
	private void addFeeItem(String itemName, String amount, Color amountColor) {
		JPanel feeItemPanel = new JPanel(new BorderLayout());
		feeItemPanel.setOpaque(false);
		feeItemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		JLabel itemLabel = new JLabel(itemName);
		itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		itemLabel.setHorizontalAlignment(SwingConstants.LEFT);
		itemLabel.setBorder(BorderFactory.createEmptyBorder(10, 140, 10, 0));

		JLabel amountLabel = new JLabel(amount);
		amountLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		amountLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 140));
		amountLabel.setForeground(amountColor);

		feeItemPanel.add(itemLabel, BorderLayout.WEST);
		feeItemPanel.add(amountLabel, BorderLayout.EAST);
		feeRecordPanel.add(feeItemPanel);
	}

	// 총 회비내역 전용 헬퍼 메소드
	private void addTotalFeeItem(String itemName, String amount) {
		JPanel feeItemPanel = new JPanel(new BorderLayout());
		feeItemPanel.setOpaque(false);
		// 여백을 좀 더 크게 적용하여 강조
		feeItemPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

		JLabel itemLabel = new JLabel(itemName);
		// 굵은 폰트와 크기를 크게 설정하여 강조
		itemLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		itemLabel.setHorizontalAlignment(SwingConstants.LEFT);
		itemLabel.setBorder(BorderFactory.createEmptyBorder(10, 230, 10, 10));

		JLabel amountLabel = new JLabel(amount);
		amountLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		amountLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 140));
		// 총 회비내역은 기본 색상으로 표시
		amountLabel.setForeground(Color.BLACK);

		feeItemPanel.add(itemLabel, BorderLayout.WEST);

		// 중앙에 10픽셀 폭의 빈 패널 추가하여 간격 확보
		JPanel gapPanel = new JPanel();
		gapPanel.setPreferredSize(new Dimension(10, 0));
		gapPanel.setOpaque(false);
		feeItemPanel.add(gapPanel, BorderLayout.CENTER);
		feeItemPanel.add(amountLabel, BorderLayout.EAST);
		feeRecordPanel.add(feeItemPanel);
	}

	public static void main(String[] args) {
		new MembershipFeeRecord();
	}
}
