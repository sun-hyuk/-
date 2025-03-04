package Project;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;

public class MemberListAWT extends JFrame implements ActionListener {
	JButton searchButton, backButton, homeButton, notificationButton, paymentButton, myPageButton, deleteButton;
	JPanel topPanel, topRightPanel, searchPanel, buttonGroup, bottomMenu, titlePanel, separatorContainer,
			memberListPanel;
	JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
	JTextField searchField;

	private ArrayList<MemberBean> members;
	private ArrayList<JCheckBox> rowcheckboxes = new ArrayList<>();
	private JCheckBox headerCheckBox;

	private JFrame previousFrame;
	private Point previousLocation;

	// UI 색상 상수 정의
	private final Color HEADER_BG_COLOR = new Color(33, 37, 41);
	private final Color HEADER_TEXT_COLOR = Color.WHITE;
	private final Color BUTTON_COLOR = new Color(51, 51, 51);
	private final Color BORDER_COLOR = new Color(222, 226, 230);

	class BackgroundPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, w, h);

			drawGamePattern(g2d, w, h);

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
			g2d.setColor(new Color(180, 180, 180));
			g2d.setStroke(new BasicStroke(1));
			g2d.drawPolygon(xPoints, yPoints, 5);
		}
	}

	public MemberListAWT() {
		this(null, null);
	}

	public MemberListAWT(JFrame previousFrame, Point previousLocation) {
		this.previousFrame = previousFrame;
		this.previousLocation = previousLocation;

		members = new ArrayList<>();
		setTitle("회원 관리");
		setSize(780, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new BorderLayout());
		setContentPane(backgroundPanel);

		// 상단 패널
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

		titleLabel = new JLabel("회원관리", JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 8, 10, 60));

		titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setOpaque(false);
		titlePanel.add(backButton);
		titlePanel.add(titleLabel);

		topPanel.add(titlePanel, BorderLayout.WEST);

		// 우측 상단 버튼
		logoutLabel = createStyledLabel("로그아웃", 15);
		menuLabel = createStyledLabel("≡", 23);
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

		// 구분선
		separatorLabel = new JLabel();
		separatorLabel.setOpaque(true);
		separatorLabel.setBackground(Color.LIGHT_GRAY);
		separatorLabel.setPreferredSize(new Dimension(650, 2));

		separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		separatorContainer.setOpaque(false);
		separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
		separatorContainer.add(separatorLabel);

		topPanel.add(separatorContainer, BorderLayout.SOUTH);

		// 검색 패널
		searchPanel = createStyledSearchPanel();

		// North 패널에 추가
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setOpaque(false);
		northPanel.add(topPanel);
		northPanel.add(searchPanel);
		add(northPanel, BorderLayout.NORTH);

		// 회원목록 패널
		memberListPanel = new JPanel();
		memberListPanel.setLayout(new BoxLayout(memberListPanel, BoxLayout.Y_AXIS));
		memberListPanel.setOpaque(false);
		memberListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		// 스크롤 패널
		JScrollPane scrollPane = createStyledScrollPane(memberListPanel);
		add(scrollPane, BorderLayout.CENTER);

		// 초기 데이터 로드
		loadMembers();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JLabel createStyledLabel(String text, int fontSize) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (text.equals("로그아웃")) {
					UserSession.getInstance().setCurrentUser(null);
					UserSession.getInstance().setCurrentAdmin(null); // 관리자 세션 초기화
					dispose();
					new LoginAWT();
				} else if (text.equals("≡")) {
					PopupMenuManager.showPopupMenu(label, MemberListAWT.this);
				}
			}
		});
		return label;
	}

	// 검색패널 수정: 버튼 간의 간격을 줄여 검색버튼과 삭제버튼이 붙어 있도록 함.
	private JPanel createStyledSearchPanel() {
		// FlowLayout의 hgap을 0으로 설정하여 구성요소 사이의 간격을 최소화
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 55, 10, 0));

		searchField = new JTextField(10);
		searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
		searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BUTTON_COLOR),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));

		searchButton = createStyledButton("확인", BUTTON_COLOR);
		deleteButton = createStyledButton("삭제", BUTTON_COLOR);

		panel.add(searchField);
		panel.add(searchButton);
		panel.add(deleteButton);

		return panel;
	}

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

	private JScrollPane createStyledScrollPane(JPanel panel) {
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 55, 20, 55));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		// 스크롤바 두께 설정 (세로: 8px, 가로: 8px)
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));

		// 스크롤바 스타일링 - 세로 스크롤바
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
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}
		});

		// 스크롤바 스타일링 - 가로 스크롤바
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
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}
		});

		return scrollPane;
	}

	private void updateMemberList() {
		memberListPanel.removeAll();
		rowcheckboxes.clear();

		// 컬럼 크기 설정
		Dimension checkDim = new Dimension(50, 35);
		Dimension idDim = new Dimension(40, 35);
		Dimension departmentDim = new Dimension(200, 35);
		Dimension stuIdDim = new Dimension(100, 35);
		Dimension nameDim = new Dimension(150, 35);
		Dimension memberidDim = new Dimension(100, 35);
		Dimension phoneDim = new Dimension(200, 35);
		Dimension genderDim = new Dimension(100, 35);
		Dimension roleDim = new Dimension(120, 35); // 회원등급 너비 증가 (110->120)
		Dimension feeDim = new Dimension(80, 35);

		// 헤더 패널
		JPanel headerPanel = createTableHeader(checkDim, idDim, departmentDim, stuIdDim, nameDim, memberidDim, phoneDim,
				genderDim, roleDim, feeDim);
		memberListPanel.add(headerPanel);

		// 데이터 행 추가
		for (int i = 0; i < members.size(); i++) {
			MemberBean member = members.get(i);

			// 행 패널 생성
			JPanel rowPanel = new JPanel();
			rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
			rowPanel.setOpaque(true);
			rowPanel.setBackground(Color.WHITE); // 모든 행을 흰색 배경으로 통일

			// 체크박스 생성 및 중앙 정렬 설정 추가
			JCheckBox rowCheckBox = new JCheckBox();
			rowCheckBox.setPreferredSize(checkDim);
			rowCheckBox.setMaximumSize(checkDim);
			rowCheckBox.setOpaque(false);
			rowCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
			rowCheckBox.setVerticalAlignment(SwingConstants.CENTER);
			rowCheckBox.setMargin(new Insets(0, 0, 0, 0));
			rowcheckboxes.add(rowCheckBox);

			// 데이터 레이블 생성
			JLabel idLabel = createDataLabel(String.valueOf(i + 1), idDim);
			JLabel departmentLabel = createDataLabel(member.getDepartment(), departmentDim);
			JLabel stuIdLabel = createDataLabel(member.getStudent_ID(), stuIdDim);
			JLabel nameLabel = createDataLabel(member.getName(), nameDim);
			JLabel memberidLabel = createDataLabel(member.getMember_Id(), memberidDim);
			JLabel phoneLabel = createDataLabel(member.getPhone_Number(), phoneDim);
			JLabel genderLabel = createDataLabel(member.getGender(), genderDim);
			
			// 수정된 부분: 회원등급을 JComboBox로 생성하여 수정 가능하게 함
	        String[] roles = {"회장", "임원", "일반회원"};
	        JComboBox<String> roleCombo = new JComboBox<>(roles);
	        roleCombo.setSelectedItem(member.getMember_Role());
	        
	        // 회원등급 콤보박스 스타일링
	        roleCombo.setFont(new Font("SansSerif", Font.BOLD, 14));
	        roleCombo.setForeground(new Color(51, 51, 51));
	        roleCombo.setBackground(Color.WHITE);
	        
	        // 콤보박스 테두리 설정
	        roleCombo.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createLineBorder(new Color(0, 123, 255), 1),
	            BorderFactory.createEmptyBorder(2, 5, 2, 5)
	        ));
	        
	     // UI 매니저 설정으로 콤보박스 화살표 스타일링
	        roleCombo.setUI(new BasicComboBoxUI() {
	            @Override
	            protected JButton createArrowButton() {
	                JButton button = new JButton("\u25BC"); // 아래쪽 화살표 유니코드
	                button.setFont(new Font("SansSerif", Font.PLAIN, 8));
	                button.setBackground(Color.WHITE);
	                button.setForeground(new Color(0, 123, 255));
	                button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
	                button.setFocusPainted(false);
	                button.setContentAreaFilled(false);
	                return button;
	            }
	            
	            // 드롭다운 배경색 설정
	            @Override
	            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
	                g.setColor(Color.WHITE); // 항상 흰색 배경
	                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	            }
	            
	            // 리스트 배경색 설정
	            @Override
	            protected ComboPopup createPopup() {
	                BasicComboPopup popup = new BasicComboPopup(comboBox) {
	                    @Override
	                    protected JScrollPane createScroller() {
	                        JScrollPane scroller = super.createScroller();
	                        scroller.getViewport().setBackground(Color.WHITE);
	                        scroller.setBackground(Color.WHITE);
	                        return scroller;
	                    }
	                    
	                    @Override
	                    public void paintComponent(Graphics g) {
	                        g.setColor(Color.WHITE);
	                        g.fillRect(0, 0, getWidth(), getHeight());
	                        super.paintComponent(g);
	                    }
	                };
	                return popup;
	            }
	        });
	        
	        // 콤보박스 크기 설정 - 충분한 너비 확보
	        roleCombo.setPreferredSize(new Dimension(roleDim.width - 5, roleDim.height - 5));
	        roleCombo.setMaximumSize(new Dimension(roleDim.width - 5, roleDim.height - 5));
	        
	     // 팝업 메뉴 너비 설정 - 항목 길이에 맞게 정확히 조정
	        Object popupObj = roleCombo.getUI().getAccessibleChild(roleCombo, 0);
	        if (popupObj instanceof JPopupMenu) {
	            JPopupMenu popup = (JPopupMenu) popupObj;
	            // 항목 3개만 딱 맞게 표시되도록 너비와 높이 조정
	            int itemHeight = 28; // 각 항목의 높이 (패딩 포함)
	            popup.setPopupSize(new Dimension(105, itemHeight * 3)); // 3개 항목에 맞춘 높이, 너비는 텍스트에 맞게 조정
	        }

	        // 콤보박스 렌더러 설정 - 항목간 간격 최소화
	        roleCombo.setRenderer(new DefaultListCellRenderer() {
	            @Override
	            public Component getListCellRendererComponent(JList<?> list, Object value, 
	                    int index, boolean isSelected, boolean cellHasFocus) {
	                JLabel label = (JLabel) super.getListCellRendererComponent(
	                        list, value, index, isSelected, cellHasFocus);
	                
	                // 가운데 정렬
	                label.setHorizontalAlignment(SwingConstants.CENTER);
	                
	                // 배경색 설정
	                if (isSelected) {
	                    label.setBackground(new Color(0, 123, 255));
	                    label.setForeground(Color.WHITE);
	                } else {
	                    label.setBackground(Color.WHITE);
	                    label.setForeground(new Color(51, 51, 51));
	                }
	                
	                // 패딩 최소화
	                label.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
	                
	                return label;
	            }
	        });
	        
	        roleCombo.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                String newRole = (String) roleCombo.getSelectedItem();
	                MemberMgr mgr = new MemberMgr();
	                boolean updated = mgr.updateMemberRole(member.getMember_Id(), newRole);
	                if (updated) {
	                    JOptionPane.showMessageDialog(MemberListAWT.this, "회원 등급이 변경되었습니다.");
	                } else {
	                    JOptionPane.showMessageDialog(MemberListAWT.this, "회원 등급 변경에 실패했습니다.");
	                }
	            }
	        });
	        
			// 회비납부 레이블 스타일링
			String feeStatus = member.getAdmission_Fee();
			JLabel feeLabel = new JLabel(feeStatus, SwingConstants.CENTER);
			feeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
			feeLabel.setPreferredSize(feeDim);
			feeLabel.setMinimumSize(feeDim);
			feeLabel.setMaximumSize(feeDim);
			
			// 회비납부 상태에 따른 스타일링
			if (feeStatus.equals("완료")) {
			    feeLabel.setForeground(new Color(40, 167, 69)); // 초록색
			    feeLabel.setBorder(BorderFactory.createCompoundBorder(
			        BorderFactory.createLineBorder(new Color(40, 167, 69), 1),
			        BorderFactory.createEmptyBorder(3, 5, 3, 5)
			    ));
			} else {
			    feeLabel.setForeground(new Color(220, 53, 69)); // 빨간색
			    feeLabel.setBorder(BorderFactory.createCompoundBorder(
			        BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
			        BorderFactory.createEmptyBorder(3, 5, 3, 5)
			    ));
			}

			// 행에 컴포넌트 추가
			rowPanel.add(createCenteredPanel(rowCheckBox, checkDim));
			rowPanel.add(createCenteredPanel(idLabel, idDim));
			rowPanel.add(createCenteredPanel(departmentLabel, departmentDim));
			rowPanel.add(createCenteredPanel(stuIdLabel, stuIdDim));
			rowPanel.add(createCenteredPanel(nameLabel, nameDim));
			rowPanel.add(createCenteredPanel(memberidLabel, memberidDim));
			rowPanel.add(createCenteredPanel(phoneLabel, phoneDim));
			rowPanel.add(createCenteredPanel(genderLabel, genderDim));
			rowPanel.add(createCenteredPanel(roleCombo, roleDim));
			rowPanel.add(createCenteredPanel(feeLabel, feeDim));

			memberListPanel.add(rowPanel);

			// 마지막 행이 아니면 구분선 추가
			if (i < members.size() - 1) {
				memberListPanel.add(createSeparator());
			}
		}

		memberListPanel.revalidate();
		memberListPanel.repaint();
	}

	private JPanel createTableHeader(Dimension... dimensions) {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setBackground(HEADER_BG_COLOR);
		headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

		// 전체 선택 체크박스
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

		// 헤더 레이블 생성
		String[] headers = { "", "No.", "학과", "학번", "이름", "아이디", "연락처", "성별", "회원등급", "회비납부" };

		headerPanel.add(createCenteredPanel(headerCheckBox, dimensions[0]));

		for (int i = 1; i < headers.length; i++) {
			JLabel headerLabel = new JLabel(headers[i], SwingConstants.CENTER);
			headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
			headerLabel.setForeground(HEADER_TEXT_COLOR);
			headerPanel.add(createCenteredPanel(headerLabel, dimensions[i]));
		}

		return headerPanel;
	}

	private JLabel createDataLabel(String text, Dimension dim) {
		JLabel label = new JLabel(text, SwingConstants.CENTER);
		label.setFont(new Font("SansSerif", Font.PLAIN, 14));
		label.setPreferredSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
		return label;
	}

	private JPanel createCenteredPanel(JComponent component, Dimension dim) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		panel.setPreferredSize(dim);
		panel.setMinimumSize(dim);
		panel.setMaximumSize(dim);
		panel.add(component);
		return panel;
	}

	private JSeparator createSeparator() {
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		separator.setForeground(BORDER_COLOR);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		return separator;
	}

	public void loadMembers() {
		MemberMgr memberMgr = new MemberMgr();
		Vector<MemberBean> vector = memberMgr.listMember();
		members = new ArrayList<>(vector);
		updateMemberList();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == backButton) {
			dispose();
			if (previousFrame != null && previousLocation != null) {
				previousFrame.setLocation(previousLocation);
				previousFrame.setVisible(true);
			} else {
				new MyPageAWT();
			}
		} else if (e.getSource() == searchButton) {
			handleSearch();
		} else if (e.getSource() == deleteButton) {
			handleDelete();
		}
	}

	private void handleSearch() {
		String keyword = searchField.getText().trim();
		if (keyword.isEmpty()) {
			loadMembers();
		} else {
			filterMembers(keyword);
		}
	}

	private void filterMembers(String keyword) {
		MemberMgr memberMgr = new MemberMgr();
		Vector<MemberBean> vector = memberMgr.listMember();
		members = new ArrayList<>();

		for (MemberBean member : vector) {
			if (member.getName().contains(keyword) || member.getMember_Id().contains(keyword)
					|| member.getDepartment().contains(keyword)) {
				members.add(member);
			}
		}

		updateMemberList();
	}

	private void handleDelete() {
		ArrayList<String> idsToDelete = new ArrayList<>();
		for (int i = 0; i < rowcheckboxes.size(); i++) {
			if (rowcheckboxes.get(i).isSelected() && i < members.size()) {
				idsToDelete.add(members.get(i).getMember_Id());
			}
		}

		if (idsToDelete.isEmpty()) {
			JOptionPane.showMessageDialog(this, "삭제할 회원을 선택해주세요.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "선택한 회원을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			MemberMgr memberMgr = new MemberMgr();
			for (String id : idsToDelete) {
				memberMgr.deleteMember(id);
			}
			JOptionPane.showMessageDialog(this, "선택한 회원이 삭제되었습니다.");
			loadMembers();
		}
	}

	public static void main(String[] args) {
		new MemberListAWT();
	}
}