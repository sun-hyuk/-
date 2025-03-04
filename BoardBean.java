package Project;

public class BoardBean {
	private int board_id;
	private String board_title;
	private String board_content;
	private String created_date;
	private String manager_id;
	private String member_id;

	// 생성자: int id, String title, String createdDate을 받는 생성자
	public BoardBean(int board_id, String board_title, String created_date) {
		this.board_id = board_id;
		this.board_title = board_title;
		this.created_date = created_date;
	}

	public int getBoard_Id() {
		return board_id;
	}

	public void setBoard_Id(int board_id) {
		this.board_id = board_id;
	}

	public String getBoard_Title() {
		return board_title;
	}

	public void setBoard_Title(String board_title) {
		this.board_title = board_title;
	}

	public String getBoard_Content() {
		return board_content;
	}

	public void setBoard_Content(String board_content) {
		this.board_content = board_content;
	}

	public String getCreated_Date() {
		return created_date;
	}

	public void setCreated_Date(String created_date) {
		this.created_date = created_date;
	}
	
	public String getManager_Id() {
		return manager_id;
	}

	public void setManager_Id(String manager_id) {
		this.manager_id = manager_id;
	}
	
	public String getMember_Id() {
		return member_id;
	}

	public void setMember_Id(String member_id) {
		this.member_id = member_id;
	}
}
