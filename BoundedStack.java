import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * BoundedStack — ADT แทนสแต็กรถยนต์ (ระบบจอดรถยนต์แบบ LIFO) ที่มีความจุจำกัด
 *
 *   Abstraction Function:
 *   AF(cars ) = สแต็กรถยนต์ที่มีความจุสูงสุด MAX_CARS คัน
 *                        โดยเรียงจากคันล่างสุด cars.get(0) ถึงคันบนสุด cars.get(cars.size() - 1)
 *
 * Representation Invariant:
 *   1. cars != null
 *   2. cars.size() <= MAX_CARS
 *   3. ไม่มีสมาชิกใดใน cars เป็น null
 *
 * Safety from rep exposure:
 *   1. field cars ถูกประกาศเป็น final
 *   2. ไม่มี method ใดคืน reference ของ cars ออกไปโดยตรง
 */
public class BoundedStack<E> {

    private final List<E> cars; 
    
    public static final int MAX_CARS = 100;
    
    private void checkRep() {
        assert cars != null : "cars list must not be null";
        assert cars.size() <= MAX_CARS : "size exceeds MAX_CARS capacity";
        for (E car : cars) {
            assert car != null : "car must not be null";
        }
    }

   /**
    * สร้างสแต็กรถยนต์ว่าง โดยมีความจุสูงสุดเท่ากับ MAX_CARS (100 คัน)
    */
    
    public BoundedStack() {
    this.cars = new ArrayList<>();
    checkRep();
    }


    /**
     * สร้างสแต็กรถยนต์ใหม่โดยนำข้อมูลมาจากรายการเริ่มต้น (initial list)
     * โดยข้อมูลตำแหน่งแรก (index 0) จะกลายเป็นรถคันล่างสุดของสแต็ก
     *
     * @param initial รายการรถยนต์เริ่มต้น ห้ามเป็น null ห้ามมีขนาดเกิน MAX_CARS (100 คัน)
     *                และสมาชิกภายในห้ามเป็น null
     * @throws IllegalArgumentException ถ้า initial เป็น null, มีขนาดเกิน MAX_CARS, 
     *                                  หรือมีสมาชิกข้างในตัวใดตัวหนึ่งเป็น null
     */

    public BoundedStack(List<E> initial) {
        // 1. เช็ค initial == null → throw ก่อนเลย (ต้องเช็คก่อนแตะ .size() หรือ loop ใดๆ ไม่งั้นจะได้ NullPointerException แทน)
        if (initial == null) {
            throw new IllegalArgumentException("initial list cannot be null");
        }

        // 2. เช็ค initial.size() > MAX_CARS → throw
        if (initial.size() > MAX_CARS) {
            throw new IllegalArgumentException("initial list size exceeds MAX_CARS capacity");
        }

         // 3. เช็คว่ามี element เป็น null ปนอยู่ไหม → throw (ต้อง loop ดูทุกตัวใน initial)
        for (E car : initial) {
            if (car == null) {
                throw new IllegalArgumentException("car element inside initial list cannot be null");
            }
        }

        // 4. defensive copy — สร้าง list ใหม่คนละ object กับ initial แล้วเก็บใน this.cars
            this.cars = new ArrayList<>(initial);

        // 5. checkRep();
        checkRep();
    }   

    /**
     * นำรถยนต์เข้าจอดไว้ที่ยอดบนสุด (Top) ของสแต็ก
     *
     * @param car ออบเจกต์รถยนต์ที่ต้องการนำเข้าจอด ต้องไม่เป็น null
     * @return true หากนำรถเข้าจอดสำเร็จ,
     *         false หากสแต็กเต็ม (ครบความจุ MAX_CARS)
     * @throws IllegalArgumentException ถ้า car เป็น null
     */

    public boolean push(E car) {
        if (car == null) {
            throw new IllegalArgumentException("car cannot be null");
        }
        if (cars.size() >= MAX_CARS) {
            return false;
        }
        cars.add(car);
        checkRep();
        return true;
    }

    /**
     * ดึงรถยนต์คันบนสุดออกจากสแต็ก (LIFO)
     *
     * @return รถยนต์คันบนสุด
     * @throws NoSuchElementException ถ้าสแต็กว่าง 
     */
    public E pop() {
        if (cars.isEmpty()) {
            throw new NoSuchElementException("stack is empty");
        }
        E removedCar = cars.remove(cars.size() - 1);

        checkRep();

        return removedCar;
    }

    /**
     * คืนค่าจำนวนรถยนต์ปัจจุบันที่อยู่ในสแต็ก
     *
     * @return จำนวนรถยนต์ในสแต็ก
     */
    public int size() {
        return cars.size();
    }

    /**
     * คืนค่าความจุสูงสุด (Capacity) ของสแต็กนี้
     *
     * @return ความจุสูงสุดของสแต็ก (MAX_CARS)
     */
    public int capacity() {
        return MAX_CARS;
    }

    /**
     * ตรวจสอบว่ามีรถยนต์ที่ระบุอยู่ในสแต็กหรือไม่
     *
     * @param car ออบเจกต์รถยนต์ที่ต้องการค้นหา
     * @return true หากมีรถยนต์นี้อยู่ในสแต็ก, 
     *         false หากไม่มีรถยนต์นี้อยู่ หรือถ้า car เป็น null
     */
    public boolean contains(E car) {
        if (car == null) {
            return false;
        }
        return cars.contains(car);
    }

    /**
     * ดูรถยนต์คันที่อยู่บนสุด (Top) ของสแต็ก โดยไม่นำรถออกจากสแต็ก
     *
     * @return ออบเจกต์รถยนต์ที่อยู่บนสุด
     * @throws NoSuchElementException ถ้าสแต็กว่างเปล่า
     */
    public E peek() {
        if (cars.isEmpty()) {
            throw new NoSuchElementException("stack is empty");
        }
        return cars.get(cars.size() - 1);
    }
    /**
     * สร้างและคืนค่าสแต็กรถยนต์ใบใหม่ที่มีลำดับของรถยนต์กลับกันจากสแต็กปัจจุบัน
     * โดยที่สแต็กเดิม (this) จะไม่ถูกเปลี่ยนแปลงข้อมูลใดๆ
     *
     * @return BoundedStack<E> ใบใหม่ที่มีองค์ประกอบกลับลำดับจากสแต็กเดิม
     */
    public BoundedStack<E> reversed() {
        // 1. Copy ข้อมูลจาก this.cars ออกมาเป็น List ใหม่เพื่อไม่ให้กระทบของเดิม
        List<E> reversedCars = new ArrayList<>(this.cars);
        
        // 2. กลับลำดับใน List ใหม่ที่เพิ่ง copy มา
        Collections.reverse(reversedCars);
        
        // 3. สร้างและส่งคืน BoundedStack ใบใหม่ผ่าน Constructor ที่รับ List
        return new BoundedStack<>(reversedCars);
    }
    @Override
    public String toString() {
        return cars.toString();
    }
}