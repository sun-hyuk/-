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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Project.MainPageAWT.BackgroundPanel;

public class NoticePageAWT extends NavigableFrame implements ActionListener {
	JButton searchButton, backButton, homeButton, notificationButton, paymentButton, myPageButton, writeButton;
	JPanel topPanel, topRightPanel, searchPanel, buttonGroup, bottomMenu, titlePanel, separatorContainer,
			noticeListPanel, paginationPanel;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
	JTextField searchField;

	int currentPage = 1;
	int noticesPerPage = 4; // 한 페이지당 공지 개수
	ArrayList<JButton> pageButtons = new ArrayList<>();
	private ArrayList<NoticeBean> notices;
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

	// 기본 생성자 (독립 실행 시)
	public NoticePageAWT() {
		this(null, null);
	}

	public NoticePageAWT(JFrame previousFrame, Point previousLocation) {
		super(previousFrame);
		this.previousLocation = previousLocation;

		notices = new ArrayList<>();
		setTitle("공지사항");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// 기존 패널
		initializeOriginalTopPanel();

		setLocationRelativeTo(null);
		setVisible(true);
		loadNotices();
	}

	private void initializeOriginalTopPanel() {
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
		titleLabel = new JLabel("공지사항", JLabel.CENTER);
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
				PopupMenuManager.showPopupMenu(menuLabel, NoticePageAWT.this); // 팝업 메뉴 호출
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
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
		separatorContainer.add(separatorLabel);

		// topPanel에 구분선 추가
		topPanel.add(separatorContainer, BorderLayout.SOUTH); // topPanel 내부에 추가

		// 검색 패널
		searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		searchPanel.setOpaque(false); // 배경 투명하게 설정
		searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100)); // 오른쪽에 여백 추가

		searchField = new JTextField(15);
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
		searchField.setPreferredSize(new Dimension(150, 30)); // 너비 150, 높이 30
		searchField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

		searchButton = new JButton("검색");
		searchButton.setFont(new Font("SansSerif", Font.BOLD, 13));
		searchButton.setPreferredSize(new Dimension(80, 30)); // 너비 80, 높이 30
		searchButton.setBackground(Color.DARK_GRAY);
		searchButton.setForeground(Color.WHITE);
		searchButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		searchButton.addActionListener(this);

		searchPanel.add(searchField);
		searchPanel.add(searchButton);

		// 이제 topPanel과 searchPanel을 포함할 새로운 northPanel 생성
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setOpaque(false); // 배경 투명하게 설정
		northPanel.add(topPanel); // 상단 패널 추가
		northPanel.add(searchPanel); // 검색 패널 추가

		add(northPanel, BorderLayout.NORTH);

		// 공지사항 목록 패널
		noticeListPanel = new JPanel(new GridBagLayout());
		noticeListPanel.setOpaque(false); // 배경 투명하게 설정
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; // 첫 번째 열에 배치
		gbc.gridy = GridBagConstraints.RELATIVE; // 행은 자동 증가
		gbc.anchor = GridBagConstraints.CENTER; // 가운데 정렬

		updateNoticeList(); // 초기 공지사항 표시
		add(noticeListPanel, BorderLayout.CENTER);

		// 글쓰기 버튼 생성
		writeButton = new JButton("글쓰기");
		writeButton.setFont(new Font("SansSerif", Font.BOLD, 13));
		writeButton.setBackground(Color.DARK_GRAY);
		writeButton.setForeground(Color.WHITE);
		writeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		writeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		writeButton.addActionListener(this);

		// 글쓰기 버튼을 오른쪽에 배치할 패널 생성
		JPanel writePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		writePanel.setOpaque(false); // 배경 투명하게 설정
		writePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));
		writePanel.add(writeButton);

		// 페이지네이션 패널
		paginationPanel = new JPanel(new FlowLayout());
		paginationPanel.setOpaque(false); // 배경 투명하게 설정
		paginationPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		updateNoticeList(); // 공지사항 목록 업데이트
		updatePagination(); // 페이지네이션 생성

		// 하단에 글쓰기 버튼과 페이지네이션 패널을 담을 컨테이너 패널 생성 (수직 배치)
		JPanel southContainer = new JPanel();
		southContainer.setLayout(new BoxLayout(southContainer, BoxLayout.Y_AXIS));
		southContainer.setOpaque(false); // 배경 투명하게 설정
		southContainer.add(writePanel); // 위쪽에 글쓰기 버튼 패널
		southContainer.add(paginationPanel); // 아래쪽에 페이지네이션 패널

		add(southContainer, BorderLayout.SOUTH);
	}

	public void loadNotices() {
		// DB에서 공지사항 목록을 가져오는 메소드
		NoticeMgr noticeMgr = new NoticeMgr();
		noticeMgr.loadNoticesFromDB();
		notices = noticeMgr.getNotices(); // DB에서 가져온 공지사항 목록 저장
		updateNoticeList(); // 공지사항 목록 화면 갱신

		// 페이지네이션 업데이트
		updatePagination();
	}

	/** 공지사항 목록을 현재 페이지에 맞게 업데이트 */
	private void updateNoticeList() {
		noticeListPanel.removeAll(); // 기존 내용 삭제

		// 각 열의 고정 크기 설정
		Dimension idDim = new Dimension(100, 20);
		Dimension titleDim = new Dimension(300, 20); // truncateText 적용 시 최대 250px 사용
		Dimension dateDim = new Dimension(228, 20);

		// 헤더 패널 생성: 각 열의 시작 위치 및 너비 고정
		JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 0));
		headerPanel.setOpaque(false); // 배경 투명하게 설정
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

		Font headerFont = new Font("SansSerif", Font.BOLD, 16);

		JLabel idHeader = new JLabel("번호", SwingConstants.CENTER);
		idHeader.setFont(headerFont);

		JLabel titleHeader = new JLabel("제목", SwingConstants.CENTER);
		titleHeader.setBorder(BorderFactory.createEmptyBorder(0, 105, 0, 105));
		titleHeader.setFont(headerFont);

		JLabel dateHeader = new JLabel("등록일", SwingConstants.CENTER);
		dateHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
		dateHeader.setFont(headerFont);

		headerPanel.add(idHeader);
		headerPanel.add(titleHeader);
		headerPanel.add(dateHeader);

		// GridBagLayout을 사용하여 헤더 패널을 맨 위에 추가 (행 0)
		GridBagConstraints gbcHeader = new GridBagConstraints();
		gbcHeader.gridx = 0;
		gbcHeader.gridy = 0;
		gbcHeader.anchor = GridBagConstraints.NORTH;
		noticeListPanel.add(headerPanel, gbcHeader);

		// 공지사항 목록 추가
		int gridY = 1;
		int start = (currentPage - 1) * noticesPerPage;
		int end = Math.min(start + noticesPerPage, notices.size());

		for (int i = start; i < end; i++) {
			NoticeBean notice = notices.get(i);

			// 각 행을 위한 패널 (BoxLayout X_AXIS로 고정 열 너비 사용)
			JPanel rowPanel = new JPanel(new GridLayout(1, 3, 10, 0));
			rowPanel.setOpaque(false); // 배경 투명하게 설정
			rowPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 17, 10));

			Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 14);

			// 번호 열 DB의 아이디 대신 현재 페이지 내에서 1부터 순차적으로 표시
			int rowNumber = i + 1;
			JLabel idLabel = new JLabel(String.valueOf(rowNumber), SwingConstants.CENTER);
			idLabel.setFont(MAIN_FONT);
			idLabel.setPreferredSize(idDim);
			idLabel.setMinimumSize(idDim);
			idLabel.setMaximumSize(idDim);

			// 제목 열 (필요 시 truncateText() 메소드로 잘라서 표시)
			JLabel noticeTitleLabel = new JLabel();
			noticeTitleLabel.setFont(MAIN_FONT);
			String fullTitle = notice.getNotice_Title();
			String truncatedTitle = truncateText(fullTitle, MAIN_FONT, 300);
			noticeTitleLabel.setText(truncatedTitle);
			noticeTitleLabel.setPreferredSize(titleDim);
			noticeTitleLabel.setMinimumSize(titleDim);
			noticeTitleLabel.setMaximumSize(titleDim);
			noticeTitleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

			// 제목 클릭 시 상세페이지로 이동 (공지 id 전달)
			noticeTitleLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// 현재 NoticePageAWT의 인스턴스와 위치 정보를 함께 전달
					Point currentLocation = NoticePageAWT.this.getLocation();
					new NoticeDetailPageAWT(notice.getNotice_Id(), NoticePageAWT.this, currentLocation);
					// 현재 NoticePageAWT를 숨기는 코드 추가
					NoticePageAWT.this.setVisible(false);
				}
			});

			// 등록일 열
			JLabel createdAtLabel = new JLabel(notice.getCreated_At(), SwingConstants.CENTER);
			createdAtLabel.setFont(MAIN_FONT);
			createdAtLabel.setPreferredSize(dateDim);
			createdAtLabel.setMinimumSize(dateDim);
			createdAtLabel.setMaximumSize(dateDim);
			// 등록일 열에도 여백을 줄 수 있음
			createdAtLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

			// 행 패널에 각 열 추가
			rowPanel.add(idLabel);
			rowPanel.add(noticeTitleLabel);
			rowPanel.add(createdAtLabel);

			// rowPanel을 noticeListPanel에 추가 (GridBagLayout 사용)
			GridBagConstraints gbcRow = new GridBagConstraints();
			gbcRow.gridx = 0;
			gbcRow.gridy = gridY;
			gbcRow.fill = GridBagConstraints.HORIZONTAL;
			gbcRow.weightx = 1.0;
			noticeListPanel.add(rowPanel, gbcRow);
			gridY++;

			// 마지막 항목이 아니라면, 행과 행 사이에 구분선 추가
			if (i < end - 1) {
				JPanel dividerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				dividerPanel.setOpaque(false); // 배경 투명하게 설정
				JPanel divider = createDivider(650, 1, 0, 0);
				dividerPanel.add(divider);

				GridBagConstraints gbcDivider = new GridBagConstraints();
				gbcDivider.gridx = 0;
				gbcDivider.gridy = gridY;
				gbcDivider.fill = GridBagConstraints.HORIZONTAL; // 가로로 채움
				gbcDivider.weightx = 1.0; // 가로 확장 우선순위 부여
				gbcDivider.anchor = GridBagConstraints.CENTER;
				noticeListPanel.add(dividerPanel, gbcDivider);
				gridY++;
			}
		}
		noticeListPanel.revalidate();
		noticeListPanel.repaint();
	}

	/** 페이지네이션 버튼 업데이트 */
	private void updatePagination() {
		NoticeMgr noticeMgr = new NoticeMgr();
		int totalPages = noticeMgr.getTotalPages(); // NoticeMgr에서 총 페이지 수 가져오기
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
				pageButton.setForeground(new Color(0, 122, 255)); // 현재 페이지 강조
			} else {
				pageButton.setForeground(new Color(33, 37, 41));
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
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MainPageAWT(null, null);
			}
		}

		if (e.getSource() == writeButton) {
			dispose();
			new NoticeForm();
		} else if (e.getSource() == searchButton) { // 검색 버튼 클릭 시
			String keyword = searchField.getText().trim();
			if (keyword.isEmpty()) {
				// 검색어가 없으면 전체 공지사항을 불러옴
				loadNotices();
			} else {
				// 검색어가 있으면, NoticeMgr의 searchNotices() 호출
				NoticeMgr noticeMgr = new NoticeMgr();
				noticeMgr.searchNotices(keyword);
				// searchNotices() 메소드 내부에서 리턴된 리스트를 notices에 저장
				notices = noticeMgr.searchNotices(keyword);
				// 공지사항 목록과 페이지네이션 업데이트
				updateNoticeList();
				updatePagination();
			}
		} else {
			// 페이지 버튼이 눌렸을 경우
			for (int i = 0; i < pageButtons.size(); i++) {
				if (e.getSource() == pageButtons.get(i)) {
					currentPage = i + 1;
					updateNoticeList();
					updatePagination();
					break;
				}
			}
		}
	}

	public static void main(String[] args) {
		new NoticePageAWT(null, null);
	}
}