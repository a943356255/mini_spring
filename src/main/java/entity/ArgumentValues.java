package entity;

import java.util.*;

public class ArgumentValues {

    private final Map<Integer, ArgumentValue> indexedArgumentValues = new HashMap<>(0);

    private final List<ArgumentValue> genericArgumentValues = new LinkedList<>();

    public ArgumentValues() {

    }

    private void addArgumentValue(Integer key, ArgumentValue newValue) {
        this.indexedArgumentValues.put(key, newValue);
    }

    public boolean hasIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.containsKey(index);
    }

    public ArgumentValue getIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.get(index);
    }

    public void addGenericArgumentValue(Object value, String type) {
        this.genericArgumentValues.add(new ArgumentValue(value, type));
    }

    private void addGenericArgumentValue(ArgumentValue newValue) {
        if (newValue.getName() != null) {
            // 删除掉和newValue的name一样的值
            this.genericArgumentValues.removeIf(currentValue -> newValue.getName().equals(currentValue.getName()));
        }
        // 新添加
        this.genericArgumentValues.add(newValue);
    }

    public ArgumentValue getGenericArgumentValue(String requiredName) {
        for (ArgumentValue valueHolder : this.genericArgumentValues) {
            // 根据requiredName找到对应的实例
            if (valueHolder.getName() != null && (!valueHolder.getName().equals(requiredName))) {
                continue;
            }
            return valueHolder;
        }
        return null;
    }

    public int getArgumentCount() {
        return this.genericArgumentValues.size();
    }

    public boolean isEmpty() {
        return this.genericArgumentValues.isEmpty();
    }
}