package Project;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

public class MainPageAWT extends JFrame implements ActionListener {
	JButton boardGameButton, rankingButton, surveyButton, homeButton, notificationButton, paymentButton, myPageButton;
	JFrame frame;
	JPanel topPanel, topRightPanel, buttonGroup, mainPanel, introPanel, introContentPanel, buttonPanel, noticePanel,
			noticeContentPanel, boardPanel, boardContentPanel, bottomMenu;
	JLabel titleLabel, logoutLabel, menuLabel, introLabel, noticeLabel, boardLabel, spaceLabel;
	private int index;
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

	// 기존 기본 생성자
	public MainPageAWT() {
		this(null, null);
	}

	public MainPageAWT(JFrame previousFrame, Point previousLocation) {
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;

		// 기존 LookAndFeel 설정 백업
		String currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		frame = new JFrame("체크메이트");
		frame.setSize(780, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		frame.setContentPane(backgroundPanel);

		// ============= 상단 패널 설정 =============
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false); // 배경 투명하게 설정
		titleLabel = new JLabel("체크메이트");
		titleLabel.setFont(FontUtil.getGoogleFont(60f));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 40, -15, 40));
		topPanel.add(titleLabel, BorderLayout.WEST);

		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false); // 배경 투명하게 설정

		logoutLabel = new JLabel("로그아웃");
		logoutLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				frame.dispose();
				new LoginAWT();
			}
		});

		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, frame);
			}
		});

		spaceLabel = new JLabel("  ");

		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setOpaque(false); // 배경 투명하게 설정
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 40));
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		frame.add(topPanel, BorderLayout.NORTH);

		// ============= 메인 패널 =============
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 15, 40));
		mainPanel.setOpaque(false); // 배경 투명하게 설정

		// ------- 동아리 소개 섹션 -------
		introPanel = new JPanel(new BorderLayout());
		introPanel.setOpaque(false); // 배경 투명하게 설정

		JPanel introTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		introTitlePanel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 5, 0));
		introTitlePanel.setOpaque(false); // 배경 투명하게 설정
		introLabel = new JLabel("동아리 소개");
		introLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		introTitlePanel.add(introLabel);
		introPanel.add(introTitlePanel, BorderLayout.NORTH);
		introPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

		introContentPanel = new JPanel(new BorderLayout());
		introContentPanel.setBackground(new Color(245, 245, 245, 220)); // 약간의 투명도 추가
		introContentPanel.setBorder(new RoundedBorder(8, new Color(230, 230, 230), 1));

		JPanel introContentInnerPanel = new JPanel(new BorderLayout(10, 0));
		introContentInnerPanel.setOpaque(false); // 배경 투명하게 설정
		introContentInnerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		ImageIcon boardGameIcon = createImageIcon("boardgame_club.jpg", 100, 100);
		if (boardGameIcon == null) {
			boardGameIcon = createPlaceholderIcon(70, 70, "보드게임");
		}
		JLabel imageLabel = new JLabel(boardGameIcon);

		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.setOpaque(false); // 배경 투명하게 설정
		JLabel introContentLabel = new JLabel(
				"<html><div style='width:350px'>" + "<p style='margin:0'>보드게임을 사랑하는 사람들이 모여 즐거운 시간을 함께하는 동아리입니다.</p>"
						+ "<p style='margin-top:3px'>매주 금요일 저녁 7시에 모여 다양한 보드게임을 즐기고 있습니다.</p>"
						+ "<p style='margin-top:3px'>초보자부터 전문가까지 누구나 환영합니다!</p>" + "</div></html>");
		introContentLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
		textPanel.add(introContentLabel, BorderLayout.CENTER);

		introContentInnerPanel.add(imageLabel, BorderLayout.WEST);
		introContentInnerPanel.add(textPanel, BorderLayout.CENTER);
		introContentPanel.add(introContentInnerPanel, BorderLayout.CENTER);

		introPanel.add(introContentPanel, BorderLayout.CENTER);
		mainPanel.add(introPanel);

		// ------- 버튼 그룹 섹션 -------
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		buttonPanel.setOpaque(false); // 배경 투명하게 설정
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

		boardGameButton = createIconButton1("보드게임", new Color(52, 152, 219), "game_icon.jpg");
		rankingButton = createIconButton1("인기순위", new Color(155, 89, 182), "ranking_icon.jpg");
		surveyButton = createIconButton1("설문", new Color(46, 204, 113), "survey_icon.jpg");

		boardGameButton.addActionListener(this);
		rankingButton.addActionListener(this);
		surveyButton.addActionListener(this);

		buttonPanel.add(boardGameButton);
		buttonPanel.add(rankingButton);
		buttonPanel.add(surveyButton);
		mainPanel.add(buttonPanel);

		// ------- 공지사항 섹션 -------
		noticePanel = new JPanel(new BorderLayout());
		noticePanel.setOpaque(false); // 배경 투명하게 설정
		noticePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));

		JPanel noticeTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		noticeTitlePanel.setOpaque(false); // 배경 투명하게 설정
		noticeLabel = new JLabel("공지사항 >");
		noticeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		noticeLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		noticeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		noticeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
				Point currentLocation = frame.getLocation();
				new NoticePageAWT(frame, currentLocation);
			}
		});
		noticeTitlePanel.add(noticeLabel);
		noticePanel.add(noticeTitlePanel, BorderLayout.NORTH);

		noticeContentPanel = new JPanel();
		noticeContentPanel.setLayout(new BoxLayout(noticeContentPanel, BoxLayout.Y_AXIS));
		noticeContentPanel.setBackground(new Color(245, 245, 245, 220)); // 약간의 투명도 추가
		noticeContentPanel.setBorder(new RoundedBorder(8, new Color(230, 230, 230), 1));

		updateNoticeContentPanel();

		noticePanel.add(noticeContentPanel, BorderLayout.CENTER);
		mainPanel.add(noticePanel);

		// ------- 자유게시판 섹션 -------
		boardPanel = new JPanel(new BorderLayout());
		boardPanel.setOpaque(false); // 배경 투명하게 설정
		boardPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 3, 0));

		JPanel boardTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		boardTitlePanel.setOpaque(false); // 배경 투명하게 설정
		boardLabel = new JLabel("자유게시판 >");
		boardLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
		boardLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		boardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		boardLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
				new BoardPageAWT(frame);
			}
		});
		boardTitlePanel.add(boardLabel);
		boardPanel.add(boardTitlePanel, BorderLayout.NORTH);

		boardContentPanel = new JPanel();
		boardContentPanel.setLayout(new BoxLayout(boardContentPanel, BoxLayout.Y_AXIS));
		boardContentPanel.setBackground(new Color(245, 245, 245, 220)); // 약간의 투명도 추가
		boardContentPanel.setBorder(new RoundedBorder(8, new Color(230, 230, 230), 1));

		updateBoardContentPanel();

		boardPanel.add(boardContentPanel, BorderLayout.CENTER);
		mainPanel.add(boardPanel);

		frame.add(mainPanel, BorderLayout.CENTER);

		// ============= 하단 메뉴 =============
		bottomMenu = new JPanel(new GridLayout(1, 4, 5, 5));
		bottomMenu.setBorder(BorderFactory.createEmptyBorder(30, 70, 15, 70));
		bottomMenu.setOpaque(false); // 배경 투명하게 설정
		homeButton = new JButton("홈");
		notificationButton = new JButton("알림");
		paymentButton = new JButton("회비내역");
		myPageButton = new JButton("마이페이지");

		Font menuFont = new Font("SansSerif", Font.BOLD, 16);
		JButton[] buttons = { homeButton, notificationButton, paymentButton, myPageButton };

		for (JButton button : buttons) {
			button.setFont(menuFont);
			button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			button.setFocusPainted(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.addActionListener(this);
			bottomMenu.add(button);
		}

		frame.add(bottomMenu, BorderLayout.SOUTH);

		// 이전 프레임의 위치가 있다면 해당 위치에 프레임 생성
		if (previousLocation != null) {
			frame.setLocation(previousLocation);
		} else {
			frame.setLocationRelativeTo(null);
		}

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// 종료 후 기본 LookAndFeel로 복원
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(currentLookAndFeel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void updateNoticeContentPanel() {
		noticeContentPanel.removeAll();

		NoticeMgr noticeMgr = new NoticeMgr();
		ArrayList<NoticeBean> latestNotices = noticeMgr.getLatestNotices(2);

		int index = 0;
		for (NoticeBean notice : latestNotices) {
			JPanel noticeItemPanel = new JPanel();
			noticeItemPanel.setLayout(new BoxLayout(noticeItemPanel, BoxLayout.Y_AXIS));
			noticeItemPanel.setOpaque(false); // 배경 투명하게 설정

			JLabel noticeLabel = new JLabel("<html>" + notice.getNotice_Title() + "</html>");
			noticeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
			noticeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			noticeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			noticeLabel.setHorizontalAlignment(SwingConstants.LEFT);

			noticeLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					frame.dispose();
					new NoticeDetailPageAWT(notice.getNotice_Id());
				}
			});

			noticeItemPanel.add(noticeLabel);

			if (index < latestNotices.size() - 1) {
				JSeparator separator = new JSeparator();
				separator.setPreferredSize(new Dimension(300, 1));
				separator.setAlignmentX(Component.CENTER_ALIGNMENT);
				noticeItemPanel.add(separator);
			}

			noticeContentPanel.add(noticeItemPanel);
			index++;
		}

		if (latestNotices.isEmpty()) {
			JLabel emptyLabel = new JLabel("등록된 공지사항이 없습니다.");
			emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
			noticeContentPanel.add(emptyLabel);
		}

		noticeContentPanel.revalidate();
		noticeContentPanel.repaint();
	}

	private void updateBoardContentPanel() {
		boardContentPanel.removeAll();

		BoardMgr boardMgr = new BoardMgr();
		boardMgr.loadBoardsFromDB();

		ArrayList<BoardBean> latestBoards = new ArrayList<>();
		ArrayList<BoardBean> allBoards = boardMgr.getBoards();

		for (int i = 0; i < Math.min(2, allBoards.size()); i++) {
			latestBoards.add(allBoards.get(i));
		}

		int index = 0;
		for (BoardBean board : latestBoards) {
			JPanel boardItemPanel = new JPanel();
			boardItemPanel.setLayout(new BoxLayout(boardItemPanel, BoxLayout.Y_AXIS));
			boardItemPanel.setOpaque(false); // 배경 투명하게 설정

			JLabel boardLabel = new JLabel("<html>" + board.getBoard_Title() + "</html>");
			boardLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
			boardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			boardLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			boardLabel.setHorizontalAlignment(SwingConstants.LEFT);

			boardLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					frame.dispose();
					new BoardDetailPageAWT(board.getBoard_Id());

				}
			});

			boardItemPanel.add(boardLabel);

			if (index < latestBoards.size() - 1) {
				JSeparator separator = new JSeparator();
				separator.setPreferredSize(new Dimension(300, 1));
				separator.setAlignmentX(Component.CENTER_ALIGNMENT);
				boardItemPanel.add(separator);
			}

			boardContentPanel.add(boardItemPanel);
			index++;
		}

		if (latestBoards.isEmpty()) {
			JLabel emptyLabel = new JLabel("등록된 게시물이 없습니다.");
			emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
			boardContentPanel.add(emptyLabel);
		}

		boardContentPanel.revalidate();
		boardContentPanel.repaint();
	}

	private JButton createIconButton1(String text, Color color, String iconPath) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setBackground(color);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(130, 40));

		ImageIcon icon = createImageIcon(iconPath, 16, 16);
		if (icon != null) {
			button.setIcon(icon); // 아이콘을 버튼에 설정
			button.setIconTextGap(8); // 아이콘과 텍스트 사이 간격 설정
			// 버튼 내 아이콘과 텍스트 전체를 중앙에 정렬
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setVerticalAlignment(SwingConstants.CENTER);
			// 아이콘은 왼쪽, 텍스트는 오른쪽에 배치하여 수평 정렬
			button.setHorizontalTextPosition(SwingConstants.RIGHT);
			button.setVerticalTextPosition(SwingConstants.CENTER);
		}

		button.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 7));
		button.setUI(new CustomButtonUI(8, color));

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(darken(color, 0.1f));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(color);
			}
		});
		return button;
	}

	private Color darken(Color color, float factor) {
		int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
		int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
		int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
		return new Color(r, g, b);
	}

	private ImageIcon createImageIcon(String path, int width, int height) {
		try {
			ImageIcon imageIcon = new ImageIcon(getClass().getResource(path));
			Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(image);
		} catch (Exception e) {
			return null;
		}
	}

	private ImageIcon createPlaceholderIcon(int width, int height, String text) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		g2d.setColor(new Color(200, 200, 200));
		g2d.fillRect(0, 0, width, height);

		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

		FontMetrics metrics = g2d.getFontMetrics();
		int x = (width - metrics.stringWidth(text)) / 2;
		int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();

		g2d.drawString(text, x, y);
		g2d.dispose();

		return new ImageIcon(image);
	}

	class RoundedBorder extends AbstractBorder {
		private int radius;
		private Color color;
		private int thickness;

		public RoundedBorder(int radius, Color color, int thickness) {
			this.radius = radius;
			this.color = color;
			this.thickness = thickness;
		}

		public RoundedBorder(int radius, Color color) {
			this(radius, color, 1);
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(thickness));
			g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
			g2d.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
		}

		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			insets.left = insets.top = insets.right = insets.bottom = radius / 2;
			return insets;
		}
	}

	class CustomButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
		private int radius;

		private Color color;

		public CustomButtonUI(int radius, Color color) {
			this.radius = radius;
			this.color = color;
		}

		@Override
		public void paint(Graphics g, JComponent c) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			AbstractButton button = (AbstractButton) c;
			ButtonModel model = button.getModel();

			int width = button.getWidth();
			int height = button.getHeight();

			if (model.isPressed()) {
				g2d.setColor(darken(button.getBackground(), 0.2f));
			} else {
				g2d.setColor(button.getBackground());
			}

			g2d.fill(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));

			super.paint(g, c);
			g2d.dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == boardGameButton) { // 보드게임 페이지로 이동
			frame.dispose();
			Point currentLocation = frame.getLocation();
			new BoardGamePageAWT(); // 이전 프레임과 위치 전달
		} else if (e.getSource() == rankingButton) { // 인기순위 페이지로 이동
			frame.dispose();
			Point currentLocation = frame.getLocation();
			new BoardGameRankingPageAWT();
		} else if (e.getSource() == surveyButton) { // 설문 페이지로 이동
			frame.dispose();
			new SurveyPageAWT();
		} else if (e.getSource() == homeButton) { // 홈 페이지로 이동
			frame.dispose();
			Point currentLocation = frame.getLocation();
			new MainPageAWT(frame, currentLocation);
		} else if (e.getSource() == notificationButton) { // 알림 페이지로 이동
			frame.dispose();
			Point currentLocation = frame.getLocation();
			new NotificationPageAWT(frame, currentLocation);
		} else if (e.getSource() == paymentButton) { // 회비내역 페이지로 이동
			frame.dispose();
			Point currentLocation = frame.getLocation();
			new MembershipFeeRecord(frame, currentLocation);
		} else if (e.getSource() == myPageButton) { // 마이페이지로 이동
			frame.dispose();
			Point currentLocation = frame.getLocation();
			new MyPageAWT(frame, currentLocation);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainPageAWT();
		});
	}
}