package Project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

//import Project.NoticeDetailPageAWT.BackgroundPanel;

public class MyPageAWT extends JFrame implements ActionListener {
	JButton backButton, homeButton, notificationButton, paymentButton, myPageButton;
	JPanel topPanel, bottomMenu, topRightPanel, buttonGroup, separatorContainer;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;

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

	// 기본 생성자 (독립 실행 시)
	public MyPageAWT() {
		this(null, null);
	}

	// 이전 프레임과 위치 정보를 받는 생성자 (네비게이션 용)
	public MyPageAWT(JFrame previousFrame, Point previousLocation) {
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;

		setTitle("마이페이지");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// BackgroundPanel을 ContentPane으로 설정
		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// 세션에서 로그인 정보 가져오기
		ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
		MemberBean currentUser = UserSession.getInstance().getCurrentUser();

		// 로그인 정보가 없으면 로그인 페이지로 이동
		if (currentAdmin == null && currentUser == null) {
			JOptionPane.showMessageDialog(this, "로그인 후 이용해주세요.");
			dispose();
			new LoginAWT();
			return;
		}

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

		// 제목
		titleLabel = new JLabel("마이페이지", JLabel.LEFT);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);
		topPanel.add(titlePanel, BorderLayout.WEST);

		// 로그아웃 버튼
		logoutLabel = new JLabel("로그아웃");
		logoutLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
		logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logoutLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 로그아웃 처리: 세션 정보 초기화
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				System.out.println("로그아웃: 세션 정보 초기화됨");
				dispose();
				new LoginAWT();
			}
		});

		// 메뉴 버튼
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, MyPageAWT.this); // 팝업 메뉴 호출
			}
		});

		// topRightPanel을 먼저 생성하여 NullPointerException 방지
		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false);

		spaceLabel = new JLabel("  "); // 공백 추가

		// 로그아웃, 메뉴 버튼 그룹화
		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 버튼 그룹 패널
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60)); // 오른쪽에서 60px 띄우기
		buttonGroup.setOpaque(false);
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
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		separatorContainer.add(separatorLabel);

		// topPanel에 구분선 추가
		topPanel.add(separatorContainer, BorderLayout.SOUTH); // topPanel 내부에 추가

		add(topPanel, BorderLayout.NORTH);

		// 프로필 패널 생성: FlowLayout을 사용하여 내부의 컴포넌트를 가운데 정렬
		JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		profilePanel.setOpaque(false);

		// 프로필 이미지 설정
		// 관리자인 경우 ManagerMgr의 getManagerProfile, 회원인 경우 MemberMgr의 getMemberProfile 사용
		ImageIcon profileIcon = null;
		if (currentAdmin != null) {
			ManagerMgr mgr = new ManagerMgr();
			profileIcon = mgr.getManagerProfile(currentAdmin.getManager_Id());
		} else if (currentUser != null) {
			MemberMgr mgr = new MemberMgr();
			profileIcon = mgr.getMemberProfile(currentUser.getMember_Id());
		}
		if (profileIcon == null) {
			profileIcon = new ImageIcon("C:\\Java\\myJava\\Project\\kakao.jpg");
		}
		Image img = profileIcon.getImage();
		Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
		JLabel profileLabel = new JLabel(new ImageIcon(resizedImg));

		// 사용자 정보 패널 생성 (관리자와 회원 구분)
		JPanel userInfoPanel = new JPanel();
		userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
		userInfoPanel.setOpaque(false);
		JLabel userRoleLabel;
		JLabel idLabel;
		if (currentAdmin != null) {
			userRoleLabel = new JLabel("관리자");
			userRoleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
			userRoleLabel.setForeground(Color.BLACK);
			idLabel = new JLabel(currentAdmin.getManager_Id());
			idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
			idLabel.setForeground(Color.GRAY);
		} else {
			// "사용자" 대신 member 테이블에 저장된 역할을 가져옴
		    String role = (currentUser != null && currentUser.getMember_Role() != null)
		                  ? currentUser.getMember_Role()
		                  : "아이디없음";
		    userRoleLabel = new JLabel(role);
		    userRoleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		    userRoleLabel.setForeground(Color.BLACK);
		    String actualUserId = (currentUser != null && currentUser.getMember_Id() != null)
		                          ? currentUser.getMember_Id()
		                          : "아이디없음";
		    idLabel = new JLabel(actualUserId);
		    idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
		    idLabel.setForeground(Color.GRAY);
		}
		userInfoPanel.add(userRoleLabel);
		userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		userInfoPanel.add(idLabel);

		profilePanel.add(profileLabel);
		profilePanel.add(userInfoPanel);

		// 중간 메뉴 패널 생성 (BoxLayout 사용)
		JPanel middleMenu = new JPanel();
		middleMenu.setLayout(new BoxLayout(middleMenu, BoxLayout.Y_AXIS));
		middleMenu.setOpaque(false);

		// 첫 번째 행: "내 정보 관리"와 "회비 및 예산 관리"
		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		row1.setOpaque(false);

		JLabel infoLabel = new JLabel("내 정보 관리");
		infoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		infoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		infoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose(); // 현재 창 닫기
				new MyInfoAWT(); // 공지사항 페이지로 이동
			}
		});

		// 텍스트 크기에 맞게 크기 고정
		Dimension infoSize = infoLabel.getPreferredSize();
		infoLabel.setPreferredSize(infoSize);
		infoLabel.setMaximumSize(infoSize);

		JLabel feeLabel = new JLabel("회비 및 예산 관리");
		feeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		feeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
        // 회비 및 예산 관리 기능은 관리자만 사용할 수 있도록 처리
        if (currentAdmin == null) {
            feeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(MyPageAWT.this, "해당 기능은 관리자 전용입니다.");
                }
            });
        } else {
            feeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose();
                    // 실제 회비 및 예산 관리 페이지 호출 코드
                    new FeeManagement();
                }
            });
        }

		Dimension feeSize = feeLabel.getPreferredSize();
		feeLabel.setPreferredSize(feeSize);
		feeLabel.setMaximumSize(feeSize);

		row1.add(infoLabel);
		row1.add(Box.createRigidArea(new Dimension(50, 0))); // 100픽셀 간격 지정
		row1.add(feeLabel);
		middleMenu.add(row1);

		// 두 번째 행: "대여 관리"와 "회원 관리"
		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		row2.setOpaque(false);

		JLabel rentalLabel = new JLabel("대여 관리");
		rentalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		rentalLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		 if (currentAdmin == null) {
	            rentalLabel.addMouseListener(new MouseAdapter() {
	                @Override
	                public void mouseClicked(MouseEvent e) {
	                    JOptionPane.showMessageDialog(MyPageAWT.this, "해당 기능은 관리자 전용입니다.");
	                }
	            });
	        } else {
	            rentalLabel.addMouseListener(new MouseAdapter() {
	                @Override
	                public void mouseClicked(MouseEvent e) {
	                    dispose();
	                    // 실제 대여 관리 페이지 호출 코드
	                    new RentalAWT();
	                }
	            });
	        }

		Dimension rentalSize = rentalLabel.getPreferredSize();
		rentalLabel.setPreferredSize(rentalSize);
		rentalLabel.setMaximumSize(rentalSize);

		JLabel memberLabel = new JLabel("회원 관리");
		memberLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		memberLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (currentAdmin == null) {
            memberLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(MyPageAWT.this, "해당 기능은 관리자 전용입니다.");
                }
            });
        } else {
            memberLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dispose();
                    // 실제 회원 관리 페이지 호출 코드
                    new MemberListAWT();
                }
            });
        }

		Dimension memberSize = memberLabel.getPreferredSize();
		memberLabel.setPreferredSize(memberSize);
		memberLabel.setMaximumSize(memberSize);

		row2.add(rentalLabel);
		row2.add(Box.createRigidArea(new Dimension(125, 0))); // 100픽셀 간격 지정
		row2.add(memberLabel);
		middleMenu.add(row2);

		// 세 번째 행: "공지사항"과 ">"
		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		row3.setOpaque(false);

		JLabel noticeLabel = new JLabel("공지사항");
		noticeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		Dimension noticeSize = noticeLabel.getPreferredSize();
		noticeLabel.setPreferredSize(noticeSize);
		noticeLabel.setMaximumSize(noticeSize);

		JLabel arrowLabel = new JLabel(">");
		arrowLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
		arrowLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		arrowLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Point currentLocation = MyPageAWT.this.getLocation();
				dispose();
				new NoticePageAWT(MyPageAWT.this, currentLocation);
			}
		});

		Dimension arrowSize = arrowLabel.getPreferredSize();
		arrowLabel.setPreferredSize(arrowSize);
		arrowLabel.setMaximumSize(arrowSize);

		row3.add(noticeLabel);
		row3.add(Box.createRigidArea(new Dimension(300, 0))); // 50픽셀 간격 지정
		row3.add(arrowLabel);
		middleMenu.add(createDivider(450, 1)); // 구분선 추가
		middleMenu.add(Box.createVerticalStrut(25)); // 아래쪽 gap 20px
		middleMenu.add(row3);

		
        // ── 관리자 전용 기능: 회비/대여/회원 관리 버튼 활성화 ──
        if (UserSession.getInstance().getCurrentAdmin() == null) {
        	feeLabel.setEnabled(false);
            rentalLabel.setEnabled(false);
            memberLabel.setEnabled(false);
    		
            // 버튼의 텍스트 색상도 변경하여 비활성화된 느낌을 줌
            feeLabel.setForeground(Color.GRAY);
            rentalLabel.setForeground(Color.GRAY);
            memberLabel.setForeground(Color.GRAY);
        }
        
		// contentPanel에 프로필 패널과 중간 메뉴를 추가
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(false);
		contentPanel.add(profilePanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		centerPanel.add(Box.createVerticalStrut(25)); // 위쪽 gap 20px
		centerPanel.add(createDivider(450, 1)); // 구분선 추가
		centerPanel.add(Box.createVerticalStrut(25)); // 아래쪽 gap 20px
		centerPanel.add(middleMenu);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);

		// 하단 메뉴
		bottomMenu = new JPanel(new GridLayout(1, 4, 5, 5));
		bottomMenu.setBorder(BorderFactory.createEmptyBorder(30, 70, 15, 70));
		bottomMenu.setOpaque(false);
		homeButton = new JButton("홈");
		notificationButton = new JButton("알림");
		paymentButton = new JButton("회비내역");
		myPageButton = new JButton("마이페이지");

		Font menuFont = new Font("SansSerif", Font.BOLD, 16);
		JButton[] buttons = { homeButton, notificationButton, paymentButton, myPageButton };
		for (JButton button : buttons) {
			button.setFont(menuFont);
			button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			button.setFocusPainted(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.addActionListener(this);
			bottomMenu.add(button);
		}
		
		add(bottomMenu, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		setVisible(true);

    }

	// 구분선(divider)을 생성하는 헬퍼 메소드 (폭, 높이 지정)
	private JPanel createDivider(int width, int height) {
		JPanel divider = new JPanel();
		divider.setBackground(Color.LIGHT_GRAY);
		divider.setMaximumSize(new Dimension(width, height));
		divider.setPreferredSize(new Dimension(width, height));
		divider.setAlignmentX(Component.CENTER_ALIGNMENT);
		return divider;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton || e.getSource() == homeButton) {
			dispose();
			// 이전 프레임 정보가 있다면 위치 복원 후 표시
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MainPageAWT(null, null); // 이전 정보 없으면 기본 홈 화면
			}
		} else if (e.getSource() == notificationButton) {
			dispose();
			Point currentLocation = this.getLocation();
			new NotificationPageAWT(this, currentLocation);
			this.dispose();
		} else if (e.getSource() == paymentButton) {
			dispose();
			Point currentLocation = this.getLocation();
			new MembershipFeeRecord(this, currentLocation);
			this.dispose();
		} else if (e.getSource() == myPageButton) {
			dispose();
			Point currentLocation = this.getLocation();
			new MyPageAWT(this, currentLocation);
			this.dispose();
		}
	}

	public static void main(String[] args) {
		new MyPageAWT();
	}
}