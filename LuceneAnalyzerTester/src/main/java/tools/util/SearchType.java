package tools.util;

public class SearchType {
	
	public enum Type {
		regular,
		fuzzy,
		phrase,
		range,
		prefix
	}
	
	/**
	 * Please, handle with care. The order of messages should match the order in type enumeration 
	 */
	private static final String[] MESSAGES = {
		"Regular",
		"Fuzzy",
		"Phrase",
		"Range",
		"Prefix"
	};
	
	public static String getMessage(Type type){
		return MESSAGES[type.ordinal()];
	}
	
	public static Type getType(String type){
		for(int i = 0; i < MESSAGES.length; i++){
			if(MESSAGES[i].equalsIgnoreCase(type)){
				return Type.values()[i];
			}
		}
		return null;
	}
	
	public static String[] getMessages(){
		return MESSAGES;
	}
	
	public static final Type REGULAR = Type.regular;
	public static final Type FUZZY = Type.fuzzy;
	public static final Type PHRASE = Type.phrase;
	public static final Type RANGE = Type.range;
	public static final Type PREFIX = Type.prefix;

}
