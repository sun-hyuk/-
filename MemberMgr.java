package Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.ImageIcon;

public class MemberMgr {

	private DBConnectionMgr pool;

	public MemberMgr() {
		try {
            // 미리 10개의 Connection 객체를 생성하는 DBConnectionMgr 인스턴스를 가져옴
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 회원 인증 메서드 : 아이디와 비밀번호를 확인하여 인증 성공 시 회원 정보를 Bean에 세팅
	public boolean authenticateMember(MemberBean bean) {
		String sql = "SELECT * FROM member WHERE member_id = ? AND password = ?";

		try (Connection con = pool.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, bean.getMember_Id());
			pstmt.setString(2, bean.getPassword());

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) { // 인증 성공
                    // DB 컬럼 값을 MemberBean에 설정
					bean.setMember_Id(rs.getString("member_id"));
					bean.setPassword(rs.getString("password"));
					bean.setName(rs.getString("name"));
					bean.setStudent_ID(rs.getString("student_id"));
					bean.setGender(rs.getString("gender"));
					bean.setDepartment(rs.getString("department"));
					bean.setPhone_Number(rs.getString("phone_number"));
					bean.setMember_Role(rs.getString("member_role"));
					bean.setAdmission_Fee(rs.getString("admission_fee"));
					bean.setPenalty(rs.getInt("penalty"));

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

	// 전화번호 중복 확인 메서드
	public boolean checkDuplicatePhone(String phone) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) FROM member WHERE phone_number = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, phone);
			rs = pstmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				return true; // 이미 존재하는 전화번호
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return false; // 중복되지 않음
	}

