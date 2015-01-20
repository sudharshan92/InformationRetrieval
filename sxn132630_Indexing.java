import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
//import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

class StanfordLemmatizer {

    protected StanfordCoreNLP pipeline;

    public StanfordLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);
    }

    public String lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas.get(0);
        //return lemmas;
    }
}


class ValueComparator implements Comparator<String> {

	Map<String, Integer> map;

	public ValueComparator(Map<String, Integer> base) {
		this.map = base;
	}

	public int compare(String a, String b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}

class NewString 
{
	public String str;

	NewString() 
	{
		str = "";
	}
}

class Stemmer 
{
	private String Clean( String str ) 
	{
		int last = str.length();
  
		Character ch = new Character( str.charAt(0) );
		String temp = "";

		for ( int i=0; i < last; i++ ) 
		{
			if ( ch.isLetterOrDigit( str.charAt(i) ) )
				temp += str.charAt(i);
		}

		return temp;
	} //clean

	private boolean hasSuffix( String word, String suffix, NewString stem ) 
	{

		String tmp = "";

		if ( word.length() <= suffix.length() )
			return false;
		if (suffix.length() > 1) 
			if ( word.charAt( word.length()-2 ) != suffix.charAt( suffix.length()-2 ) )
				return false;

		stem.str = "";
		
		for ( int i=0; i<word.length()-suffix.length(); i++ )
			stem.str += word.charAt( i );
			tmp = stem.str;

		for ( int i=0; i<suffix.length(); i++ )
			tmp += suffix.charAt( i );

		if ( tmp.compareTo( word ) == 0 )
			return true;
		else
			return false;
	}

	private boolean vowel( char ch, char prev ) 
	{
		switch ( ch ) 
		{
			case 'a': case 'e': case 'i': case 'o': case 'u': 
				return true;
			case 'y': 
			{

				switch ( prev ) 
				{
					case 'a': case 'e': case 'i': case 'o': case 'u': 
						return false;

					default: 
						return true;
				}
			}
			
			default : 
				return false;
		}
	}

	private int measure( String stem ) 
	{
 
		int i=0, count = 0;
		int length = stem.length();

		while ( i < length ) 
		{
			for ( ; i < length ; i++ ) 
			{
				if ( i > 0 ) 
				{
					if ( vowel(stem.charAt(i),stem.charAt(i-1)) )
						break;
				}
				else 
				{  
					if ( vowel(stem.charAt(i),'a') )
						break; 
				}
			}

			for ( i++ ; i < length ; i++ ) 
			{
				if ( i > 0 ) 
				{
					if ( !vowel(stem.charAt(i),stem.charAt(i-1)) )
						break;
				}
				else 
				{  
					if ( !vowel(stem.charAt(i),'?') )
						break;
				}
			} 
			if ( i < length ) 
			{
				count++;
				i++;
			}
		} //while
 
		return(count);
	}

	private boolean containsVowel( String word ) 
	{
		for (int i=0 ; i < word.length(); i++ )
			if ( i > 0 ) 
			{
				if ( vowel(word.charAt(i),word.charAt(i-1)) )
					return true;
			}
			else 
			{  
				if ( vowel(word.charAt(0),'a') )
					return true;
			}
     
		return false;
	}

	private boolean cvc( String str ) 
	{
		int length=str.length();

		if ( length < 3 )
			return false;
 
		if ( (!vowel(str.charAt(length-1),str.charAt(length-2)) )
				&& (str.charAt(length-1) != 'w') && (str.charAt(length-1) != 'x') && (str.charAt(length-1) != 'y')
				&& (vowel(str.charAt(length-2),str.charAt(length-3))) ) 
		{

			if (length == 3) 
			{
				if (!vowel(str.charAt(0),'?')) 
					return true;
				else
					return false;
			}
			else 
			{
				if (!vowel(str.charAt(length-3),str.charAt(length-4)) ) 
					return true; 
				else
					return false;
			} 
		}   

		return false;
	}

