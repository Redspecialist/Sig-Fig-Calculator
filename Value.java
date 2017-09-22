
public class Value {
	double scalar;
	double uncertainty;

	public Value(double s, double u){

		scalar = s;
		uncertainty = Math.abs(u);

	}
	
	public String sigFigs(){
		
		String ret = "";
		double unc = uncertainty;
		int distance = 0;
		if(uncertainty >= 1){

			while(unc >= 10){
				distance++;
				unc /= 10;
			}
			String uncert = ((uncertainty + .5 * Math.pow(10,distance))+ "").charAt(0) + "";
			for(int i = 0; i< distance; i++)
				uncert = uncert + "0";
			String val = String.format("%.1023f",((scalar + Math.signum(scalar) *.5 * Math.pow(10,distance))));
			val = val.substring(0, val.indexOf(".")-distance);
			for(int i = 0; i < distance; i++)
				val = val + "0";
			
			ret = val + " +/- " + uncert;

		}
		else if(uncertainty < 1 && uncertainty > 0){

			while(unc < 1){
				distance++;
				unc *= 10;
			}
			String uncert =  String.format("%.1023f",uncertainty+ (.5 * Math.pow(10, -distance)));
			uncert = uncert.substring(0,2 + distance);
			String val = String.format("%.1023f",((scalar + Math.signum(scalar) * .5 * Math.pow(10,-distance))));
			val = val.substring(0,val.indexOf(".") + distance+1);
			
			ret = val + " +/- " + uncert;
		}
		else{
			return (""+scalar);
		}

		return ret;
		
	}
	
	public String toString(){
		return sigFigs();
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
