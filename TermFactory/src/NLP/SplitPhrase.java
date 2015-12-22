package NLP;

import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

public class SplitPhrase {
	public static void main(String[] args){
		String str = "注射用甲强龙琥玻酸钠500毫克粉瓶的解决方案";
		System.out.println(ToAnalysis.parse(str));
		System.out.println(BaseAnalysis.parse(str));
		System.out.println(NlpAnalysis.parse(str));
	}
}
