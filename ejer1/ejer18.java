public class ejer18
{
    public static boolean scan(String s)
    {
	int state = 0;
	int i = 0;

	while (state >= 0 && i < s.length()) {
	    final char ch = s.charAt(i++);

	    switch (state) {
	    case 0:
		if (ch == 'j')
		    state = 1;
		else
		    state = 6;
		break;

	    case 1:
		if (ch == 'o')
		    state = 2;
		else
		    state = 11;
		break;

	    case 2:
		if (ch == 'r')
		    state = 3;
		else
		    state = 15;
		break;

	    case 3:
		if (ch == 'g')
		    state = 4;
		else
		    state = 18;
		break;

		case 4:
			state = 5;
			break;
	    case 5:
			state = -1;
			break;

	    case 6:
		if (ch == 'o')
		    state = 7;
		else
		    state = -1;
		break;
		

		case 7:
		if (ch == 'r')
		    state = 8;
		else
		    state = -1;
		break;
		

		case 8:
		if (ch == 'g')
		    state = 9;
		else
		    state = -1;
		break;
		

		case 9:
		if (ch == 'e')
		    state = 10;
		else
		    state = -1;
		break;
		

		case 11:
		if (ch == 'r')
		    state = 12;
		else
		    state = -1;
		break;
		

		case 12:
		if (ch == 'g')
		    state = 13;
		else
		    state = -1;
		break;
		

		case 13:
		if (ch == 'e')
		    state =14;
		else
		    state = -1;
		break;
		

		case 15:
		if (ch == 'g')
		    state = 16;
		else
		    state = -1;
		break;
		
	    
		case 16:
		if (ch == 'e')
		    state = 17;
		else
		    state = -1;
		break;
		
	    
		case 18:
		if (ch == 'e')
		    state = 19;
		else
		    state = -1;
		break;
	    	}
	}
	return (state == 5 || state == 6 || state == 20 || state == 19|| state == 17 || state == 14 || state == 10);
    }

    public static void main(String[] args)
    {
	System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}