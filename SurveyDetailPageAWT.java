package Project;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;


import java.util.*;

public class SurveyDetailPageAWT extends JFrame implements ActionListener {
	private String Memberid;
    JButton backButton;
    JPanel topPanel, topRightPanel, buttonGroup, titlePanel, separatorContainer;
    JPanel contentPanel, surveyListPanel;
    JLabel titleLabel, separatorLabel, logoutLabel, menuLabel, spaceLabel;
    JLabel subtitleLabel;
    ArrayList<SurveyItem> surveyItems;
    
    private SurveyItem selectedSurveyItem = null; // 현재 선택된 항목
    private SurveyItem votedSurveyItem = null; // 투표가 적용된 항목
    private JLabel totalLabel; // 총 참여 인원 표시 라벨
    private int pollId;
    private SurveyBean currentSurvey; // 현재 설문 상세 정보를 저장하는 변수
    
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
            for(int x = 25; x < width; x += patternSize) {
                for(int y = 0; y < height; y += patternSize) {
                    // 주사위 그리기
                    drawDice(g2d, x + 20, y + 20);
                    
                    // 체스말 그리기
                    if(((x -25) + y) % (patternSize * 2) == 0) {
                        drawChessPiece(g2d, x + 80, y + 40);
                    }
                    
                    // 보드게임 말 그리기
                    if(((x -25) + y) % (patternSize * 2) == patternSize) {
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
            int[] xPoints = {x, x+20, x+16, x+12, x+8, x+4};
            int[] yPoints = {y+30, y+30, y+20, y+10, y+20, y+30};
            g2d.fillPolygon(xPoints, yPoints, 6);
            g2d.setColor(new Color(180, 180, 180));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawPolygon(xPoints, yPoints, 6);
        }
        
        private void drawGamePiece(Graphics2D g2d, int x, int y) {
            g2d.setColor(new Color(210, 210, 210));
            // 미플 형태의 게임말
            int[] xPoints = {x, x+20, x+40, x+30, x+10};
            int[] yPoints = {y+30, y, y+30, y+45, y+45};
            g2d.fillPolygon(xPoints, yPoints, 5);
            g2d.setColor(new Color(180, 180, 180));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawPolygon(xPoints, yPoints, 5);
        }
    }
    
    public SurveyDetailPageAWT(int pollId) {
    	MemberBean currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            
        }
       
        this.pollId = pollId;
        setTitle("설문");
        setSize(780, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        //getContentPane().setBackground(Color.WHITE);
        
        // BackgroundPanel을 ContentPane으로 설정
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);
        
        UIManager.put("Button.disabledText", new Color(211, 211, 211));
        
        // surveyItems 리스트 초기화
        surveyItems = new ArrayList<>(); // 이 부분을 추가해서 초기화합니다.
        
        // surveyListPanel을 초기화
        surveyListPanel = new JPanel();
        surveyListPanel.setLayout(new BoxLayout(surveyListPanel, BoxLayout.Y_AXIS));
        surveyListPanel.setBackground(new Color(240, 240, 240));
        
        // ── 상단 헤더 영역 ─────────────────────────────
        topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);  // 배경 투명하게 설정
        
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
        titlePanel.setOpaque(false);  // 배경 투명하게 설정
        titlePanel.add(backButton);
        titlePanel.add(titleLabel);
        
        topPanel.add(titlePanel, BorderLayout.WEST);

