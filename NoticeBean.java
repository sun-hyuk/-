package Project;

public class NoticeBean {
	private int notice_id;
	private String notice_title;
	private String created_at;
	private String managerId;

	// 생성자: int id, String title, String createdAt을 받는 생성자
	public NoticeBean(int notice_id, String notice_title, String created_at, String mangerId) {
		this.notice_id = notice_id;
		this.notice_title = notice_title;
		this.created_at = created_at;
		this.managerId = mangerId;
	}

	public int getNotice_Id() {
		return notice_id;
	}

	public void setNotice_Id(int notice_id) {
		this.notice_id = notice_id;
	}

	public String getNotice_Title() {
		return notice_title;
	}

	public void setNotice_Title(String notice_title) {
		this.notice_title = notice_title;
	}

	public String getCreated_At() {
		return created_at;
	}

	public void setCreated_At(String created_at) {
		this.created_at = created_at;
	}

	public String getManagerId() {
		return managerId; // 관리자 ID 반환
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId; // 관리자 ID 설정
	}
}
