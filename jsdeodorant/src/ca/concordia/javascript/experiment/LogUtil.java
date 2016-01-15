package ca.concordia.javascript.experiment;

import java.util.List;

import ca.concordia.javascript.analysis.decomposition.AbstractExpression;
import ca.concordia.javascript.analysis.util.IdentifierHelper;

public class LogUtil {
	public static String getParametersName(List<AbstractExpression> expressions) {
		int parameterSize = expressions.size();
		if (parameterSize > 0) {
			StringBuilder parameters = new StringBuilder();
			int parameterIndex = 0;
			for (AbstractExpression parameter : expressions) {
				parameters.append(IdentifierHelper.getIdentifier(parameter.getExpression()).toString().replace(",", "-"));
				if (parameterIndex < parameterSize - 1)
					parameters.append("|");
				parameterIndex++;
			}
			return parameters.toString();
		}
		return "";
	}
}
