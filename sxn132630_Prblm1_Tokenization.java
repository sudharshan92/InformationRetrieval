import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

//A program to gather information about tokens in the Cranfield database

/**
 * @author sudharshan Narasimhan Vasudevan
 * 
 */
public class sxn132630_Prblm1_Tokenization {

	// Definition of necessary data structures used in the program
	static Hashtable<String, Integer> Wordlist = new Hashtable<String, Integer>();
	static ArrayList<Map.Entry<String, Integer>> Sorted_list;
	static BufferedReader br;
	// variables for the count of documents, count of tokens, and count of
	// tokens that occur only once
	static int document_count = 0;
	static int token_count = 0;
	static int token_once = 0;

	// The function that process each file in the cranfield collection and
	// generates token from them.
	// It puts each token generated in to a hashmap along with its frequency
	public static void processfiles(File folder) throws IOException {

		for (File file : folder.listFiles()) {
			// On parsing each file we increase the document count by 1
			document_count = document_count + 1;
			String line;
			String words[];
			br = new BufferedReader(new FileReader(file));

			int wordcount = 0;
			while ((line = br.readLine()) != null) {

				// Since SGML tags are not considered words, they are replaced
				// with "". Thus we we omit SGML tags
				// Possessives "'s" are removed. if the possessive is after s
				// (i.e) if it is like humans' then the apostrophe is removed
				// all Acronyms are considered as single word. (i.e) the "." in
				// between them are removed eg U.S.A will become USA
				line = line.replaceAll("\\<[^>]*>", "").replace("'s", "")
						.replace("'", "").replace(".", "");
				// Everything other than alphabets(A-Z & a-z) are replaced with
				// space
				// Thus numbers are not considered as tokens
				// words with dashes are considered as two different words.
				String sentence = line.replaceAll("[^a-zA-Z]+", " ");
				// The algorithm is case insensitive (i.e) All words are changed
				// into lower case
				sentence = sentence.toLowerCase();
				// The words are split based on space
				words = sentence.split(" ");

				for (String word : words) {

					// For each word, we check if it is already present in the
					// hash table
					if (!word.equals("")) {
						if (!Wordlist.containsKey(word)) {

							// if not we insert the word into the hash table
							// with key as the words and frequency as 1
							Wordlist.put(word, 1);

						} else {
							// If present, we retrieve the frequency of the
							// word, add 1 to it and then again re-insert it
							// back into the hash table
							wordcount = Wordlist.get(word) + 1;
							Wordlist.remove(word);
							Wordlist.put(word, wordcount);
						}
					}
				}

			}

		}

	}

	public static int tokencount(Hashtable<String, Integer> Wordlist) {

		// The function counts the total number of tokens in the documents and
		// the total number of tokens that occur only once in the document
		for (Integer val : Wordlist.values()) {
			token_count = token_count + val;
			if (val == 1) {
				token_once = token_once + 1;
			}
		}
		return token_count;

	}

	// Function to sort the hash table in descending order of the token
	// frequency
	public static ArrayList<Map.Entry<String, Integer>> Sort() {
		ArrayList<Map.Entry<String, Integer>> l = new ArrayList<Map.Entry<String, Integer>>(
				Wordlist.entrySet());
		Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return l;
	}

	public static void main(String args[]) throws IOException {

		// input is the path to the cranfield collection
		File folder = new File(args[0]);

		long start = System.currentTimeMillis();
		processfiles(folder);

		// storing the total count of tokens in totaltokens.
		int totaltokens = tokencount(Wordlist);

		System.out.println("Total number of tokens in the collection is "
				+ totaltokens);
		System.out.println();

		System.out
				.println("Total number of distinct tokens in the collection is "
						+ Wordlist.size());
		System.out.println();

		System.out
				.println("Number of tokens that occur only once in the collection is "
						+ token_once);
		System.out.println();

		Sorted_list = Sort();

		System.out
				.println("30 most frequent words in the collection and their frequencies are: ");
		System.out.println();
		for (int i = 0; i < 30; i++) {
			System.out.println(Sorted_list.get(i));
		}

		System.out.println();
		System.out.println("Average number of tokens per document = "
				+ (totaltokens / document_count));
		System.out.println();

		long last = System.currentTimeMillis();

		System.out.println("Time taken = " + (last - start) + " msec");

	}

}
