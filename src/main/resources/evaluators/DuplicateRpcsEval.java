import java.util.*;

/**
 * The evaluator for the Duplicate RPCs question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main Muromuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class DuplicateRpcsEval {

    // Start caller code implementation.
    public static final class KVListClient<K, V> {

        private final KVListService<K, V> serviceProxy;

        public KVListClient(KVListService<K, V> serviceProxy) {
            this.serviceProxy = serviceProxy;
        }

        public List<V> get(K key) {
            return serviceProxy.get(key);
        }

        public void append(K key, V value) {
            serviceProxy.append(key, value);
        }

        public void replaceAll(K key, Iterable<V> values) {
            serviceProxy.replaceAll(key, values);
        }

        public void delete(K key) {
            serviceProxy.delete(key);
        }
    }
    // End caller code implementation.

    // Start main definition implementation.
    public interface KVListService<K, V> {
        List<V> get(K key);
        void append(K key, V value);
        void replaceAll(K key, Iterable<V> values);
        void delete(K key);
    }

    public static final class KVListServiceImpl<K, V>
            implements KVListService<K, V> {

        private final KVListDb<K, V> db;

        public KVListServiceImpl(KVListDb<K, V> db) {
            this.db = db;
        }

        @Override
        public List<V> get(K key) {
            return db.get(key);
        }

        @Override
        public void append(K key, V value) {
            db.append(key, value);
        }

        @Override
        public void replaceAll(K key, Iterable<V> values) {
            db.replaceAll(key, values);
        }

        @Override
        public void delete(K key) {
            db.delete(key);
        }
    }
    // End main definition implementation.

    public static final class KVListDb<K, V> {

        private final Map<K, List<V>> listMap;

        private KVListDb() {
            listMap = new HashMap<>();
        }

        // For those who need a brand new separate key/value db to use.
        public static <K, V> KVListDb<K, V> createDb() {
            return new KVListDb<>();
        }

        // Returns null if no list exists for the given key.
        public List<V> get(K key) {
            return listMap.get(key);
        }

        // Creates a new internal list first if none exists for the given key.
        public void append(K key, V value) {
            if (!listMap.containsKey(key)) {
                listMap.put(key, new ArrayList<>());
            }
            List<V> list = listMap.get(key);
            list.add(value);
        }

        // Creates a new internal list and populates it with all the
        // elements from the given iterable into the list, for the given key.
        public void replaceAll(K key, Iterable<V> values) {
            List<V> list = new ArrayList<>();
            for (V value : values) {
                list.add(value);
            }
            listMap.put(key, list);
        }

        // Trying to delete a non-existing key will be a no-op.
        public void delete(K key) {
            listMap.remove(key);
        }
    }

    public void runTest01() {
        KVListDb<Integer, String> database =
                KVListDb.<Integer, String>createDb();
        KVListService<Integer, String> service =
                new KVListServiceImpl<>(database);
        KVListClient1<Integer, String> client =
                new KVListClient1<>(service);

        if (client.get(15) != null) {
            System.out.println(
                    "Test 1 failed. Get should have returned null for nonexisting key.");
            return;
        }
        client.append(15, "testword1");
        client.append(15, "testword2");
        client.append(25, "testword3");
        if (!areListsEqual(
                List.of("testword1", "testword2"),
                client.get(15))) {
            System.out.println(
                    "Test 1 failed. Append failed for key 15.");
            return;
        }
        client.replaceAll(
                15,
                List.of("etc", "etcetc", "etcetcetc"));
        if (!areListsEqual(
                List.of("etc", "etcetc", "etcetcetc"),
                client.get(15))) {
            System.out.println(
                    "Test 1 failed. Replace failed for key 15.");
            return;
        }
        client.delete(15);
        if (client.get(15) != null) {
            System.out.println(
                    "Test 1 failed. Delete failed for key 15.");
            return;
        }
        System.out.println("Test 1 passed.");
    }

    public void runTest02() {
        KVListDb<Integer, String> database =
                KVListDb.<Integer, String>createDb();
        KVListService<Integer, String> service =
                new KVListServiceImpl<>(database);
        KVListClient2<Integer, String> client =
                new KVListClient2<>(service);

        if (client.get(15) != null) {
            System.out.println(
                    "Test 2 failed. Get should have returned null for nonexisting key.");
            return;
        }
        client.append(15, "testword1");
        client.append(15, "testword2");
        client.append(25, "testword3");
        if (!areListsEqual(
                List.of("testword1", "testword2"),
                client.get(15))) {
            System.out.println(
                    "Test 2 failed. Append failed for key 15 when duplicated twice.");
            return;
        }
        client.replaceAll(
                15,
                List.of("etc", "etcetc", "etcetcetc"));
        if (!areListsEqual(
                List.of("etc", "etcetc", "etcetcetc"),
                client.get(15))) {
            System.out.println(
                    "Test 2 failed. Replace failed for key 15 when duplicated twice.");
            return;
        }
        client.delete(15);
        if (client.get(15) != null) {
            System.out.println(
                    "Test 2 failed. Delete failed for key 15 when duplicated twice.");
            return;
        }
        System.out.println("Test 2 passed.");
    }

    public void runTest03() {
        KVListDb<Integer, String> database =
                KVListDb.<Integer, String>createDb();
        KVListService<Integer, String> service =
                new KVListServiceImpl<>(database);
        KVListClient3<Integer, String> client =
                new KVListClient3<>(service);

        if (client.get(15) != null) {
            System.out.println(
                    "Test 3 failed. Get should have returned null for nonexisting key.");
            return;
        }
        client.append(15, "testword1");
        client.append(15, "testword2");
        client.append(25, "testword3");
        if (!areListsEqual(
                List.of("testword1", "testword2"),
                client.get(15))) {
            System.out.println(
                    "Test 3 failed. Append failed for key 15 when duplicated three times.");
            return;
        }
        client.replaceAll(
                15,
                List.of("etc", "etcetc", "etcetcetc"));
        if (!areListsEqual(
                List.of("etc", "etcetc", "etcetcetc"),
                client.get(15))) {
            System.out.println(
                    "Test 3 failed. Replace failed for key 15 when duplicated three times.");
            return;
        }
        client.delete(15);
        if (client.get(15) != null) {
            System.out.println(
                    "Test 3 failed. Delete failed for key 15 when duplicated three times.");
            return;
        }
        System.out.println("Test 3 passed.");
    }

    private static <V> boolean areListsEqual(
            List<V> list1, List<V> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        DuplicateRpcsEval eval = new DuplicateRpcsEval();
        eval.runTest01();
        eval.runTest02();
        eval.runTest03();
    }
}