	private String step1( String str ) 
	{

		NewString stem = new NewString();

		if ( str.charAt( str.length()-1 ) == 's' ) 
		{
			if ( (hasSuffix( str, "sses", stem )) || (hasSuffix( str, "ies", stem)) )
			{
				String tmp = "";
				for (int i=0; i<str.length()-2; i++)
					tmp += str.charAt(i);
					str = tmp;
			}
			else
			{
				if ( ( str.length() == 1 ) && ( str.charAt(str.length()-1) == 's' ) ) 
				{
					str = "";
					return str;
				}
				if ( str.charAt( str.length()-2 ) != 's' ) 
				{
					String tmp = "";
					for (int i=0; i<str.length()-1; i++)
						tmp += str.charAt(i);
						str = tmp;
				}
			}  
		}

		if ( hasSuffix( str,"eed",stem ) ) 
		{
			if ( measure( stem.str ) > 0 ) 
			{
				String tmp = "";
				for (int i=0; i<str.length()-1; i++)
					tmp += str.charAt( i );
					str = tmp;
			}
		}
		else 
		{  
			if (  (hasSuffix( str,"ed",stem )) || (hasSuffix( str,"ing",stem )) ) 
			{ 
				if (containsVowel( stem.str ))  
				{

					String tmp = "";
					for ( int i = 0; i < stem.str.length(); i++)
						tmp += str.charAt( i );
						str = tmp;
						if ( str.length() == 1 )
							return str;

						if ( ( hasSuffix( str,"at",stem) ) || ( hasSuffix( str,"bl",stem ) ) || ( hasSuffix( str,"iz",stem) ) ) {
							str += "e";
        
						}
						else
						{   
							int length = str.length(); 
							if ( (str.charAt(length-1) == str.charAt(length-2)) 
							&& (str.charAt(length-1) != 'l') && (str.charAt(length-1) != 's') && (str.charAt(length-1) != 'z') ) 
							{
								
								tmp = "";
								for (int i=0; i<str.length()-1; i++)
									tmp += str.charAt(i);
									str = tmp;
							}
							else
								if ( measure( str ) == 1 ) 
								{
									if ( cvc(str) ) 
										str += "e";
								}
						}
				}
			}
		}

		if ( hasSuffix(str,"y",stem) ) 
			if ( containsVowel( stem.str ) ) 
			{
				String tmp = "";
				for (int i=0; i<str.length()-1; i++ )
					tmp += str.charAt(i);
					str = tmp + "i";
			}
		return str;  
	}

	private String step2( String str ) 
	{

		String[][] suffixes = { { "ational", "ate" },
                             { "tional",  "tion" },
                             { "enci",    "ence" },
                             { "anci",    "ance" },
                             { "izer",    "ize" },
                             { "iser",    "ize" },
                             { "abli",    "able" },
                             { "alli",    "al" },
                             { "entli",   "ent" },
                             { "eli",     "e" },
                             { "ousli",   "ous" },
                             { "ization", "ize" },
                             { "isation", "ize" },
                             { "ation",   "ate" },
                             { "ator",    "ate" },
                             { "alism",   "al" },
                             { "iveness", "ive" },
                             { "fulness", "ful" },
                             { "ousness", "ous" },
                             { "aliti",   "al" },
                             { "iviti",   "ive" },
                             { "biliti",  "ble" }};
		NewString stem = new NewString();

  
		for ( int index = 0 ; index < suffixes.length; index++ ) 
		{
			if ( hasSuffix ( str, suffixes[index][0], stem ) ) 
			{
				if ( measure ( stem.str ) > 0 ) 
				{
					str = stem.str + suffixes[index][1];
					return str;
				}
    	 	}
		}

		return str;
	}

	private String step3( String str ) 
	{

			String[][] suffixes = { { "icate", "ic" },
                                 { "ative", "" },
                                 { "alize", "al" },
                                 { "alise", "al" },
                                 { "iciti", "ic" },
                                 { "ical",  "ic" },
                                 { "ful",   "" },
                                 { "ness",  "" }};
			NewString stem = new NewString();

			for ( int index = 0 ; index<suffixes.length; index++ ) 
			{
				if ( hasSuffix ( str, suffixes[index][0], stem ))
					if ( measure ( stem.str ) > 0 ) 
					{
						str = stem.str + suffixes[index][1];
						return str;
					}
			}
			return str;
	}

	private String step4( String str ) 
	{
     
		String[] suffixes = { "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "sion", "tion",
                        "ou", "ism", "ate", "iti", "ous", "ive", "ize", "ise"};
  
		NewString stem = new NewString();
     
		for ( int index = 0 ; index<suffixes.length; index++ ) 
		{
			if ( hasSuffix ( str, suffixes[index], stem ) ) 
			{
        
				if ( measure ( stem.str ) > 1 ) 
				{
					str = stem.str;
					return str;
				}
			}
		}
		return str;
	}

