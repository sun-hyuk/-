package Project;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import Project.MainPageAWT.BackgroundPanel;

public class NoticeForm extends JFrame implements ActionListener {
	// 기존 헤더 관련 컴포넌트
	JButton backButton, homeButton, notificationButton, paymentButton, myPageButton;
	JPanel topPanel, topRightPanel, buttonGroup, bottomMenu, titlePanel, separatorContainer;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;

	// 게시글 입력 폼 관련 컴포넌트
	private JTextField titleField;
	private JTextArea contentArea;

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

	public NoticeForm() {
		// 관리자만 공지글을 작성할 수 있도록 로그인 연동 처리
		ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
		if (currentAdmin == null) { // 관리자가 아닌 경우
			JOptionPane.showMessageDialog(this, "관리자만 공지글을 작성할 수 있습니다.");
			dispose(); // 창을 닫고
			new NoticePageAWT();
			return; // 더 이상 코드 실행되지 않도록 처리
		}

		String managerId = currentAdmin.getManager_Id();

		setTitle("공지글 작성");
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

		// 왼쪽: 뒤로가기 버튼과 헤더 제목
		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		// 왼쪽 여백 추가
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		titleLabel = new JLabel("공지글 작성", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false); // 배경 투명하게 설정
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 오른쪽: 로그아웃 및 메뉴 버튼
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
				PopupMenuManager.showPopupMenu(menuLabel, NoticeForm.this);
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

		// 구분선: 1px 높이의 밝은 회색 선
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);

		add(topPanel, BorderLayout.NORTH);

