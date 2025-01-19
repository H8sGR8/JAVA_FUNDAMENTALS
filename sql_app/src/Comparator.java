import java.util.Objects;

abstract class Comparator {

     abstract boolean compare(Object a, Object b);
}

class IsEqual extends Comparator {

    boolean compare(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a == null || b == null) return false;
        return Objects.equals(a.toString(), b.toString());
    }
}

class IsNotEqual extends Comparator {

    boolean compare(Object a, Object b) {
        return !(new IsEqual()).compare(a, b);
    }
}

class IsGreater extends Comparator {


    boolean compare(Object a, Object b) {
        if(a == null || b == null) throw new NullPointerException();
        return Double.parseDouble(a.toString()) > Double.parseDouble(b.toString());
    }
}

class IsGreaterOrEqual extends Comparator {

    boolean compare(Object a, Object b) {
        if(a == null || b == null) throw new NullPointerException();
        return Double.parseDouble(a.toString()) >= Double.parseDouble(b.toString());
    }
}

class IsLessOrEqual extends Comparator {

    boolean compare(Object a, Object b) {
        if(a == null || b == null) throw new NullPointerException();
        return Double.parseDouble(a.toString()) <= Double.parseDouble(b.toString());
    }
}

class IsLess extends Comparator {

    boolean compare(Object a, Object b) {
        if(a == null || b == null) throw new NullPointerException();
        return Double.parseDouble(a.toString()) < Double.parseDouble(b.toString());
    }
}