	private String step5( String str ) 
	{

		if ( str.charAt(str.length()-1) == 'e' ) 
		{ 
			if ( measure(str) > 1 ) 
			{/* measure(str)==measure(stem) if ends in vowel */
				String tmp = "";
				for ( int i=0; i<str.length()-1; i++ ) 
					tmp += str.charAt( i );
					str = tmp;
			}
			else
				if ( measure(str) == 1 ) 
				{
					String stem = "";
					for ( int i=0; i<str.length()-1; i++ ) 
						stem += str.charAt( i );

					if ( !cvc(stem) )
						str = stem;
				}
		}
  
		if ( str.length() == 1 )
			return str;
		if ( (str.charAt(str.length()-1) == 'l') && (str.charAt(str.length()-2) == 'l') && (measure(str) > 1) )
		{
			if ( measure(str) > 1 ) 
			{/* measure(str)==measure(stem) if ends in vowel */
				String tmp = "";
				for ( int i=0; i<str.length()-1; i++ ) 
					tmp += str.charAt( i );
					str = tmp;
			}
		}
		return str;
	}

	private String stripPrefixes ( String str) 
	{

		String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra", "mega", "nano", "pico", "pseudo"};

		int last = prefixes.length;
		for ( int i=0 ; i<last; i++ ) 
		{
			if ( str.startsWith( prefixes[i] ) ) 
			{
				String temp = "";
				for ( int j=0 ; j< str.length()-prefixes[i].length(); j++ )
					temp += str.charAt( j+prefixes[i].length() );
				return temp;
			}
		}
  
		return str;
	}


	private String stripSuffixes( String str ) 
	{

		str = step1( str );
		if ( str.length() >= 1 )
			str = step2( str );
		if ( str.length() >= 1 )
			str = step3( str );
		if ( str.length() >= 1 )
			str = step4( str );
		if ( str.length() >= 1 )
			str = step5( str );

		return str; 
	}


	public String stripAffixes( String str ) 
	{

		str = str.toLowerCase();
		str = Clean(str);

		if (( str != "" ) && (str.length() > 2)) 
		{
			str = stripPrefixes(str);

			if (str != "" ) 
				str = stripSuffixes(str);

		}   

		return str;
 } //stripAffixes

} //class



public class sxn132630_Indexing {

	static List<String> stopwords = new ArrayList<String>();
	static int document_count = 0;
	static BufferedReader br;
	//static int index1count = 0;
	//static int index2count = 0;
	static Map<String, TreeMap<String, Integer>> index2uncompPost = new TreeMap<String, TreeMap<String, Integer>>();
	@SuppressWarnings("rawtypes")
	static Map<String, List> index2uncompDict = new TreeMap<String, List>();
	static Map<String, String> mostfreq = new TreeMap<String, String>();
	static Map<String, ArrayList<String>> index2compPost = new TreeMap<String, ArrayList<String>>();
	static Map<String, ArrayList<String>> index2compDict = new TreeMap<String, ArrayList<String>>();

	static Map<String, TreeMap<String, Integer>> index1uncompPost = new TreeMap<String, TreeMap<String, Integer>>();
	@SuppressWarnings("rawtypes")
	static Map<String, List> index1uncompDict = new TreeMap<String, List>();
	static Map<String, ArrayList<String>> index1compPost = new TreeMap<String, ArrayList<String>>();
	static Map<String, ArrayList<String>> index1compDict = new TreeMap<String, ArrayList<String>>();
	static TreeMap<String, TreeMap<String, Integer>> word_occurances = new TreeMap<String, TreeMap<String, Integer>>();

	public static void index1uncompressedPosting(File folder)
			throws IOException {
		// this is your print stream, store the reference
		PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
		

		// now make all writes to the System.err stream silent

		for (File file : folder.listFiles()) {
			// document_count = document_count + 1;
			String line;
			String words[];
			StanfordLemmatizer slem = new StanfordLemmatizer();
			br = new BufferedReader(new FileReader(file));
			// int wordcount = 0;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\<[^>]*>", "").replace("'s", "")
						.replace("'", "").replace(".", "");
				String sentence = line.replaceAll("[^a-zA-Z]+", " ");
				sentence = sentence.toLowerCase();
				words = sentence.split(" ");
				for (String word : words) {
					if (!word.equals("")) {
						if (!stopwords.contains(word)) {
							String lemmaword = slem.lemmatize(word);
							if (!index1uncompPost.containsKey(lemmaword)) {
								TreeMap<String, Integer> doclist = new TreeMap<String, Integer>();
								doclist.put(file.getName(), 1);
								// Set<String> doclist = new HashSet<String>();
								// doclist.add();
								index1uncompPost.put(lemmaword, doclist);
							} else {
								TreeMap<String, Integer> doclist = index1uncompPost
										.get(lemmaword);
								if (!doclist.containsKey(file.getName())) {
									doclist.put(file.getName(), 1);
								} else {
									int tf = doclist.get(file.getName());
									tf = tf + 1;
									doclist.remove(file.getName());
									doclist.put(file.getName(), tf);
								}

								index1uncompPost.remove(lemmaword);
								index1uncompPost.put(lemmaword, doclist);
							}

						}
					}
				}
			}

		}
		// set everything bck to its original state afterwards
		System.setErr(err);

	}

