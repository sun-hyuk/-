package Project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class PopupMenuManager {

	public static void showPopupMenu(Component invoker, JFrame frame) {
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setOpaque(true);
		popupMenu.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		popupMenu.setBackground(new Color(255, 255, 255, 200));

		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBackground(Color.WHITE);

		// Row 1: 바로가기 X (닫기 기능)
		JPanel row1 = new JPanel(new BorderLayout());
		row1.setBackground(Color.WHITE);
		JLabel shortcutLabel = new JLabel("바로가기");
		shortcutLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
		JLabel closeLabel = new JLabel("X");
		closeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				popupMenu.setVisible(false);
			}
		});
		row1.add(shortcutLabel, BorderLayout.WEST);
		row1.add(closeLabel, BorderLayout.EAST);
		row1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		addHoverEffect(row1, true); // hover 효과 적용
		menuPanel.add(row1);

		// Row 3: 홈 > (클릭 시 MainPageAWT 창으로 이동)
		menuPanel.add(createMenuRow("홈            ", ">", () -> {
			new MainPageAWT();
			frame.dispose();
		}));

		// Row 3: 공지사항 > (클릭 시 NoticePageAWT 창으로 이동)
		menuPanel.add(createMenuRow("공지사항            ", ">", () -> {
			Point currentLocation = frame.getLocation();
			new NoticePageAWT(frame, currentLocation);
			frame.setVisible(false); // 현재 프레임을 단순히 숨기고
		}));

		// Row 4: 마이페이지 >
		menuPanel.add(createMenuRow("마이페이지            ", ">", () -> {
			Point currentLocation = frame.getLocation();
			new MyPageAWT(frame, currentLocation);
			frame.setVisible(false); // 현재 프레임을 단순히 숨기고
		}));

		// Row 5: 자유게시판 > (클릭 시 BoardPageAWT 창으로 이동)
		menuPanel.add(createMenuRow("자유게시판            ", ">", () -> {
			new BoardPageAWT(frame);
			frame.setVisible(false); // 현재 프레임을 단순히 숨기고
		}));

		popupMenu.add(menuPanel);

		// 🔹 팝업 메뉴 크기 가져오기
		popupMenu.pack();
		int popupWidth = popupMenu.getPreferredSize().width;
		int popupHeight = popupMenu.getPreferredSize().height;

		// 🔹 삼단바(≡) 버튼의 화면상 좌표 가져오기
		Point invokerLocation = invoker.getLocationOnScreen();

		// 🔹 우측 정렬하여 팝업 표시 (버튼의 우측 상단 기준)
		popupMenu.show(invoker, invoker.getWidth() - popupWidth, invoker.getHeight() - 30);

	}

	// ── 헬퍼 메서드: 메뉴 행 생성 ─────────────────────────────
	private static JPanel createMenuRow(String leftText, String rightText, Runnable onClick) {
		JPanel row = new JPanel(new BorderLayout());
		row.setBackground(Color.WHITE);
		JLabel leftLabel = new JLabel(leftText);
		leftLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		JLabel rightLabel = new JLabel(rightText);
		rightLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		rightLabel.setForeground(Color.GRAY);
		row.add(leftLabel, BorderLayout.WEST);
		row.add(rightLabel, BorderLayout.EAST);
		row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		addHoverEffect(row, false);
		row.setCursor(new Cursor(Cursor.HAND_CURSOR));
		row.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (onClick != null) {
					onClick.run();
				}
			}
		});
		return row;
	}

	// ── 헬퍼 메서드: 마우스 오버 시 배경색 변경 (hover 효과) ─────────────────────────────
	private static void addHoverEffect(JPanel panel, boolean excludeHover) {
		if (!excludeHover) {
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					panel.setBackground(new Color(245, 245, 245));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					panel.setBackground(Color.WHITE);
				}
			});
		}
	}
}
