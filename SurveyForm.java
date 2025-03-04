package Project;

//v.1
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import Project.MyInfoAWT.BackgroundPanel;

public class SurveyForm extends JFrame implements ActionListener {
	// 상단 헤더 관련 컴포넌트
	JButton backButton;
	JPanel topPanel, topRightPanel, buttonGroup, titlePanel, separatorContainer;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;

	// 메인 컨텐츠 영역 관련 컴포넌트
	JPanel mainPanel, formPanel, answersPanel, titleInputPanel;
	JLabel formTitleLabel;
	java.util.List<JTextField> answerFields;
	JButton addItemButton, submitButton;

	// 설문 제목 입력 필드 (tblpolllist의 question 컬럼에 저장됨)
	private JTextField titleField;

	// 상수: 고정 폭, 높이, 간격
	private static final int FIELD_WIDTH = 550; // 필드 폭 조정
	private static final int FIELD_HEIGHT = 35; // 필드 높이 조정
	private static final int VERTICAL_GAP = 8; // 항목 간격 줄이기

	// 로그인된 관리자와 회원 정보를 가져오기
	private ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
	private MemberBean currentUser = UserSession.getInstance().getCurrentUser();
	
	
	
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

	public SurveyForm() {
		setTitle("설문");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);
		
		 // 관리자가 아닐 경우 설문 작성 불가능 알림
	    if (currentAdmin == null) {
	        JOptionPane.showMessageDialog(this, "권한이 없습니다. 관리자만 설문을 작성할 수 있습니다.", "권한 오류", JOptionPane.ERROR_MESSAGE);
	        dispose(); // 설문 작성 화면 종료
	        return; // 더 이상 코드 실행되지 않도록 처리
	    }