        // 로그아웃 및 메뉴
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
                PopupMenuManager.showPopupMenu(menuLabel, SurveyDetailPageAWT.this);
            }
        });

        spaceLabel = new JLabel("  ");
        
        buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 60));
        buttonGroup.setOpaque(false);  // 배경 투명하게 설정
        buttonGroup.add(logoutLabel);
        buttonGroup.add(spaceLabel);
        buttonGroup.add(menuLabel);
        
        topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);  // 배경 투명하게 설정
        topRightPanel.add(buttonGroup);
        
        topPanel.add(topRightPanel, BorderLayout.EAST);
        
        separatorLabel = new JLabel();
        separatorLabel.setOpaque(true);
        separatorLabel.setBackground(Color.LIGHT_GRAY);
        separatorLabel.setPreferredSize(new Dimension(650, 2));
        
        separatorContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        separatorContainer.setOpaque(false);  // 배경 투명하게 설정
        separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
        separatorContainer.add(separatorLabel);
        
        topPanel.add(separatorContainer, BorderLayout.SOUTH);

        // ── 콘텐츠 영역 ─────────────────────────────
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);  // 배경 투명하게 설정

        // headerPanel 생성 (BorderLayout 사용)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);  // 배경 투명하게 설정
        // 여기서 오른쪽 여백만 50px 주기 (왼쪽, 상하 여백은 그대로)
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 70, 20, 62));

        // CENTER에 "상세보기" 라벨 추가
        subtitleLabel = new JLabel("상세보기", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        JButton ellipsisButton = new JButton("...");
        ellipsisButton.setOpaque(true);
        ellipsisButton.setContentAreaFilled(false); // paintComponent에서 직접 배경을 그리므로 false로 설정
        // 롤오버 효과를 비활성화하여 마우스 올렸을 때 색상이 변하지 않도록 함
        ellipsisButton.setRolloverEnabled(false);
        ellipsisButton.setFocusPainted(false);
        ellipsisButton.setOpaque(false);  // 배경 투명하게 설정
        ellipsisButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        // 테두리를 빈 테두리로 설정해서 보이지 않게 함
        ellipsisButton.setBorder(BorderFactory.createEmptyBorder());
        ellipsisButton.setRolloverEnabled(false);
        ellipsisButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ellipsisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	MemberBean currentUser = UserSession.getInstance().getCurrentUser();
                ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();

                // 관리자만 공지 삭제할 수 있도록 제한
                if (currentAdmin == null) {
                    JOptionPane.showMessageDialog(SurveyDetailPageAWT.this, "권한이 없습니다.", "권한 오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    EditDeletePopupMenu2.showPopupMenu(ellipsisButton, SurveyDetailPageAWT.this, pollId);
                }
            }
        });
        
        // headerPanel의 EAST 영역에 래퍼 패널 추가
        headerPanel.add(ellipsisButton, BorderLayout.EAST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // 설문 제목, 시작일, 종료일 DB에서 가져오기
        loadSurveyDetails();

        // 설문 항목들을 담을 컨테이너 패널 생성
        JPanel surveyItemsContainer = new JPanel();
        surveyItemsContainer.setLayout(new BoxLayout(surveyItemsContainer, BoxLayout.Y_AXIS));
        surveyItemsContainer.setBackground(new Color(240, 240, 240));
        surveyItemsContainer.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
        surveyItemsContainer.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        
        // SurveyMgr를 사용하여 DB에서 설문 항목을 불러옵니다.
        SurveyMgr surveyMgr = new SurveyMgr();
        SurveyBean survey = surveyMgr.getSurveyDetail(pollId);  // 설문 ID를 기반으로 설문 상세 정보와 항목을 불러옵니다.

        if (survey != null) {
            // 설문 항목들을 동적으로 불러오기
            for (SurveyBean.Item item : survey.getItems()) {
            	String displayVoteCount = item.getVoteCount() + "명";
                addSurveyItem(surveyItemsContainer, item.getContent(), displayVoteCount, item.getItemListNum());    
            }
        }

        // 설문 항목들을 surveyItemsContainer에 추가
        contentPanel.add(surveyItemsContainer, BorderLayout.CENTER);

        // 참여 체크 버튼
        JPanel recheckPanel = new JPanel();
        recheckPanel.setLayout(new BoxLayout(recheckPanel, BoxLayout.X_AXIS));
        recheckPanel.setBackground(new Color(240, 240, 240));
        recheckPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton recheckButton = new JButton("체크하기");
        // 만료 여부 체크: 종료일(예: 25일)까지는 유효하도록 today.isAfter(endDate) 조건 사용
        if (currentSurvey != null && isExpired(currentSurvey)) {
        	for (SurveyItem item : surveyItems) {
                item.itemPanel.setCursor(Cursor.getDefaultCursor());
                for (MouseListener ml : item.itemPanel.getMouseListeners()) {
                    item.itemPanel.removeMouseListener(ml);
                }
            }
        	
            recheckButton.setEnabled(false);  // 버튼을 비활성화
            recheckButton.setToolTipText("이 설문은 종료되었습니다.");  // 툴팁 추가
        }
        recheckButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        recheckButton.setBackground(new Color(100, 150, 220));
        recheckButton.setForeground(Color.WHITE);
        recheckButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recheckButton.setFocusPainted(false);
        recheckButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        recheckButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        recheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// 현재 로그인한 사용자가 관리자라면 아무 동작도 하지 않음 (이벤트 비활성화)
                ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
                if (currentAdmin != null) {
                    JOptionPane.showMessageDialog(null, "관리자는 투표할 수 없습니다.");
                    return;
                }
            	
                if(selectedSurveyItem == null) {
                	JOptionPane.showMessageDialog(null, "항목을 먼저 선택하세요.");
                    return;
                }
                
             // 2. 현재 로그인한 회원 정보 가져오기
                MemberBean currentUser = UserSession.getInstance().getCurrentUser();
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(SurveyDetailPageAWT.this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    new LoginAWT();
                    return;
                }
             // 3. 사용자가 이미 투표했는지 확인
                boolean hasVoted = surveyMgr.hasUserVoted(currentUser.getMember_Id(), pollId);
                if (hasVoted) {
                    JOptionPane.showMessageDialog(SurveyDetailPageAWT.this, "이미 투표하셨습니다. 한 항목에만 투표할 수 있습니다.");
                    return;
                }
                // 기존 투표 항목을 취소하고 새로운 항목에 투표
                if (votedSurveyItem != null && votedSurveyItem != selectedSurveyItem) {
                    int newVoteCount = Math.max(0, votedSurveyItem.getVoteCount() - 1);
                    votedSurveyItem.setVoteCount(newVoteCount);
                    surveyMgr.updateVoteCount(votedSurveyItem.getItemListNum(), newVoteCount);
                }

                // 6. 투표 기록 저장 (member_id, poll_id, vote_item_id)
                surveyMgr.recordVote(currentUser.getMember_Id(), pollId, selectedSurveyItem.getItemListNum());

                // 7. 현재 투표한 항목 업데이트
                votedSurveyItem = selectedSurveyItem;  // 현재 투표한 항목 저장
                int newVoteCount = selectedSurveyItem.getVoteCount() + 1;  // 새로운 투표 수
                selectedSurveyItem.setVoteCount(newVoteCount); // UI에서 투표 수 갱신
                surveyMgr.updateVoteCount(selectedSurveyItem.getItemListNum(), newVoteCount);

                // 8. UI 업데이트
                updateUIWithSurveyData(currentSurvey); // 설문 데이터를 반영한 UI 업데이트
            }
        });

        recheckPanel.add(recheckButton);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(new Color(240, 240, 240));
        totalLabel = new JLabel("0명 참여");
        totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        totalPanel.add(totalLabel);

        surveyListPanel.add(surveyItemsContainer);
        surveyListPanel.add(recheckPanel);
        surveyListPanel.add(totalPanel);

        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapperPanel.setOpaque(false);  // 배경 투명하게 설정
        surveyListPanel.setPreferredSize(new Dimension(650, surveyListPanel.getPreferredSize().height));
        wrapperPanel.add(surveyListPanel);

        contentPanel.add(wrapperPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);  // 배경 투명하게 설정
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // 스크롤바 디자인 개선
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
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
        
        // JScrollPane을 감싸는 JPanel 생성
        JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
        scrollPaneWrapper.setOpaque(false);  // 배경 투명하게 설정
        scrollPaneWrapper.add(scrollPane, BorderLayout.CENTER);
        scrollPaneWrapper.add(Box.createVerticalStrut(30), BorderLayout.SOUTH); // 25px 아래 여백 추가

        add(topPanel, BorderLayout.NORTH);
        add(scrollPaneWrapper, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setVisible(true);

        updateTotalCount();
    }
    
    // 날짜 비교 메서드: 종료일을 포함(즉, 오늘이 종료일이면 아직 유효)
    private boolean isExpired(SurveyBean survey) {
        LocalDate today = LocalDate.now();
        // 종료일이 "yyyy-MM-dd" 형식이라고 가정
        LocalDate endDate = LocalDate.parse(survey.getEndDate());
        return today.isAfter(endDate);
    }
    
    private void updateDatabaseVoteCount(int itemListNum, int newVoteCount) {
        SurveyMgr surveyMgr = new SurveyMgr();
        surveyMgr.updateVoteCount(itemListNum, newVoteCount); // 데이터베이스 업데이트
    }

    // 설문 상세 정보를 불러오는 메서드에서 null 체크 추가
    private void loadSurveyDetails() {
        SurveyMgr surveyMgr = new SurveyMgr();
        currentSurvey = surveyMgr.getSurveyDetail(pollId); // 설문 상세 정보를 불러와 저장
        
        if (currentSurvey != null) {
            updateUIWithSurveyData(currentSurvey); // UI를 업데이트
        } else {
            // 설문 데이터가 없을 경우 처리
            JOptionPane.showMessageDialog(this, "설문 데이터를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUIWithSurveyData(SurveyBean survey) {
    	if (survey == null) {
            System.out.println("설문 데이터가 없습니다.");
            return; // survey가 null일 때, 더 이상 진행하지 않음
        }
    	
    	String question = survey.getQuestion();
        String startDate = survey.getStartDate();
        String endDate = survey.getEndDate();

        // DB에서 가져온 제목 패널 (세로 정렬이 필요하다면 BoxLayout.Y_AXIS 사용)
        JPanel titlePanelFromDB = new JPanel();
        titlePanelFromDB.setLayout(new BoxLayout(titlePanelFromDB, BoxLayout.Y_AXIS));
        titlePanelFromDB.setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel(question);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        titlePanelFromDB.add(titleLabel);

        // 시작일과 종료일 패널을 수평(BoxLayout.X_AXIS)으로 배치
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
        datePanel.setBackground(new Color(240, 240, 240));
        JLabel startDateLabel = new JLabel("시작일: " + startDate);
        startDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JLabel endDateLabel = new JLabel("종료일: " + endDate);
        endDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        datePanel.add(startDateLabel);
        datePanel.add(Box.createRigidArea(new Dimension(10, 0))); // 수평 간격 10px
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        datePanel.add(endDateLabel);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);  // 현재 날짜에 7일을 더함
        Date defaultDate = cal.getTime();  // 설정된 날짜를 defaultDate에 할당
        SpinnerDateModel dateModel = new SpinnerDateModel(defaultDate, null, null, Calendar.DAY_OF_MONTH); // 7일 뒤 날짜를 모델에 반영
        JSpinner dateSpinner = new JSpinner(dateModel);

        // 부모 패널을 BorderLayout으로 생성
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.setBackground(new Color(240, 240, 240));
        // 왼쪽에서 30px, 오른쪽에서 30px 여백을 고정
        combinedPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 8, 20));

        combinedPanel.add(titlePanelFromDB, BorderLayout.WEST);
        combinedPanel.add(datePanel, BorderLayout.EAST);
        
        // surveyListPanel에 combinedPanel을 추가하거나 원하는 위치에 배치
        surveyListPanel.add(combinedPanel);
        
        
        
        // 만료 여부 확인: 종료일이 지나면 알림 (오늘이 종료일이면 아직 유효)
        if (survey != null && isExpired(survey)) {
            JOptionPane.showMessageDialog(this, "이 설문은 종료되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void addSurveyItem(JPanel container, String date, String count, int itemListNum) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        itemPanel.add(dateLabel, BorderLayout.WEST);
        itemPanel.add(countLabel, BorderLayout.EAST);

        int countValue = 0;
        try {
            countValue = Integer.parseInt(count.replace("명", "").trim());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        SurveyItem surveyItem = new SurveyItem(date, countValue, countLabel, itemPanel, itemListNum);

        // 현재 관리자인 경우, 설문 항목의 마우스 이벤트를 비활성화합니다.
        ManagerBean currentAdmin = UserSession.getInstance().getCurrentAdmin();
        if (currentAdmin == null) {
        	itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            itemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedSurveyItem != null) {
                        selectedSurveyItem.itemPanel.setBackground(Color.WHITE);
                    }
                    selectedSurveyItem = surveyItem;
                    surveyItem.itemPanel.setBackground(new Color(200, 220, 255));
                    System.out.println("선택됨: " + surveyItem.date);
                }
            });
        } else {
            // 관리자인 경우 클릭 이벤트 없이 기본 커서만 설정
            itemPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        container.add(itemPanel);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        surveyItems.add(surveyItem);
    }

    private class SurveyItem {
    	String date;
        int count; // 이제 이 필드를 'voteCount'로 사용합니다.
        JLabel countLabel;
        JPanel itemPanel;
        boolean checked;
        int itemListNum; // itemListNum 필드 추가

        // 생성자
        public SurveyItem(String date, int count, JLabel countLabel, JPanel itemPanel, int itemListNum) {
            this.date = date;
            this.count = count;
            this.countLabel = countLabel;
            this.itemPanel = itemPanel;
            this.itemListNum = itemListNum;
            this.checked = false;
        }

        // 필요한 게터 및 세터 추가
        public int getVoteCount() {
            return count;
        }

        public void setVoteCount(int count) {
            this.count = count;
            this.countLabel.setText(count + "명");
        }

        public int getItemListNum() {
            return itemListNum;
        }
    }

    private void updateTotalCount() {
        int total = 0;
        for (SurveyItem item : surveyItems) {
            total += item.count;
        }
        totalLabel.setText(total + "명 참여");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
            new SurveyPageAWT();
        }
    }

    public static void main(String[] args) {
        new SurveyDetailPageAWT(1); // 설문 ID 1 예시
    }
}

