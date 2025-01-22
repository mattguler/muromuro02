import java.util.*;
import java.util.stream.*;


/**
 * The evaluator for the DesignApiWithPagination question.
 * This is a resource file, not part of the main codebase.
 * It is read by the main MuroMuro server, and then its relevant parts are
 * replaced by the user-input solutions. Then the resulting code is sent to the
 * remote Docker container to do an evaluation of the user solution.
 */
public class DesignApiWithPaginationEval {
    static final int TOTAL_EMPLOYEES = 20;
    static final int MAX_SIZE = 5;
    static final int COMPANY_ID = 15;

    static class Employee {
        long id;
        String firstName;
        String lastName;

        Employee(long id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        long getId() {
            return id;
        }

        String getFirstName() {
            return firstName;
        }

        String getLastName() {
            return lastName;
        }

        @Override
        public String toString() {
            return String.format(
                    "Employee id: %d, first_name: %s, last_name: %s",
                    getId(),
                    getFirstName(),
                    getLastName());
        }
    }

    interface DatabaseProxy {
        List<Employee> getEmployees(long companyId);
    }

    static class DatabaseProxyImpl implements DatabaseProxy {
        @Override
        public List<Employee> getEmployees(long companyId) {
            // TODO: Also verify the companyId.
            return Stream.iterate(1, i -> i + 1)
                    .limit(TOTAL_EMPLOYEES)
                    .map(id -> new Employee(id, "first_name_" + id, "last_name_" + id))
                    .collect(Collectors.toList());
        }
    }

    public void printEmployees(String listTitle, List<Employee> employees) {
        System.out.println(listTitle);
        for (Employee employee : employees) {
            System.out.println(employee);
        }
        System.out.println();
    }

    public void verifyEmployees(
            String listTitle, List<Employee> employees, int startId, int endId) {
        if (employees.size() != endId - startId + 1) {
            System.out.println(listTitle + " is incorrectly formed.");
            return;
        }
        int id = startId;
        for (int i = 0; i < endId - startId + 1; i++) {
            Employee employee = employees.get(i);
            if (employee.getId() != id || !employee.getFirstName().equals("first_name_" + id)
                    || !employee.getLastName().equals("last_name_" + id)) {
                System.out.println(listTitle + " is incorrectly formed.");
                return;
            }
            id++;
        }
        System.out.println(listTitle + " is correctly formed.");
    }

    // Start caller code implementation.
    public void callerFunction(
            List<Employee> list1,
            List<Employee> list2,
            List<Employee> list3,
            List<Employee> list4,
            int maxSize,
            long companyId,
            DatabaseProxy dbProxy) {
        List<Employee> results =
                retrieveEmployees(
                        dbProxy, companyId, /* offset= */ 0, maxSize);
        list1.addAll(results);
        results =
                retrieveEmployees(
                        dbProxy, companyId, /* offset= */ maxSize, maxSize);
        list2.addAll(results);
        results =
                retrieveEmployees(
                        dbProxy, companyId, /* offset= */ 2 * maxSize, maxSize);
        list3.addAll(results);
        results =
                retrieveEmployees(
                        dbProxy, companyId, /* offset= */ 3 * maxSize, maxSize);
        list4.addAll(results);
    }
    // End caller code implementation.

    // Start main definition implementation.
    public List<Employee> retrieveEmployees(
            DatabaseProxy dbProxy,
            long companyId,
            int offset,
            int maxSize) {
        List<Employee> allEmployees = dbProxy.getEmployees(companyId);
        List<Employee> results = new ArrayList<>();
        for (int i = offset; i < allEmployees.size(); i++) {
            results.add(allEmployees.get(i));
            if (results.size() >= maxSize) {
                break;
            }
        }
        return results;
    }
    // End main definition implementation.

    public static void main(String[] args) {
        DatabaseProxy dbProxy = new DatabaseProxyImpl();

        List<Employee> list1 = new ArrayList<>();
        List<Employee> list2 = new ArrayList<>();
        List<Employee> list3 = new ArrayList<>();
        List<Employee> list4 = new ArrayList<>();

        DesignApiWithPaginationEval eval = new DesignApiWithPaginationEval();

        eval.callerFunction(
                list1, list2, list3, list4, MAX_SIZE, COMPANY_ID, dbProxy);

        eval.printEmployees("List 1", list1);
        eval.printEmployees("List 2", list2);
        eval.printEmployees("List 3", list3);
        eval.printEmployees("List 4", list4);

        System.out.println();

        eval.verifyEmployees("List 1", list1, 1, MAX_SIZE);
        eval.verifyEmployees("List 2", list2, MAX_SIZE + 1, 2 * MAX_SIZE);
        eval.verifyEmployees("List 3", list3, 2 * MAX_SIZE + 1, 3 * MAX_SIZE);
        eval.verifyEmployees("List 4", list4, 3 * MAX_SIZE + 1, 4 * MAX_SIZE);
    }
}
