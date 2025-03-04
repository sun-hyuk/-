package Project;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Project.NoticeDetailPageAWT.BackgroundPanel;

public class FeeManagement extends JFrame implements ActionListener {
	JButton searchButton, backButton, homeButton, notificationButton, paymentButton, myPageButton, addButton,
			deleteButton;
	JPanel topPanel, topRightPanel, searchPanel, buttonGroup, bottomMenu, titlePanel, separatorContainer, feeListPanel,
			paginationPanel;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
	JTextField searchField;

	int currentPage = 1;
	int itemsPerPage = 5; // 한 페이지당 항목 수
	ArrayList<JButton> pageButtons = new ArrayList<>();
	// FeeRecordBean을 이용하여 회비 내역을 저장
	private ArrayList<FeeRecordBean> feeItems;
	private ArrayList<JCheckBox> rowcheckboxes = new ArrayList<>();
	private JCheckBox headerCheckBox; // 전체 선택 체크박스
	// FeeManagement.java (클래스 변수 선언 부분)
	private JLabel totalAssetLabel;

	// ← 추가된 필드 (이전 프레임 및 위치 정보)
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
	public FeeManagement() {
		this(null, null);
	}

	// 이전 프레임과 위치 정보를 받는 생성자
	public FeeManagement(JFrame previousFrame, Point previousLocation) {
		feeItems = new ArrayList<>();
		setTitle("회비 및 예산관리");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// 상단 패널
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false); // 배경 투명하게 설정

