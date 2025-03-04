package Project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditDeletePopupMenu2 {

	public static void showPopupMenu(Component invoker, JFrame frame, int pollId) {
		JPopupMenu popup = new JPopupMenu();
		popup.setOpaque(true);
		popup.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		popup.setBackground(new Color(255, 255, 255, 200));

		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBackground(Color.WHITE);

		// "삭제하기" 메뉴 항목 생성
		JPanel row = new JPanel(new BorderLayout());
		row.setBackground(Color.WHITE);
		JLabel deleteLabel = new JLabel("삭제하기");
		deleteLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
		row.add(deleteLabel, BorderLayout.WEST);
		row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		addHoverEffect(row, deleteLabel);
		row.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 삭제 클릭 이벤트를 위한 통합 MouseAdapter 생성
		MouseAdapter deleteClickListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int response = JOptionPane.showConfirmDialog(frame, "삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					// SurveyMgr의 deleteSurvey 메서드를 호출하여 설문 삭제
					SurveyMgr surveyMgr = new SurveyMgr();
					boolean success = surveyMgr.deleteSurvey(pollId);
					if (success) {
						JOptionPane.showMessageDialog(frame, "삭제되었습니다.");
						// 삭제 후 화면 갱신: 현재 창 닫고 설문 목록 화면으로 이동
						frame.dispose();
						new SurveyPageAWT();
					} else {
						JOptionPane.showMessageDialog(frame, "삭제에 실패했습니다.");
					}
				}
				popup.setVisible(false);
			}
		};

		// row와 deleteLabel 모두에 클릭 리스너 등록
		row.addMouseListener(deleteClickListener);
		deleteLabel.addMouseListener(deleteClickListener);

		menuPanel.add(row);
		popup.add(menuPanel);

		popup.pack();
		int popupWidth = popup.getPreferredSize().width;
		popup.show(invoker, invoker.getWidth() - popupWidth, invoker.getHeight());
	}

	// 마우스 오버 효과: 마우스가 올라가면 배경색 변경
	private static void addHoverEffect(JPanel panel, JLabel label) {
		MouseAdapter hover = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				panel.setBackground(new Color(245, 245, 245));
				label.setBackground(new Color(245, 245, 245));
				panel.setOpaque(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				panel.setBackground(Color.WHITE);
				label.setBackground(Color.WHITE);
				panel.setOpaque(true);
			}
		};
		panel.addMouseListener(hover);
		label.addMouseListener(hover);
	}
}