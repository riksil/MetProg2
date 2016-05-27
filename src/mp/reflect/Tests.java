package mp.reflect;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import mp.Dipendente;

import static java.lang.System.out;
import static mp.reflect.Utils.*;

/** Classe per testare metodi e classi basati sulla reflection */
public class Tests {
    public static void main(String[] args) throws ClassNotFoundException {
        //out.println(classToString("java.util.ArrayList"));
        //out.println(classToString("mp.Dipendente"));
        Dipendente d = new Dipendente("Mario Rossi");
        out.println(d);
        out.println("Size: "+ ObjSize.estimate(d));
    }
}
