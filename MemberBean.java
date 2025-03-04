package Project;

public class MemberBean {

	// 테이블 스키마
	private String member_id;
	private String password;
	private String name;
	private String student_id;
	private String gender;
	private String department;
	private String phone_number;
	private String member_role;
	private String admission_fee;
	private int penalty;
	private String member_profile;

	public String getMember_Id() {
		return member_id;
	}

	public void setMember_Id(String member_id) {
		this.member_id = member_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStudent_ID() {
		return student_id;
	}

	public void setStudent_ID(String student_id) {
		this.student_id = student_id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPhone_Number() {
		return phone_number;
	}

	public void setPhone_Number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getMember_Role() {
		return member_role;
	}

	public void setMember_Role(String member_role) {
		this.member_role = member_role;
	}

	public String getAdmission_Fee() {
		return admission_fee;
	}

	public void setAdmission_Fee(String admission_fee) {
		this.admission_fee = admission_fee;
	}

	public int getPenalty() {
		return penalty;
	}

	public void setPenalty(int penalty) {
		this.penalty = penalty;
	}

	public String getMember_Profile() {
		return member_profile;
	}

	public void setMember_Profile(String member_profile) {
		this.member_profile = member_profile;
	}

	public static void main(String[] args) {

	}

}