		// ── 상단 헤더 영역 ─────────────────────────────
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

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);
		topPanel.add(titlePanel, BorderLayout.WEST);

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
				new LoginAWT();
			}
		});

		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, SurveyForm.this);
			}
		});

		spaceLabel = new JLabel("  ");
		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false);
		topRightPanel.add(buttonGroup);

		topPanel.add(topRightPanel, BorderLayout.EAST);

		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);

		// ── 메인 컨텐츠 영역 ─────────────────────────────
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(false);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));

		formTitleLabel = new JLabel("글 작성하기");
		formTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
		formTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// formPanel: 전체 폼 영역(입력 필드 + 버튼들)
		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBackground(new Color(240, 240, 240));
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// 제목 입력 패널 추가 (제목 입력 필드를 담는 패널)
		titleInputPanel = new JPanel(new BorderLayout());
		titleInputPanel.setBackground(new Color(240, 240, 240));
		titleField = new JTextField("제목");
		titleField.setFont(new Font("SansSerif", Font.PLAIN, 14));
		titleField.setForeground(Color.GRAY);
		titleField
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
						BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		Dimension titleSize = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);
		titleField.setPreferredSize(titleSize);
		titleField.setMaximumSize(titleSize);
		titleField.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (titleField.getText().equals("제목")) {
					titleField.setText("");
					titleField.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (titleField.getText().isEmpty()) {
					titleField.setText("제목");
					titleField.setForeground(Color.GRAY);
				}
			}
		});
		// "X" 버튼을 제목 입력 필드에 추가
		JButton clearTitleButton = new JButton("X");
		clearTitleButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		clearTitleButton.setForeground(Color.GRAY);
		clearTitleButton.setFocusPainted(false);
		clearTitleButton.setBorderPainted(false);
		clearTitleButton.setContentAreaFilled(false);
		clearTitleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		clearTitleButton.addActionListener(e -> titleField.setText(""));
		JPanel titlePanelWrapper = new JPanel(new BorderLayout());
		titlePanelWrapper.setBackground(new Color(240, 240, 240));
		titlePanelWrapper.add(titleField, BorderLayout.CENTER);
		titlePanelWrapper.add(clearTitleButton, BorderLayout.EAST);
		titleInputPanel.add(titlePanelWrapper, BorderLayout.CENTER);
		formPanel.add(titleInputPanel);
		formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));

		// answersPanel: 항목 입력 필드들을 담는 패널
		answersPanel = new JPanel();
		answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
		answersPanel.setBackground(new Color(240, 240, 240));

		answerFields = new ArrayList<>();
		// 최초 항목 3개 생성 (createTextFieldWithClearButton()에서 JTextField를 answerFields에 추가)
		for (int i = 0; i < 3; i++) {
			JPanel fieldPanel = createTextFieldWithClearButton("항목 입력");
			answersPanel.add(fieldPanel);
			answersPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
		}

		// "항목 추가" 버튼 (answersPanel 아래에 위치)
		addItemButton = new JButton("+ 항목 추가");
		addItemButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		addItemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addItemButton.setBorderPainted(false);
		addItemButton.setContentAreaFilled(false);
		addItemButton.setFocusPainted(false);
		addItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addItemButton.setForeground(Color.GRAY);
		addItemButton.setPreferredSize(new Dimension(200, FIELD_HEIGHT));
		addItemButton.addActionListener(e -> {
			JPanel newFieldPanel = createTextFieldWithClearButton("항목 입력");
			answersPanel.add(newFieldPanel);
			answersPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
			answersPanel.revalidate();
			answersPanel.repaint();
		});

		formPanel.add(answersPanel);
		formPanel.add(addItemButton);

		// 설문 종료시간 설정을 위한 JSpinner 패널
		JPanel datePanel = new JPanel(new BorderLayout());
		datePanel.setBackground(new Color(230, 230, 230));
		datePanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 20));

		JPanel leftDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftDatePanel.setBackground(new Color(230, 230, 230));
		JLabel dateTextLabel = new JLabel("설문 종료시간 설정");
		dateTextLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		leftDatePanel.add(dateTextLabel);

		JPanel rightDatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightDatePanel.setBackground(new Color(230, 230, 230));

		// 날짜를 선택하는 기본 날짜 설정
		Calendar cal = Calendar.getInstance(); // 현재 날짜 가져오기
		cal.add(Calendar.DAY_OF_MONTH, 7); // 현재 날짜에서 7일 더하기
		Date defaultDate = cal.getTime(); // 일주일 후 날짜

		SpinnerDateModel dateModel = new SpinnerDateModel(defaultDate, null, null, Calendar.DAY_OF_MONTH);
		JSpinner dateSpinner = new JSpinner(dateModel);

		// JSpinner에 대한 DateEditor 설정
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy.MM.dd");
		dateSpinner.setEditor(dateEditor);
		dateSpinner.setFont(new Font("SansSerif", Font.PLAIN, 12));

		// 날짜 선택 영역의 테두리와 배경 스타일 변경
		dateSpinner.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // 테두리 색상 및 두께 설정
		dateSpinner.setOpaque(false);

		// 테두리와 필드 간의 간격을 맞추기 위한 패딩 설정
		rightDatePanel.add(dateSpinner);
		datePanel.add(leftDatePanel, BorderLayout.WEST);
		datePanel.add(rightDatePanel, BorderLayout.EAST);
		datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		formPanel.add(datePanel);


		rightDatePanel.add(dateSpinner);
		datePanel.add(leftDatePanel, BorderLayout.WEST);
		datePanel.add(rightDatePanel, BorderLayout.EAST);
		datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		formPanel.add(datePanel);

		// ── 하단 제출 패널 ─────────────────────────────
		JPanel submitPanel = new JPanel();
		submitPanel.setLayout(new BoxLayout(submitPanel, BoxLayout.Y_AXIS));
		submitPanel.setBackground(new Color(230, 230, 230));
		submitPanel.setBorder(null); // 버튼의 테두리 제거

		submitButton = new JButton("추가하기");
		submitButton.setFont(new Font("SansSerif", Font.BOLD, 12));
		submitButton.setBackground(new Color(100, 150, 220));
		submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		submitButton.setForeground(Color.WHITE);
		submitButton.setFocusPainted(false);
		submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		submitButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		submitButton.addActionListener(e -> {
			// 제목 입력 필드 체크
			String title = titleField.getText().trim();
			if (title.isEmpty() || title.equals("제목")) {
				JOptionPane.showMessageDialog(SurveyForm.this, "제목을 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 항목 입력이 2개 이상 있는지 검사
			int filledCount = 0;
			for (JTextField field : answerFields) {
				String txt = field.getText().trim();
				if (!txt.isEmpty() && !txt.equals("항목 입력")) {
					filledCount++;
				}
			}
			if (filledCount < 2) {
				JOptionPane.showMessageDialog(SurveyForm.this, "두 개 이상의 항목을 입력해주세요.", "입력 오류",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 종료 날짜 가져오기
			java.util.Date utilEndDate = (java.util.Date) dateSpinner.getValue();
			java.sql.Date sqlEndDate = new java.sql.Date(utilEndDate.getTime());

			// 설문 항목 수집
			Vector<String> items = new Vector<>();
			for (JTextField field : answerFields) {
				String itemText = field.getText().trim();
				if (!itemText.isEmpty() && !itemText.equals("항목 입력")) {
					items.add(itemText);
				}
			}

			// SurveyMgr를 통해 설문 생성 (입력한 제목은 tblpolllist의 question 컬럼에 저장됨)
			SurveyMgr surveyMgr = new SurveyMgr();
			boolean result = surveyMgr.createSurvey(title, sqlEndDate, items);

			if (result) {
				JOptionPane.showMessageDialog(SurveyForm.this, "설문이 성공적으로 생성되었습니다.");
				dispose();
				new SurveyPageAWT(); // 설문 생성 후 설문 목록 페이지로 이동
			} else {
				JOptionPane.showMessageDialog(SurveyForm.this, "설문 생성에 실패했습니다.");
			}
		});

		submitPanel.add(submitButton);
		submitPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

		formPanel.add(Box.createVerticalStrut(VERTICAL_GAP));
		formPanel.add(submitPanel);

		mainPanel.add(formTitleLabel);
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(formPanel);

		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setOpaque(false); // 배경 투명하게 설정
		scrollPane.getViewport().setOpaque(false);
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setPreferredSize(new Dimension(0, 0));
		verticalBar.setUI(new BasicScrollBarUI() {
			@Override
			protected JButton createDecreaseButton(int orientation) {
				return new JButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return new JButton();
			}

			@Override
			protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			}
		});

		// JScrollPane을 감싸는 JPanel 생성
		JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
		scrollPaneWrapper.setOpaque(false);
		scrollPaneWrapper.add(scrollPane, BorderLayout.CENTER);
		scrollPaneWrapper.add(Box.createVerticalStrut(25), BorderLayout.SOUTH); // 25px 아래 여백 추가

		add(topPanel, BorderLayout.NORTH);
		add(scrollPaneWrapper, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// "X" 버튼이 포함된 텍스트 필드 패널 생성 (항목 입력용)
	private JPanel createTextFieldWithClearButton(String placeholder) {
		JTextField field = new JTextField(placeholder);
		field.setFont(new Font("SansSerif", Font.PLAIN, 12));
		field.setForeground(Color.GRAY);
		field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		Dimension size = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);
		field.setPreferredSize(size);
		field.setMaximumSize(size);
		field.setAlignmentX(Component.CENTER_ALIGNMENT);

		// answerFields 리스트에 추가 (이렇게 하면 제출 시 항목이 제대로 체크됩니다.)
		answerFields.add(field);

		JButton clearButton = new JButton("X");
		clearButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
		clearButton.setForeground(Color.GRAY);
		clearButton.setFocusPainted(false);
		clearButton.setBorderPainted(false);
		clearButton.setContentAreaFilled(false);
		clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		clearButton.addActionListener(e -> field.setText(""));

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(new Color(240, 240, 240));
		panel.add(field, BorderLayout.CENTER);
		panel.add(clearButton, BorderLayout.EAST);

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (field.getText().isEmpty()) {
					field.setText(placeholder);
					field.setForeground(Color.GRAY);
				}
			}
		});
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			new SurveyPageAWT(); // 설문 목록 페이지로 돌아가기
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new SurveyForm());
	}
}
