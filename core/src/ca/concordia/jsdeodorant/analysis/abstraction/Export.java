package ca.concordia.jsdeodorant.analysis.abstraction;

import ca.concordia.jsdeodorant.analysis.decomposition.AbstractExpression;

public class Export {
	private String name;
	private AbstractExpression rValue;

	public Export(String name, AbstractExpression rValue) {
		this.name = name;
		this.rValue = rValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractExpression getRValue() {
		return rValue;
	}

	public void setrValue(AbstractExpression rValue) {
		this.rValue = rValue;
	}
	
	public boolean  equals(Object o){
		if(!(o instanceof Export)){
			return false;
		}else{
			if(o.hashCode()==this.hashCode()){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	public int hashCode(){
		return this.rValue.getExpression().hashCode();
	}
}
