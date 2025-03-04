package Project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.text.SimpleDateFormat;
import java.util.Date;
import Project.MyInfoAWT.BackgroundPanel;

import java.util.*;
import java.sql.*;

public class SurveyPageAWT extends JFrame implements ActionListener {
	JButton backButton;
	JPanel topPanel, separatorContainer;
	JPanel surveyPanel;
	JPanel ongoingPanel;
	JPanel finishedPanel;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel;
	JButton writeButton;
	Color primaryColor = new Color(65, 105, 225); // 로열 블루
	Color lightGrey = new Color(240, 240, 240);
	Color darkGrey = new Color(120, 120, 120);

	// 현재 로그인된 사용자 ID를 세션에서 받아올 수 있어야 함 (세션에서 userId를 가져온다고 가정)
	private int userId;

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

	public SurveyPageAWT() {
		// 임시로 userId 설정
		userId = 1; // 로그인된 사용자 ID

		setTitle("설문");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		JPanel northContainer = new JPanel();
		northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
		northContainer.setOpaque(false);
		northContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		createTopPanel(); // 기존 상단 패널 생성
		northContainer.add(topPanel);

		// "글작성하기" 버튼 패널 - 디자인 개선
		JPanel writeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		writeContainer.setOpaque(false);
		writeContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 60));
		writeButton = new JButton("글 작성하기");
		writeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
		writeButton.setForeground(Color.WHITE);
		writeButton.setBackground(primaryColor);
		writeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		writeButton.setFocusPainted(false);
		writeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
		writeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MemberBean currentUser = UserSession.getInstance().getCurrentUser();
				ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();

				// 관리자가 아닌 경우에 권한 없다는 메시지 띄우기
				if (currentAdmin == null) {
					JOptionPane.showMessageDialog(SurveyPageAWT.this, "권한이 없습니다.", "권한 오류", JOptionPane.ERROR_MESSAGE);
				} else {
					// 관리자인 경우 설문 작성 페이지로 이동
					dispose();
					new SurveyForm();
				}
			}
		});

		writeContainer.add(writeButton);
		northContainer.add(writeContainer);

		add(northContainer, BorderLayout.NORTH);

		createSurveyList(); // 설문 목록을 생성하고 표시
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void createTopPanel() {
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		titleLabel = new JLabel("설문", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃 및 메뉴
		logoutLabel = new JLabel("로그아웃");
		logoutLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 로그아웃 처리: 세션 정보 초기화
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				dispose();
				new LoginAWT(); // 로그인 화면으로 이동
			}
		});

		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 메뉴 팝업 관리 (기능은 유지)
				PopupMenuManager.showPopupMenu(menuLabel, SurveyPageAWT.this);
			}
		});

		JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(new JLabel("  "));
		buttonGroup.add(menuLabel);

		topPanel.add(buttonGroup, BorderLayout.EAST);

		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);
	}

	private void createSurveyList() {
	    SurveyMgr surveyMgr = new SurveyMgr();
	    Vector<SurveyBean> surveys = surveyMgr.listSurveys(); // 설문 목록 조회

	    surveyPanel = new JPanel(new BorderLayout());
	    surveyPanel.setOpaque(false);

	    JPanel contentPanel = new JPanel();
	    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
	    contentPanel.setOpaque(false);
	    contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 20, 40));

	    // 진행 중인 설문 섹션 - 디자인 개선
	    JPanel headerPanel1 = new JPanel(new BorderLayout());
	    headerPanel1.setOpaque(false);
	    JLabel ongoingHeader = new JLabel("진행 중인 설문");
	    ongoingHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
	    ongoingHeader.setForeground(primaryColor);
	    ongoingHeader.setBorder(BorderFactory.createEmptyBorder(30, 30, 5, 0)); // 위 여백 줄이기
	    headerPanel1.add(ongoingHeader, BorderLayout.WEST);
	    
	    
	    // 진행 중 설문 카운트 표시 추가
	    JLabel countLabel1 = new JLabel();
	    int ongoingCount = (int) surveys.stream()
	            .filter(s -> isSurveyOngoing(s)) // 진행 중인 설문만 필터링
	            .map(SurveyBean::getPollId)
	            .distinct().count();
	    countLabel1.setText(ongoingCount + "개");
	    countLabel1.setFont(new Font("SansSerif", Font.PLAIN, 14));
	    countLabel1.setForeground(darkGrey);
	    countLabel1.setBorder(BorderFactory.createEmptyBorder(30, 0, 5, 30)); // 위 여백 줄이기
	    headerPanel1.add(countLabel1, BorderLayout.EAST);

	    // 구분선 추가
	    JSeparator ongoingSeparator = new JSeparator();
	    ongoingSeparator.setForeground(primaryColor);
	    ongoingSeparator.setMaximumSize(new Dimension(650, 2));

	    // 진행 중인 설문 섹션 내용 추가
	    contentPanel.add(headerPanel1);
	    contentPanel.add(ongoingSeparator);
	    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

	    ongoingPanel = new JPanel();
	    ongoingPanel.setLayout(new BoxLayout(ongoingPanel, BoxLayout.Y_AXIS));
	    ongoingPanel.setOpaque(false);
	    ongoingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

	    Set<Integer> addedSurveyIds = new HashSet<>(); // 중복된 설문 제목 추가 방지
	    boolean hasOngoingSurveys = false;
	    for (SurveyBean survey : surveys) {
	        if (isSurveyOngoing(survey) && !addedSurveyIds.contains(survey.getPollId())) { // 진행 중인 설문만 추가
	            addOngoingSurveyItem(survey.getQuestion(), "진행 중", survey.getPollId());
	            addedSurveyIds.add(survey.getPollId()); // 설문 제목은 한 번만 추가
	            hasOngoingSurveys = true;
	        }
	    }

	    // 진행 중인 설문이 없을 경우 빈 상태 표시
	    if (!hasOngoingSurveys) {
	        addEmptyStatePanel(ongoingPanel, "진행 중인 설문이 없습니다.", true);
	    }

	    contentPanel.add(ongoingPanel);

	    // 종료된 설문 섹션 - 디자인 개선
	    JPanel headerPanel2 = new JPanel(new BorderLayout());
	    headerPanel2.setOpaque(false);
	    JLabel finishedHeader = new JLabel("종료된 설문");
	    finishedHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
	    finishedHeader.setForeground(new Color(100, 100, 100));
	    finishedHeader.setBorder(BorderFactory.createEmptyBorder(30, 30, 5, 0)); // 위 여백 줄이기
	    headerPanel2.add(finishedHeader, BorderLayout.WEST);

	    // 종료된 설문 카운트 표시 추가
	    JLabel countLabel2 = new JLabel();
	    int finishedCount = (int) surveys.stream()
	            .filter(s -> !isSurveyOngoing(s)) // 종료된 설문만 필터링
	            .map(SurveyBean::getPollId)
	            .distinct().count();
	    countLabel2.setText(finishedCount + "개");
	    countLabel2.setFont(new Font("SansSerif", Font.PLAIN, 14));
	    countLabel2.setForeground(darkGrey);
	    countLabel2.setBorder(BorderFactory.createEmptyBorder(30, 0, 5, 30)); // 위 여백 줄이기
	    headerPanel2.add(countLabel2, BorderLayout.EAST);

	    // 구분선 추가
	    JSeparator finishedSeparator = new JSeparator();
	    finishedSeparator.setForeground(new Color(180, 180, 180));
	    finishedSeparator.setMaximumSize(new Dimension(650, 2));

	    // 종료된 설문 섹션 내용 추가
	    contentPanel.add(headerPanel2);
	    contentPanel.add(finishedSeparator);
	    contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

	    finishedPanel = new JPanel();
	    finishedPanel.setLayout(new BoxLayout(finishedPanel, BoxLayout.Y_AXIS));
	    finishedPanel.setOpaque(false);
	    finishedPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 추가

	    // 종료된 설문 항목 추가
	    addedSurveyIds.clear(); // 종료된 설문에서도 중복 처리
	    boolean hasFinishedSurveys = false;
	    for (SurveyBean survey : surveys) {
	        if (!isSurveyOngoing(survey) && !addedSurveyIds.contains(survey.getPollId())) { // 종료된 설문만 추가
	            addFinishedSurveyItem(survey.getQuestion(), "종료됨", survey.getPollId());
	            addedSurveyIds.add(survey.getPollId()); // 설문 제목은 한 번만 추가
	            hasFinishedSurveys = true;
	        }
	    }

	    // 종료된 설문이 없을 경우 빈 상태 표시
	    if (!hasFinishedSurveys) {
	        addEmptyStatePanel(finishedPanel, "종료된 설문이 없습니다.", false);
	    }

	    contentPanel.add(finishedPanel);

	    JPanel centerPanel = new JPanel(new BorderLayout());
	    centerPanel.setOpaque(false);
	    centerPanel.add(contentPanel, BorderLayout.CENTER);

	    surveyPanel.add(centerPanel, BorderLayout.CENTER);

	    JScrollPane scrollPane = new JScrollPane(surveyPanel);
	    scrollPane.setBorder(null);
	    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	    scrollPane.setOpaque(false); // 배경 투명하게 설정
	    scrollPane.getViewport().setOpaque(false);

	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    // 스크롤바 디자인 개선
	    scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
	        @Override
	        protected void configureScrollBarColors() {
	            this.thumbColor = new Color(200, 200, 200);
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

	    // JScrollPane을 감싸는 JPanel 생성
	    JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
	    scrollPaneWrapper.setOpaque(false);
	    scrollPaneWrapper.add(scrollPane, BorderLayout.CENTER);
	    scrollPaneWrapper.add(Box.createVerticalStrut(30), BorderLayout.SOUTH); // 25px 아래 여백 추가

	    add(scrollPaneWrapper, BorderLayout.CENTER);
	}
	
	private boolean isSurveyOngoing(SurveyBean survey) {
	    // 현재 날짜가 종료일보다 이전이면 진행 중으로 판단
	    Date currentDate = new Date();  // 현재 날짜
	    String surveyEndDateString = survey.getEndDate();  // String 타입으로 반환된 종료일

	    // 날짜 문자열을 Date 객체로 변환
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 날짜 형식에 맞게 패턴 설정
	    try {
	        Date surveyEndDate = sdf.parse(surveyEndDateString);  // 문자열을 Date로 변환
	        return surveyEndDate.after(currentDate);  // 비교
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;  // 오류 발생 시 false 반환 (예: 날짜 파싱 오류)
	    }
	}


	// 빈 상태 패널을 추가하는 메서드
	private void addEmptyStatePanel(JPanel targetPanel, String message, boolean isOngoing) {
		JPanel emptyPanel = new JPanel();
		emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
		emptyPanel.setOpaque(false);
		emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		emptyPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
		emptyPanel.setMaximumSize(new Dimension(650, 120));

		// 아이콘 추가 (빈 박스 아이콘으로 대체)
		JLabel iconLabel = new JLabel("📊", JLabel.CENTER);
		iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
		iconLabel.setForeground(isOngoing ? primaryColor : darkGrey);
		iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 메시지 라벨
		JLabel messageLabel = new JLabel(message, JLabel.CENTER);
		messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		messageLabel.setForeground(darkGrey);
		messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 추가 안내 메시지
		JLabel hintLabel = new JLabel(isOngoing ? "새로운 설문을 작성해보세요." : "종료된 설문이 여기에 표시됩니다.", JLabel.CENTER);
		hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		hintLabel.setForeground(new Color(150, 150, 150));
		hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		emptyPanel.add(Box.createVerticalStrut(10));
		emptyPanel.add(iconLabel);
		emptyPanel.add(Box.createVerticalStrut(15));
		emptyPanel.add(messageLabel);
		emptyPanel.add(Box.createVerticalStrut(5));
		emptyPanel.add(hintLabel);

		targetPanel.add(emptyPanel);
	}

	// 진행 중인 설문 항목을 추가하는 메서드
	private void addOngoingSurveyItem(String title, String status, int pollId) {
		JPanel surveyItem = createSurveyItem(title, status, true);
		surveyItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		ongoingPanel.add(surveyItem);
		ongoingPanel.add(Box.createRigidArea(new Dimension(0, 15)));

		// 제목 클릭 시, 상세 보기 페이지로 이동
		surveyItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				new SurveyDetailPageAWT(pollId); // 설문 ID를 전달하여 상세 페이지로 이동
			}
		});
	}

	// 종료된 설문 항목을 추가하는 메서드
	private void addFinishedSurveyItem(String title, String status, int pollId) {
		JPanel surveyItem = createSurveyItem(title, status, false);
		surveyItem.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 추가
		finishedPanel.add(surveyItem);
		finishedPanel.add(Box.createRigidArea(new Dimension(0, 15)));

		// 제목 클릭 시, 상세 보기 페이지로 이동
		surveyItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
				new SurveyDetailPageAWT(pollId); // 설문 ID를 전달하여 상세 페이지로 이동
			}
		});
	}

	// 설문 항목을 생성하는 메서드
	private JPanel createSurveyItem(String title, String status, boolean isOngoing) {
		JPanel surveyItem = new JPanel(new BorderLayout());
		surveyItem.setOpaque(false);
		surveyItem.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
				BorderFactory.createEmptyBorder(0, 15, 10, 15)));
		surveyItem.setMaximumSize(new Dimension(650, 80));
		surveyItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 좌측 Q 라벨 패널
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setOpaque(false);
		leftPanel.setPreferredSize(new Dimension(40, 60));

		JLabel qLabel = new JLabel("Q", JLabel.CENTER);
		qLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
		qLabel.setForeground(isOngoing ? primaryColor : darkGrey);
		leftPanel.add(qLabel, BorderLayout.CENTER);

		// 중앙 내용 패널
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		// 제목 라벨 (왼쪽 정렬)
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		titleLabel.setForeground(isOngoing ? new Color(50, 50, 50) : darkGrey);
		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// 진행 중 상태 라벨 (수직으로 배치)
		JPanel contentPanel1 = new JPanel();
		contentPanel1.setLayout(new BoxLayout(contentPanel1, BoxLayout.Y_AXIS)); // 수직 정렬
		contentPanel1.setOpaque(false);
		contentPanel1.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		// 제목 라벨 먼저 추가
		contentPanel1.add(titleLabel);

		// "진행 중" 상태 라벨
		JLabel statusLabel = new JLabel(status);
		statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		statusLabel.setForeground(isOngoing ? new Color(0, 150, 0) : new Color(150, 150, 150));
		statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPanel1.add(statusLabel);

		// 우측 화살표 추가
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setOpaque(false);
		rightPanel.setPreferredSize(new Dimension(30, 0));
		rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		JLabel arrowLabel = new JLabel("›", JLabel.CENTER);
		arrowLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		arrowLabel.setForeground(new Color(180, 180, 180));
		rightPanel.add(arrowLabel, BorderLayout.CENTER);

		// 패널들을 메인 패널에 추가
		surveyItem.add(leftPanel, BorderLayout.WEST);
		surveyItem.add(contentPanel1, BorderLayout.CENTER);
		surveyItem.add(rightPanel, BorderLayout.EAST);

		return surveyItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			new MainPageAWT(); // 메인 페이지로 돌아가기
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SurveyPageAWT()); // 설문 페이지 실행
	}
}