		// ── 본문 영역 (게시글 입력 폼) ─────────────────────────────
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setOpaque(false);
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 70, 20, 70));

		// 1. 제목 입력 영역 (BorderLayout 사용)
		JPanel titleInputPanel = new JPanel(new BorderLayout());
		titleInputPanel.setOpaque(false);

		JLabel lblTitle = new JLabel(" 제목   ");
		lblTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		titleInputPanel.add(lblTitle, BorderLayout.WEST);

		titleField = new JTextField();
		titleField.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
		titleField.setBackground(Color.WHITE);
		titleField.setForeground(Color.BLACK);
		titleField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		titleField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		titleField.setCaretColor(Color.BLACK);
		// 제목 텍스트필드 높이를 50픽셀로 설정 (너비는 자동 조절)
		titleField.setPreferredSize(new Dimension(640, 40));
		titleField.setMaximumSize(new Dimension(640, 40));
		titleField.setMargin(new Insets(5, 5, 5, 5));

		// 플레이스홀더 텍스트 설정
		String placeholder = "  30자 이내로 작성해 주세요";
		titleField.setText(placeholder);
		titleField.setForeground(Color.LIGHT_GRAY);
		titleField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		titleField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		titleField.setCaretColor(Color.BLACK);
		// 제목 텍스트필드 높이 설정 (너비는 자동 조절)
		titleField.setPreferredSize(new Dimension(640, 40));
		titleField.setMaximumSize(new Dimension(640, 40));
		titleField.setMargin(new Insets(5, 5, 5, 5));

		// 플레이스홀더 효과: 포커스 시 플레이스홀더 제거, 포커스 아웃 시 비어 있으면 플레이스홀더 복원
		titleField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				if (titleField.getText().equals(placeholder)) {
					titleField.setText("");
					titleField.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (titleField.getText().isEmpty()) {
					titleField.setText(placeholder);
					titleField.setForeground(Color.LIGHT_GRAY);
				}
			}
		});

		// 입력 길이 제한: 30자 이하만 입력하도록 DocumentFilter 적용
		((AbstractDocument) titleField.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if (fb.getDocument().getLength() + string.length() <= 30) {
					super.insertString(fb, offset, string, attr);
				}
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
					throws BadLocationException {
				if (fb.getDocument().getLength() - length + text.length() <= 30) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});

		titleInputPanel.add(titleField, BorderLayout.CENTER);
		titleInputPanel.setMaximumSize(new Dimension(640, 40));

		// 2. 내용 입력 영역
		JPanel contentInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		contentInputPanel.setOpaque(false);
		JLabel lblContent = new JLabel("내용");
		lblContent.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		contentArea = new JTextArea();
		contentArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
		contentArea.setLineWrap(true);
		contentArea.setBackground(Color.WHITE);
		contentArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		contentArea.setCaretColor(Color.BLACK);
		contentArea.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		JScrollPane scrollPane = new JScrollPane(contentArea);

		// 내용 텍스트영역(스크롤패널)의 높이를 400픽셀에서 350픽셀로 줄임
		scrollPane.setPreferredSize(new Dimension(619, 220));
		// 스크롤 패널의 기본 테두리를 제거
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		contentInputPanel.add(lblContent);
		contentInputPanel.add(scrollPane);

		// 3. 버튼 영역 (등록, 취소)
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setOpaque(false);
		JButton submitButton = new JButton("등록");
		submitButton.setBackground(Color.DARK_GRAY);
		submitButton.setForeground(Color.WHITE);
		submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		submitButton.setRolloverEnabled(false); // 마우스 오버 시 색상 변화를 방지
		JButton cancelButton = new JButton("취소");
		cancelButton.setBackground(Color.DARK_GRAY);
		cancelButton.setForeground(Color.WHITE);
		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cancelButton.setRolloverEnabled(false); // 마우스 오버 시 색상 변화를 방지
		submitButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		cancelButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		submitButton.setPreferredSize(new Dimension(80, 35));
		cancelButton.setPreferredSize(new Dimension(80, 35));

		// 등록 버튼: 제목과 내용 입력 여부 확인
		submitButton.addActionListener(e -> {
		    String titleText = titleField.getText().trim();
		    String contentText = contentArea.getText().trim();

		    if (titleText.isEmpty() || contentText.isEmpty() || titleText.equals(placeholder)) {
		        JOptionPane.showMessageDialog(null, "제목과 내용을 입력하세요.");
		    } else {
		        // DB에 공지사항 저장 후 생성된 notice_id를 반환 받음
		        NoticeMgr noticeMgr = new NoticeMgr();
		        int noticeId = noticeMgr.insertNotice(titleText, contentText, managerId);

		        if (noticeId != -1) {
		        	MemberMgr memberMgr = new MemberMgr();
		        	Vector<String> vectorIds = memberMgr.getMemberIdList();
		        	ArrayList<String> memberIds = new ArrayList<>(vectorIds);

		            // 공지사항에 따른 알림 생성
		            NotificationMgr notificationMgr = new NotificationMgr();
		            notificationMgr.createNotificationForUsers(noticeId, memberIds);

		            JOptionPane.showMessageDialog(null, "게시글이 등록되었습니다.");
		            dispose();
		            NoticePageAWT noticePage = new NoticePageAWT(null, null);
		            noticePage.loadNotices();
		        } else {
		            JOptionPane.showMessageDialog(null, "게시글 등록에 실패했습니다.");
		        }
		    }
		});


		// 취소 버튼: 입력값 초기화 전 사용자 확인
		cancelButton.addActionListener(e -> {
			int response = JOptionPane.showConfirmDialog(null, "작성을 취소하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.YES_OPTION) {
				titleField.setText("");
				contentArea.setText("");
				dispose(); // 현재 창 종료
				new NoticePageAWT(null, null); // NoticePageAWT 창 띄우기
			}
		});

		buttonPanel.add(submitButton);
		buttonPanel.add(cancelButton);

		// formPanel에 순서대로 추가
		formPanel.add(titleInputPanel);
		formPanel.add(contentInputPanel);
		formPanel.add(buttonPanel);

		add(formPanel, BorderLayout.CENTER);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 예시: 뒤로가기 버튼 클릭 시 NoticePageAWT 창으로 이동
		if (e.getSource() == backButton) {
			dispose();
			new NoticePageAWT(null, null);
		}
	}

	public static void main(String[] args) {
		new NoticeForm();
	}
}
