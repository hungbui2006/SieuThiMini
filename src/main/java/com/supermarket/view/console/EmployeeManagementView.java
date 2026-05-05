package com.supermarket.view.console;

import com.supermarket.model.Employee;
import com.supermarket.model.User;
import com.supermarket.service.EmployeeService;
import com.supermarket.util.*;

import java.util.List;

/**
 * Console view for Employee management.
 */
public class EmployeeManagementView {

    private final EmployeeService employeeService;

    public EmployeeManagementView(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("QUẢN LÝ NHÂN VIÊN");
            System.out.println("  1. Xem danh sách nhân viên");
            System.out.println("  2. Thêm nhân viên mới");
            System.out.println("  3. Cập nhật nhân viên");
            System.out.println("  4. Xem lương nhân viên");
            System.out.println("  5. Vô hiệu hóa tài khoản");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 5);
            switch (choice) {
                case 1 -> listEmployees();
                case 2 -> addEmployee();
                case 3 -> updateEmployee();
                case 4 -> showSalary();
                case 5 -> deactivateEmployee();
                case 0 -> { return; }
            }
        }
    }

    private void listEmployees() {
        ConsoleHelper.printHeader("DANH SÁCH NHÂN VIÊN");
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("  Chưa có nhân viên nào.");
        } else {
            System.out.printf("  %-6s %-20s %-12s %-15s %-10s%n",
                    "Mã", "Họ tên", "Username", "SĐT", "Trạng thái");
            ConsoleHelper.printSeparator();
            for (Employee e : employees) {
                System.out.printf("  %-6s %-20s %-12s %-15s %-10s%n",
                        e.getId(), e.getFullName(), e.getUsername(), e.getPhone(),
                        e.isActive() ? "Hoạt động" : "Vô hiệu");
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void addEmployee() {
        ConsoleHelper.printHeader("THÊM NHÂN VIÊN MỚI");
        String id = IdGenerator.nextId(IdGenerator.USER_PREFIX);
        String fullName = ConsoleHelper.readString("  Họ tên: ");
        String username = ConsoleHelper.readString("  Tên đăng nhập: ");
        String password = ConsoleHelper.readString("  Mật khẩu: ");
        String phone = ConsoleHelper.readPhone("  Số điện thoại: ");
        double salary = ConsoleHelper.readPositiveDouble("  Lương cơ bản (VND): ");
        double commission = ConsoleHelper.readPositiveDouble("  Tỷ lệ hoa hồng (ví dụ 0.02 = 2%): ");

        Employee emp = new Employee(id, username, password, fullName, phone, salary, commission);
        employeeService.addEmployee(emp);
        ConsoleHelper.printSuccess("Đã thêm nhân viên: " + emp);
        ConsoleHelper.pressEnterToContinue();
    }

    private void updateEmployee() {
        String id = ConsoleHelper.readString("  Nhập mã nhân viên: ");
        try {
            Employee emp = employeeService.getEmployee(id);
            System.out.println("  NV hiện tại: " + emp);
            emp.setFullName(ConsoleHelper.readStringOptional("  Họ tên mới: ", emp.getFullName()));
            emp.setPhone(ConsoleHelper.readStringOptional("  SĐT mới: ", emp.getPhone()));
            String salaryStr = ConsoleHelper.readStringOptional(
                    "  Lương mới (" + CurrencyFormatter.format(emp.getBaseSalary()) + "): ", "");
            if (!salaryStr.isEmpty()) emp.setBaseSalary(Double.parseDouble(salaryStr));
            employeeService.updateEmployee(emp);
            ConsoleHelper.printSuccess("Đã cập nhật nhân viên " + id);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void showSalary() {
        ConsoleHelper.printHeader("BẢNG LƯƠNG NHÂN VIÊN");
        List<Employee> employees = employeeService.getAllEmployees();
        System.out.printf("  %-6s %-20s %15s %15s %15s%n",
                "Mã", "Họ tên", "Lương CB", "Hoa hồng", "Tổng lương");
        ConsoleHelper.printSeparator();
        for (Employee e : employees) {
            System.out.printf("  %-6s %-20s %15s %15s %15s%n",
                    e.getId(), e.getFullName(),
                    CurrencyFormatter.format(e.getBaseSalary()),
                    CurrencyFormatter.format(e.calculateCommission()),
                    CurrencyFormatter.format(e.calculateTotalSalary()));
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void deactivateEmployee() {
        String id = ConsoleHelper.readString("  Nhập mã NV cần vô hiệu hóa: ");
        if (ConsoleHelper.confirm("  Xác nhận vô hiệu hóa?")) {
            try {
                employeeService.deactivateEmployee(id);
                ConsoleHelper.printSuccess("Đã vô hiệu hóa tài khoản " + id);
            } catch (Exception e) {
                ConsoleHelper.printError(e.getMessage());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }
}
