package beans.factory.config;

import java.util.*;

public class ConstructorArgumentValues {

    private final Map<Integer, ConstructorArgumentValue> indexedArgumentValues = new HashMap<>(0);

    private final List<ConstructorArgumentValue> genericConstructorArgumentValues = new LinkedList<>();

    private final List<ConstructorArgumentValue> constructorArgumentValueList = new ArrayList<>();

    public ConstructorArgumentValues() {

    }

    public void addArgumentValue(ConstructorArgumentValue constructorArgumentValue) {
        this.constructorArgumentValueList.add(constructorArgumentValue);
    }

    private void addArgumentValue(Integer key, ConstructorArgumentValue newValue) {
        this.indexedArgumentValues.put(key, newValue);
    }

    public boolean hasIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.containsKey(index);
    }

    public ConstructorArgumentValue getIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.get(index);
    }

    public void addGenericArgumentValue(Object value, String type) {
        this.genericConstructorArgumentValues.add(new ConstructorArgumentValue(value, type));
    }

    private void addGenericArgumentValue(ConstructorArgumentValue newValue) {
        if (newValue.getName() != null) {
            // 删除掉和newValue的name一样的值
            this.genericConstructorArgumentValues.removeIf(currentValue -> newValue.getName().equals(currentValue.getName()));
        }
        // 新添加
        this.genericConstructorArgumentValues.add(newValue);
    }

    public ConstructorArgumentValue getGenericArgumentValue(String requiredName) {
        for (ConstructorArgumentValue valueHolder : this.genericConstructorArgumentValues) {
            // 根据requiredName找到对应的实例
            if (valueHolder.getName() != null && (!valueHolder.getName().equals(requiredName))) {
                continue;
            }
            return valueHolder;
        }
        return null;
    }

    public int getArgumentCount() {
        return this.genericConstructorArgumentValues.size();
    }

    public boolean isEmpty() {
        return this.genericConstructorArgumentValues.isEmpty();
    }
}