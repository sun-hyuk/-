package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import Project.BoardPageAWT.BackgroundPanel;

public class BoardDetailPageAWT extends JFrame implements ActionListener {
	JButton backButton;
	JPanel topPanel, titlePanel, buttonGroup, topRightPanel, separatorContainer;
	JLabel titleLabel, logoutLabel, menuLabel, spaceLabel, separatorLabel, editLabel, deleteLabel;
	JPanel mainPanel, postPanel, commentPanel, inputPanel;

	private JTextArea contentArea;
	private JTextField titleField;
	private JFrame previousFrame;
	private Point previousLocation;
	private int boardId;
	private BoardMgr boardMgr;
	private JLabel dateLabel, commentCountLabel;
	// 편집 버튼 패널을 위한 멤버 변수 추가
	private JPanel editButtonsPanel;
	private JPopupMenu currentPopup = null;
	
	// 현재 로그인된 사용자 (관리자 또는 회원)
	private ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
	private MemberBean currentUser = UserSession.getInstance().getCurrentUser();
	// 현재 로그인한 사용자가 게시글 작성자인지 여부를 저장하는 변수
	private boolean canEditPermission = false;

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

	public BoardDetailPageAWT(int boardId) {
		this(boardId, null, null);
	}

	public BoardDetailPageAWT(int boardId, JFrame previousFrame, Point previousLocation) {
		this.boardId = boardId;
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;
		boardMgr = new BoardMgr();
		initComponents();
		loadBoardDetail();
		// 이전 위치로 창 위치 설정
		if (previousLocation != null) {
			setLocation(previousLocation);
		}
		setVisible(true);
	}

	public void initComponents() {
		setTitle("게시글");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// ── 상단 헤더 영역 ─────────────────────────────
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false); // 배경 투명하게 설정

		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		titleLabel = new JLabel("게시글", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false); // 배경 투명하게 설정
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃 및 삼단바 영역
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

