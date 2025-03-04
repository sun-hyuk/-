package Project;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class BoardPageAWT extends NavigableFrame implements ActionListener {
	
    // 배경 패턴을 그리기 위한 내부 클래스
	class BackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
            // 안티앨리어싱 활성화 (부드러운 그래픽 표현)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

            // 배경을 흰색으로 채움
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, w, h);

            // 보드게임 패턴 그리기
			drawGamePattern(g2d, w, h);

            // 반투명 오버레이 추가
			g2d.setColor(new Color(255, 255, 255, 180));
			g2d.fillRect(0, 0, w, h);
		}

        // 보드게임 패턴을 반복해서 그림
		private void drawGamePattern(Graphics2D g2d, int width, int height) {
			int patternSize = 150;
			for (int x = 25; x < width; x += patternSize) {
				for (int y = 0; y < height; y += patternSize) {
                    // 주사위 그리기 (좌표 조정)
                    drawDice(g2d, x + 20, y + 20);

                    // 체스 말 (폰) 그리기: 특정 조건일 때 그리기
                    if (((x - 25) + y) % (patternSize * 2) == 0) {
                        drawChessPiece(g2d, x + 80, y + 40);
                    }

                    // 게임 말 (미플 모양) 그리기: 특정 조건일 때 그리기
                    if (((x - 25) + y) % (patternSize * 2) == patternSize) {
                        drawGamePiece(g2d, x + 40, y + 80);
                    }
				}
			}
		}

        // 주사위 그리기
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

        // 체스 폰 모양 그리기
        private void drawChessPiece(Graphics2D g2d, int x, int y) {
            g2d.setColor(new Color(200, 200, 200));
            // 체스 폰 실루엣 좌표 설정
            int[] xPoints = { x, x + 20, x + 16, x + 12, x + 8, x + 4 };
            int[] yPoints = { y + 30, y + 30, y + 20, y + 10, y + 20, y + 30 };
            g2d.fillPolygon(xPoints, yPoints, 6);
            g2d.setColor(new Color(180, 180, 180));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawPolygon(xPoints, yPoints, 6);
        }

        // 미플 모양 게임 말 그리기
        private void drawGamePiece(Graphics2D g2d, int x, int y) {
            g2d.setColor(new Color(210, 210, 210));
            // 미플 모양 다각형 좌표 설정
            int[] xPoints = { x, x + 20, x + 40, x + 30, x + 10 };
            int[] yPoints = { y + 30, y, y + 30, y + 45, y + 45 };
            g2d.fillPolygon(xPoints, yPoints, 5);
            g2d.setColor(new Color(180, 180, 180));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawPolygon(xPoints, yPoints, 5);
        }
    }

    // 전역 변수 선언
	private JButton backButton, searchButton;
	private JPanel topPanel, centerPanel, boardListPanel;
	private JLabel titleLabel, logoutLabel, menuLabel;
	private JTextField searchField;
	private ArrayList<BoardBean> boards;

    // 상수: 배경색, 버튼 색상, 항목 패널 폭
	private final Color BACKGROUND_COLOR = Color.WHITE;
	private final Color BUTTON_COLOR = new Color(52, 152, 219);
	private final Color HOVER_COLOR = new Color(241, 242, 243);
	private final int CONTENT_WIDTH = 650;

    // 생성자: 이전 프레임을 전달받아 초기화
	public BoardPageAWT(JFrame previousFrame) {
		super(previousFrame);
		
		// 로그인 세션 확인
        if (UserSession.getInstance().getCurrentUser() == null 
            && UserSession.getInstance().getCurrentAdmin() == null) {
            JOptionPane.showMessageDialog(this, "로그인 후 이용해주세요.");
            new LoginAWT();
            dispose();
            return;
        }
		
		boards = new ArrayList<>();
		initializeFrame();
		initializeComponents();

        // 하단 여백 패널 (원하는 경우 투명하게 설정)
		JPanel bottomGap = new JPanel();
		bottomGap.setPreferredSize(new Dimension(0, 20));
		bottomGap.setOpaque(false);
		add(bottomGap, BorderLayout.SOUTH);

		loadBoards();
	}
	
    // 프레임 초기 설정 (타이틀, 크기, 레이아웃, 배경패널 등)
	private void initializeFrame() {
		setTitle("자유게시판");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

        // 커스텀 배경 패널을 컨텐트 팬으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);
	}
	
    // 컴포넌트 초기화 및 배치 설정
    private void initializeComponents() {
        // 상단 패널 초기화 (로그아웃, 메뉴, 뒤로가기 버튼 등)
		initializeTopPanel();

        // 중앙 패널 생성
        centerPanel = createCenterPanel();
        // 중앙 패널을 래퍼 패널에 담아 상단패널 아래 중앙에 배치
		JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		centerWrapper.setOpaque(false);
		centerWrapper.add(centerPanel);
		add(centerWrapper, BorderLayout.CENTER);

		setLocationRelativeTo(null);
		setVisible(true);
	}

 // 상단 패널 구성 (뒤로가기 버튼, 제목, 로그아웃, 메뉴, 구분선 등)
    private void initializeTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // 왼쪽 영역: 뒤로가기 버튼과 제목 ("자유게시판")
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        // 뒤로가기 버튼 설정
        backButton = new JButton("<");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);
        backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

        // 제목 레이블 설정 ("자유게시판")
		titleLabel = new JLabel("자유게시판");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		leftPanel.add(backButton);
		leftPanel.add(titleLabel);
		topPanel.add(leftPanel, BorderLayout.WEST);

        // 오른쪽 영역: 로그아웃 및 메뉴 버튼 설정
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topRightPanel.setOpaque(false);

        // 로그아웃 레이블 설정 (클릭 시 로그인 페이지로 전환)
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

        // 메뉴 레이블 설정 (클릭 시 팝업 메뉴 표시)
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, BoardPageAWT.this);
			}
		});

		// 버튼 그룹 패널 (오른쪽 여백 포함)
		JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(new JLabel("  ")); // 추가 여백
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

        // 구분선 패널 추가 (상단 패널 하단에 위치)
		JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorPanel.setOpaque(false);
		separatorPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		JLabel separator = new JLabel();
		separator.setOpaque(true);
		separator.setBackground(Color.LIGHT_GRAY);
		separator.setPreferredSize(new Dimension(CONTENT_WIDTH, 1));
		separatorPanel.add(separator);
		topPanel.add(separatorPanel, BorderLayout.SOUTH);

		// 최상단 패널에 추가 (세로 배치)
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setOpaque(false);
		northPanel.add(topPanel);
		add(northPanel, BorderLayout.NORTH);
	}

    // 중앙 패널 생성: 검색/글작성 영역과 게시글 목록 영역 포함
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        // 패널 여백 설정: 검색 패널과 게시글 목록 시작 위치 정렬
        panel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // 상단: 검색 및 글 작성 영역
        JPanel topActionsPanel = createTopActionsPanel();
        panel.add(topActionsPanel, BorderLayout.NORTH);

        // 중앙: 게시글 목록 영역 (스크롤 가능)
        JScrollPane scrollPane = createBoardListScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // 상단 액션 패널: 검색 필드, 검색 버튼, 글 작성 버튼 배치
    private JPanel createTopActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // 검색 필드 설정
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // 검색 버튼 설정
		searchButton = createStyledButton("검색", 80, 35);
		searchButton.setBackground(Color.DARK_GRAY);
		searchButton.addActionListener(e -> performSearch(searchField.getText()));

		// spacer 설정: 좌우 여백을 맞추기 위해 사용
        JLabel spacer = new JLabel();
        spacer.setPreferredSize(new Dimension(210, 35));

        // 글 작성하기 버튼 설정 (클릭 시 BoardForm 실행)
		JButton writeButton = createStyledButton("글 작성하기", 120, 35);
		writeButton.setBackground(Color.DARK_GRAY);
		writeButton.addActionListener(e -> {
			dispose();
			new BoardForm();
		});

		panel.add(searchField);
		panel.add(searchButton);
		panel.add(spacer);
		panel.add(writeButton);

		return panel;
	}

    // 공통 스타일의 버튼 생성 메서드
	private JButton createStyledButton(String text, int width, int height) {
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(width, height));
		button.setFont(new Font("SansSerif", Font.BOLD, 14));
		button.setBackground(BUTTON_COLOR);
		button.setForeground(Color.WHITE);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}

    // 게시글 목록 스크롤 패널 생성
    private JScrollPane createBoardListScrollPane() {
        boardListPanel = new JPanel();
        boardListPanel.setOpaque(false);
        boardListPanel.setLayout(new BoxLayout(boardListPanel, BoxLayout.Y_AXIS));
        boardListPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // 고정 크기 컨테이너 패널 생성 (상단에 게시글 목록 배치)
		JPanel containerPanel = new JPanel(new BorderLayout());
		containerPanel.setOpaque(false);
		containerPanel.add(boardListPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(containerPanel);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		// 스크롤 패널 크기 설정
		Dimension scrollSize = new Dimension(CONTENT_WIDTH, 400);
		scrollPane.setPreferredSize(scrollSize);
		scrollPane.setMaximumSize(scrollSize);
		scrollPane.setMinimumSize(new Dimension(CONTENT_WIDTH, 400));

		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));

		// 스크롤바를 투명하게 설정
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getVerticalScrollBar().setOpaque(false);

        // 기본 스크롤바 UI 제거 및 사용자 정의 UI 적용
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(0, 0, 0, 0);
				this.trackColor = new Color(0, 0, 0, 0);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

            // 크기가 0인 버튼 생성 (스크롤바 버튼 제거)
			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
	               // 스크롤바 썸(thumb)을 그리지 않음
	            }

	            @Override
	            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
	                // 스크롤바 트랙(track)을 그리지 않음
			}
		});

		return scrollPane;
	}

    // 게시글 목록 업데이트: DB에서 읽어온 데이터를 기반으로 목록 갱신
	private void updateBoardList() {
		boardListPanel.removeAll();

		for (BoardBean board : boards) {
			JPanel postPanel = createPostPanel(board);
			boardListPanel.add(postPanel);
			boardListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		}

		boardListPanel.revalidate();
		boardListPanel.repaint();
	}

	// 개별 게시글 패널 생성: 제목, 내용 표시 및 클릭/호버 이벤트 추가
    private JPanel createPostPanel(BoardBean board) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // 패널 크기 설정 (최소, 선호, 최대 크기)
        Dimension panelSize = new Dimension(CONTENT_WIDTH, 90);
        panel.setMinimumSize(panelSize);
        panel.setPreferredSize(panelSize);
        panel.setMaximumSize(new Dimension(CONTENT_WIDTH, Integer.MAX_VALUE));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // 제목 라벨 생성 (너무 긴 경우 자르기)
        JLabel titleLabel = new JLabel(
                truncateText(board.getBoard_Title(), new Font("SansSerif", Font.BOLD, 18), CONTENT_WIDTH - 60));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 내용 라벨 생성 (HTML 태그를 사용하여 줄바꿈 처리)
		JLabel contentLabel = new JLabel("<html>"
				+ truncateText(board.getBoard_Content(), new Font("SansSerif", Font.PLAIN, 16), CONTENT_WIDTH - 60)
				+ "</html>");
		contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		contentLabel.setForeground(Color.DARK_GRAY);
		contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// 호버 및 클릭 이벤트 추가
		MouseAdapter adapter = createMouseAdapter(panel, board);
		panel.addMouseListener(adapter);
		titleLabel.addMouseListener(adapter);
		contentLabel.addMouseListener(adapter);

		panel.add(titleLabel);
		panel.add(Box.createRigidArea(new Dimension(0, 8)));
		panel.add(contentLabel);

		return panel;
	}

    // 마우스 이벤트 리스너 생성: 호버 시 배경색 변경 및 클릭 시 상세 페이지로 이동
    private MouseAdapter createMouseAdapter(JPanel panel, BoardBean board) {
        return new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				panel.setBackground(HOVER_COLOR);
				panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				panel.setBackground(Color.WHITE);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
                // 현재 창 위치를 저장 후 상세 페이지로 이동
				Point currentLocation = BoardPageAWT.this.getLocation();
				dispose();
				new BoardDetailPageAWT(board.getBoard_Id(), BoardPageAWT.this, currentLocation);
			}
		};
	}

    // 텍스트가 너무 길 경우 지정된 폭에 맞게 자르고 "..." 추가
	private String truncateText(String text, Font font, int maxWidth) {
		FontMetrics fm = getFontMetrics(font);
		if (fm.stringWidth(text) <= maxWidth) {
			return text;
		}

		String ellipsis = "...";
		int ellipsisWidth = fm.stringWidth(ellipsis);
		int availableWidth = maxWidth - ellipsisWidth;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			sb.append(text.charAt(i));
			if (fm.stringWidth(sb.toString()) > availableWidth) {
				sb.deleteCharAt(sb.length() - 1);
				break;
			}
		}
		return sb.toString() + ellipsis;
	}

    // 데이터베이스에서 게시글 로드 후 목록 업데이트
    public void loadBoards() {
        BoardMgr boardMgr = new BoardMgr();
        boardMgr.loadBoardsFromDB();
        boards = boardMgr.getBoards();
        updateBoardList();
    }

    // 검색어에 따라 게시글 검색 후 목록 업데이트
	private void performSearch(String searchText) {
		BoardMgr boardMgr = new BoardMgr();
		boards = boardMgr.searchBoards(searchText);
		updateBoardList();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			if (previousFrame != null) {
				previousFrame.setVisible(true);
			} else {
				new MainPageAWT(null, null);
			}
		}
	}

	public static void main(String[] args) {
		new BoardPageAWT(null);
	}
}
