package hello;

public class Application {

	private final long id;
	private final String content;

	public Application(long id, String content) {
		this.id = id;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

}
