package NLP;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class NLPStanford {
	/** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] */
	  public static void main(String[] args) throws IOException {
	    // set up optional output files
	    PrintWriter out;
	    if (args.length > 1) {
	      out = new PrintWriter(args[1]);
	    } else {
	      out = new PrintWriter(System.out);
	    }
	    PrintWriter xmlOut = null;
	    if (args.length > 2) {
	      xmlOut = new PrintWriter(args[2]);
	    }

	    // Create a CoreNLP pipeline. This line just builds the default pipeline.
	    // In comments we show how you can build a particular pipeline
	    // Properties props = new Properties();
	    // props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
	    // props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    // props.put("ner.applyNumericClassifiers", "false");
	    // StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    StanfordCoreNLP pipeline = new StanfordCoreNLP();

	    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
	    Annotation annotation;
	    if (args.length > 0) {
	      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
	    } else {
	      annotation = new Annotation("Kosgi Santosh sent an email to Stanford University. He didn't get a reply.");
	      //annotation = new Annotation("Abnormal weight gain in pregnancy");
	      //annotation = new Annotation("测试一下中文");
	    }

	    // run all the selected Annotators on this text
	    pipeline.annotate(annotation);

	    // print the results to file(s)
	    pipeline.prettyPrint(annotation, out);
	    if (xmlOut != null) {
	      pipeline.xmlPrint(annotation, xmlOut);
	    }

	    // Access the Annotation in code
	    // The toString() method on an Annotation just prints the text of the Annotation
	    // But you can see what is in it with other methods like toShorterString()
	    out.println();
	    out.println("The top level annotation");
	    out.println(annotation.toShorterString());

	    // An Annotation is a Map and you can get and use the various analyses individually.
	    // For instance, this gets the parse tree of the first sentence in the text.
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    if (sentences != null && ! sentences.isEmpty()) {
	      CoreMap sentence = sentences.get(0);
	      
	      out.println();
	      out.println("The first sentence is:");
	      out.println(sentence.toShorterString());
	      out.println();
	      out.println("The first sentence tokens are:");
	      for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
	        out.println(token.toShorterString());

		      String word = token.get(TextAnnotation.class);  
		      String lema = token.get(LemmaAnnotation.class);
		      System.out.println(word + "  " + lema);
	      }
	      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
	      out.println();
	      out.println("The first sentence parse tree is:");
	      tree.pennPrint(out);
	      out.println();
	      out.println("The first sentence basic dependencies are:");
	      out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
	      out.println("The first sentence collapsed, CC-processed dependencies are:");
	      SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
	      out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

	      // Access coreference. In the coreference link graph,
	      // each chain stores a set of mentions that co-refer with each other,
	      // along with a method for getting the most representative mention.
	      // Both sentence and token offsets start at 1!
	      out.println("Coreference information");
	      Map<Integer, CorefChain> corefChains =
	          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	      if (corefChains == null) { return; }
	      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
	        out.println("Chain " + entry.getKey() + " ");
	        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
	          // We need to subtract one since the indices count from 1 but the Lists start from 0
	          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
	          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
	          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
	                  ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
	        }
	      }
	    }
	    IOUtils.closeIgnoringExceptions(out);
	    IOUtils.closeIgnoringExceptions(xmlOut);
	  }
}
