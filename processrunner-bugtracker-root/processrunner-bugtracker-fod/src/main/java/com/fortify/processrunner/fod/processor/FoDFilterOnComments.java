package com.fortify.processrunner.fod.processor;

import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONArray;
import org.springframework.expression.Expression;

import com.fortify.processrunner.context.Context;
import com.fortify.processrunner.processor.AbstractProcessor;
import com.fortify.processrunner.processor.IProcessor;
import com.fortify.util.spring.SpringExpressionUtil;
import com.fortify.util.spring.expression.TemplateExpression;

/**
 * This {@link IProcessor} implementation will filter vulnerabilities based on
 * FoD comments. Depending on the excludePattern flag, vulnerabilities for which 
 * a comment matches the configured filterPattern will either be excluded (flag set
 * to true) or included (flag set to false, default).
 */
public class FoDFilterOnComments extends AbstractProcessor {
	private static final Expression EXPR_COMMENTS = SpringExpressionUtil.parseSimpleExpression("FoDCurrentVulnerability.summary.comments");
	private static final Expression EXPR_COMMENT = SpringExpressionUtil.parseSimpleExpression("comment");
	private TemplateExpression filterPatternTemplateExpression = null;
	private boolean excludePattern = false;
	
	@Override
	protected boolean process(Context context) {
		String filterPatternString = SpringExpressionUtil.evaluateExpression(context, getFilterPatternTemplateExpression(), String.class);
		Pattern filterPattern = Pattern.compile(filterPatternString);
		JSONArray array = SpringExpressionUtil.evaluateExpression(context, EXPR_COMMENTS, JSONArray.class);
		if ( array != null ) {
			for ( int i = 0 ; i < array.length() ; i++ ) {
				String expressionValue = SpringExpressionUtil.evaluateExpression(array.opt(i), EXPR_COMMENT, String.class);
				if ( filterPattern.matcher(expressionValue).matches() ) {
					return !excludePattern;
				}
			}
		}
		return excludePattern;
	}

	

	public boolean isExcludePattern() {
		return excludePattern;
	}

	public void setExcludePattern(boolean excludePattern) {
		this.excludePattern = excludePattern;
	}

	public TemplateExpression getFilterPatternTemplateExpression() {
		return filterPatternTemplateExpression;
	}

	public void setFilterPatternTemplateExpression(TemplateExpression filterPatternTemplateExpression) {
		this.filterPatternTemplateExpression = filterPatternTemplateExpression;
	}
	
	public void setFilterPatternTemplateExpression(String filterPatternTemplateExpression) {
		this.filterPatternTemplateExpression = SpringExpressionUtil.parseTemplateExpression(filterPatternTemplateExpression);
	}
	
	
}
