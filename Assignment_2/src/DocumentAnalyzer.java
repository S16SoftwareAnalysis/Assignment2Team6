import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.RAMDirectory;

public class DocumentAnalyzer {

	private static final String[] Remove_WORDS = { "import", "package", "a", "about", "above", "according", "across",
			"after", "afterwards", "again", "against", "albeit", "all", "almost", "alone", "along", "already", "also",
			"although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow",
			"anyone", "anything", "anyway", "anywhere", "apart", "are", "around", "as", "at", "av", "be", "became",
			"because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below",
			"beside", "besides", "between", "beyond", "both", "but", "by", "can", "cannot", "canst", "certain", "cf",
			"choose", "contrariwise", "cos", "could", "cu", "day", "do", "does", "doesn\'t", "doing", "dost", "doth",
			"double", "down", "dual", "during", "each", "either", "else", "elsewhere", "enough", "et", "etc", "even",
			"ever", "every", "everybody", "everyone", "everything", "everywhere", "except", "excepted", "excepting",
			"exception", "exclude", "excluding", "exclusive", "far", "farther", "farthest", "few", "ff", "first", "for",
			"formerly", "forth", "forward", "from", "front", "further", "furthermore", "furthest", "get", "go", "had",
			"halves", "hardly", "has", "hast", "hath", "have", "he", "hence", "henceforth", "her", "here", "hereabouts",
			"hereafter", "hereby", "herein", "hereto", "hereupon", "hers", "herself", "him", "himself", "hindmost",
			"his", "hither", "hitherto", "how", "however", "howsoever", "i", "ie", "if", "in", "inasmuch", "inc",
			"include", "included", "including", "indeed", "indoors", "inside", "insomuch", "instead", "into", "inward",
			"inwards", "is", "it", "its", "itself", "just", "kind", "kg", "km", "last", "latter", "latterly", "less",
			"lest", "let", "like", "little", "ltd", "many", "may", "maybe", "me", "meantime", "meanwhile", "might",
			"moreover", "most", "mostly", "more", "mr", "mrs", "ms", "much", "must", "my", "myself", "namely", "need",
			"neither", "never", "nevertheless", "next", "no", "nobody", "none", "nonetheless", "noone", "nope", "nor",
			"not", "nothing", "notwithstanding", "now", "nowadays", "nowhere", "of", "off", "often", "ok", "on", "once",
			"one", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out",
			"outside", "over", "own", "per", "perhaps", "plenty", "provide", "quite", "rather", "really", "round",
			"said", "sake", "same", "sang", "save", "saw", "see", "seeing", "seem", "seemed", "seeming", "seems",
			"seen", "seldom", "selves", "sent", "several", "shalt", "she", "should", "shown", "sideways", "since",
			"slept", "slew", "slung", "slunk", "smote", "so", "some", "somebody", "somehow", "someone", "something",
			"sometime", "sometimes", "somewhat", "somewhere", "spake", "spat", "spoke", "spoken", "sprang", "sprung",
			"stave", "staves", "still", "such", "supposing", "than", "that", "the", "thee", "their", "them",
			"themselves", "then", "thence", "thenceforth", "there", "thereabout", "thereabouts", "thereafter",
			"thereby", "therefore", "therein", "thereof", "thereon", "thereto", "thereupon", "these", "they", "this",
			"those", "thou", "though", "thrice", "through", "throughout", "thru", "thus", "thy", "thyself", "till",
			"to", "together", "too", "toward", "towards", "ugh", "unable", "under", "underneath", "unless", "unlike",
			"until", "up", "upon", "upward", "upwards", "us", "use", "used", "using", "very", "via", "vs", "want",
			"was", "we", "week", "well", "were", "what", "whatever", "whatsoever", "when", "whence", "whenever",
			"whensoever", "where", "whereabouts", "whereafter", "whereas", "whereat", "whereby", "wherefore",
			"wherefrom", "wherein", "whereinto", "whereof", "whereon", "wheresoever", "whereto", "whereunto",
			"whereupon", "wherever", "wherewith", "whether", "whew", "which", "whichever", "whichsoever", "while",
			"whilst", "whither", "who", "whoa", "whoever", "whole", "whom", "whomever", "whomsoever", "whose",
			"whosoever", "why", "will", "wilt", "with", "within", "without", "worse", "worst", "would", "wow", "ye",
			"yet", "year", "yippee", "you", "your", "yours", "yourself", "yourselves", "abstract", "assert", "boolean",
			"break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else",
			"enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
			"int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short",
			"static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
			"void", "volatile", "while", "String", "Integer", "Double", "Boolean", "Float", "Long", "Set", "List",
			"ArrayList", "Map", "Exception", "System", "HashMap", "Object", "Thread", "Class", "Date", "Iterator",
			"Math", };

