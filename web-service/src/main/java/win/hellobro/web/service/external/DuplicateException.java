package win.hellobro.web.service.external;

public class DuplicateException extends Exception {
	private boolean isDuplicateEmail;
	private boolean isDuplicateNickname;

	public void duplicateEmail() {
		this.isDuplicateEmail = true;
	}

	public void duplicateNickname() {
		this.isDuplicateNickname = true;
	}

	public boolean isDuplicateEmail() {
		return isDuplicateEmail;
	}

	public boolean isDuplicateNickname() {
		return isDuplicateNickname;
	}
}
