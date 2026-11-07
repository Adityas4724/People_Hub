// ======================================================
// 1. Package Declaration
// ======================================================
package com.peoplehub;


// ======================================================
// 2. Import Statements
// ======================================================
import java.util.Scanner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


// ======================================================
// 3. GeneratePassword Class
// ======================================================
public class GeneratePassword {

    // ==================================================
    // 4. Main Method
    // ==================================================
    public static void main(String[] args) {

        // ==================================================
        // 5. Create Scanner Object
        // ==================================================
        Scanner scanner = new Scanner(System.in);

        // ==================================================
        // 6. Create BCrypt Password Encoder
        // ==================================================
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // ==================================================
        // 7. Accept Password from User
        // ==================================================
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // ==================================================
        // 8. Generate BCrypt Password Hash
        // ==================================================
        String hashedPassword = encoder.encode(password);

        // ==================================================
        // 9. Display Generated Hash
        // ==================================================
        System.out.println("BCrypt Password Hash:");
        System.out.println(hashedPassword);

        // ==================================================
        // 10. Close Scanner
        // ==================================================
        scanner.close();
    }
}