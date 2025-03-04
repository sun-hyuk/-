package Project;

import java.sql.*;
import java.util.ArrayList;

public class NotificationMgr {
	private DBConnectionMgr pool;

	public NotificationMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
			if (pool == null) {
				System.out.println("⚠️ DBConnectionMgr");
			} else {
				System.out.println("✅ NotificationMgr");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 관리자 전용 또는 전체 알림을 가져오는 메소드 (필요시)
	public ArrayList<NotificationBean> getAllNotifications() {
		ArrayList<NotificationBean> notifications = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// notice 테이블에서 전체 알림을 가져옴 (is_read 정보는 회원별 관리가 아니므로 기본값 false로 처리)
		String query = "SELECT notice_id, notice_title, created_at FROM notice ORDER BY created_at DESC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int noticeId = rs.getInt("notice_id");
				String title = rs.getString("notice_title");
				String createdAt = rs.getString("created_at");
				notifications.add(new NotificationBean(noticeId, title, createdAt, false));
			}
		} catch (SQLException e) {
			System.out.println("❌ SQL 오류 (getAllNotifications): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("❌ 오류 (getAllNotifications): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				pool.freeConnection(con, pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return notifications;
	}

	// 특정 공지를 모든 사용자(또는 대상 사용자 목록)에게 알림으로 삽입
	public void createNotificationForUsers(int noticeId, ArrayList<String> memberIds) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO user_notifications (notice_id, member_id, is_read) VALUES (?, ?, 0)";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			for (String memberId : memberIds) {
				pstmt.setInt(1, noticeId);
				pstmt.setString(2, memberId);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (SQLException e) {
			System.out.println("❌ SQL 오류 (createNotificationForUsers): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("❌ 오류 (createNotificationForUsers): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				pool.freeConnection(con, pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 로그인한 회원의 알림만 가져오는 메소드 (user_notifications 테이블 활용)
	public ArrayList<NotificationBean> getNotificationsForMember(String memberId) {
		ArrayList<NotificationBean> notifications = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// user_notifications 테이블과 notice 테이블을 조인하여 해당 회원의 알림을 가져옵니다.
		String query = "SELECT n.notice_id, n.notice_title, n.created_at, u.is_read " + "FROM notice n "
				+ "JOIN user_notifications u ON n.notice_id = u.notice_id " + "WHERE u.member_id = ? "
				+ "ORDER BY n.created_at DESC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, memberId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int noticeId = rs.getInt("notice_id");
				String title = rs.getString("notice_title");
				String createdAt = rs.getString("created_at");
				boolean isRead = rs.getBoolean("is_read");
				notifications.add(new NotificationBean(noticeId, title, createdAt, isRead));
			}
		} catch (SQLException e) {
			System.out.println("❌ SQL 오류 (getNotificationsForMember): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("❌ 오류 (getNotificationsForMember): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				pool.freeConnection(con, pstmt, rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return notifications;
	}

	// 특정 회원의 알림을 읽음 처리 (user_notifications 테이블 업데이트)
	public void markNotificationAsRead(String memberId, int noticeId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE user_notifications SET is_read = 1 WHERE notice_id = ? AND member_id = ?";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, noticeId);
			pstmt.setString(2, memberId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("❌ SQL 오류 (markNotificationAsRead): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("❌ 오류 (markNotificationAsRead): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				pool.freeConnection(con, pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 회원이 알림을 삭제할 때 user_notifications 테이블에서 해당 알림 삭제
	public boolean deleteNotificationForMember(String memberId, int noticeId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM user_notifications WHERE notice_id = ? AND member_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, noticeId);
			pstmt.setString(2, memberId);
			int cnt = pstmt.executeUpdate();
			if (cnt == 1) {
				flag = true;
			}
		} catch (SQLException e) {
			System.out.println("❌ SQL 오류 (deleteNotificationForMember): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("❌ 오류 (deleteNotificationForMember): " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				pool.freeConnection(con, pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
}
