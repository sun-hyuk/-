package Project;

public class NotificationBean {
	private int noticeId;
	private String title;
	private String createdAt;
	private boolean isRead; // 읽지 않은 알림 여부

	public NotificationBean(int noticeId, String title, String createdAt, boolean isRead) {
		this.noticeId = noticeId;
		this.title = title;
		this.createdAt = createdAt;
		this.isRead = isRead;
	}

	public int getNoticeId() {
		return noticeId;
	}

	public String getTitle() {
		return title;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
}
