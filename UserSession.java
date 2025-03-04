package Project;

import Project.ManagerBean;
import Project.MemberBean;
import Project.UserSession;

public class UserSession {
	private static UserSession instance = new UserSession();
	private MemberBean currentUser;
	private ManagerBean currentAdmin;

	private UserSession() {
	}

	public static UserSession getInstance() {
		return instance;
	}

	public void setCurrentUser(MemberBean user) {
		this.currentUser = user;
	}

	public MemberBean getCurrentUser() {
		return currentUser;
	}

	public void setCurrentAdmin(ManagerBean admin) {
		this.currentAdmin = admin;
	}

	public ManagerBean getCurrentAdmin() {
		return currentAdmin;
	}
}
