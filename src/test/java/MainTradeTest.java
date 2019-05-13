import com.jeancoder.app.sdk.server.DevServer;

public class MainTradeTest {
	public static void main(String[] args) throws Exception {
		String fp = System.getProperty("user.dir");
		DevServer.start(8092, fp);
	}
}