		// 뒤로가기 버튼
		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);

		// 버튼 위치 조정: 왼쪽 마진 50px 추가
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		// 제목 (단순히 표시만, 클릭 시 이동 없음)
		titleLabel = new JLabel("회비 및 예산관리", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		// 뒤로가기 버튼과 제목을 감싸는 패널
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
				dispose(); // 현재 창 닫기
				new LoginAWT(); // 로그인 페이지로 이동
			}
		});

		// 메뉴(텍스트 버튼)
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, FeeManagement.this); // 팝업 메뉴 호출
			}
		});

		// topRightPanel을 먼저 생성하여 NullPointerException 방지
		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false); // 배경 투명하게 설정

		spaceLabel = new JLabel("  "); // 공백 추가

		// 로그아웃, 메뉴 버튼 그룹화
		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 버튼 그룹 패널
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60)); // 오른쪽에서 60px 띄우기
		buttonGroup.setOpaque(false); // 배경 투명하게 설정
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 경계를 구분하는 1px 높이의 밝은 회색 선
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true); // JLabel을 배경색으로 채움
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2)); // 높이 1px

		// 선을 감싸는 패널 (가운데 정렬)
		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false); // 배경 투명하게 설정
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		// topPanel에 구분선 추가
		topPanel.add(separatorContainer, BorderLayout.SOUTH); // topPanel 내부에 추가

		// 검색 패널
		searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchPanel.setOpaque(false); // 배경 투명하게 설정
		searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // 오른쪽에 여백 추가

		searchField = new JTextField(10);
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
		searchButton = new JButton("검색");
		searchButton.setFont(new Font("SansSerif", Font.BOLD, 13));
		searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		searchButton.addActionListener(this);

		// 삭제 버튼
		deleteButton = new JButton("삭제");
		deleteButton.setFont(new Font("SansSerif", Font.BOLD, 13));
		deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		deleteButton.setBackground(Color.DARK_GRAY);
		deleteButton.setForeground(Color.WHITE);
		deleteButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		deleteButton.addActionListener(this);

		// 검색 패널에서 삭제 버튼 이후에 추가 버튼 생성
		addButton = new JButton("추가");
		addButton.setFont(new Font("SansSerif", Font.BOLD, 13));
		addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addButton.setBackground(Color.DARK_GRAY);
		addButton.setForeground(Color.WHITE);
		addButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		addButton.addActionListener(this);

		// 텍스트 필드와 버튼의 높이를 동일하게 지정 (예: 30픽셀)
		int uniformHeight = 25;
		searchField.setPreferredSize(new Dimension(100, uniformHeight)); // 너비 150, 높이 30
		searchButton.setPreferredSize(new Dimension(50, uniformHeight)); // 너비 80, 높이 30
		deleteButton.setPreferredSize(new Dimension(50, uniformHeight));
		addButton.setPreferredSize(new Dimension(50, uniformHeight));

		// darkgray 배경, 테두리, 대비를 위한 흰색 글씨 적용
		searchField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		searchButton.setBackground(Color.DARK_GRAY);
		searchButton.setForeground(Color.WHITE);
		searchButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		searchPanel.add(deleteButton);
		searchPanel.add(addButton);

		// 이제 topPanel과 searchPanel을 포함할 새로운 northPanel 생성
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setOpaque(false); // 배경 투명하게 설정
		northPanel.add(topPanel); // 상단 패널 추가
		northPanel.add(searchPanel); // 검색 패널 추가
		searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 65, 10, 0));

		add(northPanel, BorderLayout.NORTH);

		// 회원목록 패널
		feeListPanel = new JPanel(new GridBagLayout());
		feeListPanel.setOpaque(false); // 배경 투명하게 설정
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; // 첫 번째 열에 배치
		gbc.gridy = GridBagConstraints.RELATIVE; // 행은 자동 증가
		gbc.anchor = GridBagConstraints.CENTER; // 가운데 정렬

		updateFeeList(); // 초기 공지사항 표시
		JScrollPane scrollPane = new JScrollPane(feeListPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(600, 300));
		scrollPane.setMaximumSize(new Dimension(600, 300));
		scrollPane.setMinimumSize(new Dimension(600, 300));
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
		scrollPane.setOpaque(false); // 배경 투명하게 설정
		scrollPane.getViewport().setOpaque(false); // 뷰포트 배경 투명하게 설정

		// 래퍼 패널을 만들어 스크롤 패널을 감싸고, 좌우 여백을 20px 부여
		JPanel scrollWrapper = new JPanel(new BorderLayout());
		scrollWrapper.setOpaque(false); // 배경 투명하게 설정
		scrollWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		scrollWrapper.add(scrollPane, BorderLayout.CENTER);
		add(scrollWrapper);

		add(scrollWrapper);

		// 총 자산 표시 패널 생성 (오른쪽 정렬)
		totalAssetLabel = new JLabel("총 자산: 0원");
		totalAssetLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		JPanel assetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		assetPanel.setOpaque(false); // 배경 투명하게 설정
		assetPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 55));
		assetPanel.add(totalAssetLabel);

		// 페이지네이션 패널
		paginationPanel = new JPanel(new FlowLayout());
		paginationPanel.setOpaque(false); // 배경 투명하게 설정
		paginationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 23, 0));

		// 하단에 글쓰기 버튼과 페이지네이션 패널을 담을 컨테이너 패널 생성 (수직 배치)
		JPanel southContainer = new JPanel();
		southContainer.setLayout(new BoxLayout(southContainer, BoxLayout.Y_AXIS));
		southContainer.setOpaque(false); // 배경 투명하게 설정
		southContainer.add(assetPanel);
		southContainer.add(paginationPanel); // 아래쪽에 페이지네이션 패널
		add(southContainer, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
		loadFees();
	}

	// DB에서 FeeMgr를 통해 회비(지출) 내역을 불러옴
	public void loadFees() {
		FeeMgr feeMgr = new FeeMgr();
		Vector<FeeRecordBean> vector = feeMgr.listFee();
		feeItems = new ArrayList<>();
		for (FeeRecordBean bean : vector) {
			feeItems.add(bean);
		}
		currentPage = 1;
		updateFeeList();
		updatePagination();
		updateTotalAsset(); // 총 자산 업데이트
	}

	/** 현재 페이지에 맞게 회비 목록 업데이트 */
	private void updateFeeList() {
		feeListPanel.removeAll();
		rowcheckboxes.clear();

		// 각 열의 고정 크기 설정
		Dimension checkDim = new Dimension(50, 30);
		Dimension idDim = new Dimension(60, 30); // No. (체크박스 옆 번호)
		Dimension noteDim = new Dimension(200, 30); // 지출품목
		Dimension amountDim = new Dimension(150, 30); // 지출금액
		Dimension dateDim = new Dimension(200, 30); // 지출날짜

		// 헤더 패널 생성: 각 열의 시작 위치 및 너비 고정
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setOpaque(false); // 배경 투명하게 설정
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));

		// 헤더의 체크박스: 전체 선택
		headerCheckBox = new JCheckBox();
		headerCheckBox.setPreferredSize(checkDim);
		headerCheckBox.setMaximumSize(checkDim);
		headerCheckBox.setOpaque(false); // 배경 투명하게 설정
		headerCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = headerCheckBox.isSelected();
				// 현재 페이지에 보이는 모든 행 체크박스 선택/해제
				for (JCheckBox cb : rowcheckboxes) {
					cb.setSelected(selected);
				}
			}
		});
		headerPanel.add(headerCheckBox);

		JLabel idHeader = new JLabel("No.", SwingConstants.CENTER);
		idHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
		idHeader.setPreferredSize(idDim); // 번호 열 고정 너비 40px
		idHeader.setMinimumSize(idDim);
		idHeader.setMaximumSize(idDim);

		JLabel noteHeader = new JLabel("품목", SwingConstants.CENTER);
		noteHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
		noteHeader.setPreferredSize(noteDim); // 지출품목 열 고정 너비 200px
		noteHeader.setMinimumSize(noteDim);
		noteHeader.setMaximumSize(noteDim);

		JLabel amountHeader = new JLabel("지출금액", SwingConstants.CENTER);
		amountHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
		amountHeader.setPreferredSize(amountDim); // 지출금액 열 고정 너비 150px
		amountHeader.setMinimumSize(amountDim);
		amountHeader.setMaximumSize(amountDim);

		JLabel phoneHeader = new JLabel("지출일자", SwingConstants.CENTER);
		phoneHeader.setFont(new Font("SansSerif", Font.BOLD, 16));
		phoneHeader.setPreferredSize(dateDim); // 지출날짜 열 고정 너비 200px
		phoneHeader.setMinimumSize(dateDim);
		phoneHeader.setMaximumSize(dateDim);

		headerPanel.add(idHeader);
		headerPanel.add(noteHeader);
		headerPanel.add(amountHeader);
		headerPanel.add(phoneHeader);

		// GridBagLayout을 사용하여 헤더 패널을 맨 위에 추가 (행 0)
		GridBagConstraints gbcHeader = new GridBagConstraints();
		gbcHeader.gridx = 0;
		gbcHeader.gridy = 0;
		gbcHeader.anchor = GridBagConstraints.CENTER;
		feeListPanel.add(headerPanel, gbcHeader);

		// 공지사항 목록 추가
		int gridY = 1;
		int start = (currentPage - 1) * itemsPerPage;
		int end = Math.min(start + itemsPerPage, feeItems.size());

		for (int i = start; i < end; i++) {
			FeeRecordBean fee = feeItems.get(i);
			// 각 행을 위한 패널 (BoxLayout X_AXIS로 고정 열 너비 사용)
			JPanel rowPanel = new JPanel();
			rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
			rowPanel.setOpaque(false); // 배경 투명하게 설정
			rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));

			// 개별 체크박스
			JCheckBox rowCheckBox = new JCheckBox();
			rowCheckBox.setPreferredSize(checkDim);
			rowCheckBox.setMaximumSize(checkDim);
			rowCheckBox.setOpaque(false); // 배경 투명하게 설정
			rowcheckboxes.add(rowCheckBox);

			// 번호 열 DB의 아이디 대신 현재 페이지 내에서 1부터 순차적으로 표시
			int rowNumber = i + 1;
			JLabel idLabel = new JLabel(String.valueOf(rowNumber), SwingConstants.CENTER);
			idLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
			idLabel.setPreferredSize(idDim);
			idLabel.setMinimumSize(idDim);
			idLabel.setMaximumSize(idDim);

			// 품목 열
			JLabel noteLabel = new JLabel(fee.getItem(), SwingConstants.CENTER);
			noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
			noteLabel.setPreferredSize(noteDim);
			noteLabel.setMinimumSize(noteDim);
			noteLabel.setMaximumSize(noteDim);

			// 지출금액 열
			JLabel amountLabel = new JLabel(fee.getAmount(), SwingConstants.CENTER);
			amountLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
			amountLabel.setPreferredSize(amountDim);
			amountLabel.setMinimumSize(amountDim);
			amountLabel.setMaximumSize(amountDim);

			// 지출날짜 열 (필요 시 truncateText() 메소드로 잘라서 표시)
			JLabel dateLabel = new JLabel();
			dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
			String fullDate = fee.getFeeDate();
			String truncatedPhone = truncateText(fullDate, dateLabel.getFont(), 250);
			dateLabel.setText(truncatedPhone);
			dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
			dateLabel.setPreferredSize(dateDim);
			dateLabel.setMinimumSize(dateDim);
			dateLabel.setMaximumSize(dateDim);

			// 행 패널에 각 열 추가
			rowPanel.add(rowCheckBox);
			rowPanel.add(idLabel);
			rowPanel.add(noteLabel);
			rowPanel.add(amountLabel);
			rowPanel.add(dateLabel);

			// rowPanel을 noticeListPanel에 추가 (GridBagLayout 사용)
			GridBagConstraints gbcRow = new GridBagConstraints();
			gbcRow.gridx = 0;
			gbcRow.gridy = gridY;
			gbcRow.anchor = GridBagConstraints.CENTER;
			feeListPanel.add(rowPanel, gbcRow);
			gridY++;

			// 마지막 항목이 아니라면, 행과 행 사이에 구분선 추가
			if (i < end - 1) {
				// 구분선을 CENTER 정렬하는 dividerPanel 생성
				JPanel dividerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				dividerPanel.setOpaque(false); // 배경 투명하게 설정
				dividerPanel.add(createDivider(640, 1, 0, 0));

				GridBagConstraints gbcDivider = new GridBagConstraints();
				gbcDivider.gridx = 0;
				gbcDivider.gridy = gridY;
				gbcDivider.anchor = GridBagConstraints.CENTER;
				feeListPanel.add(dividerPanel, gbcDivider);
				gridY++;
			}
		}
		feeListPanel.revalidate();
		feeListPanel.repaint();
	}

	/** 페이지네이션 버튼 업데이트 */
	private void updatePagination() {
		int totalPages = (int) Math.ceil((double) feeItems.size() / itemsPerPage); // MemberMgr에서 총 페이지 수 가져오기
		paginationPanel.removeAll();
		pageButtons.clear();

		for (int i = 1; i <= totalPages; i++) {
			JButton pageButton = new JButton(String.valueOf(i));
			pageButton.setFont(new Font("SansSerif", Font.BOLD, 16));
			pageButton.setBorderPainted(false);
			pageButton.setContentAreaFilled(false);
			pageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			pageButton.addActionListener(this);

			if (i == currentPage) {
				pageButton.setForeground(Color.BLUE); // 현재 페이지 강조
			}
			pageButtons.add(pageButton);
			paginationPanel.add(pageButton);
		}
		paginationPanel.revalidate();
		paginationPanel.repaint();
	}

	// 헬퍼 메소드: 주어진 폰트와 최대 너비(maxWidth)를 기준으로 문자열을 자르고 "..."을 붙여 반환
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

	// 구분선(divider)을 생성하는 헬퍼 메소드 (폭, 높이, 위쪽 gap, 아래쪽 gap 지정)
	private JPanel createDivider(int width, int height, int topGap, int bottomGap) {
		JPanel divider = new JPanel();
		divider.setBackground(Color.LIGHT_GRAY);
		divider.setMaximumSize(new Dimension(width, height));
		divider.setPreferredSize(new Dimension(width, height));
		divider.setAlignmentX(Component.CENTER_ALIGNMENT);
		divider.setBorder(BorderFactory.createEmptyBorder(topGap, 0, bottomGap, 0));
		return divider;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			// 뒤로가기 시 이전 프레임과 위치 정보 복원
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MyPageAWT();
			}
		} else if (e.getSource() == searchButton) { // 검색 버튼 클릭 시
			String keyword = searchField.getText().trim();
			if (keyword.isEmpty()) {
				// 검색어가 없으면 전체 회원관리를 불러옴
				loadFees();
			} else {
				// FeeMgr를 이용해 전체 회비 목록 중 키워드(품목 등) 포함 여부 필터링
				FeeMgr feeMgr = new FeeMgr();
				Vector<FeeRecordBean> vector = feeMgr.listFee();
				feeItems = new ArrayList<>();
				for (FeeRecordBean fee : vector) {
					if (fee.getItem().contains(keyword)) {
						feeItems.add(fee);
					}
				}
				currentPage = 1;
				updateFeeList();
				updatePagination();
			}
		} else if (e.getSource() == deleteButton) {
			// 삭제 전 확인 (선택된 회원이 있는지 확인)
			ArrayList<String> itemsToDelete = new ArrayList<>();
			int start = (currentPage - 1) * itemsPerPage;
			for (int i = 0; i < rowcheckboxes.size(); i++) {
				if (rowcheckboxes.get(i).isSelected()) {
					// members 리스트의 실제 인덱스는 start + i
					itemsToDelete.add(feeItems.get(start + i).getItem());
				}
			}
			if (itemsToDelete.isEmpty()) {
				JOptionPane.showMessageDialog(this, "삭제할 지출내역을 선택해주세요.");
				return;
			}
			int confirm = JOptionPane.showConfirmDialog(this, "선택한 지출내역을 삭제하시겠습니까?", "삭제 확인",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				FeeMgr feeMgr = new FeeMgr();
				for (String item : itemsToDelete) {
					feeMgr.deleteFee(item);
				}
				JOptionPane.showMessageDialog(this, "선택한 지출내역이 삭제되었습니다.");
				loadFees();
			}
		} else if (e.getSource() == addButton) {
			// 추가 버튼 클릭 시, 입력 다이얼로그 띄우기
			showAddFeeDialog();
		} else {
			// 페이지 버튼이 눌렸을 경우
			for (int i = 0; i < pageButtons.size(); i++) {
				if (e.getSource() == pageButtons.get(i)) {
					currentPage = i + 1;
					updateFeeList();
					updatePagination();
					break;
				}
			}
		}
	}

	// 새 지출 내역 입력 다이얼로그를 띄우는 메소드
	private void showAddFeeDialog() {
		// 입력 패널 구성: 품목, 금액 두 개의 입력창
		JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
		panel.add(new JLabel("지출 품목:"));
		JTextField itemField = new JTextField();
		panel.add(itemField);

		panel.add(new JLabel("지출 금액:"));
		JTextField amountField = new JTextField();
		panel.add(amountField);

		int result = JOptionPane.showConfirmDialog(this, panel, "새 지출 내역 추가", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String item = itemField.getText().trim();
			String amountStr = amountField.getText().trim();
			if (item.isEmpty() || amountStr.isEmpty()) {
				JOptionPane.showMessageDialog(this, "품목과 금액을 모두 입력해주세요.");
				return;
			}
			int amount;
			try {
				amount = Integer.parseInt(amountStr);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "금액은 숫자만 입력 가능합니다.");
				return;
			}
			// DB에 저장
			FeeMgr feeMgr = new FeeMgr();
			boolean inserted = feeMgr.insertFee(item, amount);
			if (inserted) {
				JOptionPane.showMessageDialog(this, "새 지출 내역이 추가되었습니다.");
				loadFees(); // 리스트 갱신
			} else {
				JOptionPane.showMessageDialog(this, "지출 내역 추가에 실패하였습니다.");
			}
		}
	}

	private void updateTotalAsset() {
		FeeMgr feeMgr = new FeeMgr();
		int totalAsset = feeMgr.getTotalAsset();
		// 천 단위 구분 기호 적용
		String formattedAsset = String.format("%,d", totalAsset);
		totalAssetLabel.setText("총 자산: " + formattedAsset + "원");
	}

	public static void main(String[] args) {
		new FeeManagement();
	}
}
