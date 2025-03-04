package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

//배경 이미지를 위한 커스텀 패널 클래스 수정
class BackgroundPanel extends JPanel {
	private Image backgroundImage;
	private Image scaledImage;
	private int lastWidth = -1;
	private int lastHeight = -1;

	public BackgroundPanel(String imagePath) {
		try {
			ImageIcon imageIcon = new ImageIcon(imagePath);
			backgroundImage = imageIcon.getImage();
			setOpaque(false); // 패널을 투명하게 설정
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// 현재 패널의 크기 가져오기
		int width = getWidth();
		int height = getHeight();

		// 패널 크기가 변경되었거나 처음 그리는 경우에만 이미지 스케일링
		if (backgroundImage != null && (width != lastWidth || height != lastHeight || scaledImage == null)) {
			scaledImage = backgroundImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			lastWidth = width;
			lastHeight = height;
		}

		// 스케일링된 이미지 그리기
		if (scaledImage != null) {
			Graphics2D g2d = (Graphics2D) g;
			// 이미지 렌더링 품질 향상을 위한 설정
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// 배경 이미지 그리기
			g2d.drawImage(scaledImage, 0, 0, this);

			// 반투명 오버레이 추가하여 가독성 향상
			g2d.setColor(new Color(255, 255, 255, 180)); // 흰색 반투명 오버레이
			g2d.fillRect(0, 0, width, height);
		}
	}
}

class LoginAWT extends JFrame implements ActionListener {
	// 기존 필드들은 그대로 유지
	private JTextField idTf, pwTf, adminIdTf, adminPwTf;
	private JTextField signUpIdTf, signUpPwTf, pwConfirmTf, phoneTf, departmentTf, studentIdTf, nameTf;
	private JLabel idCheckLabel;
	private JButton logBtn, adminLogBtn, idCheckBtn, departmentSearchBtn, signUpBtn;
	private JRadioButton maleRadio, femaleRadio;
	private ButtonGroup genderGroup;
	private String title = "체크메이트";
	private Color primaryColor = new Color(59, 89, 240);
	private Color backgroundColor = Color.WHITE;
	private Font titleFont = new Font("맑은 고딕", Font.BOLD, 24);
	private Font labelFont = new Font("맑은 고딕", Font.PLAIN, 14);
	private Font buttonFont = new Font("맑은 고딕", Font.BOLD, 14);
	private CardLayout cardLayout = new CardLayout();
	private JPanel cardPanel = new JPanel(cardLayout);

	// 배경 이미지를 사용하는 패널로 변경
	private BackgroundPanel loginPanel;
	private BackgroundPanel adminLoginPanel;
	private BackgroundPanel signUpPanel;

	public LoginAWT() {
		setSize(400, 580);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setTitle(title);
		setLocationRelativeTo(null);

		// 배경 이미지를 사용하는 패널 초기화
		loginPanel = new BackgroundPanel("C:\\Java\\myJava\\Project\\login_icon3.png");
		adminLoginPanel = new BackgroundPanel("C:\\Java\\myJava\\Project\\login_icon3.png");
		signUpPanel = new BackgroundPanel("C:\\Java\\myJava\\Project\\login_icon3.png");

		cardPanel.setBounds(0, 0, 400, 580);
		cardPanel.setLayout(cardLayout);

		setupLoginPanel();
		setupSignUpPanel();
		setupAdminLoginPanel();

		cardPanel.add(loginPanel, "Login");
		cardPanel.add(signUpPanel, "SignUp");
		cardPanel.add(adminLoginPanel, "AdminLogin");

		add(cardPanel);
		setVisible(true);
	}

