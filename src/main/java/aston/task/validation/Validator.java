package aston.task.validation;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static int getValidatedInt(Scanner scanner) {
        while (true) {
            try {
                int number = scanner.nextInt();
                scanner.nextLine();
                if (number < 0) throw new IllegalArgumentException("Число должно быть положительным");
                return number;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите корректное число!");
                scanner.nextLine(); //
            }
        }
    }

    public static long getValidatedLong(Scanner scanner) {
        while (true) {
            try {
                long number = scanner.nextLong();
                scanner.nextLine();
                return number;
            } catch (Exception e) {
                System.out.println("Ошибка: введите корректное число!");
                scanner.nextLine();
            }
        }
    }

    public static String getValidatedEmail(Scanner scanner) {
        while (true) {
            String email = scanner.nextLine().trim();
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            } else {
                System.out.println("Ошибка: некорректный email! Попробуйте снова.");
            }
        }
    }
}