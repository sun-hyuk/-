package Project;

public class FeeRecordBean {
	private String item; // 품목 (예: "사무용품", "식대" 등)
	private String amount; // 지출금액
	private String feeDate; // 지출일자 (예: "2025-02-14")

	private int totalDeposit;
	private int totalExpenditure;
	private int latestDepositAmount;
	private String latestDepositNote;
	private int latestExpenditureAmount;
	private String latestExpenditureNote;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFeeDate() {
		return feeDate;
	}

	public void setFeeDate(String feeDate) {
		this.feeDate = feeDate;
	}

	//////////////////////////////////////////////////
	public int getTotalDeposit() {
		return totalDeposit;
	}

	public void setTotalDeposit(int totalDeposit) {
		this.totalDeposit = totalDeposit;
	}

	public int getTotalExpenditure() {
		return totalExpenditure;
	}

	public void setTotalExpenditure(int totalExpenditure) {
		this.totalExpenditure = totalExpenditure;
	}

	public int getLatestDepositAmount() {
		return latestDepositAmount;
	}

	public void setLatestDepositAmount(int latestDepositAmount) {
		this.latestDepositAmount = latestDepositAmount;
	}

	public String getLatestDepositNote() {
		return latestDepositNote;
	}

	public void setLatestDepositNote(String latestDepositNote) {
		this.latestDepositNote = latestDepositNote;
	}

	public int getLatestExpenditureAmount() {
		return latestExpenditureAmount;
	}

	public void setLatestExpenditureAmount(int latestExpenditureAmount) {
		this.latestExpenditureAmount = latestExpenditureAmount;
	}

	public String getLatestExpenditureNote() {
		return latestExpenditureNote;
	}

	public void setLatestExpenditureNote(String latestExpenditureNote) {
		this.latestExpenditureNote = latestExpenditureNote;
	}
}