	// 로그인 화면 설정
	private void setupLoginPanel() {
		loginPanel.setLayout(null);
		loginPanel.setBackground(backgroundColor);
		
		// 제목
		JLabel logo = new JLabel(title);
		logo.setFont(titleFont);
		logo.setHorizontalAlignment(JLabel.CENTER);
		logo.setBounds(100, 60, 200, 40);
		
		// 이미지 추가
		ImageIcon imageIcon = new ImageIcon(""); // 이미지 경로 설정
		Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // 이미지 크기 조정
		imageIcon = new ImageIcon(image);

		JLabel imageLabel = new JLabel(imageIcon);
		imageLabel.setBounds(150, 110, 100, 100); // 이미지 위치와 크기 설정

		// 아이디 입력
		JLabel idl = new JLabel("아이디");
		idl.setFont(labelFont);
		idl.setBounds(50, 220, 100, 20);

		idTf = new JTextField();
		idTf.setBounds(50, 245, 300, 40);
		idTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		idTf.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

		// 비밀번호 입력
		JLabel pwl = new JLabel("비밀번호");
		pwl.setFont(labelFont);
		pwl.setBounds(50, 295, 100, 20);

		pwTf = new JPasswordField();
		pwTf.setBounds(50, 320, 300, 40);
		pwTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 로그인 버튼
		logBtn = new JButton("로그인");
		logBtn.setBounds(50, 390, 300, 45);
		logBtn.setBackground(primaryColor);
		logBtn.setForeground(Color.WHITE);
		logBtn.setFont(buttonFont);
		logBtn.setBorder(new RoundedBorder(10));
		logBtn.setFocusPainted(false);
		logBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logBtn.addActionListener(this);

		// 회원가입 링크
		JLabel signUpLabel = new JLabel("회원가입");
		signUpLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		signUpLabel.setForeground(Color.DARK_GRAY);
		signUpLabel.setBounds(50, 440, 60, 20);
		signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		signUpLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cardLayout.show(cardPanel, "SignUp");
				getRootPane().setDefaultButton(signUpBtn); // 회원가입 버튼을 기본 버튼으로 설정
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				signUpLabel.setForeground(primaryColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				signUpLabel.setForeground(Color.DARK_GRAY);
			}
		});