	public static void index2uncompressedPosting(File folder)
			throws IOException {

		for (File file : folder.listFiles()) {
			document_count = document_count + 1;
			String line;
			String words[];
			Stemmer stem = new Stemmer();
			br = new BufferedReader(new FileReader(file));
			// int wordcount = 0;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\<[^>]*>", "").replace("'s", "")
						.replace("'", "").replace(".", "");
				String sentence = line.replaceAll("[^a-zA-Z]+", " ");
				sentence = sentence.toLowerCase();
				words = sentence.split(" ");
				for (String word : words) {
					if (!word.equals("")) {
						if (!stopwords.contains(word)) {
							String stemmedword = stem.stripAffixes(word);
							if (!index2uncompPost.containsKey(stemmedword)) {
								TreeMap<String, Integer> doclist = new TreeMap<String, Integer>();
								doclist.put(file.getName(), 1);
								// Set<String> doclist = new HashSet<String>();
								// doclist.add();
								index2uncompPost.put(stemmedword, doclist);
							} else {
								TreeMap<String, Integer> doclist = index2uncompPost
										.get(stemmedword);
								if (!doclist.containsKey(file.getName())) {
									doclist.put(file.getName(), 1);
								} else {
									int tf = doclist.get(file.getName());
									tf = tf + 1;
									doclist.remove(file.getName());
									doclist.put(file.getName(), tf);
								}

								index2uncompPost.remove(stemmedword);
								index2uncompPost.put(stemmedword, doclist);
							}

						}
					}
				}
			}

		}

	}

	public static void index1uncompressedDictionary() throws IOException {

		Set<String> keys = index1uncompPost.keySet();
		for (String key : keys) {
			TreeMap<String, Integer> temp = index1uncompPost.get(key);
			int docfreq = temp.size();
			int termfreq = 0;
			for (Map.Entry<String, Integer> entry : temp.entrySet()) {
				termfreq = termfreq + entry.getValue();
			}
			List<Integer> val = new ArrayList<Integer>();
			val.add(termfreq);
			val.add(docfreq);
			index1uncompDict.put(key, val);
		}
	}

	public static void index2uncompressedDictionary() throws IOException {

		Set<String> keys = index2uncompPost.keySet();
		for (String key : keys) {
			TreeMap<String, Integer> temp = index2uncompPost.get(key);
			int docfreq = temp.size();
			int termfreq = 0;
			for (Map.Entry<String, Integer> entry : temp.entrySet()) {
				termfreq = termfreq + entry.getValue();
			}
			List<Integer> val = new ArrayList<Integer>();
			val.add(termfreq);
			val.add(docfreq);
			index2uncompDict.put(key, val);
		}
	}

	public static void index1compressedPostings() {
		for (String key : index1uncompPost.keySet()) {

			TreeMap<String, Integer> temp = index1uncompPost.get(key);
			// TreeMap<String,String> temp2 = new TreeMap<String,String>();
			ArrayList<String> temp2 = new ArrayList<String>();
			int docfreq = temp.size();
			String docid;
			int termfreq;
			String deltaresult;
			String gammaresult;
			if (docfreq == 1) {
				docid = temp.firstKey();
				termfreq = temp.get(docid);
				docid = docid.replaceAll("[a-zA-Z]", "").replace(".", "");
				deltaresult = delta(Integer.parseInt(docid));
				gammaresult = gamma(termfreq);
				temp2.add(deltaresult.concat("  ").concat(gammaresult));
				index1compPost.put(key, temp2);

			} else {

				ArrayList<String> a = new ArrayList<String>();
				for (String tempkey : temp.keySet()) {
					a.add(tempkey);
				}
				docid = temp.firstKey();
				termfreq = temp.get(docid);
				docid = docid.replaceAll("[a-zA-Z]", "").replace(".", "");
				deltaresult = delta(Integer.parseInt(docid));
				gammaresult = gamma(termfreq);
				temp2.add(deltaresult.concat("  ").concat(gammaresult));

				for (int i = 1; i < a.size(); i++) {

					String prevdocid = a.get(i - 1);
					docid = a.get(i);
					deltaresult = delta(docDistance(prevdocid, docid));
					termfreq = temp.get(docid);
					gammaresult = gamma(termfreq);
					temp2.add(deltaresult.concat("  ").concat(gammaresult));
				}
				index1compPost.put(key, temp2);

			}

		}
	}

	public static void index2compressedPostings() {
		for (String key : index2uncompPost.keySet()) {

			TreeMap<String, Integer> temp = index2uncompPost.get(key);
			// TreeMap<String,String> temp2 = new TreeMap<String,String>();
			ArrayList<String> temp2 = new ArrayList<String>();
			int docfreq = temp.size();
			String docid;
			int termfreq;
			String deltaresult;
			String gammaresult;
			if (docfreq == 1) {
				docid = temp.firstKey();
				termfreq = temp.get(docid);
				docid = docid.replaceAll("[a-zA-Z]", "").replace(".", "");
				deltaresult = delta(Integer.parseInt(docid));
				gammaresult = gamma(termfreq);
				temp2.add(deltaresult.concat("  ").concat(gammaresult));
				index2compPost.put(key, temp2);

			} else {

				ArrayList<String> a = new ArrayList<String>();
				for (String tempkey : temp.keySet()) {
					a.add(tempkey);
				}
				docid = temp.firstKey();
				termfreq = temp.get(docid);
				docid = docid.replaceAll("[a-zA-Z]", "").replace(".", "");
				deltaresult = delta(Integer.parseInt(docid));
				gammaresult = gamma(termfreq);
				temp2.add(deltaresult.concat("  ").concat(gammaresult));

				for (int i = 1; i < a.size(); i++) {

					String prevdocid = a.get(i - 1);
					docid = a.get(i);
					deltaresult = delta(docDistance(prevdocid, docid));
					termfreq = temp.get(docid);
					gammaresult = gamma(termfreq);
					temp2.add(deltaresult.concat("  ").concat(gammaresult));
				}
				index2compPost.put(key, temp2);

			}

		}
	}

	public static void index1compressedDictionary() {
		for (String key : index1uncompDict.keySet()) {
			@SuppressWarnings("unchecked")
			List<Integer> val = index1uncompDict.get(key);
			String termgamma;
			String docgamma;
			ArrayList<String> finalval = new ArrayList<String>();
			termgamma = gamma(val.get(0));
			docgamma = gamma(val.get(1));
			finalval.add(termgamma);
			finalval.add(docgamma);
			index1compDict.put(key, finalval);

		}

	}

	public static void index2compressedDictionary() {
		for (String key : index2uncompDict.keySet()) {
			@SuppressWarnings("unchecked")
			List<Integer> val = index2uncompDict.get(key);
			String termgamma;
			String docgamma;
			ArrayList<String> finalval = new ArrayList<String>();
			termgamma = gamma(val.get(0));
			docgamma = gamma(val.get(1));
			finalval.add(termgamma);
			finalval.add(docgamma);
			index2compDict.put(key, finalval);

		}

	}

	/*
	 * Gets the difference between two documents
	 */
	public static int docDistance(String doc1, String doc2) {
		String d1 = doc1.replaceAll("[a-zA-Z]", "").replace(".", "");
		String d2 = doc2.replaceAll("[a-zA-Z]", "").replace(".", "");
		return Integer.parseInt(d2) - Integer.parseInt(d1);
	}

	/*
	 * Function to generate gamma code.
	 */
	public static String gamma(int number) {
		if (number == 1) {
			return "0";
		}

		else {
			String gamma;
			String binary = Integer.toBinaryString(number);
			String substr = binary.substring(1);
			int len = substr.length();
			String temp = "";

			for (int i = 0; i < len; i++) {
				temp += "1";
			}

			temp += "0";
			gamma = temp + substr;

			return gamma;
		}
	}

	/*
	 * Function to generate Delta code.
	 */
	public static String delta(int number) {
		if (number == 1) {
			return "0";
		}

		else {
			String binary = Integer.toBinaryString(number);
			int len = binary.length();
			String substr = binary.substring(1);
			String DtoG = gamma(len);
			String delta = DtoG + substr;

			return delta;
		}
	}

	public static TreeMap<String, Integer> SortByValue(
			TreeMap<String, Integer> map) {
		ValueComparator vc = new ValueComparator(map);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}

	public static void mostfreqstem(File folder) throws IOException {

		for (File file : folder.listFiles()) {
			// document_count = document_count + 1;
			String line;
			String words[];
			Stemmer stem = new Stemmer();
			br = new BufferedReader(new FileReader(file));
			int wordcount = 0;
			TreeMap<String, Integer> Wordlist = new TreeMap<String, Integer>();
			while ((line = br.readLine()) != null) {

				line = line.replaceAll("\\<[^>]*>", "").replace("'s", "")
						.replace("'", "").replace(".", "");
				String sentence = line.replaceAll("[^a-zA-Z]+", " ");
				sentence = sentence.toLowerCase();
				words = sentence.split(" ");
				for (String word : words) {
					if (!word.equals("")) {
						if (!stopwords.contains(word)) {
							String stemmedword = stem.stripAffixes(word);
							if (!Wordlist.containsKey(stemmedword)) {

								// if not we insert the word into the hash table
								// with key as the words and frequency as 1
								Wordlist.put(stemmedword, 1);

							} else {
								// If present, we retrieve the frequency of the
								// word, add 1 to it and then again re-insert it
								// back into the hash table
								wordcount = Wordlist.get(stemmedword) + 1;
								Wordlist.remove(stemmedword);
								Wordlist.put(stemmedword, wordcount);
							}
						}
					}
				}
			}
			TreeMap<String, Integer> sortedMap = SortByValue(Wordlist);
			// System.out.println(sortedMap);
			String k = sortedMap.firstEntry().toString();
			mostfreq.put(file.getName(), k);

		}
	}

	public static void numofWordsIncStopWords(File folder) throws IOException {
		String line;
		String words[];
		for (File file : folder.listFiles()) {
			// int count = 0;
			TreeMap<String, Integer> tmap = new TreeMap<String, Integer>();
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\\<[^>]*>", "").replace("'s", "")
						.replace("'", "").replace(".", "");
				String sentence = line.replaceAll("[^a-zA-Z]+", " ");
				sentence = sentence.toLowerCase();
				words = sentence.split(" ");
				int countWord = 0;
				for (String word : words) {
					if (!tmap.containsKey(word)) {
						if (!word.equals(""))
							// first occurrence of this word
							tmap.put(word, 1);
					} else {
						countWord = tmap.get(word) + 1; // Get current count and
														// increment
						tmap.put(word, countWord); // Now put it back with new
													// value
					}
				}
				word_occurances.put(file.getName(), tmap);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		float start_time = System.nanoTime();
		// TODO Auto-generated method stub
		// Takes the folder location as an argument from the user.
		final File folder = new File(args[0]);
		
		String stopsentence = "a all an and any are as be been but by few for have he her here him his how i in is it its many me my none of on or our she some the their them there they that this us was what when where which who why will with you your";
		String words[];
		words = stopsentence.split(" ");
		for (String word : words) {
			if (!word.equals(""))
				stopwords.add(word);
		}

		//final File results = new File(args[1]);
		//results.mkdir();
		index2uncompressedPosting(folder);
		index2uncompressedDictionary();
		index2compressedPostings();
		index2compressedDictionary();
		index1uncompressedPosting(folder);
		index1uncompressedDictionary();
		index1compressedPostings();
		index1compressedDictionary();
		mostfreqstem(folder);
		numofWordsIncStopWords(folder);

		// FileOutputStream fileOs = new FileOutputStream(
		// "./Results/testuncomp.uncompress");
		// ObjectOutputStream os = new ObjectOutputStream(fileOs);
		PrintWriter writer = new PrintWriter(
				args[1].concat("index2_UncompressedDictionary.uncompress"), "UTF-8");

		for (String key : index2uncompDict.keySet()) {
			// os.writeBytes(key);
			writer.println(key);

			List<Integer> l = index2uncompDict.get(key);
			for (Integer listelmt : l) {
				// os.writeByte((listelmt));
				writer.println(listelmt);
			}

		}

		FileOutputStream abcd = new FileOutputStream(
				args[1].concat("index2_CompressedDictionary.compress"));
		ObjectOutputStream pqrs = new ObjectOutputStream(abcd);
		// PrintWriter writer1 = new PrintWriter("./Results/index2compDict.txt",
		// "UTF-8");
		for (String key : index2compDict.keySet()) {
			pqrs.writeBytes(key);
			// writer1.println(key);

			ArrayList<String> l = index2compDict.get(key);
			for (String listelmt : l) {
				// writer1.println(listelmt);
				// System.out.println(listelmt);
				if (listelmt.length() > 8) {
					pqrs.writeByte(Integer.parseInt(listelmt.substring(0, 8), 2));
					if ((listelmt.length() - listelmt.substring(8).length()) > 8) {
						pqrs.writeByte(Integer.parseInt(
								listelmt.substring(8, 16), 2));

						pqrs.writeByte(Integer.parseInt(listelmt.substring(16),
								2));

					} else {
						pqrs.writeByte(Integer.parseInt(listelmt.substring(8),
								2));
					}

				} else {
					pqrs.writeByte(Integer.parseInt(listelmt, 2));
				}
				// pqrs.write(System.getProperty("line.separator").getBytes());
			}
			// pqrs.write(System.getProperty("line.separator").getBytes());
		}
		pqrs.flush();
		pqrs.close();

		writer.close();
		writer = new PrintWriter(
				args[1].concat("index1_UncompressedDictionary.uncompress"), "UTF-8");

		for (String key : index1uncompDict.keySet()) {
			// os.writeBytes(key);
			writer.println(key);

			List<Integer> l = index1uncompDict.get(key);
			for (Integer listelmt : l) {
				// os.writeByte((listelmt));
				writer.println(listelmt);
			}

		}

		abcd = new FileOutputStream(
				args[1].concat("index1_CompressedDictionary.compress"));
		pqrs = new ObjectOutputStream(abcd);
		// PrintWriter writer1 = new PrintWriter("./Results/index2compDict.txt",
		// "UTF-8");
		for (String key : index1compDict.keySet()) {
			pqrs.writeBytes(key);
			// writer1.println(key);

			ArrayList<String> l = index1compDict.get(key);
			for (String listelmt : l) {
				// writer1.println(listelmt);
				// System.out.println(listelmt);
				if (listelmt.length() > 8) {
					pqrs.writeByte(Integer.parseInt(listelmt.substring(0, 8), 2));
					if ((listelmt.length() - listelmt.substring(8).length()) > 8) {
						pqrs.writeByte(Integer.parseInt(
								listelmt.substring(8, 16), 2));

						pqrs.writeByte(Integer.parseInt(listelmt.substring(16),
								2));

					} else {
						pqrs.writeByte(Integer.parseInt(listelmt.substring(8),
								2));
					}

				} else {
					pqrs.writeByte(Integer.parseInt(listelmt, 2));
				}
				// pqrs.write(System.getProperty("line.separator").getBytes());
			}
			// pqrs.write(System.getProperty("line.separator").getBytes());
		}
		pqrs.flush();
		pqrs.close();
		writer.close();
		writer = new PrintWriter(
				args[1].concat("index2_UncompressedPosting.uncompress"), "UTF-8");

		for (String key : index2uncompPost.keySet()) {

			writer.println(key);

			TreeMap<String, Integer> l = index2uncompPost.get(key);
			for (Map.Entry<String, Integer> entry : l.entrySet()) {
				String k = entry.getKey();
				Integer value = entry.getValue();
				// os.writeByte((listelmt));
				writer.println(k);
				writer.println(value);

			}
			writer.println();

		}

		abcd = new FileOutputStream(
				args[1].concat("index2_CompressedPosting.compress"));
		pqrs = new ObjectOutputStream(abcd);
		// PrintWriter writer1 = new PrintWriter("./Results/index2compDict.txt",
		// "UTF-8");
		for (String key : index2compPost.keySet()) {
			pqrs.writeBytes(key);
			// writer1.println(key);

			ArrayList<String> l = index2compPost.get(key);
			for (String listelmt : l) {
				listelmt = listelmt.replace(" ", "");
				// writer1.println(listelmt);
				// System.out.println(listelmt);
				if (listelmt.length() > 8) {
					pqrs.writeByte(Integer.parseInt(listelmt.substring(0, 8), 2));
					if ((listelmt.length() - listelmt.substring(8).length()) > 8) {
						pqrs.writeByte(Integer.parseInt(
								listelmt.substring(8, 16), 2));

						pqrs.writeByte(Integer.parseInt(listelmt.substring(16),
								2));

					} else {
						pqrs.writeByte(Integer.parseInt(listelmt.substring(8),
								2));
					}

				} else {
					pqrs.writeByte(Integer.parseInt(listelmt, 2));
				}
				// pqrs.write(System.getProperty("line.separator").getBytes());
			}
			pqrs.write(System.getProperty("line.separator").getBytes());
		}
		pqrs.flush();
		pqrs.close();
		writer.close();

		writer = new PrintWriter(
				args[1].concat("index1_UncompressedPosting.uncompress"), "UTF-8");

		for (String key : index1uncompPost.keySet()) {

			writer.println(key);

			TreeMap<String, Integer> l = index1uncompPost.get(key);
			for (Map.Entry<String, Integer> entry : l.entrySet()) {
				String k = entry.getKey();
				Integer value = entry.getValue();
				// os.writeByte((listelmt));
				writer.println(k);
				writer.println(value);

			}
			writer.println();

		}

		abcd = new FileOutputStream(
				args[1].concat("index1_CompressedPosting.compress"));
		pqrs = new ObjectOutputStream(abcd);
		// PrintWriter writer1 = new PrintWriter("./Results/index2compDict.txt",
		// "UTF-8");

		for (String key : index1compPost.keySet()) {
			pqrs.writeBytes(key);
			// writer1.println(key);

			ArrayList<String> l = index1compPost.get(key);
			for (String listelmt : l) {
				listelmt = listelmt.replace(" ", "");
				// writer1.println(listelmt);
				// System.out.println(listelmt);
				if (listelmt.length() > 8) {
					pqrs.writeByte(Integer.parseInt(listelmt.substring(0, 8), 2));
					if ((listelmt.length() - listelmt.substring(8).length()) > 8) {
						pqrs.writeByte(Integer.parseInt(
								listelmt.substring(8, 16), 2));

						pqrs.writeByte(Integer.parseInt(listelmt.substring(16),
								2));

					} else {
						pqrs.writeByte(Integer.parseInt(listelmt.substring(8),
								2));
					}

				} else {
					pqrs.writeByte(Integer.parseInt(listelmt, 2));
				}
				// pqrs.write(System.getProperty("line.separator").getBytes());
			}
			pqrs.write(System.getProperty("line.separator").getBytes());
		}
		pqrs.flush();
		pqrs.close();
		writer.close();
		writer = new PrintWriter(args[1].concat("mostfreq.txt"), "UTF-8");
		writer.println(mostfreq);
		writer.close();
		writer = new PrintWriter(args[1].concat("wordoccurance.txt"), "UTF-8");
		writer.println(word_occurances);
		writer.close();
		
		float end_time = System.nanoTime(); // The ending time of the program.
		System.out.println("Time Taken to Build Index: "
				+ (end_time - start_time) / 1000000000 + " sec");
		System.out.println("Size of Index1_UncompressedDictionary: "+((new File(args[1].concat("index1_UncompressedDictionary.uncompress")))
				.length())+ " bytes");
		System.out.println("Size of Index1_UncompressedPosting: "+((new File(args[1].concat("index1_UncompressedPosting.uncompress")))
		.length())+ " bytes");
		System.out.println("Size of Index1_CompressedDictionary: "+((new File(args[1].concat("index1_CompressedDictionary.compress")))
		.length())+ " bytes");
		System.out.println("Size of Index1_CompressedPosting: "+((new File(args[1].concat("index1_CompressedPosting.compress")))
		.length())+ " bytes");
		System.out.println("Size of Index2_UncompressedDictionary: "+((new File(args[1].concat("index2_UncompressedDictionary.uncompress")))
		.length())+ " bytes");
		System.out.println("Size of Index2_UncompressedPosting: "+((new File(args[1].concat("index2_UncompressedPosting.uncompress")))
		.length())+ " bytes");
		System.out.println("Size of Index2_CompressedDictionary: "+((new File(args[1].concat("index2_CompressedDictionary.compress")))
		.length())+ " bytes");
		System.out.println("Size of Index2_CompressedPosting: "+((new File(args[1].concat("index2_CompressedPosting.compress")))
		.length())+ " bytes");
		
		System.out.println("Number of Inverted Lists in Index1: "+ index1uncompDict.size());
		System.out.println("Number of Inverted Lists in Index2: "+ index2uncompDict.size());
		
		Stemmer stem = new Stemmer();
		String stemmedword = stem.stripAffixes("reynolds");
		//System.out.println(stemmedword);
		System.out.println("Reynolds");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
		System.out.println("NASA");
		stemmedword = stem.stripAffixes("nasa");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
		System.out.println("Prandtl");
		stemmedword = stem.stripAffixes("prandtl");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
		System.out.println("flow");
		stemmedword = stem.stripAffixes("flow");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
		System.out.println("pressure");
		stemmedword = stem.stripAffixes("pressure");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
		System.out.println("boundary");
		stemmedword = stem.stripAffixes("boundary");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
		System.out.println("shock");
		stemmedword = stem.stripAffixes("shock");
		System.out.println("Document Frequency: "+index2uncompDict.get(stemmedword).get(1)+ " Term Frequency: "+index2uncompDict.get(stemmedword).get(0));
	}

}