			@Override
			public void mouseEntered(MouseEvent e) {
				logoutLabel.setForeground(Color.DARK_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				logoutLabel.setForeground(Color.BLACK);
			}
		});

		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, BoardDetailPageAWT.this);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				menuLabel.setForeground(Color.DARK_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				menuLabel.setForeground(Color.BLACK);
			}
		});

		spaceLabel = new JLabel("  ");

		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false); // 배경 투명하게 설정
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false); // 배경 투명하게 설정
		topRightPanel.add(buttonGroup);

		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 구분선 추가
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false); // 배경 투명하게 설정
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 17, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);

		// ── 메인 패널 ─────────────────────────────
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(false); // 배경 투명하게 설정
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 46)); // Add padding

		// ── 게시글 패널 ─────────────────────────────
		postPanel = new JPanel();
		postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
		postPanel.setOpaque(false); // 배경 투명하게 설정

		// 작성자 정보 패널
		JPanel authorPanel = new JPanel(new BorderLayout());
		authorPanel.setOpaque(false); // 배경 투명하게 설정
		authorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel authorLabel = new JLabel("익명");
		authorLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

		JPanel dateMorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		dateMorePanel.setOpaque(false); // 배경 투명하게 설정

		dateLabel = new JLabel();
		dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

		JLabel moreLabel = new JLabel("...");
		moreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		moreLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 9, 0));
		moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		moreLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showBoardPopupMenu(moreLabel);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				moreLabel.setForeground(Color.DARK_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				moreLabel.setForeground(Color.BLACK);
			}
		});

		dateMorePanel.add(dateLabel);
		dateMorePanel.add(moreLabel);

		authorPanel.add(authorLabel, BorderLayout.WEST);
		authorPanel.add(dateMorePanel, BorderLayout.EAST);

		// ── 게시글 내용 패널 ─────────────────────────────
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(false); // 배경 투명하게 설정
		contentPanel.setMinimumSize(new Dimension(0, 100));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

		// 제목: 멤버 변수 titleField로 생성 (읽기 전용)
		titleField = new JTextField("제목입니다");
		titleField.setFont(new Font("SansSerif", Font.BOLD, 18));
		titleField.setEditable(false);
		titleField.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		titleField.setOpaque(false); // 배경 투명하게 설정
		// 초기 상태: 편집 불가능하고 포커스를 받지 않음
		titleField.setEditable(false);
		titleField.setFocusable(false);

		// 내용: 멤버 변수 contentArea로 생성 (읽기 전용)
		contentArea = new JTextArea("내용입니다");
		contentArea.setEditable(false);
		contentArea.setOpaque(false); // 배경 투명하게 설정
		contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		// 초기 상태: 편집 불가능하고 포커스를 받지 않음
		contentArea.setEditable(false);
		contentArea.setFocusable(false);

		contentPanel.add(titleField, BorderLayout.NORTH);
		JScrollPane contentScroll = new JScrollPane(contentArea);
		contentScroll.setBorder(null); // 스크롤 페인의 테두리 제거
		contentScroll.setOpaque(false); // 배경 투명하게 설정
		contentScroll.getViewport().setOpaque(false); // 뷰포트 배경 투명하게 설정
		contentPanel.add(contentScroll, BorderLayout.CENTER);

		// 메인 패널에 게시글 내용 패널 추가
		mainPanel.add(contentPanel);

		// 게시글 하단 (댓글 수, 좋아요 등)
		JPanel postFooterPanel = new JPanel(new BorderLayout());
		postFooterPanel.setOpaque(false); // 배경 투명하게 설정
		postFooterPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		JPanel commentCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		commentCountPanel.setOpaque(false); // 배경 투명하게 설정

		JLabel commentCountIcon = new JLabel("💬");
		commentCountLabel = new JLabel("댓글 0"); // 타입 제거하여 클래스 멤버 변수에 할당
		commentCountLabel.setForeground(Color.DARK_GRAY);
		commentCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

		commentCountPanel.add(commentCountIcon);
		commentCountPanel.add(commentCountLabel);

		postFooterPanel.add(commentCountPanel, BorderLayout.WEST);

		// 구분선
		JSeparator postSeparator = new JSeparator();
		postSeparator.setForeground(Color.LIGHT_GRAY);
		postSeparator.setBackground(Color.LIGHT_GRAY);
		postSeparator.setPreferredSize(new Dimension(650, 1));
		postSeparator.setMaximumSize(new Dimension(650, 1));
		postPanel.add(authorPanel);
		postPanel.add(contentPanel);
		postPanel.add(postFooterPanel);
		postPanel.add(postSeparator);

		// 게시글 내용 패널 추가 후, 최소 높이의 빈 패널 추가
		JPanel gapPanel = new JPanel();
		gapPanel.setPreferredSize(new Dimension(0, 10)); // 100픽셀 고정
		gapPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
		gapPanel.setOpaque(false); // 배경 투명하게 설정
		postPanel.add(gapPanel);

		// ── 댓글 패널 ─────────────────────────────
		commentPanel = new JPanel();
		commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
		commentPanel.setOpaque(false); // 배경 투명하게 설정

		// 댓글 레이블
		JLabel commentSectionLabel = new JLabel("댓글");
		commentSectionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		// 수평 중앙 정렬
		commentSectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		commentSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);

		commentPanel.add(commentSectionLabel);

		loadComments();

		// ── 입력 패널 ─────────────────────────────
		inputPanel = new JPanel(new BorderLayout(10, 0));
		inputPanel.setBackground(new Color(230, 230, 230));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 46));

		JTextField commentInput = new JTextField();
		commentInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(10, 15, 10, 15)));
		commentInput.setFont(new Font("SansSerif", Font.PLAIN, 14));

		commentInput.setText("댓글을 입력하세요.");
		commentInput.setForeground(Color.GRAY);
		commentInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (commentInput.getText().equals("댓글을 입력하세요.")) {
					commentInput.setText("");
					commentInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (commentInput.getText().isEmpty()) {
					commentInput.setText("댓글을 입력하세요.");
					commentInput.setForeground(Color.GRAY);
				}
			}
		});

		JButton submitButton = new JButton("등록");
		submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		submitButton.setBackground(new Color(230, 230, 230));
		submitButton.setForeground(Color.BLACK);
		submitButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(8, 15, 8, 15)));
		submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		submitButton.setFocusPainted(false);

		submitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				submitButton.setBackground(Color.GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				submitButton.setBackground(new Color(230, 230, 230));
			}
		});

		// 댓글 등록 버튼 ActionListener 
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String commentText = commentInput.getText();
				if (!commentText.isEmpty() && !commentText.equals("댓글을 입력하세요.")) {
					boolean success = boardMgr.insertComment(boardId, commentText);
					if (success) {
						// 댓글 등록 성공 후, DB에 저장된 댓글들을 모두 불러와 화면을 갱신
						loadComments();
						commentInput.setText(""); // 댓글 입력창 비우기
						commentInput.requestFocus();
					} else {
						JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "댓글 등록 실패");
					}
				}
			}
		});

		inputPanel.add(commentInput, BorderLayout.CENTER);
		inputPanel.add(submitButton, BorderLayout.EAST);

		// 스크롤 패널에 메인 컨텐츠 추가
		mainPanel.add(postPanel);
		mainPanel.add(commentPanel);

		// containerPanel을 생성하여 mainPanel을 NORTH에 추가 (내용이 짧을 때 위쪽에 고정)
		JPanel containerPanel = new JPanel(new BorderLayout());
		containerPanel.setOpaque(false); // 배경 투명하게 설정
		containerPanel.add(mainPanel, BorderLayout.NORTH);

		// 스크롤 페인 생성: containerPanel을 뷰포트에 설정하고, 스크롤바 정책을 AS_NEEDED로 변경
		JScrollPane scrollPane = new JScrollPane(containerPanel);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false); // 배경 투명하게 설정
		scrollPane.getViewport().setOpaque(false);

		// 스크롤(혹은 뷰포트 변경)이 발생하면 현재 팝업이 있으면 숨깁니다.
		scrollPane.getViewport().addChangeListener(e -> {
			if (currentPopup != null && currentPopup.isVisible()) {
				currentPopup.setVisible(false);
				currentPopup = null;
				scrollPane.repaint();
			}
		});

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);

		SwingUtilities.invokeLater(() -> {
			scrollPane.getViewport().setViewPosition(new Point(0, 0));
		});

		// 스크롤바 UI 커스터마이징
		scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.GRAY; // 스크롤 thumb 색상
				this.trackColor = Color.WHITE; // 스크롤 track 색상
			}
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof MouseWheelEvent) {
					if (currentPopup != null && currentPopup.isVisible()) {
						currentPopup.setVisible(false);
						currentPopup = null;
						// 팝업이 닫힌 후 전체 프레임을 repaint 하여 잔상 제거
						SwingUtilities.invokeLater(() -> BoardDetailPageAWT.this.repaint());
					}
				}
			}
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	// enableInlineEdit() 메소드 (페이지 내에서 직접 수정 모드로 전환)
	private void enableInlineEdit() {
		// 제목과 내용 편집 가능하도록 전환
		titleField.setEditable(true);
		titleField.setFocusable(true);
		contentArea.setEditable(true);
		contentArea.setFocusable(true);

		// 이미 편집 모드인 경우 중복 추가 방지
		if (editButtonsPanel != null && editButtonsPanel.getParent() != null) {
			return;
		}

		// "저장" 버튼 생성
		JButton saveButton = new JButton("저장");
		saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		saveButton.setBackground(Color.LIGHT_GRAY);
		saveButton.setForeground(Color.BLACK);
		saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newTitle = titleField.getText();
				String newContent = contentArea.getText();
				boolean success = boardMgr.updateBoard(boardId, newTitle, newContent);
				if (success) {
					JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "게시글이 수정되었습니다.");
					titleField.setEditable(false);
					contentArea.setEditable(false);
					postPanel.remove(editButtonsPanel);
					postPanel.revalidate();
					postPanel.repaint();
					loadBoardDetail();
				} else {
					JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "게시글 수정 실패.");
				}
			}
		});

		// "취소" 버튼 생성
		JButton cancelButton = new JButton("취소");
		cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
		cancelButton.setBackground(Color.LIGHT_GRAY);
		cancelButton.setForeground(Color.BLACK);
		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 수정 취소 시 원래 내용으로 되돌리고 편집 모드 종료
				loadBoardDetail();
				titleField.setEditable(false);
				contentArea.setEditable(false);
				postPanel.remove(editButtonsPanel);
				postPanel.revalidate();
				postPanel.repaint();
			}
		});

		// 편집 버튼들을 담을 패널 생성
		editButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		editButtonsPanel.setOpaque(false); // 배경 투명하게 설정
		editButtonsPanel.add(saveButton);
		editButtonsPanel.add(cancelButton);

		// 게시글 패널(postPanel)에 편집 버튼 패널 추가
		postPanel.add(editButtonsPanel);
		postPanel.revalidate();
		postPanel.repaint();

		// 여기서 postPanel의 최대 크기를 다시 재설정하여 저장/취소 버튼이 보이도록 함
		postPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, postPanel.getPreferredSize().height));

		// 내용 텍스트 영역에 포커스 주기
		contentArea.requestFocusInWindow();
		contentArea.setCaretPosition(contentArea.getText().length());
	}

	// 댓글 수정용 인라인 편집 모드 활성화 메소드
	private void enableInlineEditComment(int commentId) {
		Component[] components = commentPanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JPanel) {
				JPanel container = (JPanel) components[i];
				Object idObj = container.getClientProperty("commentId");
				if (idObj instanceof Integer && ((Integer) idObj) == commentId) {
					// container 내의 댓글 내용을 담고 있는 JTextArea 찾기
					JTextArea currentTextArea = null;
					for (Component comp : container.getComponents()) {
						if (comp instanceof JTextArea) {
							currentTextArea = (JTextArea) comp;
							break;
						}
					}
					if (currentTextArea != null) {
						String originalText = currentTextArea.getText();
						// 인라인 편집용 패널 구성
						JPanel editPanel = new JPanel(new BorderLayout());
						JTextArea editableTextArea = new JTextArea(originalText);
						editableTextArea.setLineWrap(true);
						editableTextArea.setWrapStyleWord(true);
						editableTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
						JScrollPane scrollPane = new JScrollPane(editableTextArea);
						scrollPane.setPreferredSize(new Dimension(650, 60));
						scrollPane.setMaximumSize(new Dimension(650, 60));

						// 저장 및 취소 버튼
						JButton saveButton = new JButton("저장");
						saveButton.setFont(new Font("SansSerif", Font.BOLD, 12));
						saveButton.setBackground(Color.LIGHT_GRAY);
						saveButton.setForeground(Color.BLACK);
						saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
						JButton cancelButton = new JButton("취소");
						cancelButton.setFont(new Font("SansSerif", Font.BOLD, 12));
						cancelButton.setBackground(Color.LIGHT_GRAY);
						cancelButton.setForeground(Color.BLACK);
						cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
						JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
						buttonPanel.setOpaque(false); // 배경 투명하게 설정
						buttonPanel.add(saveButton);
						buttonPanel.add(cancelButton);

						editPanel.add(scrollPane, BorderLayout.CENTER);
						editPanel.add(buttonPanel, BorderLayout.SOUTH);

						// 기존 댓글 컨테이너를 인라인 편집 패널로 교체
						int index = i;
						commentPanel.remove(index);
						commentPanel.add(editPanel, index);
						commentPanel.revalidate();
						commentPanel.repaint();

						// 저장 버튼 액션
						saveButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								String newContent = editableTextArea.getText().trim();
								if (!newContent.isEmpty()) {
									boolean success = boardMgr.updateComment(commentId, newContent);
									if (success) {
										JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "댓글이 수정되었습니다.");
										loadComments(); // 전체 댓글 다시 로드
									} else {
										JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "댓글 수정 실패.");
									}
								}
							}
						});
						// 취소 버튼 액션
						cancelButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								loadComments(); // 수정 모드 종료
							}
						});
					}
					break;
				}
			}
		}
	}

	private void addComment(String author, String content, int commentId) {
		// 임시 텍스트 영역을 만들어 내용의 preferredHeight 계산
		JTextArea temp = new JTextArea(content);
		temp.setFont(new Font("SansSerif", Font.PLAIN, 14));
		temp.setLineWrap(true);
		temp.setWrapStyleWord(true);
		temp.setSize(new Dimension(300, Short.MAX_VALUE)); // 너비 제약
		int preferredHeight = temp.getPreferredSize().height;
		// 예: 텍스트 높이가 40픽셀 미만이면 하단 여백을 5픽셀, 그렇지 않으면 15픽셀로 설정

		// 댓글 컨테이너의 외부 여백 설정 (상, 좌, 하, 우)
		Border outer = BorderFactory.createEmptyBorder(0, 0, 0, 0);
		// 내부 여백과 선 설정
		Border inner = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
				BorderFactory.createEmptyBorder(10, 15, 10, 15));

		JPanel commentContainer = new JPanel(new BorderLayout());
		commentContainer.setBackground(Color.WHITE);
		commentContainer.setBorder(BorderFactory.createCompoundBorder(outer, inner));

		// 댓글 컨테이너에 commentId 저장
		commentContainer.putClientProperty("commentId", commentId);

		JPanel authorPanel = new JPanel(new BorderLayout());
		authorPanel.setOpaque(false);

		JLabel authorLabel = new JLabel(author);
		authorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

		// moreLabel을 감싸는 JPanel을 생성하여 위치 조정
		JPanel morePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // 오른쪽 상단 정렬
		morePanel.setOpaque(false); // 배경 투명하게 설정

		JLabel moreLabel = new JLabel("...");
		moreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		moreLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 위쪽으로 올리기 위한 여백 추가 (더 올리고 싶으면 top 값을 조정)
		moreLabel.setBorder(BorderFactory.createEmptyBorder(-10, 0, 0, 0));
		moreLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				moreLabel.setForeground(Color.DARK_GRAY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				moreLabel.setForeground(Color.BLACK);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				showCommentPopupMenu(moreLabel, commentId);
			}
		});

		// moreLabel을 포함하는 morePanel에 추가
		morePanel.add(moreLabel);
		authorPanel.add(authorLabel, BorderLayout.WEST);
		authorPanel.add(morePanel, BorderLayout.EAST);

		JTextArea contentArea = new JTextArea(content);
		contentArea.setEditable(false);
		contentArea.setFocusable(false); // 초기 상태에서 포커스 불가
		contentArea.setOpaque(false); // 배경 투명하게 설정
		contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);

		commentContainer.add(authorPanel, BorderLayout.NORTH);
		commentContainer.add(contentArea, BorderLayout.CENTER);

		commentPanel.add(commentContainer);
	}

	// 게시글의 "..."을 클릭했을 때 보이는 팝업 메뉴
	private void showBoardPopupMenu(Component invoker) {
	    EditDeletePopupMenu popupMenu = new EditDeletePopupMenu("수정하기", e -> {
	        if (!canEditPermission) {
	            JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "작성자만 수정할 수 있습니다.", "권한 없음", JOptionPane.WARNING_MESSAGE);
	        } else {
	            enableInlineEdit();
	        }
	    }, "삭제하기", e -> {
	        if (!canEditPermission) {
	            JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "작성자만 삭제할 수 있습니다.", "권한 없음", JOptionPane.WARNING_MESSAGE);
	        } else {
	            int response = JOptionPane.showConfirmDialog(BoardDetailPageAWT.this, "게시글을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
	            if (response == JOptionPane.YES_OPTION) {
	                boolean success = boardMgr.deleteBoard(boardId);
	                if (success) {
	                    JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "게시글이 삭제되었습니다.");
	                    dispose();
	                    new BoardPageAWT(null);
	                } else {
	                    JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "게시글 삭제 실패.");
	                }
	            }
	        }
	    });
	    currentPopup = popupMenu;
	    popupMenu.show(invoker, invoker.getWidth() - popupMenu.getPreferredSize().width, 25);
	}


	// 댓글의 "..."을 클릭했을 때 보이는 팝업 메뉴
	private void showCommentPopupMenu(Component invoker, int commentId) {
		EditDeletePopupMenu popupMenu = new EditDeletePopupMenu("수정하기", e -> {
			// 인라인 편집 모드 활성화
			enableInlineEditComment(commentId);
		}, "삭제하기", e -> {
			int response = JOptionPane.showConfirmDialog(BoardDetailPageAWT.this, "댓글을 삭제하시겠습니까?", "삭제 확인",
					JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.YES_OPTION) {
				boolean success = boardMgr.deleteComment(commentId);
				if (success) {
					JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "댓글이 삭제되었습니다.");
					loadComments();
				} else {
					JOptionPane.showMessageDialog(BoardDetailPageAWT.this, "댓글 삭제 실패.");
				}
			}
		});
		currentPopup = popupMenu; // 현재 팝업 저장
		popupMenu.show(invoker, invoker.getWidth() - popupMenu.getPreferredSize().width, 15);
	}

	// BoardMgr를 이용해 boardId에 해당하는 BoardBean을 불러와 제목과 내용을 업데이트
	private void loadBoardDetail() {
	    BoardBean board = boardMgr.getBoardById(boardId);
	    if (board != null) {
	        titleField.setText(board.getBoard_Title());
	        String content = boardMgr.getBoardContent(boardId);
	        contentArea.setText(content);
	        dateLabel.setText(board.getCreated_Date());
	        
	        // 작성자 권한 부여: 현재 로그인한 사용자와 게시글 작성자가 일치하는지 비교
	        canEditPermission = false;
	        if (board.getManager_Id() != null && !board.getManager_Id().isEmpty() && currentAdmin != null) {
	            canEditPermission = currentAdmin.getManager_Id().equals(board.getManager_Id());
	        } else if (board.getMember_Id() != null && !board.getMember_Id().isEmpty() && currentUser != null) {
	            canEditPermission = currentUser.getMember_Id().equals(board.getMember_Id());
	        }
	    } else {
	        JOptionPane.showMessageDialog(this, "게시글을 찾을 수 없습니다.");
	    }
	}

	private void loadComments() {
		commentPanel.removeAll();
		// 댓글 섹션 레이블 재추가
		JLabel commentSectionLabel = new JLabel("댓글");
		commentSectionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		commentSectionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
		commentSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		commentSectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		commentPanel.add(commentSectionLabel);

		ArrayList<CommentBean> comments = boardMgr.getComments(boardId);

		// 댓글 수 업데이트
		commentCountLabel.setText("댓글 " + comments.size());

		if (comments.isEmpty()) {
			// 댓글이 없을 경우에도 댓글 영역의 전체 높이를 일정하게 유지하도록
			JPanel placeholder = new JPanel();
			placeholder.setPreferredSize(new Dimension(0, 10));
			placeholder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
			placeholder.setOpaque(false); // 배경 투명하게 설정
			commentPanel.add(placeholder);
		} else {
			for (int i = 0; i < comments.size(); i++) {
				CommentBean comment = comments.get(i);
				addComment("익명", comment.getCommentContent(), comment.getCommentId());
				// 마지막 항목이 아니라면 빈 패널 추가 (예: 높이 10픽셀)
				if (i < comments.size()) {
					JPanel gapPanel = new JPanel();
					gapPanel.setPreferredSize(new Dimension(0, 15));
					gapPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));
					gapPanel.setOpaque(false); // 배경 투명하게 설정
					commentPanel.add(gapPanel);
				}
			}
		}

		commentPanel.revalidate();
		commentPanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			// 이전 프레임이 있고 위치 정보가 있다면 복원
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new BoardPageAWT(null);
			}
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new BoardDetailPageAWT(1, null, null));
	}
}