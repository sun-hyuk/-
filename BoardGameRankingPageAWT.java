package Project;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BoardGameRankingPageAWT extends JFrame implements ActionListener {
	private Connection conn;
	private JPanel mainPanel, contentPanel;
	private JPanel topPanel, titlePanel, topRightPanel, buttonGroup;
	private JButton backButton;
	private JLabel titleLabel, logoutLabel, menuLabel, spaceLabel;
	private JScrollPane scrollPane;

	private static final String DB_URL = "jdbc:mysql://localhost:3306/project?characterEncoding=UTF-8&serverTimezone=UTC";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "1234";

	// 클래스 멤버 변수 추가
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
	public BoardGameRankingPageAWT() {
		this(null, null);
	}

	public BoardGameRankingPageAWT(JFrame previousFrame, Point previousLocation) {
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;
		initializeDatabase();
		initializeUI();
		loadRankings();
	}

	private void initializeDatabase() {
		try {
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			System.out.println("Database connected successfully!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initializeUI() {
		setTitle("인기순위");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// 상단 패널
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

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
		titleLabel = new JLabel("인기순위", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		// 뒤로가기 버튼과 제목을 감싸는 패널
		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);
		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃 버튼
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

		// 메뉴 버튼
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, BoardGameRankingPageAWT.this);
			}
		});

		// 상단 오른쪽 패널 구성
		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false);
		spaceLabel = new JLabel(" ");

		// 버튼 그룹 패널
		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 개선된 컬럼 헤더 패널 - 구분선 제거, 너비 고정
		JPanel columnHeaderPanel = new JPanel();
		columnHeaderPanel.setLayout(new BoxLayout(columnHeaderPanel, BoxLayout.X_AXIS));
		columnHeaderPanel.setOpaque(false);
		columnHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		// 헤더 라벨 생성 - 너비 고정
		JLabel rankHeader = createHeaderLabel("순위");
		rankHeader.setPreferredSize(new Dimension(200, 30));
		rankHeader.setMinimumSize(new Dimension(200, 30));
		rankHeader.setMaximumSize(new Dimension(200, 30));

		JLabel gameHeader = createHeaderLabel("게임 정보");
		gameHeader.setPreferredSize(new Dimension(300, 30));
		gameHeader.setMinimumSize(new Dimension(300, 30));
		gameHeader.setMaximumSize(new Dimension(300, 30));

		JLabel genreHeader = createHeaderLabel("장르");
		genreHeader.setPreferredSize(new Dimension(215, 30));
		genreHeader.setMinimumSize(new Dimension(215, 30));
		genreHeader.setMaximumSize(new Dimension(215, 30));

		columnHeaderPanel.add(rankHeader);
		columnHeaderPanel.add(gameHeader);
		columnHeaderPanel.add(genreHeader);

		// 개선된 Content Panel
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		// 기존 코드 부분 (initializeUI 메서드 내)
		scrollPane = new JScrollPane(contentPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		// 변경된 스크롤 스타일 적용 부분
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				// MemberListAWT의 BUTTON_COLOR: new Color(51, 51, 51)
				this.thumbColor = new Color(51, 51, 51);
				this.trackColor = Color.WHITE;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}
		});

		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
		scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(51, 51, 51);
				this.trackColor = Color.WHITE;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}
		});

		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		JPanel scrollWrapper = new JPanel(new BorderLayout());
		scrollWrapper.setOpaque(false);
		scrollWrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 12, 0));
		scrollWrapper.add(scrollPane, BorderLayout.CENTER);

		// 메인 패널
		mainPanel = new JPanel(new BorderLayout(0, 0));
		mainPanel.setOpaque(false);
		mainPanel.add(columnHeaderPanel, BorderLayout.NORTH);
		mainPanel.add(scrollWrapper, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// 헤더 라벨을 생성하는 메소드
	private JLabel createHeaderLabel(String text) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		label.setForeground(new Color(50, 50, 50));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 100, 100, 150)),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		return label;
	}

	private void loadRankings() {
		try {
			String query = "SELECT * FROM game ORDER BY ranking_points DESC";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			contentPanel.removeAll();
			int rank = 1;

			while (rs.next()) {
				JPanel gamePanel = createGamePanel(rank++, rs.getString("game_id"), rs.getString("category"),
						rs.getBytes("game_image"));
				contentPanel.add(gamePanel);
			}

			contentPanel.revalidate();
			contentPanel.repaint();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 항목 사이의 구분선 생성
	private JPanel createSeparator() {
		JPanel sepPanel = new JPanel(new BorderLayout());
		sepPanel.setOpaque(false);

		JLabel sep = new JLabel();
		sep.setPreferredSize(new Dimension(1, 1));
		sep.setOpaque(true);
		sep.setBackground(new Color(230, 230, 230));

		sepPanel.add(sep, BorderLayout.CENTER);
		sepPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

		return sepPanel;
	}

	private JPanel createGamePanel(int rank, String gameId, String genre, byte[] imageData) {
		// 게임 항목 패널 - 호버 효과 및 모서리 둥글게 적용
		JPanel panel = new JPanel() {
			// 호버 관련 변수
			private boolean isHovered = false;
			{
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						isHovered = true;
						repaint();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						isHovered = false;
						repaint();
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						dispose();
						new BoardGameDetailPageAWT(gameId, conn, BoardGameRankingPageAWT.this,
								BoardGameRankingPageAWT.this.getLocation());
					}
				});

				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int width = getWidth();
				int height = getHeight();
				int arc = 15; // 모서리 둥글기

				// 배경색 설정 (호버 상태에 따라 다르게)
				if (isHovered) {
					g2d.setColor(new Color(245, 245, 255, 220));
				} else {
					g2d.setColor(new Color(250, 250, 250, 180));
				}

				// 둥근 사각형 그리기
				g2d.fillRoundRect(0, 0, width, height, arc, arc);

				// 테두리 그리기
				g2d.setColor(new Color(220, 220, 220));
				g2d.setStroke(new BasicStroke(1));
				g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

				super.paintComponent(g);
			}
		};

		// BoxLayout 사용
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		panel.setPreferredSize(new Dimension(700, 100));
		panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));

		// 순위 표시 패널 - 중앙 정렬
		JPanel rankPanel = new JPanel();
		rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.Y_AXIS));
		rankPanel.setOpaque(false);
		rankPanel.setPreferredSize(new Dimension(200, 100));
		rankPanel.setMinimumSize(new Dimension(200, 100));
		rankPanel.setMaximumSize(new Dimension(200, 100));

		JLabel rankLabel = new JLabel(String.valueOf(rank));
		rankLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 순위에 따라 색상 다르게 표시
		if (rank == 1) {
			rankLabel.setForeground(new Color(212, 175, 55)); // 금색
		} else if (rank == 2) {
			rankLabel.setForeground(new Color(170, 169, 173)); // 은색
		} else if (rank == 3) {
			rankLabel.setForeground(new Color(176, 141, 87)); // 동색
		} else {
			rankLabel.setForeground(new Color(80, 80, 80));
		}

		// 공백 추가하여 세로 중앙에 배치
		rankPanel.add(Box.createVerticalGlue());
		rankPanel.add(rankLabel);
		rankPanel.add(Box.createVerticalGlue());

		panel.add(rankPanel);

		// 게임 정보 패널 - 왼쪽 정렬
		JPanel gameInfoPanel = new JPanel();
		gameInfoPanel.setLayout(new BoxLayout(gameInfoPanel, BoxLayout.Y_AXIS));
		gameInfoPanel.setOpaque(false);
		gameInfoPanel.setPreferredSize(new Dimension(300, 100));
		gameInfoPanel.setMinimumSize(new Dimension(300, 100));
		gameInfoPanel.setMaximumSize(new Dimension(300, 100));

		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));
		innerPanel.setOpaque(false);
		innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// 이미지 패널
		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.X_AXIS));
		imagePanel.setOpaque(false);
		imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

		JLabel imageLabel = new JLabel();
		if (imageData != null) {
			ImageIcon imageIcon = new ImageIcon(imageData);
			Image img = imageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
			imageLabel.setIcon(new ImageIcon(img));
		} else {
			// 기본 이미지 또는 대체 이미지 설정
			imageLabel.setPreferredSize(new Dimension(80, 80));
			imageLabel.setBackground(new Color(240, 240, 240));
			imageLabel.setOpaque(true);
			imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
			imageLabel.setText("No Image");
			imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}

		imagePanel.add(imageLabel);
		innerPanel.add(imagePanel);

		// 게임 이름 패널 - 왼쪽 정렬
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
		namePanel.setOpaque(false);

		JLabel gameIdLabel = new JLabel(gameId);
		gameIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		gameIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// 공백 추가하여 세로 중앙에 배치
		namePanel.add(Box.createVerticalGlue());
		namePanel.add(gameIdLabel);
		namePanel.add(Box.createVerticalGlue());

		innerPanel.add(namePanel);

		// 공백 추가하여 세로 중앙에 배치
		gameInfoPanel.add(Box.createVerticalGlue());
		gameInfoPanel.add(innerPanel);
		gameInfoPanel.add(Box.createVerticalGlue());

		panel.add(gameInfoPanel);

		// 장르 표시 패널 - 중앙 정렬
		JPanel genrePanel = new JPanel();
		genrePanel.setLayout(new BoxLayout(genrePanel, BoxLayout.Y_AXIS));
		genrePanel.setOpaque(false);
		genrePanel.setPreferredSize(new Dimension(200, 100));
		genrePanel.setMinimumSize(new Dimension(200, 100));
		genrePanel.setMaximumSize(new Dimension(200, 100));

		// 장르 라벨을 더 작은 태그 형태로 디자인
		JPanel tagPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setColor(new Color(220, 220, 240, 160));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				super.paintComponent(g);
			}
		};
		tagPanel.setOpaque(false);
		tagPanel.setLayout(new BorderLayout());

		JLabel genreLabel = new JLabel(genre, SwingConstants.CENTER);
		genreLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		genreLabel.setForeground(new Color(60, 60, 60));

		tagPanel.add(genreLabel, BorderLayout.CENTER);
		tagPanel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

		// 크기 제한 설정
		Dimension tagSize = new Dimension(100, 25);
		tagPanel.setPreferredSize(tagSize);
		tagPanel.setMaximumSize(tagSize);
		tagPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 공백 추가하여 세로 중앙에 배치
		genrePanel.add(Box.createVerticalGlue());
		genrePanel.add(tagPanel);
		genrePanel.add(Box.createVerticalGlue());

		panel.add(genrePanel);

		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MainPageAWT();
			}
		}
	}

	public static void main(String[] args) {
		// 폰트 렌더링 개선
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		SwingUtilities.invokeLater(() -> {
			new BoardGameRankingPageAWT();
		});
	}
}