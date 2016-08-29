import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

	Ast expr;
	Map<String, Value> vars;
	int index;

	public Calculator(String expression, Map<String,Value> varlist){

		vars = new HashMap<String,Value>();
		vars.put("a", new Value(9.0,.1));
		vars.put("b", new Value(2,1));
		vars.put("c", new Value(5.0,.2));
		vars.put("d", new Value(3,3));

	}


	public void printAST(Ast e){

		if(match(e.opp, new log(1))){

			System.out.print(e.opp + "(");
			printAST(e.left);
			System.out.print(")");


		}
		else if(match(e.opp, new var(""))){

			System.out.print(e.opp);

		}
		else{

			System.out.print("(");
			printAST(e.left);
			System.out.print(e.opp);
			printAST(e.right);
			System.out.print(")");
		}


	}

	class Ast{

		Ast left;
		Ast right;
		Token opp;

	}

	public Ast parseS(ArrayList<Token> tokens){

		Ast expr = new Ast();
		expr.left = parseE(tokens);
	
		if(index < tokens.size() && match(tokens.get(index), new add())){

			expr.opp = new add();
			index++;
			expr.right = parseS(tokens);

		}
		else if(index < tokens.size() && match(tokens.get(index), new sub())){

			expr.opp = new sub();
			index++;
			expr.right = parseS(tokens);

		}
		else{
			expr = expr.left;
		}

		return expr;
	}

	public Ast parseE(ArrayList<Token> tokens){

		Ast expr = new Ast();

		expr.left = parseT(tokens);
		if(index < tokens.size() && match(tokens.get(index), new div())){
			
			expr.opp = new div();
			index++;
			expr.right = parseE(tokens);
		}
		else if(index < tokens.size() && match(tokens.get(index), new mult())){
			
			expr.opp = new mult();
			index++;
			expr.right = parseE(tokens);

		}
		else{

			expr = expr.left;
		}

		return expr;
	}

	public Ast parseT(ArrayList<Token> tokens){
		Ast expr = null;
		if(match(tokens.get(index),new log(1))){
			expr = new Ast();
			expr.opp = tokens.get(index);
			index++;
			if(match(tokens.get(index), new open_paren())){
				index++;
				expr.left = parseS(tokens);
				if(!match(tokens.get(index),new end_paren()));//TODO parse error
				index++;
			}
			else{
				//TODO parsing error
			}
		}
		else if(match(tokens.get(index),new open_paren())){
			
			index++;
			expr = parseS(tokens);

			if(!match(tokens.get(index),new end_paren())){
				//TODO add parse tree error
			}
			index++;
		}
		else if(match(tokens.get(index), new var(""))){
			expr = new Ast();
			expr.opp = tokens.get(index);
			index++;

		}

		return expr;
	}

	public boolean match(Token t, Token target){

		return t.getClass().equals(target.getClass());

	}

	public ArrayList<Token> tokenize(String expression){

		ArrayList<Token> tknList = new ArrayList<Token>();
		expression = expression.replaceAll(" ", "");
		Pattern logMatch = Pattern.compile("^(log|ln)([0-9/.]*)$");
		for(int i = 0; i < expression.length(); i++){

			String tok = "";

			while(i < expression.length() && Pattern.matches("[^+-/*()]", "" + expression.charAt(i))){
				tok += expression.charAt(i++);
			}

			Matcher m = logMatch.matcher(tok);

			if(m.matches()){
				double base = 10;
				if(m.group(1).equals("ln")){

					base = Math.E;

				}
				else{

					if(!m.group(2).equals("")){

						base = Double.parseDouble(m.group(2));

					}
					//TODO catch combined natural log + specified base
				}
				tknList.add(new log(base));
			}
			else if(!tok.equals("")){

				tknList.add(new var(tok));

			}

			if(i < expression.length()){

				char c = expression.charAt(i);

				Token t = null;

				if(c == '+'){
					t = new add();
				}
				else if(c == '-'){
					t = new sub();
				}
				else if(c == '('){
					t = new open_paren();
				}
				else if(c == ')'){
					t = new end_paren();
				}
				else if(c == '/'){
					t = new div();
				}
				else if(c == '*'){
					t = new mult();
				}
				else{
					//TODO error
				}
				tknList.add(t);
			}


		}

		return tknList;
	}

	public static void main(String [] args){


		Calculator temp = new Calculator("hi", null);


		System.out.println(temp.calS(temp.parseS(temp.tokenize("log(a+log(b/(c-d))) / d"))));

	}

	public Value calS(Ast a){
		Value ret = null;
		if(match(a.opp,new sub())){
			ret = calE(a.left).sub(calS(a.right));
		}
		else if(match(a.opp, new add())){
			ret = calE(a.left).add(calS(a.right));
		}
		else{
			ret = calE(a);
		}
		return ret;
	}

	public Value calE(Ast a){

		Value ret = null;
		if(match(a.opp,new mult())){
			ret = calE(a.left).mult(calS(a.right));
		}
		else if(match(a.opp, new div())){
			ret = calE(a.left).div(calS(a.right));
		}
		else{
			ret = calT(a);
		}

		return ret;
	}

	public Value calT(Ast a){
		Value ret = null;
		if(match(a.opp,new log(1))){
			ret = calS(a.left).log(((log)a.opp).base);
		}
		else if(match(a.opp,new var(""))){
			ret = vars.get(((var)a.opp).varName);
		}
		
		if(ret == null){
			//TODO fail
		}
		return ret;
	}




	class Token{ public String toString(){return this.getClass().getName().substring(this.getClass().getName().indexOf('$') + 1);}}

	class add extends Token{public String toString(){return "+";}}
	class sub extends Token{public String toString(){return "-";}}
	class div extends Token{public String toString(){return "/";}}
	class mult extends Token{public String toString(){return "*";}}
	class log extends Token{
		double base; 
		public log(double b){base = b;}
		public String toString(){return "log" + base;}
	}
	class end_paren extends Token{}
	class open_paren extends Token{}
	class var extends Token{
		String varName;
		public var(String v){varName = v;}	
		public String toString(){return varName;}
	}

}
