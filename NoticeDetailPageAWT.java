package Project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Project.NoticePageAWT.BackgroundPanel;

public class NoticeDetailPageAWT extends JFrame implements ActionListener {
	// 기존 헤더 관련 컴포넌트 (위치 원래대로 복구)
	JButton backButton;
	JPanel topPanel, titlePanel, separatorContainer, contentPanel, buttonPanel, bottomPanel;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel;

	// 추가된 UI 요소
	private JLabel profileLabel, userIdLabel, dateLabel, registerLabel, editLabel, deleteLabel;
	private JTextField titleField;
	private JFrame previousFrame;
	private Point previousLocation;
	private JTextArea contentArea;
	private JButton listButton;
	private int noticeId;
	private String fullContent;
	private NoticeMgr noticeMgr;

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

	public NoticeDetailPageAWT(int noticeId) {
		this(noticeId, null, null);
	}

	public NoticeDetailPageAWT(int noticeId, JFrame previousFrame, Point previousLocation) {
		this.noticeId = noticeId;
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;
		noticeMgr = new NoticeMgr();

		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		initComponents();
		loadNoticeDetail();
		if (previousLocation != null) {
			setLocation(previousLocation);
		}

		setLocationRelativeTo(null);
		setVisible(true);
	}

	// UI 컴포넌트 초기화 메소드
	private void initComponents() {
		setTitle("공지글");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// ── 기존 상단 헤더 (로그아웃 & 삼단바 위치 복구) ─────────────────────────────
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

		titleLabel = new JLabel("공지글", JLabel.LEFT);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false); // 배경 투명하게 설정
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃 & 메뉴 버튼 (원래 위치로 복구)
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

		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, NoticeDetailPageAWT.this);
			}
		});

		JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false); // 배경 투명하게 설정
		topRightPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
		topRightPanel.add(logoutLabel);
		topRightPanel.add(Box.createHorizontalStrut(10));
		topRightPanel.add(menuLabel);

		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 구분선
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 1));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false); // 배경 투명하게 설정
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);
		add(topPanel, BorderLayout.NORTH);

		// ── 본문 영역 ─────────────────────────────
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false); // 배경 투명하게 설정
		contentPanel.setBorder(new EmptyBorder(20, 70, 10, 70));

		// 프로필 & 사용자 정보
		JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		profilePanel.setOpaque(false); // 배경 투명하게 설정

		// 공지글 작성자의 관리자 ID 가져오기
		NoticeBean notice = noticeMgr.getNoticeById(noticeId); // 공지글 정보 가져오기
		if (notice == null) {
			JOptionPane.showMessageDialog(this, "해당 공지글을 찾을 수 없습니다.");
			dispose(); // 창을 종료하고
			return; // 더 이상 코드 실행되지 않도록 처리
		}

		String managerId = notice.getManagerId(); // 공지글 작성자의 관리자 ID 가져오기

		// 관리자 정보 조회
		ManagerMgr managerMgr = new ManagerMgr();
		ManagerBean manager = managerMgr.getManagerById(managerId);

		if (manager == null) {
			JOptionPane.showMessageDialog(this, "해당 관리자를 찾을 수 없습니다.");
			dispose(); // 창을 종료하고
			return; // 더 이상 코드 실행되지 않도록 처리
		}

		// 이미지 경로가 아니라 DB에서 이미지를 직접 불러오고 싶다면
		ImageIcon profileImage = managerMgr.getManagerProfile(managerId);
		if (profileImage == null) {
			// 이미지 로드 실패 시 기본 이미지 사용
			profileImage = new ImageIcon("C:\\Java\\myJava\\Project\\kakao.jpg"); // 기본 이미지 경로
		}

		Image img = profileImage.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 크기 조정
		profileImage = new ImageIcon(img); // 크기 조정된 이미지를 다시 설정
		profileLabel = new JLabel(profileImage);
		profileLabel.setHorizontalAlignment(JLabel.CENTER);
		profileLabel.setPreferredSize(new Dimension(50, 50)); // 적당한 크기로 설정

		// 관리자 이름 표시
		userIdLabel = new JLabel(manager.getManager_Name()); // 관리자의 이름을 표시
		userIdLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

		// 공지글 작성일
		dateLabel = new JLabel(notice.getCreated_At()); // 공지글 작성일
		dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		dateLabel.setForeground(Color.GRAY);

		// 프로필 및 사용자 정보 레이아웃 설정
		profilePanel.add(profileLabel);
		profilePanel.add(Box.createHorizontalStrut(5));
		profilePanel.add(userIdLabel);
		profilePanel.add(Box.createHorizontalStrut(10));
		profilePanel.add(dateLabel);

		contentPanel.add(profilePanel);
		contentPanel.add(Box.createVerticalStrut(10));

		// 공지 제목
		titleField = new JTextField("제목을 작성해주세요.");
		titleField.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		titleField.setBackground(Color.WHITE);
		titleField.setBorder(BorderFactory.createEmptyBorder());
		titleField.setCaretColor(Color.BLACK);
		contentPanel.add(titleField);
		contentPanel.add(Box.createVerticalStrut(10));

		// 공지 내용
		contentArea = new JTextArea("내용을 작성해주세요.");
		contentArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
		contentArea.setBackground(Color.WHITE);
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		contentArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		contentArea.setCaretColor(Color.BLACK);
		contentArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(contentArea);
		scrollPane.setPreferredSize(new Dimension(640, 200));
		contentPanel.add(scrollPane);
		contentPanel.add(Box.createVerticalStrut(10));

		if (currentAdmin == null) {
		    // 관리자가 아닐 경우 제목과 내용의 편집 및 마우스 상호작용 비활성화
		    titleField.setEditable(false);
		    titleField.setFocusable(false);
		    contentArea.setEditable(false);
		    contentArea.setFocusable(false);
		}
		
		// 수정 / 삭제 버튼 영역 (클릭 가능한 레이블)
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false); // 배경 투명하게 설정
		editLabel = createClickableLabel("수정");
		deleteLabel = createClickableLabel("삭제");
		buttonPanel.add(editLabel);
		buttonPanel.add(new JLabel(" | "));
		buttonPanel.add(deleteLabel);
		contentPanel.add(buttonPanel);

		// ── 목록 버튼을 확실하게 한 줄 아래로 이동 ─────────────────────────────
		listButton = new JButton("목록");
		listButton.setFont(new Font("SansSerif", Font.BOLD, 17));
		listButton.setBackground(Color.DARK_GRAY);
		listButton.setForeground(Color.WHITE);
		listButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		listButton.setPreferredSize(new Dimension(117, 48));
		listButton.setRolloverEnabled(false); // 마우스 오버 시 색상 변화를 방지
		// 목록 버튼 클릭 시 NoticePageAWT로 이동
		listButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // 현재 화면 종료
				new NoticePageAWT(null, null); // NoticePageAWT 창 띄우기
			}
		});

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.setOpaque(false); // 배경 투명하게 설정
		bottomPanel.add(listButton);
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(bottomPanel);

		add(contentPanel, BorderLayout.CENTER);

		// ── 관리자만 수정/삭제 버튼 활성화 ──
		if (currentAdmin == null) { // 관리자가 아닌 경우
			editLabel.setEnabled(false); // 수정 버튼 비활성화
			deleteLabel.setEnabled(false); // 삭제 버튼 비활성화
		}
	}

	// ── 클릭 가능한 Label 생성 메소드 ──
	private JLabel createClickableLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 클릭 가능하도록 변경
		return label;
	}

	// 전달받은 공지 id를 기반으로 DB에서 상세 정보를 로드
	private void loadNoticeDetail() {
		// NoticeMgr의 getNoticeById()로 제목, 작성일을 불러옴
		NoticeBean notice = noticeMgr.getNoticeById(noticeId);
		if (notice != null) {
			titleField.setText(notice.getNotice_Title());
			dateLabel.setText(notice.getCreated_At());
			// 공지 내용을 별도로 DB에서 불러오기
			String content = noticeMgr.getNoticeContent(noticeId);
			contentArea.setText(content);
		}

		if (currentAdmin != null) {
		    // 관리자인 경우 수정, 삭제 기능 활성화
		    editLabel.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            updateNotice();
		        }
		    });
		    deleteLabel.addMouseListener(new MouseAdapter() {
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            deleteNotice();
		        }
		    });
		} else {
		    // 관리자가 아닌 경우, 클릭 시 경고 메시지를 띄워 수정/삭제 기능이 없음을 알림
		    MouseAdapter disabledAdapter = new MouseAdapter() {
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            JOptionPane.showMessageDialog(NoticeDetailPageAWT.this, "수정/삭제 권한이 없습니다.");
		        }
		    };
		    editLabel.addMouseListener(disabledAdapter);
		    deleteLabel.addMouseListener(disabledAdapter);
		}
	}

	// 수정 처리 메소드
	private void updateNotice() {
		String newTitle = titleField.getText().trim();
		String newContent = contentArea.getText().trim();
		if (newTitle.isEmpty() || newContent.isEmpty()) {
			JOptionPane.showMessageDialog(this, "제목과 내용을 입력하세요.");
			return;
		}
		boolean success = noticeMgr.updateNotice(noticeId, newTitle, newContent);
		if (success) {
			JOptionPane.showMessageDialog(this, "공지사항이 수정되었습니다.");
		} else {
			JOptionPane.showMessageDialog(this, "수정에 실패했습니다.");
		}
	}

	// 삭제 처리 메소드
	private void deleteNotice() {
		int response = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.YES_OPTION) {
			boolean success = noticeMgr.deleteNotice(noticeId);
			if (success) {
				JOptionPane.showMessageDialog(this, "공지사항이 삭제되었습니다.");
				dispose();
				new NoticePageAWT(null, null);
			} else {
				JOptionPane.showMessageDialog(this, "삭제에 실패했습니다.");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			if (previousFrame != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new NoticePageAWT(null, null); // 또는 다른 기본 처리
			}
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new NoticeDetailPageAWT(1, null, null));
	}
}
