package function;
/**
 * The whole point of this class is for the filterNull method, which
 * returns an empty string if the inputed string is null.
 * 
 */
public class StringFilter {
	/**
	 * This method returns an empty string if the inputed String is null.
	 * 
	 * 
	 * @param x The string that is to be tested for null.
	 * @return String Returns empty string if the object null. Otherwise
	 * it returns the original string.
	 */
	public String filterNull(String x)
	{
		return ( x == null ) ? "" : x;
	}
}
