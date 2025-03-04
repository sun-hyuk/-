package Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NoticeMgr {
	private DBConnectionMgr pool;
	private ArrayList<NoticeBean> notices; // Notice 객체 리스트 추가

	public NoticeMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
			notices = new ArrayList<>(); // notices 리스트 초기화
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 공지사항 저장
	public int insertNotice(String title, String content, String managerId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet generatedKeys = null;
		int noticeId = -1; // 실패 시 -1
		String sql = "INSERT INTO notice (notice_title, notice_content, manager_id) VALUES (?, ?, ?)";
		try {
			con = pool.getConnection();
			// 생성된 키를 반환하도록 설정
			pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setString(3, managerId);
			int cnt = pstmt.executeUpdate();
			if (cnt == 1) {
				generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					noticeId = generatedKeys.getInt(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, generatedKeys);
		}
		return noticeId;
	}

	// 공지 생성 후, 전체 사용자에게 알림 생성
	public void createNoticeAndNotifications(String title, String content, String managerId,
			ArrayList<String> memberIds) {
		int noticeId = insertNotice(title, content, managerId);
		if (noticeId != -1) {
			// 공지 생성 성공 시, 대상 사용자에게 알림 삽입
			NotificationMgr nMgr = new NotificationMgr();
			nMgr.createNotificationForUsers(noticeId, memberIds);
		} else {
			System.out.println("공지 생성 실패");
		}
	}

	// DB에서 공지사항을 불러오는 메소드
	public void loadNoticesFromDB() {
		notices.clear(); // 기존 리스트 초기화
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT * FROM notice ORDER BY created_at DESC"; // 최신 공지사항부터 가져오기

		try {
			con = pool.getConnection(); // DB 연결 가져오기
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			// 데이터베이스에서 공지사항 정보 가져오기
			while (rs.next()) {
				int id = rs.getInt("notice_id"); // DB에서 ID 가져오기
				String title = rs.getString("notice_title"); // DB에서 제목 가져오기
				String createdAt = rs.getString("created_at"); // DB에서 작성일 가져오기
				String managerId = rs.getString("manager_id");
				// Notice 객체 생성 후 리스트에 추가
				NoticeBean notice = new NoticeBean(id, title, createdAt, managerId);
				notices.add(notice);
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
		} catch (Exception ex) {
			ex.printStackTrace(); // 일반적인 예외 처리
		} finally {
			pool.freeConnection(con, pstmt, rs); // 연결 자원 해제
		}
	}

	// NoticeMgr 클래스 내부
	public String getNoticeContent(int noticeId) {
		String content = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT notice_content FROM notice WHERE notice_id = ?";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, noticeId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				content = rs.getString("notice_content");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return content;
	}

	public int getTotalPages() {
		int totalNotices = 0;
		int noticesPerPage = 5; // 한 페이지당 공지 개수

		// DB에서 공지사항 총 개수를 가져오는 쿼리
		try {
			Connection conn = pool.getConnection(); // DB 연결 가져오기
			String query = "SELECT COUNT(*) FROM notice"; // 공지사항의 총 개수 조회
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				totalNotices = rs.getInt(1); // 총 개수 값을 가져옴
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); // SQL 예외 처리
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 총 공지사항 개수를 noticesPerPage로 나누어 페이지 수 계산
		return (int) Math.ceil((double) totalNotices / noticesPerPage);
	}

	// 공지사항 id로 단일 공지 조회
	public NoticeBean getNoticeById(int noticeId) {
		NoticeBean notice = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT notice_id, notice_title, created_at, manager_id FROM notice WHERE notice_id = ?";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, noticeId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("notice_id");
				String title = rs.getString("notice_title");
				String createdAt = rs.getString("created_at");
				String managerId = rs.getString("manager_id");
				notice = new NoticeBean(id, title, createdAt, managerId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return notice;
	}

	// 공지사항 수정
	public boolean updateNotice(int noticeId, String title, String content) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE notice SET notice_title = ?, notice_content = ? WHERE notice_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setInt(3, noticeId);
			int cnt = pstmt.executeUpdate();
			if (cnt == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// 공지사항 삭제
	public boolean deleteNotice(int noticeId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM notice WHERE notice_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, noticeId);
			int cnt = pstmt.executeUpdate();
			if (cnt == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// 공지사항 제목으로 검색
	public ArrayList<NoticeBean> searchNotices(String keyword) {
		ArrayList<NoticeBean> result = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT notice_id, notice_title, created_at, manager_id FROM notice "
				+ "WHERE notice_title LIKE ? ORDER BY created_at DESC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, "%" + keyword + "%"); // 검색어 포함
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("notice_id");
				String title = rs.getString("notice_title");
				String createdAt = rs.getString("created_at");
				String managerId = rs.getString("manager_id");
				NoticeBean notice = new NoticeBean(id, title, createdAt, managerId);
				result.add(notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}

	public ArrayList<NoticeBean> getLatestNotices(int count) {
		ArrayList<NoticeBean> latestNotices = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT notice_id, notice_title, created_at, manager_id FROM notice ORDER BY created_at DESC LIMIT ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, count); // 파라미터로 받은 개수만큼 조회
			rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("notice_id");
				String title = rs.getString("notice_title");
				String createdAt = rs.getString("created_at");
				String managerId = rs.getString("manager_id");

				NoticeBean notice = new NoticeBean(id, title, createdAt, managerId);
				latestNotices.add(notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}

		return latestNotices;
	}

	// 공지사항 리스트를 반환하는 메소드
	public ArrayList<NoticeBean> getNotices() {
		return notices;
	}
}
