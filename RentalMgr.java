package Project;

import java.sql.*;
import java.util.Vector;

public class RentalMgr {
	private DBConnectionMgr pool;

	public RentalMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// game_rental 테이블과 member 테이블을 조인하여 대여 내역과 회원의 학번, 이름, 연락처, 패널티, 이름을 가져옴
	public Vector<RentalBean> listRental() {
		Vector<RentalBean> rentalList = new Vector<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// r.game_id에는 보드게임 제목이 저장되어 있다고 가정
		// member 테이블의 m.student_Id, m.name과 m.phone_number를 가져옵니다.
		String sql = "SELECT r.rental_id, r.rental_date, r.return_date, r.rental_status, "
				+ "       r.member_id, r.game_id, m.student_id AS student_id, m.name AS member_name, "
				+ "       m.phone_number AS member_phone, m.penalty AS penalty_count " + "FROM game_rental r "
				+ "JOIN member m ON r.member_id = m.member_id " + "ORDER BY r.rental_date DESC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				RentalBean bean = new RentalBean();
				bean.setRentalId(rs.getInt("rental_id"));
				bean.setRentalDate(rs.getString("rental_date"));
				bean.setReturnDate(rs.getString("return_date")); // null 가능
				bean.setRentalStatus(rs.getString("rental_status"));
				bean.setMemberId(rs.getString("member_id")); // game_rental 테이블의 member_id
				bean.setGameId(rs.getString("game_id")); // 보드게임 제목
				// 여기서 별칭을 소문자로 사용합니다.
				bean.setStuId(rs.getString("student_id")); // member 테이블의 student_id (학번)
				bean.setMemberName(rs.getString("member_name"));
				bean.setMemberPhone(rs.getString("member_phone"));
				bean.setPenaltyCount(rs.getInt("penalty_count"));
				rentalList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return rentalList;
	}

	public boolean insertRental(String memberId, String gameId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean flag = false;

		// 먼저, 해당 회원 또는 게임이 이미 대여 중인지 확인
		String checkSql = "SELECT * FROM game_rental WHERE (member_id = ? AND rental_status = '대여중') OR (game_id = ? AND rental_status = '대여중')";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(checkSql);
			pstmt.setString(1, memberId);
			pstmt.setString(2, gameId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// 이미 대여 중인 회원이거나 게임이면 대여 추가를 하지 않음
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException ignored) {
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException ignored) {
				}
		}

		// 대여 가능하면 대여 정보를 데이터베이스에 추가
		String sql = "INSERT INTO game_rental (rental_date, return_date, rental_status, member_id, game_id) "
				+ "VALUES (CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), '대여중', ?, ?)";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberId);
			pstmt.setString(2, gameId);
			if (pstmt.executeUpdate() == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException ignored) {
				}
			pool.freeConnection(con);
		}
		return flag;
	}

	// 삭제 메소드 (rental_id 기준)
	public boolean deleteRental(int rentalId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM game_rental WHERE rental_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, rentalId);
			if (pstmt.executeUpdate() == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// member 테이블의 penalty를 업데이트 (member_id 기준)
	public boolean updatePenalty(String memberId, int newPenalty) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE member SET penalty = ? WHERE member_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, newPenalty);
			pstmt.setString(2, memberId);
			if (pstmt.executeUpdate() == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

}
