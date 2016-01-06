package model;

import java.util.List;
import java.util.Map;

public class CategoryInfo {
	private String category_name = null;
	private List<SourceInfo> source = null;
	private Map<String, Double> stem_zh_priority = null;
	private Map<String, Double> stem_en_priority = null;
}
