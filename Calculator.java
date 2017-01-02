import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

	private Ast expr;
	private Map<String, Value> vars;
	private int index;
	private boolean varsLoaded;

	//sets the variables
	public void setVars(Map <String, Value> v){
		vars = new HashMap<String, Value>(v);
		varsLoaded = true;
	}

	//resets function
	public void setFunction(String expression){
		vars = new HashMap<String,Value>();
		expr = null;
		index = 0;
		expr = parseS(tokenize(expression));
		varsLoaded = false;
		
		
	}
	
	//calculates a value from the loaded ast
	public Value calculate(){

		if(varsLoaded)
			return calS(expr);
		else{
			return null;
		}
	}

	//method that prints the Abstract Syntax tree that represents the current expression
	public void printAST(Ast e){

		//checks for a log operation
		if(match(e.opp, new log(1))){

			System.out.print(e.opp + "(");
			printAST(e.left);
			System.out.print(")");


		}
		//checks to see if the value is a variable (Leaf of AST) 
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

	//Abstract Syntax tree represenation of the opperation
	class Ast{

		Ast left;
		Ast right;
		Token opp;

	}

	//S -> E + S | E - S | E
	private Ast parseS(ArrayList<Token> tokens){

		Ast expr = new Ast();
		expr.left = parseE(tokens);

		//addition
		if(index < tokens.size() && match(tokens.get(index), new add())){

			expr.opp = new add();
			index++;
			expr.right = parseS(tokens);

		}
		//subtraction
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

	//E -> T / E | T * E | T
	private Ast parseE(ArrayList<Token> tokens){

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

	//T -> (S) | log(S) | Var
	private Ast parseT(ArrayList<Token> tokens){
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

	//checks to see if the two classes match, used for interpreting the AST
	private boolean match(Token t, Token target){

		return t.getClass().equals(target.getClass());

	}

	//converts an expression into a series of tokens based on operations
	private ArrayList<Token> tokenize(String expression){

		ArrayList<Token> tknList = new ArrayList<Token>();
		expression = expression.replaceAll(" ", "");
		Pattern logMatch = Pattern.compile("^(log|ln)([0-9/.]*)?$");

		//parses each character to build a list of tokens
		for(int i = 0; i < expression.length(); ){
			String tok = "" + expression.charAt(i);

			//checks to see if current character is a token by itself
			if(Pattern.matches("[^+*-/()]", "" + expression.charAt(i))){
				//attempts to read a variable in character by character until another token is interpreted.
				while(++i < expression.length() && Pattern.matches("[^+*-/()]", "" + expression.charAt(i))){
					tok += expression.charAt(i);
				}
			}
			else{
				i++;
			}
			Matcher m = logMatch.matcher(tok);
			//reacts to log being the targeted token
			if(m.matches()){
				double base = 10;

				if(m.group(1).equals("ln")){
					base = Math.E;
					if(m.groupCount() == 2){
						//TODO throw error
					}
				}
				else{
					if(!m.group(2).equals("")){
						base = Double.parseDouble(m.group(2));
					}
				}
				tknList.add(new log(base));
			}
			else{

				char c = tok.charAt(0);

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
					t = new var(tok);
					vars.put(tok, null);
				}

				tknList.add(t);
			}


		}

		return tknList;
	}

	//Calculates based on CFG S -> E + S | E - S | E
	private Value calS(Ast a){
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

	//Calculates based on CFG E -> T + E | T - E | T
	private Value calE(Ast a){

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

	//Calculates based on CFG T -> log of S | Var
	private Value calT(Ast a){
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

	public Set<String> getVariables(){

		return vars.keySet();

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
