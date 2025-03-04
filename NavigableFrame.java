package Project;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class NavigableFrame extends JFrame {
	protected JFrame previousFrame;

	public NavigableFrame(JFrame previousFrame) {
		this.previousFrame = previousFrame;
		initBackButton();
	}

	private void initBackButton() {
		JButton backButton = new JButton("<");
		if (previousFrame != null) {
			backButton.addActionListener(e -> {
				previousFrame.setVisible(true);
				this.dispose();
			});
		} else {
			// 이전 프레임이 없는 경우 버튼 비활성화
			backButton.setEnabled(false);
		}
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.add(backButton);
		this.add(headerPanel, BorderLayout.NORTH);
	}
}