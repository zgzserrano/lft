public class ejer13
{
    public static boolean scan(String s)
    {
	int state = 0;
	int i = 0;

	while (state >= 0 && i < s.length()) {
	    final char ch = s.charAt(i++);

	    switch (state) {
	    case 0:
		if (Character.isDigit(ch) && (ch%2==0))
		    state = 2;
		else if (Character.isDigit(ch) && (ch%2!=0))
		    state = 3;
		else
		    state = -1;
		break;

	    case 2:
		if (Character.isLetter(ch) && (ch >= 'A' && ch <= 'K'))
		    state = 4;
		else if (Character.isDigit(ch) && (ch%2==0))
		    state = 2;
		else if (Character.isDigit(ch) && (ch%2!=0))
		    state = 3;

		else
		    state = -1;
		break;

	   
	     case 3:
		if (Character.isLetter(ch) && (ch >= 'L' && ch <= 'Z'))
		    state = 4;
		else if (Character.isDigit(ch) && (ch%2==0))
		    state = 2;
		else if (Character.isDigit(ch) && (ch%2!=0))
		    state = 3;

		else
		    state = -1;
		break;

	    
 	   case 4:
		if (Character.isLetter(ch))
		    state = 4;
		else
		    state = -1;
		break;

 
		}

	}
	return state == 4;
    }

    public static void main(String[] args)
    {
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}