		// 관리자 로그인 링크
		JLabel adminLoginLabel = new JLabel("관리자 로그인");
		adminLoginLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		adminLoginLabel.setForeground(Color.DARK_GRAY);
		adminLoginLabel.setBounds(270, 440, 80, 20);
		adminLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		adminLoginLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cardLayout.show(cardPanel, "AdminLogin");
				getRootPane().setDefaultButton(adminLogBtn); // 관리자 로그인 버튼을 기본 버튼으로 설정
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				adminLoginLabel.setForeground(primaryColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				adminLoginLabel.setForeground(Color.DARK_GRAY);
			}
		});

		loginPanel.add(logo);
		loginPanel.add(imageLabel); // 이미지 추가
		loginPanel.add(idl);
		loginPanel.add(idTf);
		loginPanel.add(pwl);
		loginPanel.add(pwTf);
		loginPanel.add(logBtn);
		loginPanel.add(signUpLabel);
		loginPanel.add(adminLoginLabel);

		// 로그인 버튼을 기본 버튼으로 설정
		getRootPane().setDefaultButton(logBtn);
	}

	// 관리자 로그인 화면 추가
	private void setupAdminLoginPanel() {
		adminLoginPanel.setLayout(null);
		adminLoginPanel.setBackground(backgroundColor);

		// 제목
		JLabel adminTitle = new JLabel("관리자 로그인");
		adminTitle.setFont(titleFont);
		adminTitle.setHorizontalAlignment(JLabel.CENTER);
		adminTitle.setBounds(100, 60, 200, 40);

		// 관리자 아이디
		JLabel adminIdLabel = new JLabel("관리자 ID");
		adminIdLabel.setFont(labelFont);
		adminIdLabel.setBounds(50, 220, 100, 20);

		adminIdTf = new JTextField();
		adminIdTf.setBounds(50, 245, 300, 40);
		adminIdTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		adminIdTf.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

		// 비밀번호
		JLabel adminPwLabel = new JLabel("비밀번호");
		adminPwLabel.setFont(labelFont);
		adminPwLabel.setBounds(50, 295, 100, 20);

		adminPwTf = new JPasswordField();
		adminPwTf.setBounds(50, 320, 300, 40);
		adminPwTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 로그인 버튼
		adminLogBtn = new JButton("관리자 로그인");
		adminLogBtn.setBounds(50, 390, 300, 45);
		adminLogBtn.setBackground(primaryColor);
		adminLogBtn.setForeground(Color.WHITE);
		adminLogBtn.setFont(buttonFont);
		adminLogBtn.setBorder(new RoundedBorder(10));
		adminLogBtn.setFocusPainted(false);
		adminLogBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		adminLogBtn.addActionListener(this);

		// 일반 로그인으로 돌아가기
		JLabel backToLoginLabel = new JLabel("일반 로그인");
		backToLoginLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		backToLoginLabel.setForeground(Color.DARK_GRAY);
		backToLoginLabel.setBounds(280, 440, 80, 20);
		backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backToLoginLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cardLayout.show(cardPanel, "Login");
				getRootPane().setDefaultButton(logBtn); // 로그인 버튼을 기본 버튼으로 설정
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				backToLoginLabel.setForeground(primaryColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				backToLoginLabel.setForeground(Color.DARK_GRAY);
			}
		});

		adminLoginPanel.add(adminTitle);
		adminLoginPanel.add(adminIdLabel);
		adminLoginPanel.add(adminIdTf);
		adminLoginPanel.add(adminPwLabel);
		adminLoginPanel.add(adminPwTf);
		adminLoginPanel.add(adminLogBtn);
		adminLoginPanel.add(backToLoginLabel);
	}

	// 회원가입 화면 설정
	private void setupSignUpPanel() {
		signUpPanel.setLayout(null);
		signUpPanel.setBackground(backgroundColor);

		JLabel signUpTitle = new JLabel("회원가입");
		signUpTitle.setFont(titleFont);
		signUpTitle.setHorizontalAlignment(JLabel.CENTER);
		signUpTitle.setBounds(100, 30, 200, 40);

		// 이름 입력 필드
		JLabel nameLabel = new JLabel("이름");
		nameLabel.setFont(labelFont);
		nameLabel.setBounds(50, 90, 100, 20);

		nameTf = new JTextField();
		nameTf.setBounds(50, 115, 140, 35);
		nameTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 성별 선택
		JLabel genderLabel = new JLabel("성별");
		genderLabel.setFont(labelFont);
		genderLabel.setBounds(210, 90, 50, 20);

		maleRadio = new JRadioButton("남성");
		femaleRadio = new JRadioButton("여성");
		genderGroup = new ButtonGroup();
		genderGroup.add(maleRadio);
		genderGroup.add(femaleRadio);

		maleRadio.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		femaleRadio.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
		maleRadio.setBackground(backgroundColor);
		femaleRadio.setBackground(backgroundColor);

		maleRadio.setBounds(210, 115, 60, 35);
		femaleRadio.setBounds(280, 115, 60, 35);

		// 아이디
		JLabel signUpIdLabel = new JLabel("아이디");
		signUpIdLabel.setFont(labelFont);
		signUpIdLabel.setBounds(50, 160, 100, 20);

		signUpIdTf = new JTextField();
		signUpIdTf.setBounds(50, 185, 210, 35);
		signUpIdTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 중복확인 버튼
		idCheckBtn = new JButton("중복확인");
		idCheckBtn.setBounds(270, 185, 80, 35);
		idCheckBtn.setBackground(new Color(240, 240, 240));
		idCheckBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		idCheckBtn.setBorder(new RoundedBorder(5));
		idCheckBtn.setFocusPainted(false);
		idCheckBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		idCheckBtn.addActionListener(this);

		// 비밀번호
		JLabel signUpPwLabel = new JLabel("비밀번호");
		signUpPwLabel.setFont(labelFont);
		signUpPwLabel.setBounds(50, 230, 100, 20);

		signUpPwTf = new JPasswordField();
		signUpPwTf.setBounds(50, 255, 300, 35);
		signUpPwTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 비밀번호 확인
		JLabel pwConfirmLabel = new JLabel("비밀번호 확인");
		pwConfirmLabel.setFont(labelFont);
		pwConfirmLabel.setBounds(50, 300, 100, 20);

		pwConfirmTf = new JPasswordField();
		pwConfirmTf.setBounds(50, 325, 300, 35);
		pwConfirmTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 전화번호
		JLabel phoneLabel = new JLabel("전화번호 (010-XXXX-XXXX)");
		phoneLabel.setFont(labelFont);
		phoneLabel.setBounds(50, 370, 200, 20);

		phoneTf = new JTextField();
		phoneTf.setBounds(50, 395, 300, 35);
		phoneTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 학과
		JLabel departmentLabel = new JLabel("학과");
		departmentLabel.setFont(labelFont);
		departmentLabel.setBounds(50, 440, 50, 20);

		departmentTf = new JTextField();
		departmentTf.setBounds(50, 465, 210, 35);
		departmentTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		departmentTf.setEnabled(false);

		// 학과 찾기 버튼
		departmentSearchBtn = new JButton("학과 찾기");
		departmentSearchBtn.setBounds(270, 465, 80, 35);
		departmentSearchBtn.setBackground(new Color(240, 240, 240));
		departmentSearchBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		departmentSearchBtn.setBorder(new RoundedBorder(5));
		departmentSearchBtn.setFocusPainted(false);
		departmentSearchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		departmentSearchBtn.addActionListener(this);

		// 학번
		JLabel studentIdLabel = new JLabel("학번");
		studentIdLabel.setFont(labelFont);
		studentIdLabel.setBounds(50, 510, 50, 20);

		studentIdTf = new JTextField();
		studentIdTf.setBounds(50, 535, 300, 35);
		studentIdTf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		// 회원가입 버튼
		signUpBtn = new JButton("회원가입");
		signUpBtn.setBounds(50, 590, 300, 45);
		signUpBtn.setBackground(primaryColor);
		signUpBtn.setForeground(Color.WHITE);
		signUpBtn.setFont(buttonFont);
		signUpBtn.setBorder(new RoundedBorder(10));
		signUpBtn.setFocusPainted(false);
		signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		signUpBtn.addActionListener(this);

		// 로그인 화면으로 돌아가기
		JLabel backToLoginLabel = new JLabel("로그인 화면으로 돌아가기");
		backToLoginLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		backToLoginLabel.setForeground(Color.DARK_GRAY);
		backToLoginLabel.setBounds(130, 650, 160, 20);
		backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backToLoginLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cardLayout.show(cardPanel, "Login");
				getRootPane().setDefaultButton(logBtn); // 로그인 버튼을 기본 버튼으로 설정
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				backToLoginLabel.setForeground(primaryColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				backToLoginLabel.setForeground(Color.DARK_GRAY);
			}
		});

		// 스크롤 패널 추가
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(null);
		contentPanel.setPreferredSize(new Dimension(380, 750));
		contentPanel.setBackground(backgroundColor);
		contentPanel.setOpaque(false); // 내부 패널을 투명하게

		contentPanel.add(signUpTitle);
		contentPanel.add(nameLabel);
		contentPanel.add(nameTf);
		contentPanel.add(genderLabel);
		contentPanel.add(maleRadio);
		contentPanel.add(femaleRadio);
		contentPanel.add(signUpIdLabel);
		contentPanel.add(signUpIdTf);
		contentPanel.add(idCheckBtn);
		contentPanel.add(signUpPwLabel);
		contentPanel.add(signUpPwTf);
		contentPanel.add(pwConfirmLabel);
		contentPanel.add(pwConfirmTf);
		contentPanel.add(phoneLabel);
		contentPanel.add(phoneTf);
		contentPanel.add(departmentLabel);
		contentPanel.add(departmentTf);
		contentPanel.add(departmentSearchBtn);
		contentPanel.add(studentIdLabel);
		contentPanel.add(studentIdTf);
		contentPanel.add(signUpBtn);
		contentPanel.add(backToLoginLabel);

		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBounds(0, 0, 400, 580);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setOpaque(false); // 스크롤 패널 자체를 투명하게
		scrollPane.getViewport().setOpaque(false); // 뷰포트를 투명하게

		signUpPanel.add(scrollPane);
	}

	// 둥근 테두리 버튼 클래스
	private class RoundedBorder implements Border {
		private int radius;

		RoundedBorder(int radius) {
			this.radius = radius;
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
		}
	}

	// 로그인 이벤트 처리
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		// 일반로그인 버튼 클릭 시
		if (obj == logBtn) {
			String id = idTf.getText().trim();
			String password = pwTf.getText().trim();

			if (id.isEmpty() || password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			MemberMgr memberMgr = new MemberMgr();
			MemberBean memberBean = new MemberBean();
			memberBean.setMember_Id(id);
			memberBean.setPassword(password);

			if (memberMgr.authenticateMember(memberBean)) {
				UserSession.getInstance().setCurrentUser(memberBean);
				System.out.println("로그인 성공: " + memberBean.getName()); // 콘솔 출력으로 확인

				JOptionPane.showMessageDialog(this, "로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
				this.dispose(); // 현재 로그인 창 닫기
				new MainPageAWT(); // mainpageAWT 일반회원 (메인 화면)
			} else {
				JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 잘못되었습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
			}
		}

		// 관리자 로그인 버튼 클릭 시
		if (obj == adminLogBtn) {
			String manager_id = adminIdTf.getText().trim();
			String manager_password = adminPwTf.getText().trim();

			if (manager_id.isEmpty() || manager_password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "관리자 아이디와 비밀번호를 입력하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			ManagerMgr managerMgr = new ManagerMgr();
			ManagerBean managerBean = new ManagerBean();
			managerBean.setManager_Id(manager_id);
			managerBean.setManager_Password(manager_password);

			if (managerMgr.authenticateManager(managerBean)) {
				UserSession.getInstance().setCurrentAdmin(managerBean);
				JOptionPane.showMessageDialog(this, "관리자 로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
				new MainPageAWT(); // 관리자 메인 페이지로 이동
			} else {
				JOptionPane.showMessageDialog(this, "관리자 아이디 또는 비밀번호가 잘못되었습니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
			}
		}

		// 아이디 중복 확인 버튼 이벤트 처리
		if (obj == idCheckBtn) {
			String inputId = signUpIdTf.getText().trim();
			if (inputId.isEmpty()) {
				JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
				return;
			}

			MemberMgr memberMgr = new MemberMgr();
			boolean exists = memberMgr.checkDuplicateId(inputId);

			if (exists) {
				JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "중복 확인", JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.", "중복 확인", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// 학과 검색 버튼 클릭 시
		if (obj == departmentSearchBtn) {
			// 미리 정의된 학과 배열
			String[] departments = { "간호학과", "건축공학과", "게임공학과", "경영학과", "경제학과", "국어국문학과", "광고홍보학과", "기계공학과", "디지털콘텐츠학과",
					"로봇자동화공학과", "물리치료학과", "바이오의약공학과", "산업경영빅데이터공학과", "산업공학과", "산업ICT공학과", "생명공학과", "신문방송학과", "소프트웨어공학과",
					"응용소프트웨어공학과", "인간공학과", "인공지능학과", "자동차공학과", "전기공학과", "전자공학과", "정보통신공학", "컴퓨터공학과", "화학공학과" };

			// 모던한 학과 선택 리스트 패널 생성
			JList<String> deptList = new JList<>(departments);
			deptList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			deptList.setFont(new Font("맑은 고딕", Font.PLAIN, 13));

			JScrollPane scrollPane = new JScrollPane(deptList);
			scrollPane.setPreferredSize(new java.awt.Dimension(250, 200));
			scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			// 사용자 정의 JOptionPane
			JPanel optionPanel = new JPanel(new BorderLayout());
			optionPanel.add(new JLabel("학과를 선택하세요", JLabel.CENTER), BorderLayout.NORTH);
			optionPanel.add(scrollPane, BorderLayout.CENTER);

			int option = JOptionPane.showConfirmDialog(this, optionPanel, "학과 선택", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);

			if (option == JOptionPane.OK_OPTION) {
				String selectedDept = deptList.getSelectedValue();
				if (selectedDept != null) {
					departmentTf.setText(selectedDept);
				} else {
					JOptionPane.showMessageDialog(this, "학과를 선택하세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		// 회원가입 버튼 클릭 시
		if (obj == signUpBtn) {
			String id = signUpIdTf.getText().trim();
			String password = signUpPwTf.getText().trim();
			String confirmPw = pwConfirmTf.getText().trim();
			String name = nameTf.getText().trim();
			String phone = phoneTf.getText().trim();
			String department = departmentTf.getText().trim();
			String studentId = studentIdTf.getText().trim();

			// 성별 선택 확인
			if (!maleRadio.isSelected() && !femaleRadio.isSelected()) {
				JOptionPane.showMessageDialog(this, "성별을 선택해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String gender = maleRadio.isSelected() ? "남" : "여";

			// 모든 필드가 채워졌는지 검증
			if (id.isEmpty() || password.isEmpty() || confirmPw.isEmpty() || name.isEmpty() || phone.isEmpty()
					|| department.isEmpty() || studentId.isEmpty()) {
				JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 비밀번호 확인 검증
			if (!password.equals(confirmPw)) {
				JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 전화번호 중복 검사
			MemberMgr memberMgr = new MemberMgr();
			if (memberMgr.checkDuplicatePhone(phone)) {
				JOptionPane.showMessageDialog(this, "이미 사용 중인 전화번호입니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 학번 중복 검사
			if (memberMgr.checkDuplicateStudentId(studentId)) {
				JOptionPane.showMessageDialog(this, "이미 사용 중인 학번입니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 아이디와 비밀번호는 영문자와 숫자만 허용 (정규표현식)
			if (!id.matches("^[A-Za-z0-9]+$")) {
				JOptionPane.showMessageDialog(this, "아이디는 영문자와 숫자만 사용할 수 있습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!password.matches("^[A-Za-z0-9]+$")) {
				JOptionPane.showMessageDialog(this, "비밀번호는 영문자와 숫자만 사용할 수 있습니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 전화번호는 010-2222-2222 형식만 허용 (정규표현식)
			if (!phone.matches("^010-\\d{4}-\\d{4}$")) {
				JOptionPane.showMessageDialog(this, "전화번호는 010-XXXX-XXXX 형식이어야 합니다.", "입력 오류",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 학번은 숫자만 허용
			if (!studentId.matches("^\\d+$")) {
				JOptionPane.showMessageDialog(this, "학번은 숫자만 입력 가능합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 회원정보 저장을 위한 MemberBean 생성
			MemberBean memberBean = new MemberBean();
			memberBean.setMember_Id(id);
			memberBean.setPassword(password);
			memberBean.setName(name);
			memberBean.setPhone_Number(phone);
			memberBean.setDepartment(department);
			memberBean.setStudent_ID(studentId);
			memberBean.setGender(gender);
			memberBean.setMember_Role("일반회원"); // 기본값
			memberBean.setAdmission_Fee("5000"); // 기본값
			memberBean.setMember_Profile(null);

			// DB에 회원정보 저장
			boolean success = memberMgr.insertMember(memberBean);

			if (success) {
				JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다!", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
				cardLayout.show(cardPanel, "Login"); // 로그인 화면으로 이동
			} else {
				JOptionPane.showMessageDialog(this, "회원가입에 실패했습니다.", "회원가입 실패", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void main(String[] args) {
		new LoginAWT();
	}
}
