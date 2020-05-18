package spotifyPackage.Utilities;

import java.io.File;
import java.util.Comparator;

public class ChunkComparator implements Comparator<File>
{
    @Override
    public int compare(File f1, File f2)
    {
        return getNum(f1.getName()).compareTo(getNum(f2.getName()));
    }

    private Integer getNum(String s){
        String ret = "";
        for (int c = s.lastIndexOf('.')-1; c >= 0; c--){
            if (Character.isDigit(s.charAt(c)))
                ret += s.charAt(c);
            else break;
        }
        return Integer.parseInt(reverse(ret));
    }

    private String reverse(String s){
        String ret = "";
        for (int c = s.length()-1; c >= 0; c--){
            ret += s.charAt(c);
        }
        return ret;
    }
}
