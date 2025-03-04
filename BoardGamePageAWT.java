package Project;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import Project.MyInfoAWT.BackgroundPanel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class BoardGamePageAWT extends JFrame implements ActionListener {
	JButton backButton;
	JPanel topPanel, titlePanel, topRightPanel, buttonGroup, mainPanel, searchAndCategoryPanel, resultPanel,
			separatorContainer;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
	JTextField searchField;
	JComboBox<String> categoryComboBox;
	int currentPage = 1;
	int gamesPerPage = 4;
	private Connection conn;

	class BackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, w, h);

			drawGamePattern(g2d, w, h);

			g2d.setColor(new Color(255, 255, 255, 180));
			g2d.fillRect(0, 0, w, h);
		}

		private void drawGamePattern(Graphics2D g2d, int width, int height) {
			int patternSize = 150;
			for (int x = 25; x < width; x += patternSize) {
				for (int y = 0; y < height; y += patternSize) {
					drawDice(g2d, x + 20, y + 20);

					if (((x - 25) + y) % (patternSize * 2) == 0) {
						drawChessPiece(g2d, x + 80, y + 40);
					}

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

			g2d.setColor(new Color(150, 150, 150));
			int dotSize = 6;
			g2d.fillOval(x + 17, y + 17, dotSize, dotSize);
			g2d.fillOval(x + 8, y + 8, dotSize, dotSize);
			g2d.fillOval(x + 26, y + 8, dotSize, dotSize);
			g2d.fillOval(x + 8, y + 26, dotSize, dotSize);
			g2d.fillOval(x + 26, y + 26, dotSize, dotSize);
		}

		private void drawChessPiece(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(200, 200, 200));
			int[] xPoints = { x, x + 20, x + 16, x + 12, x + 8, x + 4 };
			int[] yPoints = { y + 30, y + 30, y + 20, y + 10, y + 20, y + 30 };
			g2d.fillPolygon(xPoints, yPoints, 6);
			g2d.setColor(new Color(180, 180, 180));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 6);
		}

		private void drawGamePiece(Graphics2D g2d, int x, int y) {
			g2d.setColor(new Color(210, 210, 210));
			int[] xPoints = { x, x + 20, x + 40, x + 30, x + 10 };
			int[] yPoints = { y + 30, y, y + 30, y + 45, y + 45 };
			g2d.fillPolygon(xPoints, yPoints, 5);
			g2d.setColor(new Color(180, 180, 180));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 5);
		}
	}

	public BoardGamePageAWT() {
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/project?characterEncoding=UTF-8&serverTimezone=UTC", "root", "1234");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		setTitle("보드게임");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// 상단 패널
		topPanel = new JPanel(new BorderLayout());
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
		backButton.setForeground(new Color(51, 51, 51));

		// 제목
		titleLabel = new JLabel("보드게임", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));
		titleLabel.setForeground(new Color(51, 51, 51));

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃 버튼
		logoutLabel = new JLabel("로그아웃");
		logoutLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoutLabel.setForeground(new Color(51, 51, 51));
		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				dispose();
				new LoginAWT();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				logoutLabel.setForeground(new Color(100, 100, 100));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				logoutLabel.setForeground(new Color(51, 51, 51));
			}
		});

		// 메뉴 버튼
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.setForeground(new Color(51, 51, 51));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, BoardGamePageAWT.this);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				menuLabel.setForeground(new Color(100, 100, 100));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				menuLabel.setForeground(new Color(51, 51, 51));
			}
		});

		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false);

		spaceLabel = new JLabel("  ");

		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 구분선
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(new Color(200, 200, 200));
		separatorLabel.setPreferredSize(new Dimension(650, 1));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);
		add(topPanel, BorderLayout.NORTH);

		// 메인 패널
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(false);

		// 검색 및 카테고리 패널
		searchAndCategoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		searchAndCategoryPanel.setOpaque(false);

		// 카테고리 콤보박스 스타일링
		categoryComboBox = new JComboBox<>();
		categoryComboBox.setPreferredSize(new Dimension(120, 30));
		categoryComboBox.setBackground(Color.WHITE);
		categoryComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
		categoryComboBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
		loadCategories();
		categoryComboBox.addActionListener(e -> performSearch());

		// 검색 필드 스타일링
		searchField = new JTextField(15);
		searchField.setPreferredSize(new Dimension(200, 30));
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
		searchField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(180, 180, 180)), BorderFactory.createEmptyBorder(0, 8, 0, 8)));
		searchField.addActionListener(this);

		// 검색 버튼 스타일링
		JButton searchButton = new JButton("검색");
		searchButton.setPreferredSize(new Dimension(70, 30));
		searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		searchButton.setBackground(new Color(51, 51, 51));
		searchButton.setForeground(Color.WHITE);
		searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		searchButton.setFocusPainted(false);
		searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		searchButton.addActionListener(e -> performSearch());

		searchAndCategoryPanel.add(categoryComboBox);
		searchAndCategoryPanel.add(searchField);
		searchAndCategoryPanel.add(searchButton);

		mainPanel.add(searchAndCategoryPanel);

		// 결과 패널
		resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(resultPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		// 스크롤바 커스텀
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setPreferredSize(new Dimension(8, 0));
		verticalBar.setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(51, 51, 51); // MemberListAWT의 BUTTON_COLOR와 동일
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

		JPanel scrollWrapper = new JPanel(new BorderLayout());
		scrollWrapper.setOpaque(false);
		scrollWrapper.setBorder(BorderFactory.createEmptyBorder(35, 65, 35, 65));
		scrollWrapper.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(scrollWrapper);
		add(mainPanel, BorderLayout.CENTER);

		performSearch();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void loadCategories() {
		try {
			String query = "SELECT DISTINCT category FROM game";
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			categoryComboBox.addItem("전체");
			while (rs.next()) {
				String category = rs.getString("category");
				categoryComboBox.addItem(category);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void performSearch() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				String keyword = searchField.getText();
				String selectedCategory = (String) categoryComboBox.getSelectedItem();
				PreparedStatement pst;
				String query;

				if ("전체".equals(selectedCategory)) {
					query = "SELECT * FROM game WHERE game_id LIKE ?";
					pst = conn.prepareStatement(query);
					pst.setString(1, "%" + keyword + "%");
				} else {
					query = "SELECT * FROM game WHERE category = ? AND game_id LIKE ?";
					pst = conn.prepareStatement(query);
					pst.setString(1, selectedCategory);
					pst.setString(2, "%" + keyword + "%");
				}

				ResultSet rs = pst.executeQuery();

				resultPanel.removeAll();
				if (!rs.next()) {
					JLabel noResultLabel = new JLabel("검색 결과가 없습니다.");
					noResultLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
					noResultLabel.setForeground(new Color(100, 100, 100));
					noResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
					resultPanel.add(noResultLabel);
				} else {
					do {
						String gameId = rs.getString("game_id");
						String gameDescription = rs.getString("game_description");
						byte[] imageData = rs.getBytes("game_image");
						JPanel gamePanel = createBoardGameResult(gameId, gameDescription, imageData);

						// 구분선 추가
						if (resultPanel.getComponentCount() > 0) {
							JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
							separator.setForeground(new Color(220, 220, 220));
							separator.setMaximumSize(new Dimension(650, 2));
							resultPanel.add(separator);
						}

						resultPanel.add(gamePanel);
					} while (rs.next());
				}
				return null;
			}

			@Override
			protected void done() {
				resultPanel.revalidate();
				resultPanel.repaint();
			}
		}.execute();
	}

	private JPanel createBoardGameResult(String gameId, String description, byte[] imageData) {
		JPanel boardGamePanel = new JPanel(new BorderLayout(20, 0));
		boardGamePanel.setOpaque(false);
		boardGamePanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 0));
		boardGamePanel.setMaximumSize(new Dimension(900, 130));

		// 이미지 패널
		JPanel imagePanel = new JPanel(new BorderLayout());
		imagePanel.setOpaque(false);
		imagePanel.setPreferredSize(new Dimension(100, 100));
		imagePanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

		JLabel imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		if (imageData != null) {
			ImageIcon imageIcon = new ImageIcon(imageData);
			Image img = imageIcon.getImage();
			Image resizedImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			imageLabel.setIcon(new ImageIcon(resizedImg));
		} else {
			imageLabel.setText("이미지 준비중");
			imageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			imageLabel.setForeground(new Color(150, 150, 150));
		}
		imagePanel.add(imageLabel, BorderLayout.CENTER);
		boardGamePanel.add(imagePanel, BorderLayout.WEST);

		// 텍스트 패널
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setOpaque(false);
		textPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

		// 게임 제목
		JLabel gameTitleLabel = new JLabel(gameId);
		gameTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
		gameTitleLabel.setForeground(new Color(51, 51, 51));
		gameTitleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		gameTitleLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				gameTitleLabel.setForeground(new Color(100, 100, 100));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				gameTitleLabel.setForeground(new Color(51, 51, 51));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String updateQuery = "UPDATE game SET ranking_points = ranking_points + 1 WHERE game_id = ?";
					PreparedStatement pstmt = conn.prepareStatement(updateQuery);
					pstmt.setString(1, gameId);
					pstmt.executeUpdate();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				dispose();
				new BoardGameDetailPageAWT(gameId, conn);
			}
		});

		// 게임 설명
		JLabel gameDescriptionLabel = new JLabel("<html><body style='width: 300px'>" + description + "</body></html>");
		gameDescriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
		gameDescriptionLabel.setForeground(new Color(80, 80, 80));

		textPanel.add(gameTitleLabel);
		textPanel.add(Box.createVerticalStrut(10));
		textPanel.add(gameDescriptionLabel);
		boardGamePanel.add(textPanel, BorderLayout.CENTER);

		return boardGamePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			new MainPageAWT();
		}
	}

	public static void main(String[] args) {
		new BoardGamePageAWT();
	}
}