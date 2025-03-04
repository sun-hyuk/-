package Project;

public class ManagerBean {

	private String manager_id;
	private String manager_password;
	private String manager_name;
	private String manager_profile; // 프로필 이미지 경로를 저장할 필드

	public String getManager_Id() {
		return manager_id;
	}

	public void setManager_Id(String manager_id) {
		this.manager_id = manager_id;
	}

	public String getManager_Password() {
		return manager_password;
	}

	public void setManager_Password(String manager_password) {
		this.manager_password = manager_password;
	}

	public String getManager_Name() {
		return manager_name;
	}

	public void setManager_Name(String manager_name) {
		this.manager_name = manager_name;
	}

	public String getManager_Profile() {
		return manager_profile;
	}

	public void setManager_Profile(String manager_profile) {
		this.manager_profile = manager_profile;
	}

	public static void main(String[] args) {

	}

}
