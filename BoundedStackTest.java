import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Test Runner สำหรับ BoundedStack
 * ออกแบบมาเพื่อทดสอบ BoundedStack.java ตาม Specification ที่กำหนด
 */
public class BoundedStackTest {

    private static int passed = 0;
    private static int failed = 0;

    /** Helper สำหรับตรวจสอบเงื่อนไขและสะสมคะแนน PASS/FAIL */
    private static void check(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName);
        }
    }

    public static void main(String[] args) {
        // ตรวจสอบสถานะ Assertion ในการรัน
        boolean assertsEnabled = false;
        assert assertsEnabled = true;
        if (!assertsEnabled) {
            System.out.println("WARNING: Assertions disabled - re-run with: java -ea BoundedStackTest\n");
        }

        System.out.println("=== BoundedStack Test Suite ===\n");

        testConstructors();
        testPushAndLIFO();
        testPopAndPeek();
        testContainsAndObservers();
        testReversed();
        testExceptionsAndBoundaries();

        System.out.println("\n=== Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total : " + (passed + failed));
        System.out.println(failed == 0 ? ">>> ALL TESTS PASSED <<<" : ">>> SOME TESTS FAILED <<<");

        if (failed > 0) {
            System.exit(1);
        }
    }

    // --- Partition 1: Constructors & Defensive Copy ---
    private static void testConstructors() {
        System.out.println("-- Test Constructors --");

        // Default Constructor
        BoundedStack<String> stack1 = new BoundedStack<>();
        check("Default constructor -> size 0", stack1.size() == 0);
        check("Default constructor -> capacity 100", stack1.capacity() == 100);

        // Constructor with Initial List
        List<String> initList = new ArrayList<>(Arrays.asList("Toyota", "Honda"));
        BoundedStack<String> stack2 = new BoundedStack<>(initList);
        check("List constructor -> size 2", stack2.size() == 2);
        check("List constructor -> top is Honda", "Honda".equals(stack2.peek()));

        // Defensive Copy Test (แก้ list ภายนอก ต้องไม่กระทบ stack)
        initList.add("BMW");
        check("Defensive copy -> modifications outside do not affect stack size", stack2.size() == 2);

        // Constructor with empty list -> ต้องสร้าง stack ว่างได้ปกติ ไม่ throw
        BoundedStack<String> stack3 = new BoundedStack<>(new ArrayList<>());
        check("List constructor with empty list -> size 0", stack3.size() == 0);

        // Constructor with list exactly at MAX_CARS -> ต้องสร้างได้ปกติ (boundary, ไม่ throw)
        List<String> fullList = new ArrayList<>();
        for (int i = 0; i < BoundedStack.MAX_CARS; i++) {
            fullList.add("Car#" + i);
        }
        BoundedStack<String> stack4 = new BoundedStack<>(fullList);
        check("List constructor with size == MAX_CARS -> size MAX_CARS", stack4.size() == BoundedStack.MAX_CARS);
    }

    // --- Partition 2: Push & LIFO Ordering ---
    private static void testPushAndLIFO() {
        System.out.println("\n-- Test Push & LIFO Order --");

        BoundedStack<String> stack = new BoundedStack<>();
        
        boolean pushSuccess1 = stack.push("Toyota");
        check("push('Toyota') returns true", pushSuccess1);
        check("size is 1 after 1 push", stack.size() == 1);
        check("peek shows 'Toyota'", "Toyota".equals(stack.peek()));

        stack.push("Honda");
        stack.push("BMW");
        check("size is 3 after 3 pushes", stack.size() == 3);
        check("peek shows top element ('BMW')", "BMW".equals(stack.peek()));
    }

    // --- Partition 3: Pop & Peek ---
    private static void testPopAndPeek() {
        System.out.println("\n-- Test Pop & Peek --");

        BoundedStack<String> stack = new BoundedStack<>(Arrays.asList("Toyota", "Honda", "BMW"));

        check("1st pop -> returns 'BMW'", "BMW".equals(stack.pop()));
        check("size decreases to 2", stack.size() == 2);
        check("peek shows 'Honda'", "Honda".equals(stack.peek()));

        check("2nd pop -> returns 'Honda'", "Honda".equals(stack.pop()));
        check("3rd pop -> returns 'Toyota'", "Toyota".equals(stack.pop()));
        check("size is 0 after popping all", stack.size() == 0);
    }

    // --- Partition 4: Contains & Observers ---
    private static void testContainsAndObservers() {
        System.out.println("\n-- Test Contains & Observer Methods --");

        BoundedStack<String> stack = new BoundedStack<>(Arrays.asList("Toyota", "Honda"));

        check("contains existing element -> true", stack.contains("Toyota"));
        check("contains non-existing element -> false", !stack.contains("Mazda"));
        check("contains(null) -> returns false safely", !stack.contains(null));

        // Observers Side Effects Test
        int sizeBefore = stack.size();
        String peekBefore = stack.peek();
        stack.size();
        stack.capacity();
        stack.peek();
        stack.contains("Toyota");

        check("Observer methods have no side-effects on size", stack.size() == sizeBefore);
        check("Observer methods have no side-effects on peek", peekBefore.equals(stack.peek()));
    }

    // --- Partition 5: Reversed Method ---
    private static void testReversed() {
        System.out.println("\n-- Test Reversed Method --");

        BoundedStack<String> original = new BoundedStack<>(Arrays.asList("Toyota", "Honda", "BMW"));
        BoundedStack<String> rev = original.reversed();

        check("Reversed stack top is 'Toyota'", "Toyota".equals(rev.peek()));
        check("Reversed stack size is 3", rev.size() == 3);
        
        // เช็คว่า Stack เดิมไม่ถูกแก้ไข
        check("Original stack top remains 'BMW'", "BMW".equals(original.peek()));

        // เช็คลำดับการ pop ของ reversed
        check("1st pop of reversed -> 'Toyota'", "Toyota".equals(rev.pop()));
        check("2nd pop of reversed -> 'Honda'", "Honda".equals(rev.pop()));
        check("3rd pop of reversed -> 'BMW'", "BMW".equals(rev.pop()));

        // Reversed ของ stack ว่าง -> ต้องได้ stack ว่างกลับมา ไม่ error
        BoundedStack<String> emptyOriginal = new BoundedStack<>();
        BoundedStack<String> emptyRev = emptyOriginal.reversed();
        check("reversed() of empty stack -> size 0", emptyRev.size() == 0);

        // Mutating stack ที่ได้จาก reversed() ต้องไม่กระทบ original
        BoundedStack<String> original2 = new BoundedStack<>(Arrays.asList("A", "B"));
        BoundedStack<String> rev2 = original2.reversed();
        rev2.push("C");
        check("Pushing onto reversed copy does not affect original size", original2.size() == 2);
    }

    // --- Partition 6: Exceptions & Boundary Handling ---
    private static void testExceptionsAndBoundaries() {
        System.out.println("\n-- Test Exceptions & Boundary Cases --");

        // 1. Initial List เป็น null -> IllegalArgumentException
        checkThrew("new BoundedStack(null) -> throws IllegalArgumentException",
            IllegalArgumentException.class, () -> new BoundedStack<String>(null));

        // 2. Initial List มี null ข้างใน -> IllegalArgumentException
        List<String> listWithNull = new ArrayList<>();
        listWithNull.add("Toyota");
        listWithNull.add(null);
        checkThrew("new BoundedStack(list with null) -> throws IllegalArgumentException",
            IllegalArgumentException.class, () -> new BoundedStack<String>(listWithNull));

        // 3. push(null) -> IllegalArgumentException
        BoundedStack<String> stack = new BoundedStack<>();
        checkThrew("push(null) -> throws IllegalArgumentException",
            IllegalArgumentException.class, () -> stack.push(null));

        // 4. pop() เมื่อ stack ว่าง -> NoSuchElementException
        BoundedStack<String> emptyStack = new BoundedStack<>();
        checkThrew("pop() on empty stack -> throws NoSuchElementException",
            NoSuchElementException.class, emptyStack::pop);

        // 5. peek() เมื่อ stack ว่าง -> NoSuchElementException
        checkThrew("peek() on empty stack -> throws NoSuchElementException",
            NoSuchElementException.class, emptyStack::peek);

        // 6. push เมื่อ stack เต็ม (MAX_CARS = 100) -> คืนค่า false
        BoundedStack<String> fullStack = new BoundedStack<>();
        for (int i = 0; i < BoundedStack.MAX_CARS; i++) {
            fullStack.push("Car#" + i);
        }
        check("push when stack reaches MAX_CARS -> returns false", !fullStack.push("OverflowCar"));
        check("stack size remains MAX_CARS", fullStack.size() == BoundedStack.MAX_CARS);

        // 7. Initial List ขนาดเกิน MAX_CARS -> IllegalArgumentException
        List<String> oversizedList = new ArrayList<>();
        for (int i = 0; i < BoundedStack.MAX_CARS + 1; i++) {
            oversizedList.add("Car#" + i);
        }
        checkThrew("new BoundedStack(list > MAX_CARS) -> throws IllegalArgumentException",
            IllegalArgumentException.class, () -> new BoundedStack<String>(oversizedList));
    }

    /** Functional interface สำหรับส่งผ่านโค้ดในรูปแบบ Lambda */
    @FunctionalInterface
    private interface Executable {
        void execute() throws Exception;
    }

    /** Helper สำหรับทดสอบการโยน Exception ตามชนิดที่คาดหวัง */
    private static void checkThrew(String testName, Class<? extends Exception> expectedException, Executable executable) {
        try {
            executable.execute();
            failed++;
            System.out.println("[FAIL] " + testName + " (No exception thrown)");
        } catch (Exception e) {
            if (expectedException.isInstance(e)) {
                passed++;
                System.out.println("[PASS] " + testName);
            } else {
                failed++;
                System.out.println("[FAIL] " + testName + " (Expected " + expectedException.getSimpleName() 
                        + " but got " + e.getClass().getSimpleName() + ")");
            }
        }
    }
}