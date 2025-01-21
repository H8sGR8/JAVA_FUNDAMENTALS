class Pair<K, V>{
    K key;
    V value;
    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K getFirst() {
        return key;
    }

    V getSecond() {
        return value;
    }
}

public class AppSQL {

    public static void main(String[] args) throws Exception {
        SyntaxReader reader = new SyntaxReader();
        reader.analiseInput();
    }
}

/*
-------++------++
       || auto ||
-------++------++
 */