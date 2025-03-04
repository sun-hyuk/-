package Project;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import Project.BoardPageAWT.BackgroundPanel;

public class NotificationPageAWT extends JFrame implements ActionListener {
	JButton backButton;
	JPanel contentPanel, topPanel, topRightPanel, titlePanel, separatorContainer, buttonGroup;
	JLabel titleLabel, separatorLabel, logoutLabel, spaceLabel, menuLabel;
	NotificationMgr notificationMgr;

	int currentPage = 1;
	int notificationsPerPage = 6;
	private ArrayList<NotificationBean> notifications;
	private Set<Integer> dismissedNotificationIds = new HashSet<>();
	private static final String DISMISSED_FILE = "dismissed_notifications.txt";

	// 추가: 현재 로그인한 사용자 ID를 저장하는 필드
	private String currentUserId;

	// ← 추가된 필드: 이전 프레임과 위치 정보
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

	// 기본 생성자: 독립 실행 시
	public NotificationPageAWT() {
		this(null, null);
	}

	// 이전 페이지 정보를 받는 생성자 (네비게이션 용)
	public NotificationPageAWT(JFrame previousFrame, Point previousLocation) {
		// 로그인 여부 확인: 로그인한 관리자인지 또는 회원인지 확인
		if (UserSession.getInstance().getCurrentAdmin() == null && UserSession.getInstance().getCurrentUser() == null) {
			JOptionPane.showMessageDialog(this, "로그인 후 이용해주세요.", "로그인 필요", JOptionPane.WARNING_MESSAGE);
			dispose();
			new LoginAWT();
			return;
		}

		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;

		setTitle("알림");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		notificationMgr = new NotificationMgr();

		// 상단 패널
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false); // 배경 투명하게 설정

