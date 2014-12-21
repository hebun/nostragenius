package membership;

public enum UserType {

	PENDING("PENDING"), ACTIVE("ACTIVE"), ADMIN("ADMIN"), SA("SA");
	private String code;

	private UserType(String name) {
		this.code = name;
	}
	public String getCode(){
		return code;
	}
}
