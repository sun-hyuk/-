package Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;

public class ManagerMgr {

	private DBConnectionMgr pool;

	public ManagerMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean authenticateManager(ManagerBean bean) {
		String sql = "SELECT * FROM manager WHERE manager_id = ? AND manager_password = ?";

		try (Connection con = pool.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, bean.getManager_Id());
			pstmt.setString(2, bean.getManager_Password());

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) { // 인증 성공 시
					// DB 컬럼 이름과 MemberBean의 필드에 맞게 값을 설정
					bean.setManager_Id(rs.getString("manager_id"));
					bean.setManager_Password(rs.getString("manager_password"));
					bean.setManager_Name(rs.getString("manager_name"));

					return true;
				} else {
					return false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getManagerId(String managerId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT manager_id FROM manager WHERE manager_id = ?";
		String storedManagerId = "";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, managerId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				storedManagerId = rs.getString("manager_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return storedManagerId;
	}

	// 관리자 정보 조회 메소드 추가
	public ManagerBean getManagerById(String managerId) {
		ManagerBean manager = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM manager WHERE manager_id = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, managerId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				String id = rs.getString("manager_id");
				String name = rs.getString("manager_name");
				String managerProfile = rs.getString("manager_profile"); // 프로필 이미지 경로가 저장된 컬럼 예시

				manager = new ManagerBean();
				manager.setManager_Id(id);
				manager.setManager_Name(name);
				manager.setManager_Profile(managerProfile); // 프로필 이미지 경로 설정
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return manager;
	}

	// 관리자 프로필 이미지를 DB에 업데이트하는 메소드
	public boolean updateManagerProfile(String managerId, byte[] imageBytes) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE manager SET manager_profile=? WHERE manager_id=?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			if (imageBytes != null) {
				pstmt.setBytes(1, imageBytes);
			} else {
				pstmt.setNull(1, java.sql.Types.BLOB);
			}
			pstmt.setString(2, managerId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// DB에서 관리자 프로필 이미지를 조회하는 메소드
	public ImageIcon getManagerProfile(String managerId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT manager_profile FROM manager WHERE manager_id=?";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, managerId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				byte[] imageBytes = rs.getBytes("manager_profile");
				if (imageBytes != null) {
					return new ImageIcon(imageBytes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return null;
	}

	public boolean updateManagerInfo(String managerId, String newPassword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE manager SET manager_password=? WHERE manager_id=?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			// 먼저, 해당 관리자 레코드가 존재하는지 확인
			pstmt = con.prepareStatement("SELECT manager_password FROM manager WHERE manager_id=?");
			pstmt.setString(1, managerId);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				return false;
			}
			String currentPassword = rs.getString("manager_password");
			rs.close();
			pstmt.close();

			// 업데이트 쿼리 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newPassword);
			pstmt.setString(2, managerId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 1) {
				flag = true;
			} else if (rowsAffected == 0) {
				// 만약 업데이트된 행이 0이라면, 값이 이미 같은 경우일 수 있음.
				// 실제 DB의 값이 newPassword와 동일한지 재확인
				pstmt.close();
				pstmt = con.prepareStatement("SELECT manager_password FROM manager WHERE manager_id=?");
				pstmt.setString(1, managerId);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					String updatedPassword = rs.getString("manager_password");
					if (newPassword.equals(updatedPassword)) {
						flag = true;
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}
}