		// 뒤로가기 버튼
		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		// 제목
		titleLabel = new JLabel("알림", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false); // 배경 투명하게 설정
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
				dispose();
				new LoginAWT();
			}
		});

		// 메뉴(텍스트 버튼)
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, NotificationPageAWT.this);
			}
		});

		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false); // 배경 투명하게 설정

		spaceLabel = new JLabel("  ");

		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false); // 배경 투명하게 설정
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false); // 배경 투명하게 설정
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setOpaque(false); // 배경 투명하게 설정
		northPanel.add(topPanel);
		add(northPanel, BorderLayout.NORTH);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false); // 배경 투명하게 설정
		contentPanel.setBorder(new EmptyBorder(0, 20, 20, 15));

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setPreferredSize(new Dimension(780, 400));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
		scrollPane.setOpaque(false); // 배경 투명하게 설정
		scrollPane.getViewport().setOpaque(false);
		add(scrollPane, BorderLayout.CENTER);

		loadDismissedNotifications();
		loadNotifications();

		setLocationRelativeTo(null);
		setVisible(true);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JScrollBar vertical = scrollPane.getVerticalScrollBar();
				vertical.setValue(0);
			}
		});

		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
			JScrollBar vertical = (JScrollBar) e.getSource();
			if (vertical.getValue() + vertical.getVisibleAmount() >= vertical.getMaximum()) {
				loadMoreNotifications();
			}
		});
	}

	private void loadDismissedNotifications() {
		File file = new File(DISMISSED_FILE);
		if (!file.exists()) {
			return;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					dismissedNotificationIds.add(Integer.parseInt(line.trim()));
				} catch (NumberFormatException e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveDismissedNotifications() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(DISMISSED_FILE))) {
			for (Integer id : dismissedNotificationIds) {
				bw.write(String.valueOf(id));
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	// 헬퍼 메서드: 현재 로그인한 사용자 ID 반환 (회원이면 member_id, 관리자면 manager_id)
//    private String getCurrentUserId() {
//        if (UserSession.getInstance().getCurrentUser() != null) {
//            return UserSession.getInstance().getCurrentUser().getMember_Id();
//        } else if (UserSession.getInstance().getCurrentAdmin() != null) {
//            return UserSession.getInstance().getCurrentAdmin().getManager_Id();
//        }
//        return null;
//    }

	private void loadNotifications() {
		contentPanel.removeAll();

		// 현재 로그인한 사용자 ID 결정 (회원: member_id, 관리자: manager_id)
		currentUserId = "";
		if (UserSession.getInstance().getCurrentUser() != null) {
			currentUserId = UserSession.getInstance().getCurrentUser().getMember_Id();
		} else if (UserSession.getInstance().getCurrentAdmin() != null) {
			currentUserId = UserSession.getInstance().getCurrentAdmin().getManager_Id();
		}
		if (currentUserId != null && !currentUserId.isEmpty()) {
			notifications = notificationMgr.getNotificationsForMember(currentUserId);
		} else {
			notifications = new ArrayList<>();
		}

		ArrayList<NotificationBean> filteredNotifications = new ArrayList<>();
		for (NotificationBean n : notifications) {
			if (!dismissedNotificationIds.contains(n.getNoticeId())) {
				filteredNotifications.add(n);
			}
		}

		int totalNotifications = filteredNotifications.size();
		int end = Math.min(notificationsPerPage, totalNotifications);

		for (int i = 0; i < end; i++) {
			addNotification(filteredNotifications.get(i));
		}

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private void loadMoreNotifications() {
		notificationsPerPage += 6;
		loadNotifications();
	}

	private void addNotification(NotificationBean notification) {
		JPanel notificationPanel = new JPanel();
		notificationPanel.setLayout(new BorderLayout());
		notificationPanel.setBackground(new Color(245, 245, 245));
		notificationPanel.setPreferredSize(new Dimension(550, 90));
		notificationPanel.setMaximumSize(new Dimension(550, 90)); // 최대 크기도 고정

		notificationPanel.setBorder(
				new CompoundBorder(new LineBorder(new Color(230, 230, 230), 1, true), new EmptyBorder(10, 15, 10, 15)));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBackground(new Color(245, 245, 245));
		leftPanel.setBorder(new EmptyBorder(3, 0, 3, 0));

		JLabel dateLabel = new JLabel(notification.getCreatedAt());
		dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		dateLabel.setForeground(new Color(100, 100, 100));

		JLabel messageLabel = new JLabel("<html>" + notification.getTitle() + "</html>");
		messageLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		messageLabel.setForeground(new Color(50, 50, 50));
		messageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		final NotificationBean notif = notification; // for inner class
		messageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if (!notif.isRead()) {
						notificationMgr.markNotificationAsRead(currentUserId, notif.getNoticeId());
						notif.setRead(true);
					}
					loadNotifications();
					Point currentLocation = NotificationPageAWT.this.getLocation();
					new NoticeDetailPageAWT(notif.getNoticeId(), NotificationPageAWT.this, currentLocation);
					NotificationPageAWT.this.setVisible(false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		leftPanel.add(dateLabel);
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(messageLabel);

		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		rightPanel.setBackground(new Color(245, 245, 245));

		if (!notification.isRead()) {
			JLabel redDot = new JLabel("●");
			redDot.setFont(new Font("SansSerif", Font.BOLD, 10));
			redDot.setForeground(new Color(255, 59, 48));
			rightPanel.add(redDot);
		}

		JButton deleteButton = new JButton("×");
		deleteButton.setFont(new Font("SansSerif", Font.BOLD, 16));
		deleteButton.setForeground(new Color(150, 150, 150));
		deleteButton.setBorderPainted(false);
		deleteButton.setContentAreaFilled(false);
		deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		deleteButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				deleteButton.setForeground(new Color(255, 59, 48));
			}

			public void mouseExited(MouseEvent e) {
				deleteButton.setForeground(new Color(150, 150, 150));
			}
		});
		deleteButton.addActionListener(e -> {
			boolean success = notificationMgr.deleteNotificationForMember(currentUserId, notification.getNoticeId());
			if (success) {
				loadNotifications();
			} else {
				JOptionPane.showMessageDialog(NotificationPageAWT.this, "알림 삭제에 실패했습니다.", "오류",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		rightPanel.add(deleteButton);

		notificationPanel.add(leftPanel, BorderLayout.CENTER);
		notificationPanel.add(rightPanel, BorderLayout.EAST);

		contentPanel.add(notificationPanel);
		contentPanel.add(Box.createVerticalStrut(8));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			// 이전 프레임과 위치 정보가 있다면 복원
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MainPageAWT();
			}
		}
	}

	public static void main(String[] args) {
		new NotificationPageAWT();
	}
}