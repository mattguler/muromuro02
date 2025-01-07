import java.util.*;

/**
 * The initial solution for the DuplicateRpcs question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * used by the server.
 */
public class DuplicateRpcsSoln {
    // Start initial caller code implementation.
    public static final class KVListClient<K, V> {

        private final KVListService<K, V> serviceProxy;

        public KVListClient(KVListService<K, V> serviceProxy) {
            this.serviceProxy = serviceProxy;
        }

        public List<V> get(K key) {
            List<V> result = serviceProxy.get(key);
            return result;
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
    // End initial caller code implementation.

    // Start initial main definition implementation.
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
    // End initial main definition implementation.

    // To help this code compile and not give any errors in the IDE,
    // the following class definition has been provided.
    public static final class KVListDb<K, V> {

        // For those who need a brand new separate key/value db to use.
        public static <K, V> KVListDb<K, V> createDb() {
            return new KVListDb<>();
        }

        // Returns null if no list exists for the given key.
        public List<V> get(K key) {
            return List.of();
        }

        // Creates a new internal list first if none exists for the given key.
        public void append(K key, V value) {
            //
        }

        // Creates a new internal list and populates it with all the
        // elements from the given iterable into the list, for the given key.
        public void replaceAll(K key, Iterable<V> values) {
            //
        }

        // Trying to delete a non-existing key will be a no-op.
        public void delete(K key) {
            //
        }
    }
}
