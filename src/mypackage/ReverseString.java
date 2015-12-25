package mypackage;

	
import java.util.*;

public class ReverseString {
    public String reverseString(String iniString) {
        // write code here
        StringBuilder sb = new StringBuilder(iniString);
        int len = sb.length();
        System.out.println(len);
        char tem;
        for(int i=0;i<len/2;i++){
        	tem = sb.charAt(i);
        	sb.setCharAt(i, sb.charAt(len-i-1));
        	sb.setCharAt(len-i-1, tem);
        }
        return sb.toString();
    }
    
    public static void main(String args[]){
        ReverseString re = new ReverseString();
        String str = re.reverseString("hello  fu");
        System.out.println(str);
    }
}