package Project;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BoardGameDetailPageAWT extends JFrame implements ActionListener {
	private String gameId;
	private Connection conn;
	private JLabel gameImageLabel;
	private JButton backButton;

	private static final int IMAGE_WIDTH = 180;
	private static final int IMAGE_HEIGHT = 180;
	private static final Color ACCENT_COLOR = new Color(64, 123, 255);
	private static final Color TEXT_COLOR = new Color(50, 50, 50);

	// 인스턴스 변수로 UI 컴포넌트 선언 (loadGameDetails에서 사용)
	private JLabel gameTitleLabel;
	private JTextArea infoTextArea;
	private JTextArea gameWayText;

	private JFrame previousFrame;
	private Point previousLocation;
	// 이전 페이지 종류 ("ranking" 또는 "board")
	private String previousPageType;

	// 커스텀 배경 패널 클래스
	class BackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			// 그라데이션 배경 설정
			GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 250), 0, h, new Color(235, 240, 245));
			g2d.setPaint(gp);
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
			g2d.setColor(new Color(230, 230, 230));
			g2d.fillRoundRect(x, y, 40, 40, 10, 10);
			g2d.setColor(new Color(210, 210, 210));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRoundRect(x, y, 40, 40, 10, 10);

			// 주사위 점 그리기
			g2d.setColor(new Color(180, 180, 180));
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
			g2d.setColor(new Color(220, 220, 220));
			// 체스말 (폰) 실루엣
			int[] xPoints = { x, x + 20, x + 16, x + 12, x + 8, x + 4 };
			int[] yPoints = { y + 30, y + 30, y + 20, y + 10, y + 20, y + 30 };
			g2d.fillPolygon(xPoints, yPoints, 6);
			g2d.setColor(new Color(200, 200, 200));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 6);
		}

		private void drawGamePiece(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(225, 225, 225));
			// 미플 형태의 게임말
			int[] xPoints = { x, x + 20, x + 40, x + 30, x + 10 };
			int[] yPoints = { y + 30, y, y + 30, y + 45, y + 45 };
			g2d.fillPolygon(xPoints, yPoints, 5);
			g2d.setColor(new Color(205, 205, 205));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 5);
		}
	}

	// 둥근 모서리 패널
	class RoundedPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// 그림자 효과
			for (int i = 0; i < 4; i++) {
				g2d.setColor(new Color(0, 0, 0, 5));
				g2d.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 15, 15);
			}

			// 둥근 모서리의 흰색 배경
			g2d.setColor(Color.WHITE);
			g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

			g2d.dispose();
		}
	}

	// 기존 생성자 (이전 프레임 정보 없이 호출할 경우)
	public BoardGameDetailPageAWT(String gameId, Connection conn) {
		this.gameId = gameId;
		this.conn = conn;
		initializeUI();
		loadGameDetails();
	}

	// 이전 프레임과 위치 정보를 함께 전달받는 오버로딩 생성자
	public BoardGameDetailPageAWT(String gameId, Connection conn, JFrame previousFrame, Point previousLocation) {
		this(gameId, conn); // 기본 초기화 수행
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;
	}

	public void initializeUI() {
		setTitle("보드게임 상세");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// ===== 상단 패널 =====
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

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
		JLabel titleLabel = new JLabel("보드게임 상세", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 상단 패널을 프레임에 추가
		add(topPanel, BorderLayout.NORTH);

		// ===== 메인 콘텐츠 패널 =====
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(false);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

		// ===== 게임 정보 패널 =====
		RoundedPanel gameInfoPanel = new RoundedPanel();
		gameInfoPanel.setLayout(new BorderLayout(20, 0));
		gameInfoPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		// 왼쪽 이미지 패널
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		leftPanel.setOpaque(false);
		leftPanel.setPreferredSize(new Dimension(200, 250));

		// 게임 이미지 레이블
		gameImageLabel = new JLabel();
		gameImageLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
		gameImageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
		leftPanel.add(gameImageLabel);

		// 오른쪽 정보 패널
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setOpaque(false);

		// 게임 제목
		gameTitleLabel = new JLabel("");
		gameTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		gameTitleLabel.setForeground(ACCENT_COLOR);
		gameTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// 구분선
		JSeparator separator = new JSeparator();
		separator.setForeground(new Color(220, 220, 220));
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		separator.setAlignmentX(Component.LEFT_ALIGNMENT);

		// "게임 정보" 라벨
		JLabel infoHeaderLabel = new JLabel("게임 정보");
		infoHeaderLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		infoHeaderLabel.setForeground(ACCENT_COLOR);
		infoHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		rightPanel.add(gameTitleLabel);
		rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		rightPanel.add(separator);
		rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		rightPanel.add(infoHeaderLabel);
		rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

		// 정보 라벨들을 텍스트 에어리어로 변경
		infoTextArea = new JTextArea() {
			@Override
			public Insets getInsets() {
				return new Insets(0, 0, 0, 0); // 여백 제거
			}
		};
		infoTextArea.setEditable(false);
		infoTextArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
		infoTextArea.setLineWrap(true);
		infoTextArea.setWrapStyleWord(true);
		infoTextArea.setBackground(null);
		infoTextArea.setOpaque(false);
		infoTextArea.setBorder(null);
		infoTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

		rightPanel.add(infoTextArea);

		// 패널 추가
		gameInfoPanel.add(leftPanel, BorderLayout.WEST);
		gameInfoPanel.add(rightPanel, BorderLayout.CENTER);

		// ===== 게임 방법 패널 =====
		RoundedPanel gameMethodPanel = new RoundedPanel();
		gameMethodPanel.setLayout(new BorderLayout());
		gameMethodPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

		// 게임 방법 제목
		JLabel methodTitleLabel = new JLabel("게임 진행 방법");
		methodTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		methodTitleLabel.setForeground(ACCENT_COLOR);

		// 구분선
		JSeparator methodSeparator = new JSeparator();
		methodSeparator.setForeground(new Color(220, 220, 220));

		// 헤더 패널
		JPanel methodHeaderPanel = new JPanel();
		methodHeaderPanel.setLayout(new BoxLayout(methodHeaderPanel, BoxLayout.Y_AXIS));
		methodHeaderPanel.setOpaque(false);
		methodHeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		methodTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		methodSeparator.setAlignmentX(Component.LEFT_ALIGNMENT);
		methodSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

		methodHeaderPanel.add(methodTitleLabel);
		methodHeaderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		methodHeaderPanel.add(methodSeparator);

		// 게임 방법 내용
		gameWayText = new JTextArea();
		gameWayText.setFont(new Font("SansSerif", Font.PLAIN, 14));
		gameWayText.setLineWrap(true);
		gameWayText.setWrapStyleWord(true);
		gameWayText.setEditable(false);
		gameWayText.setBackground(Color.WHITE);
		gameWayText.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		// 스크롤 패널 추가
		JScrollPane scrollPane = new JScrollPane(gameWayText);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setPreferredSize(new Dimension(0, 250));

		// 스크롤바 커스텀
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setPreferredSize(new Dimension(8, 0));
		verticalBar.setUI(new BasicScrollBarUI() {
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

		gameMethodPanel.add(methodHeaderPanel, BorderLayout.NORTH);
		gameMethodPanel.add(scrollPane, BorderLayout.CENTER);

		// 메인 패널에 패널들 추가
		mainPanel.add(gameInfoPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(gameMethodPanel);

		// 스크롤 패널 설정
		JScrollPane mainScrollPane = new JScrollPane(mainPanel);
		mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
		mainScrollPane.setOpaque(false);
		mainScrollPane.getViewport().setOpaque(false);
		mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		// 메인 스크롤바 커스텀
		JScrollBar mainVerticalBar = mainScrollPane.getVerticalScrollBar();
		mainVerticalBar.setPreferredSize(new Dimension(8, 0));
		mainVerticalBar.setUI(new BasicScrollBarUI() {
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

		add(mainScrollPane, BorderLayout.CENTER);

		// 게임 상세 정보 불러오기 (매개변수 없이 인스턴스 변수 사용)
		loadGameDetails();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	// 게임 상세 정보 불러오기
	private void loadGameDetails() {
		try {
			String query = "SELECT * FROM game WHERE game_id = ?";
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, gameId);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				// 게임 제목 설정
				gameTitleLabel.setText(rs.getString("game_id"));

				// 정보 텍스트 영역에 정보 추가 - 적당한 간격으로 표시
				StringBuilder infoBuilder = new StringBuilder();
				infoBuilder.append("최대 인원: ").append(rs.getInt("max_member")).append("명\n\n");
				infoBuilder.append("장르: ").append(rs.getString("category")).append("\n\n");
				infoBuilder.append("난이도: ").append(rs.getString("difficulty_level"));
				infoTextArea.setText(infoBuilder.toString());

				// 게임 방법 텍스트 설정
				String gameWayContent = rs.getString("game_way");

				// 형식을 수동으로 다듬기
				String formattedContent = formatGameWay(gameWayContent);
				gameWayText.setText(formattedContent);

				// 이미지 처리
				byte[] imageData = rs.getBytes("game_image");
				if (imageData != null) {
					ImageIcon icon = new ImageIcon(imageData);
					Image img = icon.getImage();
					Image resizedImage = img.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
					gameImageLabel.setIcon(new ImageIcon(resizedImage));
				} else {
					gameImageLabel.setHorizontalAlignment(JLabel.CENTER);
					gameImageLabel.setText("이미지 준비중");
					gameImageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "데이터 로드 중 오류가 발생했습니다: " + e.getMessage(), "오류",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// 게임 방법 텍스트 형식화
	private String formatGameWay(String content) {
		StringBuilder result = new StringBuilder();
		String[] lines = content.split("\n");

		for (String line : lines) {
			line = line.trim();

			// 숫자로 시작하는지 확인 (예: "1.", "2.", 등)
			if (line.matches("^\\d+\\..*")) {
				// 숫자와 점 이후의 텍스트 분리
				int dotIndex = line.indexOf('.');
				if (dotIndex != -1 && dotIndex + 1 < line.length()) {
					String number = line.substring(0, dotIndex + 1);
					String title = line.substring(dotIndex + 1).trim();

					// 제목 줄 강조
					result.append("★ ").append(number).append(" ").append(title).append(" ★\n");
				} else {
					result.append(line).append("\n");
				}
			} else {
				result.append(line).append("\n");
			}
		}

		return result.toString();
	}

	// 뒤로가기 버튼 클릭 시 처리 (이전 프레임과 위치 복원)
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			// 이전 프레임 정보가 있으면 해당 프레임을 복원
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				// 만약 정보가 없다면 기본 페이지(예: BoardGamePageAWT)로 이동
				new BoardGamePageAWT();
			}
		}
	}

	// 메인 메소드 (예시로 다빈치코드를 상세 페이지로)
	public static void main(String[] args) {
		try {
			// 시스템 룩앤필 설정
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// 폰트 렌더링 개선
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 예시로 '다빈치코드' 게임 정보를 화면에 표시
		SwingUtilities.invokeLater(() -> {
			new BoardGameDetailPageAWT("다빈치코드", getConnection());
		});
	}

	// 데이터베이스 연결 설정
	private static Connection getConnection() {
		try {
			String url = "jdbc:mysql://localhost:3306/project?characterEncoding=UTF-8&serverTimezone=UTC";
			String user = "root";
			String password = "1234";
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}