package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditDeletePopupMenu extends JPopupMenu {
	/**
	 * 생성자
	 * 
	 * @param editLabelText   수정 버튼에 표시할 텍스트
	 * @param editListener    수정 액션 리스너
	 * @param deleteLabelText 삭제 버튼에 표시할 텍스트
	 * @param deleteListener  삭제 액션 리스너
	 */
	public EditDeletePopupMenu(String editLabelText, ActionListener editListener, String deleteLabelText,
			ActionListener deleteListener) {
		setOpaque(true);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setBackground(Color.WHITE);

		JPanel editPanel = createPanel(editLabelText, editListener);
		JPanel separatorPanel = new JPanel(new BorderLayout());
		separatorPanel.setBackground(Color.WHITE);
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(65, 1));
		separator.setForeground(Color.LIGHT_GRAY); // 여기서 구분선 색상을 LIGHT_GRAY로 지정
		separator.setBackground(Color.LIGHT_GRAY);
		separatorPanel.add(separator, BorderLayout.CENTER);
		JPanel deletePanel = createPanel(deleteLabelText, deleteListener);

		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBackground(Color.WHITE);
		menuPanel.add(editPanel);
		menuPanel.add(separatorPanel);
		menuPanel.add(deletePanel);

		add(menuPanel);

		// 마우스가 팝업 메뉴 위에서 떼어질 때 팝업이 닫히도록 처리
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				setVisible(false);
			}
		});
	}

	// 공통으로 사용할 패널 생성 메소드
	private JPanel createPanel(String labelText, ActionListener listener) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("SansSerif", Font.PLAIN, 14));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
				setVisible(false);
			}
		});
		panel.add(label);
		addHoverEffect(panel, label);
		return panel;
	}

	// 마우스 오버 효과
	private void addHoverEffect(JPanel panel, JLabel label) {
		MouseAdapter hoverEffect = new MouseAdapter() {
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
		panel.addMouseListener(hoverEffect);
		label.addMouseListener(hoverEffect);
	}
}
