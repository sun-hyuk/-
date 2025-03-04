package Project;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

//import Project.MyPageAWT.BackgroundPanel;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class MyInfoAWT extends JFrame implements ActionListener {
	JButton backButton, homeButton, notificationButton, paymentButton, myPageButton;
	JPanel topPanel, topRightPanel, buttonGroup, bottomMenu, titlePanel, separatorContainer;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
	private JButton previousButton;
	private JButton saveinfoButton;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JLabel profileLabel;
	private JButton changeProfileButton;

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
	public MyInfoAWT() {
		this(null, null);
	}

	// 이전 프레임과 위치 정보를 받는 생성자
	public MyInfoAWT(JFrame previousFrame, Point previousLocation) {
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

		// 상단 패널
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);

		// 뒤로가기 버튼
		backButton = new JButton("<");
		backButton.setFont(new Font("SansSerif", Font.BOLD, 35));
		backButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		backButton.addActionListener(this);
		backButton.setBorder(BorderFactory.createEmptyBorder(20, 70, 0, 0));

		// 제목 (클릭 시 이동 없음)
		titleLabel = new JLabel("마이페이지", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		// 뒤로가기 버튼과 제목을 감싸는 패널
		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
				// 로그아웃 처리: 세션 초기화 후 로그인 페이지로 이동
				UserSession.getInstance().setCurrentUser(null);
				UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
				System.out.println("로그아웃: 세션 정보 초기화됨");
				dispose(); // 현재 창 닫기
				new LoginAWT(); // 로그인 페이지로 이동
			}
		});

		// 메뉴 버튼
		menuLabel = new JLabel("≡");
		menuLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
		menuLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PopupMenuManager.showPopupMenu(menuLabel, MyInfoAWT.this); // 팝업 메뉴 호출
			}
		});

		// 상단 오른쪽 패널 구성
		topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topRightPanel.setOpaque(false);
		spaceLabel = new JLabel("  "); // 공백 추가

		// 버튼 그룹 패널 (로그아웃, 메뉴)
		buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60)); // 오른쪽에서 60px 띄우기
		buttonGroup.setOpaque(false);
		buttonGroup.add(logoutLabel);
		buttonGroup.add(spaceLabel);
		buttonGroup.add(menuLabel);

		topRightPanel.add(buttonGroup);
		topPanel.add(topRightPanel, BorderLayout.EAST);

		// 구분선 (밝은 회색 1px 선)
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));
		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		separatorContainer.add(separatorLabel);
		topPanel.add(separatorContainer, BorderLayout.SOUTH);

		add(topPanel, BorderLayout.NORTH);

		// 상단 패널
		ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
		MemberBean currentUser = UserSession.getInstance().getCurrentUser();

		// 로그인 정보가 없으면 로그인 페이지로 이동
		if (currentAdmin == null && currentUser == null) {
			JOptionPane.showMessageDialog(this, "로그인 후 이용해주세요.");
			dispose();
			new LoginAWT();
			return;
		}

		JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		profilePanel.setOpaque(false);

		// 프로필 이미지: Manager와 Member 구분하여 처리
		ImageIcon profileIcon = null;
		if (currentAdmin != null) {
			ManagerMgr mgr = new ManagerMgr();
			profileIcon = mgr.getManagerProfile(currentAdmin.getManager_Id());
		} else if (currentUser != null) {
			MemberMgr mgr = new MemberMgr();
			profileIcon = mgr.getMemberProfile(currentUser.getMember_Id());
		}

		// 프로필 이미지가 없다면 기본 이미지 사용
		if (profileIcon == null) {
			profileIcon = new ImageIcon("C:\\Java\\myJava\\Project\\kakao.jpg");
		}

		Image img = profileIcon.getImage();
		Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
		profileLabel = new JLabel(new ImageIcon(resizedImg));
		profileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 사용자 정보 패널 생성 (로그인한 사용자 정보 사용)
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
			userRoleLabel = new JLabel("사용자");
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

		profilePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		// 프로필 사진 변경 버튼 생성
		changeProfileButton = new JButton("프로필");
		changeProfileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
		changeProfileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		changeProfileButton.setContentAreaFilled(false); // 배경을 투명하게 설정
		changeProfileButton.setBorderPainted(false); // 버튼 테두리 제거
		changeProfileButton.setFocusPainted(false); // 포커스 시 생기는 테두리 제거
		changeProfileButton.setVisible(false); // 처음에는 버튼을 숨김
		changeProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 파일 선택 후 byte 배열 가져오기
				byte[] imageBytes = selectImageAndGetBytes();
				if (imageBytes != null) {
					// 세션에서 로그인된 사용자 ID 사용
					MemberBean currentUser = UserSession.getInstance().getCurrentUser();
					ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();

					if (currentUser == null && currentAdmin == null) {
						JOptionPane.showMessageDialog(MyInfoAWT.this, "로그인 정보가 없습니다.");
						return;
					}

					// 로그인된 사용자의 타입에 맞게 프로필 사진 업데이트
					boolean updated = false;
					MemberMgr memberMgr = new MemberMgr();
					ManagerMgr managerMgr = new ManagerMgr();

					if (currentUser != null) {
						updated = memberMgr.updateMemberProfile(currentUser.getMember_Id(), imageBytes);
					} else if (currentAdmin != null) {
						updated = managerMgr.updateManagerProfile(currentAdmin.getManager_Id(), imageBytes);
					}

					if (updated) {
						JOptionPane.showMessageDialog(MyInfoAWT.this, "프로필 사진이 변경되었습니다.");
						// 변경된 사진을 다시 불러와서 프로필 라벨에 설정
						ImageIcon icon = null;
						if (currentUser != null) {
							icon = memberMgr.getMemberProfile(currentUser.getMember_Id());
						} else if (currentAdmin != null) {
							icon = managerMgr.getManagerProfile(currentAdmin.getManager_Id());
						}

						if (icon != null) {
							Image img = icon.getImage();
							Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
							profileLabel.setIcon(new ImageIcon(resizedImg));
						}
					} else {
						JOptionPane.showMessageDialog(MyInfoAWT.this, "프로필 사진 변경에 실패하였습니다.");
					}
				}
			}
		});

		// 프로필 아이콘 클릭 시 바로 프로필 사진 변경
		profileLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				byte[] imageBytes = selectImageAndGetBytes();
				if (imageBytes != null) {
					MemberBean currentUser = UserSession.getInstance().getCurrentUser();
					ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();

					if (currentUser == null && currentAdmin == null) {
						JOptionPane.showMessageDialog(MyInfoAWT.this, "로그인 정보가 없습니다.");
						return;
					}

					// 로그인된 사용자의 타입에 맞게 프로필 사진 업데이트
					boolean updated = false;
					MemberMgr memberMgr = new MemberMgr();
					ManagerMgr managerMgr = new ManagerMgr();

					if (currentUser != null) {
						updated = memberMgr.updateMemberProfile(currentUser.getMember_Id(), imageBytes);
					} else if (currentAdmin != null) {
						updated = managerMgr.updateManagerProfile(currentAdmin.getManager_Id(), imageBytes);
					}

					if (updated) {
						JOptionPane.showMessageDialog(MyInfoAWT.this, "프로필 사진이 변경되었습니다.");
						ImageIcon icon = null;
						if (currentUser != null) {
							icon = memberMgr.getMemberProfile(currentUser.getMember_Id());
						} else if (currentAdmin != null) {
							icon = managerMgr.getManagerProfile(currentAdmin.getManager_Id());
						}

						if (icon != null) {
							Image img = icon.getImage();
							Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
							profileLabel.setIcon(new ImageIcon(resizedImg));
							profileLabel.revalidate();
							profileLabel.repaint();
						}
					} else {
						JOptionPane.showMessageDialog(MyInfoAWT.this, "프로필 사진 변경에 실패하였습니다.");
					}
				}
			}
		});

		// 프로필 패널에 이미지와 관리자 정보 패널 추가
		profilePanel.add(profileLabel);
		profilePanel.add(userInfoPanel);
		profilePanel.add(changeProfileButton);
		profilePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		// 내 정보 관리 텍스트 라벨 추가
		JLabel manageInfoLabel = new JLabel("내 정보 관리");
		manageInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		manageInfoLabel.setForeground(Color.BLACK);
		manageInfoLabel.setHorizontalAlignment(JLabel.CENTER);
		manageInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		// 회원정보 수정 텍스트 라벨 (플레인 글자)
		JLabel editInfoLabel = new JLabel("회원정보 수정", JLabel.CENTER);
		editInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		editInfoLabel.setForeground(Color.BLACK);
		editInfoLabel.setAlignmentX(CENTER_ALIGNMENT);

		// editInfoLabel 위와 아래에 구분선을 추가할 컨테이너 생성
		JPanel editInfoContainer = new JPanel();
		editInfoContainer.setLayout(new BoxLayout(editInfoContainer, BoxLayout.Y_AXIS));
		editInfoContainer.setOpaque(false);
		editInfoContainer.add(createDivider(400, 1)); // 위쪽 구분선
		editInfoContainer.add(Box.createVerticalStrut(15)); // 구분선과 라벨 사이 간격
		editInfoContainer.add(editInfoLabel); // 회원정보 수정 라벨
		editInfoContainer.add(Box.createVerticalStrut(15)); // 라벨과 아래 구분선 사이 간격
		editInfoContainer.add(createDivider(200, 1)); // 아래쪽 구분선

		// 프로필 패널과 텍스트 라벨들을 담을 컨테이너 패널 생성
		JPanel profileContainer = new JPanel(new BorderLayout());
		profileContainer.setOpaque(false);
		profileContainer.add(manageInfoLabel, BorderLayout.NORTH);
		profileContainer.add(profilePanel, BorderLayout.CENTER);
		profileContainer.add(editInfoContainer, BorderLayout.SOUTH);

		// contentPanel에 프로필 컨테이너 추가
		JPanel contentPanel1 = new JPanel(new BorderLayout());
		contentPanel1.setOpaque(false);
		contentPanel1.add(profileContainer, BorderLayout.NORTH);
		add(contentPanel1, BorderLayout.CENTER);

		// 하단 메뉴
		bottomMenu = new JPanel(new FlowLayout(FlowLayout.CENTER, 210, 10));
		bottomMenu.setBorder(BorderFactory.createEmptyBorder(30, 70, 15, 70));
		bottomMenu.setOpaque(false);
		previousButton = new JButton("이전");
		saveinfoButton = new JButton("회원정보 저장");

		// ===== 비밀번호 입력 패널 =====
		// 1. "비밀번호" 입력 패널
		JPanel passwordInputPanel = new JPanel();
		passwordInputPanel.setLayout(new BoxLayout(passwordInputPanel, BoxLayout.Y_AXIS));
		passwordInputPanel.setOpaque(false);
		passwordInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		JLabel passwordLabel = new JLabel("비밀번호");
		passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		passwordLabel.setAlignmentX(0.0f);

		passwordField = new JPasswordField();
		passwordField.setPreferredSize(new Dimension(200, 30));
		passwordField.setMaximumSize(new Dimension(200, 30));
		passwordField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		passwordField.setAlignmentX(0.0f);

		passwordInputPanel.add(passwordLabel);
		passwordInputPanel.add(Box.createVerticalStrut(3));
		passwordInputPanel.add(passwordField);

		// 2. "비밀번호 확인" 입력 패널
		JPanel confirmPasswordInputPanel = new JPanel();
		confirmPasswordInputPanel.setLayout(new BoxLayout(confirmPasswordInputPanel, BoxLayout.Y_AXIS));
		confirmPasswordInputPanel.setOpaque(false);
		confirmPasswordInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel confirmPasswordLabel = new JLabel("비밀번호 확인");
		confirmPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
		confirmPasswordLabel.setAlignmentX(0.0f);

		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setPreferredSize(new Dimension(200, 30));
		confirmPasswordField.setMaximumSize(new Dimension(200, 30));
		confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		confirmPasswordField.setAlignmentX(0.0f);

		confirmPasswordInputPanel.add(confirmPasswordLabel);
		confirmPasswordInputPanel.add(Box.createVerticalStrut(3));
		confirmPasswordInputPanel.add(confirmPasswordField);

		// 3. 두 입력 패널을 하나의 컨테이너에 수직으로 배치
		JPanel passwordContainerPanel = new JPanel();
		passwordContainerPanel.setLayout(new BoxLayout(passwordContainerPanel, BoxLayout.Y_AXIS));
		passwordContainerPanel.setOpaque(false);
		passwordContainerPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 100, 100));
		passwordContainerPanel.add(passwordInputPanel);
		passwordContainerPanel.add(confirmPasswordInputPanel);

		// 위의 passwordContainerPanel을 중앙에 배치하기 위한 래퍼 패널 생성
		JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		centerWrapper.setOpaque(false);
		centerWrapper.add(passwordContainerPanel);

		contentPanel1.add(centerWrapper, BorderLayout.CENTER);

		// 버튼 스타일 적용
		Font menuFont = new Font("SansSerif", Font.BOLD, 16);
		JButton[] buttons = { previousButton, saveinfoButton };
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

	public byte[] selectImageAndGetBytes() {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try (FileInputStream fis = new FileInputStream(file);
					ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				return bos.toByteArray();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton || e.getSource() == previousButton) {
			dispose();
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MyPageAWT();
			}
		} else if (e.getSource() == saveinfoButton) {
			// 두 비밀번호 필드의 값 가져오기
			String password = new String(passwordField.getPassword());
			String confirmPassword = new String(confirmPasswordField.getPassword());

			if (password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력해주세요.");
				return;
			}
			if (!password.equals(confirmPassword)) {
				JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
				return;
			}

			// 로그인된 사용자 정보를 세션에서 가져오기
			MemberBean currentUser = UserSession.getInstance().getCurrentUser();
			ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();

			if (currentUser == null && currentAdmin == null) {
				JOptionPane.showMessageDialog(this, "로그인 정보가 없습니다.");
				return;
			}

			boolean updated = false;
			if (currentUser != null) {
				// 사용자의 비밀번호 업데이트
				String memberId = currentUser.getMember_Id();
				MemberMgr mgr = new MemberMgr();
				updated = mgr.updateMemberInfo(memberId, password);
			} else if (currentAdmin != null) {
				// 관리자의 비밀번호 업데이트
				String managerId = currentAdmin.getManager_Id();
				ManagerMgr mgr = new ManagerMgr();
				updated = mgr.updateManagerInfo(managerId, password);
			}

			if (updated) {
				JOptionPane.showMessageDialog(this, "정보가 수정되었습니다.");
				dispose();
				new MyPageAWT();
			} else {
				JOptionPane.showMessageDialog(this, "정보 수정에 실패하였습니다.");
			}
		}
	}

	public static void main(String[] args) {
		new MyInfoAWT();
	}
}