	private Stemmer stem;

	public DocumentAnalyzer() {
		stem = new Stemmer();
	}

	public String removeImportsAndPackages(String line) {
		String parsedDocument = "";

		Scanner s = new Scanner(line);

		while (s.hasNext()) {
			String nextLine = s.next();

			if (nextLine.equals("import") || nextLine.equals("package")) {
				s.next();
			} else {
				parsedDocument = parsedDocument + nextLine + " ";
			}
		}
		s.close();

		return parsedDocument;
	}

	public String removeStopWords_ReservedWords_JavaClasses_ImportAndPackage(String document) {
		String parsedDocument = "";

		Scanner s = new Scanner(document);

		while (s.hasNext()) {
			String word = s.next();

			if (Arrays.asList(Remove_WORDS).contains(word.trim().toLowerCase())) {
				word = "";
			}
			parsedDocument = parsedDocument + word + " ";
		}
		s.close();

		return parsedDocument;
	}

	public String TrimWordsandRemoveCamelCase(String document) {
		String parsedDocument = "";

		Scanner s = new Scanner(document);

		while (s.hasNext()) {
			String word = s.next().trim();

			if (word.trim().length() <= 2) {
				word = "";
			}
			parsedDocument = parsedDocument + word + " ";

			if (isCamelCase(word)) {
				word = word + " " + splitCamelCaseWord(word);
			}
			parsedDocument = parsedDocument + word + " ";
		}
		s.close();

		return parsedDocument;
	}

	public String porterStemmer(String document) {
		String parsedDocument = "";

		Scanner s = new Scanner(document);

		while (s.hasNext()) {
			String word = s.next();
			if (!isCamelCase(word)) {
				word = porterStemmerWord(word);
			}
			parsedDocument = parsedDocument + word + " ";
		}
		s.close();

		return parsedDocument;
	}

	private String porterStemmerWord(String word) {
		for (int charPosition = 0; charPosition < word.length(); charPosition++) {
			stem.add(word.charAt(charPosition));
		}
		stem.stem();

		return stem.toString();
	}

	public static String splitCamelCaseWord(String word) {
		String splitted = word.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");

		return splitted;
	}

	private boolean isCamelCase(String word) {
		boolean camelCase = false;

		if (word.matches("([a-z]+[A-Z]+\\w+)+")) {
			camelCase = true;
		} else if (word.matches("([A-Z][a-z]+[A-Z]+\\w+)+")) {
			camelCase = true;
		}

		return camelCase;
	}

	public String removeNumbersandTokens(String document) {
		String parsedDocument = "";

		Scanner s = new Scanner(document);
		while (s.hasNext()) {
			String word = s.next();

			for (char c : word.toCharArray()) {
				if (Character.isDigit(c)) {
					word = "";
				}
			}
			parsedDocument = parsedDocument + word + " ";
			parsedDocument = document.replaceAll("\\p{P}", " ");
			parsedDocument = parsedDocument.replaceAll("[-+^:, ;.*]", " ");

		}
		s.close();

		return parsedDocument;
	}

	public String removeSpecialChar(String document) {
		String parsedDocument = "";
		parsedDocument = document.replaceAll("//", "");
		parsedDocument = document.replaceAll("/*", "");
		parsedDocument = document.replace("/**", "");
		parsedDocument = document.replace("*", "");
		parsedDocument = document.replace("*/", "");
		parsedDocument = document.replace("**/", "");
		return parsedDocument;
	}
}

class LuceneAnalyzer {

	private Analyzer analyzer;
	private Directory index;
	private IndexWriterConfig config;
	private IndexWriter writer;
	private File[] documents;
	private DocumentAnalyzer parser;

	public LuceneAnalyzer() {
	}

	public LuceneAnalyzer(Analyzer analyzer, Directory index, File[] documents) throws IOException {
		this.analyzer = analyzer;
		this.index = index;
		this.documents = documents;

		try {
			config = new IndexWriterConfig(analyzer);
			writer = new IndexWriter(index, config);
		} catch (IOException e) {
			e.printStackTrace();
		}

		parser = new DocumentAnalyzer();
		parseMethods(documents);
	}

	public void query(String query, int hits) throws IOException {
		writer.close();
		String processedQuery = parser(query);
		analyzeFiles(processedQuery, hits);
	}

