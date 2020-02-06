import java.lang.reflect.Field;
import java.util.*;

public class HT {
    public static void main(String[] args) throws Exception {
        // you can modify the initial capacity
        Hashtable<ValueHolder, Object> ht = new Hashtable<>(4);
        // you can modify the range of i
        for (int i = 1; i <= 4; i++) {
            ht.put(new ValueHolder(i), new Object());
            // show structure of the "table" field in Hashtable 
            showTableStructure(ht);
        }
    }

    private static void showTableStructure(Hashtable<ValueHolder, Object> ht) throws Exception {
        Field field = ht.getClass().getDeclaredField("table");
        field.setAccessible(true);
        Object[] table = (Object[]) field.get(ht);

        // show table length
        System.out.println(String.format("table.length: %s", table.length));

        // show structure of each bucket
        for (int index = 0; index < table.length; index++) {
            Object item = table[index];

            // empty bucket
            if (item == null) {
                showBucket(index, null);
                continue;
            }

            // "current" refers to a "Hashtable.Entry" instance
            Object current = item;
            List<ValueHolder> valuesInOneBucket = new LinkedList<>();
            while (current != null) {
                Field key = current.getClass().getDeclaredField("key");
                key.setAccessible(true);
                Object value = key.get(current);

                valuesInOneBucket.add((ValueHolder) value);

                Field next = current.getClass().getDeclaredField("next");
                next.setAccessible(true);
                current = next.get(current);
            }
            showBucket(index, valuesInOneBucket);
        }
        System.out.println();
    }

    private static void showBucket(int bucketIndex, List<ValueHolder> valuesInOneBucket) {
        if (valuesInOneBucket == null) {
            System.out.println(String.format("[%s]: null", bucketIndex));
            return;
        }

        StringJoiner joiner = new StringJoiner(" -> ");
        for (ValueHolder valueHolder : valuesInOneBucket) {
            joiner.add(valueHolder.toString());
        }
        System.out.println(String.format("[%s]: %s", bucketIndex, joiner.toString()));
    }
}

/**
 * "value 持有者", 这个类中只有一个名为 value 的字段, 这个类的 hashCode() 方法始终返回 0
 * A holder for value. 
 * There is only one field(i.e. "value") in this class.
 * The hashCode() of this class always returns 0, and all instances of this class will be mapped to table[0] if you use them as keys in Hashtable.
 */
class ValueHolder {
    private int value;

    ValueHolder(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueHolder that = (ValueHolder) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "VH{" +
                "value=" + value +
                '}';
    }
}


