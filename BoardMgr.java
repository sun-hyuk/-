package Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class BoardMgr {
	private DBConnectionMgr pool;
	private ArrayList<BoardBean> boards; // board 객체 리스트 추가

	public BoardMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
			boards = new ArrayList<>(); // boards 리스트 초기화
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 게시글 저장 (작성자 정보 포함)
	// 만약 관리자로 작성하면 managerId에 값이 있고, 일반 회원이면 memberId에 값이 있음
	public boolean insertBoard(String title, String content, String memberId, String managerId) {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    // board 테이블은 board_title, board_content, member_id, manager_id 컬럼을 가지고 있음
	    String sql = "INSERT INTO board (board_title, board_content, member_id, manager_id) VALUES (?, ?, ?, ?)";
	    boolean flag = false;
	    try {
	        con = pool.getConnection();
	        pstmt = con.prepareStatement(sql);
	        pstmt.setString(1, title);
	        pstmt.setString(2, content);
	        pstmt.setString(3, memberId);   // 일반 회원인 경우 member_id
	        pstmt.setString(4, managerId);  // 관리자인 경우 manager_id
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


	// DB에서 게시글을 불러오는 메소드
	public void loadBoardsFromDB() {
		boards.clear(); // 기존 리스트 초기화
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT board_id, board_title, board_content, created_date FROM board ORDER BY created_date DESC"; // 최신
																															// 게시글부터
																															// 가져오기

		try {
			con = pool.getConnection(); // DB 연결 가져오기
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();

			// 데이터베이스에서 게시글 정보 가져오기
			while (rs.next()) {
				int id = rs.getInt("board_id");
				String title = rs.getString("board_title");
				String content = rs.getString("board_content");
				// created_date를 문자열 그대로 읽어옴 (예: "2025-02-19 14:23:45")
				String createdDate = rs.getString("created_date");

				BoardBean board = new BoardBean(id, title, createdDate);
				board.setBoard_Content(content);
				boards.add(board);
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
		} catch (Exception ex) {
			ex.printStackTrace(); // 일반적인 예외 처리
		} finally {
			pool.freeConnection(con, pstmt, rs); // 연결 자원 해제
		}
	}

	// BoardMgr 클래스 내부
	public String getBoardContent(int boardId) {
		String content = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT board_content FROM board WHERE board_id = ?";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, boardId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				content = rs.getString("board_content");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return content;
	}

	public int getTotalPages() {
		int totalBoards = 0;
		int boardsPerPage = 5; // 한 페이지당 게시글 개수

		// DB에서 게시글 총 개수를 가져오는 쿼리
		try {
			Connection conn = pool.getConnection(); // DB 연결 가져오기 (pool에서 가져오도록 수정)
			String query = "SELECT COUNT(*) FROM board"; // 게시글의 총 개수 조회
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				totalBoards = rs.getInt(1); // 총 개수 값을 가져옴
			}
		} catch (SQLException ex) {
			ex.printStackTrace(); // SQL 예외 처리
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 총 공지사항 개수를 boardsPerPage로 나누어 페이지 수 계산
		return (int) Math.ceil((double) totalBoards / boardsPerPage);
	}

	// 게시글 id로 단일 공지 조회
	public BoardBean getBoardById(int boardId) {
	    BoardBean board = null;
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String query = "SELECT board_id, board_title, board_content, created_date, member_id, manager_id " +
	                   "FROM board WHERE board_id = ?";
	    try {
	        con = pool.getConnection();
	        pstmt = con.prepareStatement(query);
	        pstmt.setInt(1, boardId);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            int id = rs.getInt("board_id");
	            String title = rs.getString("board_title");
	            String content = rs.getString("board_content");
	            String createdDate = rs.getString("created_date");
	            String memberId = rs.getString("member_id");     // 추가
	            String managerId = rs.getString("manager_id");   // 추가

	            board = new BoardBean(id, title, createdDate);
	            board.setBoard_Content(content);
	            board.setMember_Id(memberId);     // BoardBean에 해당 setter가 있어야 합니다.
	            board.setManager_Id(managerId);   // BoardBean에 해당 setter가 있어야 합니다.
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        pool.freeConnection(con, pstmt, rs);
	    }
	    return board;
	}


	// 게시글 수정
	public boolean updateBoard(int boardId, String title, String content) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE board SET board_title = ?, board_content = ? WHERE board_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			pstmt.setInt(3, boardId);
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

	// 게시글 삭제
	public boolean deleteBoard(int boardId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM board WHERE board_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, boardId);
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

	public ArrayList<BoardBean> searchBoards(String keyword) {
		ArrayList<BoardBean> result = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT board_id, board_title, board_content, created_date FROM board "
				+ "WHERE board_title LIKE ? ORDER BY created_date DESC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, "%" + keyword + "%"); // 검색어 포함
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("board_id");
				String title = rs.getString("board_title");
				String content = rs.getString("board_content");
				String createdDate = rs.getString("created_date");
				BoardBean board = new BoardBean(id, title, createdDate);
				board.setBoard_Content(content); // content 값 설정
				result.add(board);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return result;
	}

	// 댓글 메소드
	public boolean insertComment(int boardId, String content) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `comment` (board_id, comment_content) VALUES (?, ?)";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, boardId);
			pstmt.setString(2, content);
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

	public ArrayList<CommentBean> getComments(int boardId) {
		ArrayList<CommentBean> comments = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT comment_id, comment_content, created_date FROM comment WHERE board_id = ? ORDER BY created_date ASC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, boardId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int commentId = rs.getInt("comment_id");
				String commentContent = rs.getString("comment_content");
				// created_date 변환
				Timestamp timestamp = rs.getTimestamp("created_date");
				String formattedDate = "";
				if (timestamp != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
					formattedDate = dateFormat.format(timestamp);
				}

				CommentBean comment = new CommentBean(commentId, commentContent, formattedDate);
				comments.add(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return comments;
	}

	// 댓글 삭제 메소드
	public boolean deleteComment(int commentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM `comment` WHERE comment_id = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, commentId);
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

	// DB에 저장된 댓글을 업데이트하는 메서드
	public boolean updateComment(int commentId, String newContent) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE `comment` SET comment_content = ? WHERE comment_id = ?";
		boolean flag = false;

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newContent);
			pstmt.setInt(2, commentId);
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

	// 수정 시 기존 댓글 내용을 가져오는 메서드
	public String getCommentContent(int commentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String content = "";
		String sql = "SELECT comment_content FROM `comment` WHERE comment_id = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, commentId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				content = rs.getString("comment_content");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return content;
	}

	// 게시글 리스트를 반환하는 메소드
	public ArrayList<BoardBean> getBoards() {
		return boards;
	}

}
