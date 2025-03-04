package Project;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class RentalAWT extends JFrame implements ActionListener {

	// UI 컴포넌트 선언
	private JButton searchButton, backButton, addButton, deleteButton;
	private JPanel topPanel, topRightPanel, searchPanel, buttonGroup, RentalListPanel;
	private JLabel titleLabel, logoutLabel, menuLabel, spaceLabel;
	private JTextField searchField;

	// 대여 데이터 및 체크박스 리스트
	private ArrayList<RentalBean> rentals;
	private ArrayList<JCheckBox> rowcheckboxes = new ArrayList<>();
	private JCheckBox headerCheckBox;

	// 이전 프레임과 위치 정보를 저장 (뒤로가기 기능 등에서 사용)
	private JFrame previousFrame;
	private Point previousLocation;

	// UI 색상 상수
	private final Color HEADER_BG_COLOR = new Color(33, 37, 41);
	private final Color HEADER_TEXT_COLOR = Color.WHITE;
	private final Color ROW_ALTERNATE_COLOR = new Color(248, 249, 250);
	private final Color BUTTON_COLOR = new Color(51, 51, 51);
	private final Color BORDER_COLOR = new Color(222, 226, 230);

	// ----------------- 내부 BackgroundPanel 클래스 -----------------
	// 배경에 보드게임 패턴과 반투명 오버레이를 그리는 커스텀 패널 클래스
	class BackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			// 기본 배경색 채우기
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, w, h);

			// 보드게임 패턴 그리기
			drawGamePattern(g2d, w, h);

			// 반투명 오버레이
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
			g2d.setColor(new Color(180, 180, 200));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 5);
		}
	}

	// ----------------- 생성자 -----------------
    // 기본 생성자 (이전 프레임, 위치 정보 없이 호출)
	public RentalAWT() {
		this(null, null);
	}

    // 이전 프레임과 위치 정보를 전달받는 생성자 (뒤로가기 시 활용)
	public RentalAWT(JFrame previousFrame, Point previousLocation) {
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;
		rentals = new ArrayList<>();

		setTitle("대여 관리");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

        // 커스텀 배경 패널 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

        // 상단 패널(제목, 로그아웃, 메뉴 등) 생성
		topPanel = createTopPanel();

		// 검색 패널 생성
		searchPanel = createStyledSearchPanel();

        // 상단 두 패널을 세로로 배치
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setOpaque(false);
		northPanel.add(topPanel);
		northPanel.add(searchPanel);
		add(northPanel, BorderLayout.NORTH);

		// 대여 목록 패널 (테이블 형식)
		RentalListPanel = new JPanel();
		RentalListPanel.setLayout(new BoxLayout(RentalListPanel, BoxLayout.Y_AXIS));
		RentalListPanel.setOpaque(false);
		RentalListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 스크롤 패널에 대여 목록 패널 추가
		JScrollPane scrollPane = createStyledScrollPane(RentalListPanel);
		add(scrollPane, BorderLayout.CENTER);

        // 위치 중앙 배치 후 프레임 표시
		setLocationRelativeTo(null);
		setVisible(true);

        // 대여 내역 로드
		loadRentals();
	}

	// ----------------- 헬퍼 메서드 -----------------
	private JLabel createStyledLabel(String text, int fontSize) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return label;
	}

    // 스타일이 적용된 버튼 생성 (배경색 지정 및 공통 스타일 적용)
	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.BOLD, 13));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		button.setFocusPainted(false);
		button.addActionListener(this);
		return button;
	}

    // 검색 패널 생성 : 검색 필드와 검색, 추가, 삭제 버튼 포함
	private JPanel createStyledSearchPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 55, 10, 0));

		searchField = new JTextField(10);
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
		searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BUTTON_COLOR),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		searchButton = createStyledButton("검색", BUTTON_COLOR);
		addButton = createStyledButton("추가", BUTTON_COLOR);
		deleteButton = createStyledButton("삭제", BUTTON_COLOR);

		panel.add(searchField);
		panel.add(searchButton);
		panel.add(addButton);
		panel.add(deleteButton);

		return panel;
	}

    // 스크롤 패널 생성 : 대여 목록 패널을 감싸며 스크롤바 스타일 적용
	private JScrollPane createStyledScrollPane(JPanel panel) {
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 55, 20, 55));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		// 스크롤바 두께 설정
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));

		// 세로 스크롤바 스타일링
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = BUTTON_COLOR;
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
				return button;
			}
		});

		// 가로 스크롤바 스타일링
		scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = BUTTON_COLOR;
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
				return button;
			}
		});

		return scrollPane;
	}

    // 컴포넌트를 중앙에 배치하는 패널 생성 (지정된 크기로 고정)
	private JPanel createCenteredPanel(JComponent comp, Dimension dim) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		panel.setPreferredSize(dim);
		panel.setMinimumSize(dim);
		panel.setMaximumSize(dim);
		panel.add(comp);
		return panel;
	}

    // 데이터 라벨 생성 (중앙 정렬, 지정 크기)
	private JLabel createDataLabel(String text, Dimension dim) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("SansSerif", Font.PLAIN, 14));
		label.setPreferredSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
		return label;
	}

    // 구분선 생성 (가로 방향, 최대 넓이 설정)
	private JSeparator createSeparator() {
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setForeground(BORDER_COLOR);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		return separator;
	}

    // 상단 패널 생성 : 뒤로가기 버튼, 제목, 로그아웃, 메뉴 등 포함
	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

        // 뒤로가기 버튼 설정
		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

        // 제목 라벨 설정
		titleLabel = new JLabel("대여관리", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

        // 왼쪽 제목 영역 패널 구성
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);
		panel.add(titlePanel, BorderLayout.WEST);

        // 로그아웃 라벨 생성 (클릭 시 로그인 화면으로 전환)
		logoutLabel = createStyledLabel("로그아웃", 15);
		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				dispose();
				new LoginAWT();
			}
		});

        // 메뉴 라벨 생성 (클릭 시 팝업 메뉴 표시)
		menuLabel = createStyledLabel("≡", 23);
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, RentalAWT.this);
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
		panel.add(topRightPanel, BorderLayout.EAST);

        // 구분선 추가 (상단 패널 하단)
		JLabel separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		JPanel separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);
		panel.add(separatorContainer, BorderLayout.SOUTH);

		return panel;
	}

    // 대여 테이블 헤더 생성 : 체크박스 및 각 컬럼 라벨 배치
	private JPanel createRentalTableHeader() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setBackground(HEADER_BG_COLOR);
		headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        // 헤더 체크박스 선택 시 모든 행의 체크박스 상태 변경
		Dimension checkDim = new Dimension(50, 30);
		headerCheckBox = new JCheckBox();
		headerCheckBox.setOpaque(false);
		headerCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		headerCheckBox.setVerticalAlignment(SwingConstants.CENTER);
		headerCheckBox.setMargin(new Insets(0, 0, 0, 0));
		headerCheckBox.addActionListener(e -> {
			for (JCheckBox cb : rowcheckboxes) {
				cb.setSelected(headerCheckBox.isSelected());
			}
		});
		headerPanel.add(createCenteredPanel(headerCheckBox, checkDim));

        // 각 컬럼에 대한 라벨과 크기 설정
		Dimension idDim = new Dimension(40, 30);
		Dimension rentaldateDim = new Dimension(150, 30);
		Dimension returndateDim = new Dimension(150, 30);
		Dimension stuIdDim = new Dimension(100, 30);
		Dimension nameDim = new Dimension(150, 30);
		Dimension memberidDim = new Dimension(100, 30);
		Dimension phoneDim = new Dimension(200, 30);
		Dimension penaltyDim = new Dimension(120, 30);
		Dimension rentalstatusDim = new Dimension(170, 30);

		String[] headers = { "No.", "대여일자", "반납일자", "학번", "이름", "아이디", "연락처", "패널티", "대여상태" };
		Dimension[] dims = { idDim, rentaldateDim, returndateDim, stuIdDim, nameDim, memberidDim, phoneDim, penaltyDim,
				rentalstatusDim };

		for (int i = 0; i < headers.length; i++) {
			JLabel label = new JLabel(headers[i], SwingConstants.CENTER);
			label.setFont(new Font("SansSerif", Font.BOLD, 16));
			label.setForeground(HEADER_TEXT_COLOR);
			headerPanel.add(createCenteredPanel(label, dims[i]));
		}

		return headerPanel;
	}

    // 대여 목록의 각 행(데이터 행)을 생성하는 메서드
	private JPanel createRentalRowPanel(RentalBean rental, int rowNumber) {
		Dimension checkDim = new Dimension(50, 30);
		Dimension idDim = new Dimension(40, 30);
		Dimension rentaldateDim = new Dimension(150, 30);
		Dimension returndateDim = new Dimension(150, 30);
		Dimension stuIdDim = new Dimension(100, 30);
		Dimension nameDim = new Dimension(150, 30);
		Dimension memberidDim = new Dimension(100, 30);
		Dimension phoneDim = new Dimension(200, 30);
		Dimension penaltyDim = new Dimension(120, 30);
		Dimension rentalstatusDim = new Dimension(170, 30);

		JPanel rowPanel = new JPanel();
		rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
		rowPanel.setOpaque(true);
        // 짝수/홀수 행에 따라 배경색 교차 적용
		Color bg = (rowNumber % 2 == 0) ? Color.WHITE : ROW_ALTERNATE_COLOR;
		rowPanel.setBackground(bg);

        // 각 행의 체크박스 생성 및 리스트에 추가
		JCheckBox rowCheckBox = new JCheckBox();
		rowCheckBox.setPreferredSize(checkDim);
		rowCheckBox.setMaximumSize(checkDim);
		rowCheckBox.setOpaque(false);
		rowCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		rowCheckBox.setVerticalAlignment(SwingConstants.CENTER);
		rowCheckBox.setMargin(new Insets(0, 0, 0, 0));
		rowcheckboxes.add(rowCheckBox);
		rowPanel.add(createCenteredPanel(rowCheckBox, checkDim));

        // 각 컬럼 데이터 라벨 생성
		JLabel idLabel = createDataLabel(String.valueOf(rowNumber), idDim);
		JLabel rentaldateLabel = createDataLabel(rental.getRentalDate(), rentaldateDim);
		String returnDate = rental.getReturnDate();
		if (returnDate == null) {
			returnDate = "-";
		}
		JLabel returndateLabel = createDataLabel(returnDate, returndateDim);
		JLabel stuIdLabel = createDataLabel(rental.getStuId(), stuIdDim);
		JLabel nameLabel = createDataLabel(rental.getMemberName(), nameDim);
		JLabel memberidLabel = createDataLabel(rental.getMemberId(), memberidDim);
		JLabel phoneLabel = createDataLabel(rental.getMemberPhone(), phoneDim);

        // 패널티 조절 패널 (마이너스, 패널티 수치, 플러스 버튼)
		JPanel penaltyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		penaltyPanel.setOpaque(false);
		penaltyPanel.setPreferredSize(penaltyDim);
		penaltyPanel.setMinimumSize(penaltyDim);
		penaltyPanel.setMaximumSize(penaltyDim);

		JLabel minusLabel = new JLabel("-");
		minusLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));
		minusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		minusLabel.setPreferredSize(new Dimension(20, 20));
		minusLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		minusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

		JLabel penaltyLabel = new JLabel(String.valueOf(rental.getPenaltyCount()), SwingConstants.CENTER);
		penaltyLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

		JLabel plusLabel = new JLabel("+");
		plusLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		plusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		plusLabel.setPreferredSize(new Dimension(20, 20));
		plusLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		penaltyPanel.add(minusLabel);
		penaltyPanel.add(penaltyLabel);
		penaltyPanel.add(plusLabel);

        // 패널티 감소 이벤트 리스너
		minusLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int currentPenalty = rental.getPenaltyCount();
				int newPenalty = currentPenalty - 1;
				if (newPenalty < 0) {
					newPenalty = 0;
				}
				RentalMgr rentalMgr = new RentalMgr();
				boolean updated = rentalMgr.updatePenalty(rental.getMemberId(), newPenalty);
				if (updated) {
					rental.setPenaltyCount(newPenalty);
					penaltyLabel.setText(String.valueOf(newPenalty));
				} else {
					JOptionPane.showMessageDialog(RentalAWT.this, "패널티 업데이트에 실패하였습니다.");
				}
			}
		});

        // 패널티 증가 이벤트 리스너 (최대 3까지만 허용)
		plusLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int currentPenalty = rental.getPenaltyCount();
				if (currentPenalty >= 3) {
					JOptionPane.showMessageDialog(RentalAWT.this, "패널티는 최대 3까지만 적용됩니다.");
					return;
				}
				int newPenalty = currentPenalty + 1;
				RentalMgr rentalMgr = new RentalMgr();
				boolean updated = rentalMgr.updatePenalty(rental.getMemberId(), newPenalty);
				if (updated) {
					rental.setPenaltyCount(newPenalty);
					penaltyLabel.setText(String.valueOf(newPenalty));
				} else {
					JOptionPane.showMessageDialog(RentalAWT.this, "패널티 업데이트에 실패하였습니다.");
				}
			}
		});

        // 대여 상태 데이터 라벨 (예: 보드게임 아이디)
		JLabel rentalstatusLabel = createDataLabel(rental.getGameId(), rentalstatusDim);

        // 각 컬럼 패널들을 행 패널에 추가
		rowPanel.add(createCenteredPanel(idLabel, idDim));
		rowPanel.add(createCenteredPanel(rentaldateLabel, rentaldateDim));
		rowPanel.add(createCenteredPanel(returndateLabel, returndateDim));
		rowPanel.add(createCenteredPanel(stuIdLabel, stuIdDim));
		rowPanel.add(createCenteredPanel(nameLabel, nameDim));
		rowPanel.add(createCenteredPanel(memberidLabel, memberidDim));
		rowPanel.add(createCenteredPanel(phoneLabel, phoneDim));
		rowPanel.add(createCenteredPanel(penaltyPanel, penaltyDim));
		rowPanel.add(createCenteredPanel(rentalstatusLabel, rentalstatusDim));

		return rowPanel;
	}

    // 대여 목록 업데이트 : 데이터 행 생성 후 목록 패널 갱신
	private void updateRentalList() {
		RentalListPanel.removeAll();
		rowcheckboxes.clear();

		RentalListPanel.add(createRentalTableHeader());

		int rowNumber = 1;
		for (RentalBean rental : rentals) {
			JPanel rowPanel = createRentalRowPanel(rental, rowNumber);
			RentalListPanel.add(rowPanel);
			RentalListPanel.add(createSeparator());
			rowNumber++;
		}

		RentalListPanel.revalidate();
		RentalListPanel.repaint();
	}

    // 대여 데이터 로드 : RentalMgr를 통해 DB에서 대여 내역을 가져와 목록 업데이트
	public void loadRentals() {
		RentalMgr rentalMgr = new RentalMgr();
		Vector<RentalBean> vector = rentalMgr.listRental();
		rentals = new ArrayList<>();
		for (RentalBean bean : vector) {
			rentals.add(bean);
		}
		updateRentalList();
	}

    // 이벤트 처리 : 뒤로가기, 검색, 추가, 삭제 버튼에 대한 동작 구현
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == backButton) {
			dispose();
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MyPageAWT();
			}
		} else if (src == searchButton) {
			String keyword = searchField.getText().trim();
			if (keyword.isEmpty()) {
				loadRentals();
			} else {
				RentalMgr rentalMgr = new RentalMgr();
				Vector<RentalBean> vector = rentalMgr.listRental();
				rentals = new ArrayList<>();
                // 검색어가 회원 이름, 연락처, 보드게임 아이디에 포함되는 대여 내역만 선택
				for (RentalBean rental : vector) {
					if (rental.getMemberName().contains(keyword) || rental.getMemberPhone().contains(keyword)
							|| rental.getGameId().contains(keyword)) {
						rentals.add(rental);
					}
				}
				updateRentalList();
			}
		} else if (src == addButton) {
			showAddRentalDialog();
		} else if (src == deleteButton) {
			ArrayList<Integer> idsToDelete = new ArrayList<>();
			// 선택된 행의 체크박스에 해당하는 대여 내역의 ID 수집
			for (int i = 0; i < rowcheckboxes.size(); i++) {
				if (rowcheckboxes.get(i).isSelected()) {
					idsToDelete.add(rentals.get(i).getRentalId());
				}
			}
			if (idsToDelete.isEmpty()) {
				JOptionPane.showMessageDialog(this, "삭제할 대여 내역을 선택해주세요.");
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "선택한 대여 내역을 삭제하시겠습니까?", "삭제 확인",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				RentalMgr rentalMgr = new RentalMgr();
				for (Integer id : idsToDelete) {
					rentalMgr.deleteRental(id);
				}
				JOptionPane.showMessageDialog(this, "선택한 대여 내역이 삭제되었습니다.");
				loadRentals();
			}
		}
	}

	// 새 대여 내역 추가 다이얼로그 표시
	private void showAddRentalDialog() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// 회원 아이디 선택 패널
		JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel memberLabel = new JLabel("회원 아이디 선택:");
		Choice memberChoice = new Choice();
		memberChoice.add("회원아이디 선택");

		MemberMgr mgr = new MemberMgr();
		Vector<String> memberIdList = mgr.getMemberIdList();
		for (String id : memberIdList) {
			memberChoice.add(id);
		}
		JTextField memberTf = new JTextField(15);
		memberTf.setEditable(false);
		memberChoice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int index = memberChoice.getSelectedIndex();
				if (index == 0) {
					memberTf.setText("");
				} else {
					memberTf.setText(memberChoice.getItem(index));
				}
			}
		});
		memberPanel.add(memberLabel);
		memberPanel.add(memberChoice);
		memberPanel.add(memberTf);
		panel.add(memberPanel);

		// 보드게임 선택 패널
		JPanel gamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel gameLabel = new JLabel("보드게임 선택:");
		BoardGameMgr boardGameMgr = new BoardGameMgr();
		Vector<BoardGameBean> gameVector = boardGameMgr.listGame();
		JList<BoardGameBean> gameList = new JList<>(gameVector);
		gameList.setVisibleRowCount(5);
		JScrollPane gameScrollPane = new JScrollPane(gameList);
		gameScrollPane.setPreferredSize(new Dimension(200, 100));
		gamePanel.add(gameLabel);
		gamePanel.add(gameScrollPane);
		panel.add(gamePanel);

        // 다이얼로그 결과에 따른 처리
		int result = JOptionPane.showConfirmDialog(this, panel, "새 대여 내역 추가", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			String memberId = memberTf.getText().trim();
			BoardGameBean selectedGame = gameList.getSelectedValue();
			if (memberId.isEmpty() || selectedGame == null) {
				JOptionPane.showMessageDialog(this, "회원 아이디와 보드게임을 모두 선택해주세요.");
				return;
			}
			RentalMgr rentalMgr = new RentalMgr();
			boolean inserted = rentalMgr.insertRental(memberId, selectedGame.getGameId());
			if (inserted) {
				JOptionPane.showMessageDialog(this, "새 대여 내역이 추가되었습니다.");
				loadRentals();
			} else {
				JOptionPane.showMessageDialog(this, "이미 대여중이거나 하나만 빌릴 수 있습니다.");
			}
		}
	}

	public static void main(String[] args) {
		new RentalAWT();
	}
}