	// 학번 중복 확인 메서드
	public boolean checkDuplicateStudentId(String studentId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) FROM member WHERE student_id = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, studentId);
			rs = pstmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				return true; // 이미 존재하는 학번
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return false; // 중복되지 않음
	}

    // 아이디 중복 확인 메서드
	public boolean checkDuplicateId(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) FROM member WHERE member_id = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				return true; // 이미 존재하는 아이디
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return false; // 중복되지 않음
	}

	// 회원 등록 메서드 (회원정보 삽입 및 입금 테이블에 입회비 정보 추가)
	public boolean insertMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			// 전화번호 중복 체크
			if (checkDuplicatePhone(bean.getPhone_Number())) {
				System.out.println("전화번호가 이미 존재합니다.");
				return false; // 중복된 전화번호가 있으면 false 반환
			}

			// 학번 중복 체크
			if (checkDuplicateStudentId(bean.getStudent_ID())) {
				System.out.println("학번이 이미 존재합니다.");
				return false; // 중복된 학번이 있으면 false 반환
			}

			con = pool.getConnection();
			// num 컬럼은 자동 증가하므로 null로 세팅하고 나머지 값은 동적으로 설정
			sql = "insert member values (?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bean.getMember_Id()); // null, 'root',
			pstmt.setString(2, bean.getPassword()); // null, 'root', '1234',
			pstmt.setString(3, bean.getName()); // null, 'root', '1234', '김동진',
			pstmt.setString(4, bean.getStudent_ID()); // null, 'root', '1234', '김동진', '20191111',
			pstmt.setString(5, bean.getGender()); // null, 'root', '1234', '김동진', '20191111', '남'
			pstmt.setString(6, bean.getDepartment());
			pstmt.setString(7, bean.getPhone_Number());
			// 회원 역할과 입회비는 null일 경우 기본값 적용
			pstmt.setString(8, bean.getMember_Role() != null ? bean.getMember_Role() : "일반회원"); // default : 일반회원
			pstmt.setString(9, bean.getAdmission_Fee() != null ? bean.getAdmission_Fee() : "5000"); // default : 5000
			pstmt.setInt(10, bean.getPenalty() <= 0 ? 0 : bean.getPenalty()); // default : 0
			pstmt.setString(11, bean.getMember_Profile());
			;

			// 회원정보 삽입 (성공 시 1 반환)
			int cnt = pstmt.executeUpdate();

			// 회원정보 삽입 성공 시, deposit 테이블에 입회비 정보 추가
			if (cnt == 1) {
				// 입금 금액을 admission_fee에서 가져오고, 입금 날짜는 현재 날짜로 설정
				int depositAmount = 0;
				try {
					depositAmount = Integer.parseInt(bean.getAdmission_Fee()); // 입회비납부가 숫자가 아닐 경우 예외 처리
				} catch (NumberFormatException e) {
					System.out.println("입회비납부가 숫자가 아니므로 기본값 5000으로 처리됩니다.");
					depositAmount = 5000;
				}

				sql = "INSERT INTO deposit (deposit_amount, deposit_date, deposit_note, member_id) VALUES (?, ?, ?, ?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, depositAmount); // 입금 금액
				// 현재 날짜 (yyyy-MM-dd 형식)
				pstmt.setString(2, new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // 입금 날짜 (현재 날짜)
				pstmt.setString(3, "입회비"); // 입금 비고
				pstmt.setString(4, bean.getMember_Id()); // 회원 ID

				int depositCnt = pstmt.executeUpdate();
				// 입금 정보도 삽입되면 최종적으로 성공 처리
				if (depositCnt == 1) {
					flag = true; // 성공적으로 처리된 경우
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// 모든 회원 목록을 가져오는 메서드
	public Vector<MemberBean> listMember() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		Vector<MemberBean> vlist = new Vector<MemberBean>();
		try {
			con = pool.getConnection();
			sql = "select * from member";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			// 모든 회원 레코드를 읽어 MemberBean에 담아 Vector에 저장
			while (rs.next()) {
				MemberBean bean = new MemberBean();
				bean.setMember_Id(rs.getString("member_id")); // 리턴받은 num값을 빈즈에 저장
				bean.setPassword(rs.getString("password"));
				bean.setName(rs.getString("name"));
				bean.setStudent_ID(rs.getString("student_id"));
				bean.setGender(rs.getString("gender"));
				bean.setDepartment(rs.getString("department"));
				bean.setPhone_Number(rs.getString("phone_number"));
				bean.setMember_Role(rs.getString("member_role"));
				bean.setAdmission_Fee(rs.getString("admission_fee"));

				// 마지막에 빈즈를 Vector에 저장
				vlist.addElement(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	// 특정 회원 정보 하나를 가져오는 메서드 (member_id를 기준으로 조회)
	public MemberBean getMember(int num/* 어떤 레코드 리턴할지 조건으로 들어가는 값 */) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		MemberBean bean = new MemberBean();
		try {
			con = pool.getConnection();
			sql = "select * from member where member_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num); // 첫번째 ?에 매개변수 num 세팅
			rs = pstmt.executeQuery();
			// Member_Id은 pk이므로 두개 이상의 레코드는 절대 리턴 될수가 없다.
			if (rs.next()) {
				bean.setMember_Id(rs.getString(1)); // 매개변수는 정수는 컬럼의 index 세팅
				bean.setPassword(rs.getString(2));
				bean.setStudent_ID(rs.getString(3));
				bean.setName(rs.getString(4));
				bean.setGender(rs.getString(5));
				bean.setDepartment(rs.getString(6));
				bean.setPhone_Number(rs.getString(7));
				bean.setMember_Role(rs.getString(8));
				bean.setAdmission_Fee(rs.getString(9));
				bean.setPenalty(rs.getInt(10));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return bean;
	}

	// 회원 역할 업데이트 메서드 (회원 아이디에 해당하는 레코드의 역할 변경)
	public boolean updateMemberRole(String memberId, String newRole) {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    String sql = "UPDATE member SET member_role=? WHERE member_id=?";
	    boolean flag = false;
	    try {
	        con = pool.getConnection();
	        pstmt = con.prepareStatement(sql);
	        pstmt.setString(1, newRole);
	        pstmt.setString(2, memberId);
	        int cnt = pstmt.executeUpdate();
	        if(cnt == 1) {
	            flag = true;
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        pool.freeConnection(con, pstmt);
	    }
	    return flag;
	}

	// 회원 정보 수정 메서드 (비밀번호, 이름, 학번, 학과, 전화번호 업데이트)
	public boolean updateMember(MemberBean bean) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "update member set password=?, name=?, student_id=?, department=?, phone_number=?"
					+ "where member_id = ?";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, bean.getPassword());
			pstmt.setString(2, bean.getName());
			pstmt.setString(3, bean.getStudent_ID());
			pstmt.setString(4, bean.getDepartment());
			pstmt.setString(5, bean.getPhone_Number());
			pstmt.setString(6, bean.getMember_Id());

			int cnt = pstmt.executeUpdate();
			if (cnt == 1)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// 회원 비밀번호 수정 메서드 (새 비밀번호로 업데이트)
	public boolean updateMemberInfo(String memberId, String newPassword) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE member SET password=? WHERE member_id=?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			// 해당 회원 레코드 존재 여부 확인
			pstmt = con.prepareStatement("SELECT password FROM member WHERE member_id=?");
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				return false;
			}
			String currentPassword = rs.getString("password");
			rs.close();
			pstmt.close();

			// 비밀번호 업데이트 실행
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, newPassword);
			pstmt.setString(2, memberId);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected == 1) {
				flag = true;
			} else if (rowsAffected == 0) {
				// 업데이트 행이 0인 경우, 실제 DB의 값과 비교
				pstmt.close();
				pstmt = con.prepareStatement("SELECT password FROM member WHERE member_id=?");
				pstmt.setString(1, memberId);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					String updatedPassword = rs.getString("password");
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

	// 회원 삭제 메서드 (회원 아이디를 기준으로 삭제)
	public boolean deleteMember(String member_id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "delete from member where member_id = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, member_id);
			if (pstmt.executeUpdate() == 1)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	// 회원 프로필 이미지를 업데이트하는 메서드
	public boolean updateMemberProfile(String memberId, byte[] imageBytes) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE member SET member_profile=? WHERE member_id=?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			if (imageBytes != null) {
				pstmt.setBytes(1, imageBytes);
			} else {
				pstmt.setNull(1, java.sql.Types.BLOB);
			}
			pstmt.setString(2, memberId);
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

	// 회원 프로필 이미지를 가져오는 메서드 (ImageIcon 객체로 반환)
	public ImageIcon getMemberProfile(String memberId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT member_profile FROM member WHERE member_id=?";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, memberId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				byte[] imageBytes = rs.getBytes("member_profile");
				System.out.println("조회된 이미지 바이트 길이: " + (imageBytes != null ? imageBytes.length : "null"));
				if (imageBytes != null && imageBytes.length > 0) {
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

	// 팀(학과) 리스트 조회 (중복 제거된 부서명 목록)
	public Vector<String> getDepartmentList() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		Vector<String> vlist = new Vector<String>();
		try {
			con = pool.getConnection();
			sql = "select distinct department from member";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				// 리턴과 동시에 바로 Vector 저장
				vlist.addElement(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	// 회원 아이디 리스트 조회 (정렬된 상태로 반환)
	public Vector<String> getMemberIdList() {
		Vector<String> vlist = new Vector<String>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT member_id FROM member ORDER BY member_id";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				vlist.add(rs.getString("member_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return vlist;
	}

	public static void main(String[] args) {
		MemberMgr mgr = new MemberMgr();
		Vector<MemberBean> vlist = mgr.listMember();
		Vector<String> memberIdList = mgr.getMemberIdList();
		System.out.println(vlist.size());
	}
}
