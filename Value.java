
public class Value {
	double scalar;
	double uncertainty;

	public Value(double s, double u){

		scalar = s;
		uncertainty = u;

	}

	public void sigfigs(){

		int exp_10 = 0;

		if(uncertainty < 1){

			double temp = uncertainty;
			while(temp < 1){

				temp *= 10;
				exp_10--;

			}

		}

		else{

			double temp = uncertainty;
			while(temp > 1){

				temp /= 10;
				exp_10++;

			}

		}

		uncertainty = (double) ((int) (.5 + (uncertainty / Math.pow(10, exp_10)))) * Math.pow(10, exp_10);
		scalar = (double) ((int) (.5 + (scalar / Math.pow(10, exp_10)))) * Math.pow(10, exp_10);


	}
	public String toString(){

		
		String ret = null;
		if(uncertainty != 0){
			sigfigs();
			ret = scalar + " +/- " + uncertainty;
		}
		else{
			ret = scalar + "";
		}
		
		
		return ret;

	}


	public Value add(Value v){

		return new Value(scalar + v.scalar,propogateAddMinUncertainty(v));


	}
	
	public Value sub(Value v){

		return new Value(scalar - v.scalar,propogateAddMinUncertainty(v));


	}

	public Value mult(Value v){
		return new Value(v.scalar * scalar, propogateMultDivUncertainty(v,v.scalar * scalar));
	}

	public Value div(Value v){
		return new Value(scalar / v.scalar, propogateMultDivUncertainty(v,scalar / v.scalar));
	}

	public Value log(double base){
		
		return new Value(Math.log(scalar)/Math.log(base), uncertainty/scalar * Math.log(Math.E)/Math.log(base));
		
	}
	
	public double propogateAddMinUncertainty(Value v){

		return Math.sqrt(Math.pow(v.uncertainty,2) + Math.pow(uncertainty, 2));

	}

	public double propogateMultDivUncertainty(Value v, double product){

		return  product * Math.sqrt(Math.pow(v.uncertainty/v.scalar,2) + Math.pow(uncertainty/scalar, 2));

	}

}
