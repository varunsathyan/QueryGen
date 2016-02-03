import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PerlTest {

	public static void main(String[] args) throws IOException {
		runPerlScript(null,null);
	}
	
	public static int runPerlScript(String s, String w) throws IOException {
		Process proc;
		String toBeParsed = null;

		try {
			Process process = Runtime.getRuntime()
					.exec("perl C:\\dev\\UMLS-Similarity-1.45\\utils\\query-umls-similarity-webinterface.pl" + s + " " + w);
			process.waitFor();
			if (process.exitValue() == 0) {
				System.out.println("Command Successful");
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					while ((line = in.readLine()) != null) {
						System.out.println(line);
						toBeParsed = line.substring(0, line.indexOf("<"));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally
				{
					in.close();
				}
			} else {
				System.out.println("Command Failure");
			}
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.toString());
		}
		return Integer.parseInt(toBeParsed);
	}
}
