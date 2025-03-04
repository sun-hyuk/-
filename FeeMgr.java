package Project;

import java.sql.*;
import java.util.Vector;

public class FeeMgr {
	private DBConnectionMgr pool;

	public FeeMgr() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FeeRecordBean getFeeRecords() {
		FeeRecordBean feeRecord = new FeeRecordBean();
		String sql = "SELECT (SELECT SUM(deposit_amount) FROM deposit) AS total_deposit, "
				+ "       (SELECT SUM(expenditure_amount) FROM expenditure) AS total_expenditure, "
				+ "       (SELECT deposit_amount FROM deposit ORDER BY deposit_date DESC LIMIT 1) AS latest_deposit_amount, "
				+ "       (SELECT deposit_note FROM deposit ORDER BY deposit_date DESC LIMIT 1) AS latest_deposit_note, "
				+ "       (SELECT expenditure_amount FROM expenditure ORDER BY expenditure_date DESC LIMIT 1) AS latest_expenditure_amount, "
				+ "       (SELECT expenditure_note FROM expenditure ORDER BY expenditure_date DESC LIMIT 1) AS latest_expenditure_note";
		try (Connection con = pool.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			if (rs.next()) {
				feeRecord.setTotalDeposit(rs.getInt("total_deposit"));
				feeRecord.setTotalExpenditure(rs.getInt("total_expenditure"));
				feeRecord.setLatestDepositAmount(rs.getInt("latest_deposit_amount"));
				feeRecord.setLatestDepositNote(rs.getString("latest_deposit_note"));
				feeRecord.setLatestExpenditureAmount(rs.getInt("latest_expenditure_amount"));
				feeRecord.setLatestExpenditureNote(rs.getString("latest_expenditure_note"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feeRecord;
	}

	// DB에서 expenditure 테이블의 지출 내역을 불러와 FeeRecordBean 객체로 반환
	public Vector<FeeRecordBean> listFee() {
		Vector<FeeRecordBean> feeList = new Vector<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// expenditure_note -> item, expenditure_amount -> amount, expenditure_date ->
		String sql = "SELECT expenditure_note AS item, expenditure_amount AS amount, expenditure_date AS feeDate "
				+ "FROM expenditure ORDER BY expenditure_date DESC";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				FeeRecordBean bean = new FeeRecordBean();
				bean.setItem(rs.getString("item"));
				// 금액은 INT형이므로, 문자열로 변환 (예: String.valueOf())
				bean.setAmount(String.valueOf(rs.getInt("amount")));
				String feeDate = rs.getString("feeDate");
				if (feeDate != null && feeDate.length() >= 10) {
	                feeDate = feeDate.substring(0, 10);
	            }
	            bean.setFeeDate(feeDate);
				feeList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return feeList;
	}

	// 삭제 메소드: expenditure 테이블에서 지출 내역을 삭제
	public boolean deleteFee(String item) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM expenditure WHERE expenditure_note = ?";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, item);
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

	//지출내역 저장
	public boolean insertFee(String item, int amount) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO expenditure (expenditure_amount, expenditure_note) VALUES (?, ?)";
		boolean flag = false;
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, amount);
			pstmt.setString(2, item);
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

	public int getTotalAsset() {
		int totalAsset = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// deposit 테이블의 deposit_amount와 expenditure 테이블의 expenditure_amount를 이용하여 계산
		String sql = "SELECT (SELECT IFNULL(SUM(deposit_amount), 0) FROM deposit) - "
				+ "       (SELECT IFNULL(SUM(expenditure_amount), 0) FROM expenditure) AS total_asset";
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				totalAsset = rs.getInt("total_asset");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return totalAsset;
	}

}
