package mp.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/** Metodi di utilità basati sulla reflection */
public class Utils {
    public static String toString(Object x) {
        if (x == null) return ""+x;
        String s = "";
        Class<?> c = x.getClass();
        s += c.getSimpleName();
        Field[] ff = c.getDeclaredFields();
        if (ff.length > 0) {
            s += "[";
            for (int i = 0 ; i < ff.length ; i++) {
                ff[i].setAccessible(true);
                try {
                    s += (i == 0 ? "" : ",") + ff[i].getName() + "=" + ff[i].get(x);
                } catch (IllegalAccessException e) {}
            }
            s += "]";
        }
        return s;
    }

    /** Equivalente a {@link Utils#classToString(Class, String)
     * classToString(Class.forName(cName),"")}.
     * @param cName  il nome completo di una classe/interfaccia
     * @return una stringa con le intestazioni di tutti i membri della classe
     * @throws ClassNotFoundException se la classe non esiste */
    public static String classToString(String cName) throws ClassNotFoundException {
        return classToString(Class.forName(cName), "");
    }

    /** Ritorna una stringa con le intestazioni di tutti i membri della classe
     * (o interfaccia) specificata.
     * @param c  una classe o interfaccia
     * @param indent  l'indentazione iniziale delle linee della stringa
     * @return una stringa con le intestazioni di tutti i membri della classe */
    public static String classToString(Class<?> c, String indent) {
        String s = "";
        String ind = indent+"    ";
        try {
            s += indent + classToStr(c)+"\n";      // Intestazione della classe
            for (Class<?> ec : c.getDeclaredClasses())       // Classi annidate
                s += classToString(ec, ind);
            for (Constructor<?> co : c.getDeclaredConstructors()) //Costruttori
                s += ind + executableToStr(co)+" {\n"; // Intestazione costruttore
            s += "\n";
            for (Method m : c.getDeclaredMethods())               // Metodi
                s += ind + executableToStr(m)+"\n";    // Intestazione metodo
            s += "\n";
            for (Field f : c.getDeclaredFields())                 // Campi
                s += ind + fieldToStr(f)+"\n";         // Intestazione campo
            s += indent+"}\n";
        } catch(Exception e) { s += "\nERROR: "+e; }
        return s;
    }

    public static String classToStr(Class<?> c) {
        String s = annMods("", c.getDeclaredAnnotations(), c.getModifiers());
        s += (s.isEmpty() ? "" : " ") + c.getSimpleName();
        s += typeParametersToStr("", c.getTypeParameters());
        Type sc = c.getGenericSuperclass();
        if (sc != null && sc != Object.class) s += " extends "+typeToStr(sc);
        Type[] tt = c.getGenericInterfaces();
        if (tt.length > 0) {
            s += " implements";
            for (Type t : tt)  s += " "+typeToStr(t);
        }
        return s;
    }

    public static String executableToStr(Executable exe) {
        String s = exe.isSynthetic() ? "SYNTHETIC": "";
        s = annMods(s, exe.getDeclaredAnnotations(), exe.getModifiers());
        s += typeParametersToStr(" ", exe.getTypeParameters());
        if (exe instanceof Method)
            s += (s.isEmpty() ? "" : " ") + typeToStr(((Method)exe).getGenericReturnType());
        s += (s.isEmpty() ? "" : " ") + simple(exe.getName())+"(";
        Parameter[] pp = exe.getParameters();
        for (int i = 0 ; i < pp.length ; i++)
            s += (i == 0 ? "" : ", ")+parameterToStr(pp[i]);
        return s+")";
    }

    public static String fieldToStr(Field f) {
        String s = f.isSynthetic() ? "SYNTHETIC": "";
        s = annMods(s, f.getDeclaredAnnotations(), f.getModifiers());
        s += (s.isEmpty() ? "" : " ")+typeToStr(f.getGenericType());
        return s + " "+ f.getName();
    }

    private static String parameterToStr(Parameter p) {
        String s = annMods("", p.getDeclaredAnnotations(), p.getModifiers());
        s += (s.isEmpty() ? "" : " ") + typeToStr(p.getParameterizedType());
        return s + " " + p.getName();
    }

    private static String annMods(String s, Annotation[] aa, int mod) {
        for (Annotation a : aa)
            s += (s.isEmpty() ? "" : " ")+a;
        String mods = Modifier.toString(mod);
        if (!mods.isEmpty()) s += (s.isEmpty() ? "" : " ") + mods;
        return s;
    }

    private static String typeParametersToStr(String pre, TypeVariable<?>[] tp) {
        String s = "";
        if (tp.length > 0) {
            s += pre + "<";
            for (int i = 0 ; i < tp.length ; i++)
                s += (i == 0 ? "" : ",")+tp[i].toString();
            s += ">";
        }
        return s;
    }

    private static String simple(String s) {
        return s.replaceAll("\\$",".").replaceAll("[^<]*\\.", "");
    }

    private static String typeToStr(Type t) { return simple(t.toString()); }
}