	protected String parser(String input) {
		String ParsedIn = "";
		ParsedIn = parser.removeImportsAndPackages(input);
		ParsedIn = parser.removeStopWords_ReservedWords_JavaClasses_ImportAndPackage(ParsedIn);
		ParsedIn = parser.TrimWordsandRemoveCamelCase(ParsedIn);
		ParsedIn = parser.removeNumbersandTokens(ParsedIn);
		ParsedIn = parser.removeSpecialChar(ParsedIn);
		ParsedIn = parser.porterStemmer(ParsedIn);
		return ParsedIn;
	}

	protected void analyzeFiles(String query, int hitsX) {
		try {
			int hitsPerPage = hitsX;
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			Query q = new QueryParser("method", analyzer).parse(query);

			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for (int i = 0; i < hits.length; i++) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println(d.get("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	protected void parseMethods(File[] files) throws IOException {
		String method = "";
		for (File file : files) {
			if (file.isDirectory()) {
				parseMethods(file.listFiles());
			} else {
				try {
					method = new Scanner(file).useDelimiter("\\Z").next();
					method = parser(method);
					try {
						addDoc(file.getName(), method);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void addDoc(String name, String method) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("name", name, Field.Store.YES));
		doc.add(new TextField("method", method, Field.Store.YES));
		writer.addDocument(doc);
	}

	public static void main(String[] args) throws IOException {
		
//		//GoldSet for Ant
//		
//		//Bug54026
//
//		String aTitle1 = "Zip task on <mappedresources> that excludes certain files by way of the mapper results in a NullPointerException";
//		String aDesc1 = "Steps to reproduce: 1. Create a folder named `test' with the following:    test/   test/test1    test/subdir/   test/subdir/test2   (`test1' and `test2' are regular files.) 2. Create a `build.xml' with the following: 3. Run `ant'. Actual result: NullPointerException thrown in org.apache.tools.ant.taskdefs.Zip.addResources. Expected result: `ant' finishes without error and there is a new ZIP file called `destzip.zip' in the current directory. `zip -sf destzip.zip' should print: Archive contains:  subdir.orig/  subdir.orig/test2 Total 2 entries Tested in: Ant 1.8.2 and 1.8.4 on Mac OS 10.7.5.  Known work-around: Add an `excludes' attribute to the <fileset> to exclude all files not matched by the from glob. For the given example, `excludes=\"test1\"'.";
//
//		String Bug54026Q1 = aTitle1 + " " + aDesc1;
//		String Bug54026Q2 = aTitle1;
//		String Bug54026Q3 = aDesc1;
//
//		
//		//Bug52188
//		String aTitle2 = "DirectoryScanner is not threadsafe";
//		String aDesc2 = "We have a build target which runs several concurrent builds inside of a parallel block.  This target would intermittently through several NPE's and fail.  Long story short, I chased the NPE's back to the DirectoryScanner class.  In looking at the class it was clear that there several methods which modified the same member, and had no locking in place to prevent concurrent modification of that member.  Since applying the attached patch, we have not seen this error (several hundred builds later). Whereas without the patch, we see the failure in at least 1 out of every 10 builds";
//
//		 String Bug52188Q1 = aTitle2 + " " + aDesc2;
//		 String Bug52188Q2 = aTitle2;
//		 String Bug52188Q3 = aDesc2;
//		
//		
//		//Bug49453
//		 String aTitle3 = "Ant get task does not interact well with transparent proxies";
//		 String aDesc3 = "We have squid as a transparent proxy and if cache hit happens, it returns content with Content-Encoding: gzip. If the content missed from cache, it is returned normally. As result, the ant get task downloads and saves content in gzip-ed or normal form depending on cache state, and this is not what most ant scripts are expecting. Note, that in the case of cache hit, Content-Length: header is returned with length of gziped content. The more technical details on this problem are available in this Ivy bug https://issues.apache.org/jira/browse/IVY-1194 , which was also affected by this problem.";
//
//		 String Bug49453Q1 = aTitle3 + " " + aDesc3;
//		 String Bug49453Q2 = aTitle3;
//		 String Bug49453Q3 = aDesc3;
//		
//		
//		
//		//Bug51110
//		 String aTitle4 = "[PATCH] Ambiguous expected result from the resourceexists condition with URLResource";
//		 String aDesc4 = "= Overview = When using a <url> as the nested resource the <resourceexists> condition, my expectation is that the condition should evaluate to true if the remote file referred to by the URL does not exist. However, the sample test case below demonstrates otherwise. Looking at the URLResource code, it seems like URLConnection.connect() is used for the evaluation. I've done some test and notice that this method returns true as long as the connection to the server can be made. I am not sure whether this is by design, but I find that in order to truly determine if the remote file exists, the URLConnection.getContent() method must be called. It is when the method returns FileNotFoundException as expected (at least for files . I assume that a call to conn.getContent() can be added to isExists() in URLResource, but I am not sure what to do with the UnknownServiceException that it throws. Perhaps someone can comment more on this to determine what the best solution is. = Steps to Reproduce = Run the following sample Ant build file on a system with Internet access.  = Actual Result = Ant build fails with the message \"Resource does not exist.\"  = Expected Result = Ant build completes \"successfully\".";
//
//		 String Bug51110Q1 = aTitle4 + " " + aDesc4;
//		 String Bug51110Q2 = aTitle4;
//		 String Bug51110Q3 = aDesc4;
//		
//		//Bug52706
//		 String aTitle5 = "[PATCH] Make Execute class extensible to allow custom CommandLauncher implementation";
//		 String aDesc5 = "Created attachment 28353 [details] A patch to implement the enhancement Right now Execute uses a static CommandLauncher instance which is initialize inside the static initializer (class constructor). CommandLauncher's responsibility is to create the Process instances. Currently there isn't a way to make Execute use a different CommandLauncher I have attached a patch containing the changes I made to Execute java class. What I have basically done is move the code that initializes the CommandLauncher from the Execute class to to the CommandLauncher class. I have made CommandLauncher class public and all the subclasses of it public too.  I have added two static methods getVMLauncher and getShellLauncher to the CommandLauncher class. These methods take a Project object as parameter and check if a reference to an existing CommandLauncher is present in the project. If present then that instance is returned. Otherwise the method checks if a system property containing the name of the launcher class has been set. If so then an instance of that class is created and returned, otherwise the default launcher is returned. I have also added setVMLauncher and setShellLauncher methods that add reference to a given CommandLauncher instance to the project. I have modified the Execute class to use these two methods to get the reference of the CommandLauncher class. I have also created a CommandLauncherTask class similar to the PropertyHelperTask that takes an instance of any CommandLauncher class and adds a reference to it to the project.";
//
//
//		 String Bug52706Q1 = aTitle5 + " " + aDesc5;
//		 String Bug52706Q2 = aTitle5;
//		 String Bug52706Q3 = aDesc5;
//		
		
		
	//GoldSet for Tomcat	
		
		//Bug57715
		String tTitle1 = "Finding security constraints can fail when HTTP methods are specified";
		String tDesc1 = "Created attachment 32576 [details] Unit test to demonstrate the problem and a fix to the RealmBase Finding security constraints can fail when HTTP methods are specified.  When HTTP methods are defined in the security constraints, the RealmBase.findSecurityConstraints() method can terminate early without adding a constraint to the results. A simple case that demonstrates this problem is to define security constraints such that the entire web site requires authentication.  Then add one additional constraint that allows the GET HTTP method for a specific URL to bypass authentication: If an HTTP POST request is sent to the /service/foo uri, the findSecurityConstraints() method matches the ‘/service/’ URL and flags the search status as having found a match. However, when the HTTP method is examined it is found not to match and the security constraint is not added to the results. Even though the HTTP method didn’t match, the search was still flagged as finding a match and the search is terminated, returning zero constraints. This allows the POST request to proceed without authentication. A patch is attached that includes a unit test to demonstrate the problem and a fix to the RealmBase.";

		String Bug57715Q1 = tTitle1 + " " + tDesc1;
		String Bug57715Q2 = tTitle1;
		String Bug57715Q3 = tDesc1;

		//Bug58581
		String tTitle2 = "StandardHostValve#custom throws NPE if custom error page is incorrectly configured";
		String tDesc2 = "Created attachment 33255 [details] proposed fix to avoid NPE  Minimal files to reproduce this issue: 1) create a directory named test and deploy it under webapps test ├── WEB-INF │   └── web.xml ├── error.html └── test.html the web.xml is as follows: 2 ) start tomcat and visit http://localhost:8080/test/abc.html The exception is thrown as follows: 04-Nov-2015 22:24:04.984 SEVERE [http-nio-8080-exec-1] org.apache.catalina.core.StandardHostValve.custom Exception Processing                      ErrorPage[errorCode=404, location=/error.html] java.lang.NullPointerException    at org.apache.catalina.core.StandardHostValve.custom(StandardHostValve.java:391)    at org.apache.catalina.core.StandardHostValve.status(StandardHostValve.java:257)    at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:180)    at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:79)    at org.apache.catalina.valves.AbstractAccessLogValve.invoke (AbstractAccessLogValve.java:616)    at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:87)    at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:361)    at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:1057)    at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:75)     at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process (AbstractProtocol.java:737)    at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.run(NioEndpoint.java:1531)    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)    at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)    at java.lang.Thread.run(Thread.java:745) The exception is not user-friendly, and somehow misleading. From the stack trace, user is likely to consider it as a tomcat bug.  The solution might be adding url-pattern /error.html to the default servlet, or just change the url-pattern to match all the html resources. My suggestion is to avoid such NPE and tell user what to do. I have attached a proposed fix against trunk.";

		String Bug58581Q1 = tTitle2 + " " + tDesc2;
		String Bug58581Q2 = tTitle2;
		String Bug58581Q3 = tDesc2;
		
		//Bug58053
		String tTitle3 = "Use loop rather than assuming the size of the array won't change";
		String tDesc3 = "Created attachment 32835 [details] Replace numeric offsets with loop Code in FastHttpDateFormat uses numeric offsets into an array when it should use a loop. In this case the loop is no shorter than the existing 3 lines, but using a loop avoids the problem of ensuring that the array size and the max index agree.";

		String Bug58053Q1 = tTitle3 + " " + tDesc2;
		String Bug58053Q2 = tTitle3;
		String Bug58053Q3 = tDesc3;

		//Bug58735
		String tTitle4 = "Add support for X-XSS-Protection header";
		String tDesc4 = "Created attachment 33349 [details] The patch that implements this feature. The Tomcat's HttpHeaderSecurityFilter allows to set useful security related headers but it doesn't support the X-XSS-Protection header: https://www.owasp.org/index.php/List_of_useful_HTTP_headers The attached patch enhance the filter to support this header.";

		String Bug58735Q1= tTitle4 + " " + tDesc4;
		String Bug58735Q2 = tTitle4;
		String Bug58735Q3 = tDesc4;

		//Bug57431
		String tTitle5 = "Enable custom context implementation when using embedded tomcat";
		String tDesc5 = "Enable custom context class when using embedded tomcat When creating a context with the embedded tomcat it will always use the StandardContext and ignore the context class that was setup in the host. The attached patch will try to use the configured class from the host.";

		String Bug57431Q1 = tTitle5 + " " + tDesc5;
		String Bug57431Q2 = tTitle5;
		String Bug57431Q3 = tDesc5;

		
		
		File[] files = new File("C:\\Users\\Kunal-Kapoor\\Desktop\\tomcat methods").listFiles();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();

		LuceneAnalyzer luceneAnalyzer = new LuceneAnalyzer(analyzer, index, files);
		System.out.println("Bug1");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(Bug57715Q1, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(Bug57715Q1, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(Bug57715Q1, 20);
		System.out.println("/////////////////");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(Bug57715Q2, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(Bug57715Q2, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(Bug57715Q2, 20);
		System.out.println("/////////////////");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(Bug57715Q3, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(Bug57715Q3, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(Bug57715Q3, 20);
		
		System.out.println("/////////////////");
		System.out.println("Bug2");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(Bug58581Q1, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(Bug58581Q1, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(Bug58581Q1, 20);
		System.out.println("/////////////////");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(Bug58581Q2, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(Bug58581Q2, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(Bug58581Q2, 20);
		System.out.println("/////////////////");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(Bug58581Q3, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(Bug58581Q3, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(Bug58581Q3, 20);

		System.out.println("/////////////////");
		System.out.println("Bug3");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(Bug58053Q1, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(Bug58053Q1, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(Bug58053Q1, 20);
		System.out.println("/////////////////");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(Bug58053Q2, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(Bug58053Q2, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(Bug58053Q2, 20);
		System.out.println("/////////////////");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(Bug58053Q3, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(Bug58053Q3, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(Bug58053Q3, 20);

		System.out.println("/////////////////");
		System.out.println("Bug4");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(Bug58735Q1, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(Bug58735Q1, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(Bug58735Q1, 20);
		System.out.println("/////////////////");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(Bug58735Q2, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(Bug58735Q2, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(Bug58735Q2, 20);
		System.out.println("/////////////////");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(Bug58735Q3, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(Bug58735Q3, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(Bug58735Q3, 20);

		System.out.println("/////////////////");
		System.out.println("Bug5");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(Bug57431Q1, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(Bug57431Q1, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(Bug57431Q1, 20);
		System.out.println("/////////////////");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(Bug57431Q2, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(Bug57431Q2, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(Bug57431Q2, 20);
		System.out.println("/////////////////");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(Bug57431Q3, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(Bug57431Q3, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(Bug57431Q3, 20);
	}
}
