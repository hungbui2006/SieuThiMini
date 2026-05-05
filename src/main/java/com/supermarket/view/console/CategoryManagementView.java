package com.supermarket.view.console;

import com.supermarket.model.Category;
import com.supermarket.service.CategoryService;
import com.supermarket.util.ConsoleHelper;
import com.supermarket.util.IdGenerator;

import java.util.List;

/**
 * Console view for Category management.
 */
public class CategoryManagementView {

    private final CategoryService categoryService;

    public CategoryManagementView(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public void show() {
        while (true) {
            ConsoleHelper.printHeader("QUẢN LÝ DANH MỤC SẢN PHẨM");
            System.out.println("  1. Xem danh sách danh mục");
            System.out.println("  2. Thêm danh mục mới");
            System.out.println("  3. Cập nhật danh mục");
            System.out.println("  4. Xóa danh mục");
            System.out.println("  0. Quay lại");
            ConsoleHelper.printSeparator();

            int choice = ConsoleHelper.readInt("  Chọn chức năng: ", 0, 4);
            switch (choice) {
                case 1 -> listCategories();
                case 2 -> addCategory();
                case 3 -> updateCategory();
                case 4 -> deleteCategory();
                case 0 -> { return; }
            }
        }
    }

    private void listCategories() {
        ConsoleHelper.printHeader("DANH SÁCH DANH MỤC");
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("  Chưa có danh mục nào.");
        } else {
            System.out.printf("  %-8s %-25s %-30s%n", "Mã", "Tên danh mục", "Mô tả");
            ConsoleHelper.printSeparator();
            for (Category c : categories) {
                System.out.printf("  %-8s %-25s %-30s%n", c.getId(), c.getName(), c.getDescription());
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void addCategory() {
        ConsoleHelper.printHeader("THÊM DANH MỤC MỚI");
        String id = IdGenerator.nextId(IdGenerator.CATEGORY_PREFIX);
        String name = ConsoleHelper.readString("  Tên danh mục: ");
        String desc = ConsoleHelper.readString("  Mô tả: ");
        categoryService.addCategory(new Category(id, name, desc));
        ConsoleHelper.printSuccess("Đã thêm danh mục: [" + id + "] " + name);
        ConsoleHelper.pressEnterToContinue();
    }

    private void updateCategory() {
        ConsoleHelper.printHeader("CẬP NHẬT DANH MỤC");
        String id = ConsoleHelper.readString("  Nhập mã danh mục: ");
        try {
            Category cat = categoryService.getCategory(id);
            System.out.println("  Danh mục hiện tại: " + cat);
            cat.setName(ConsoleHelper.readStringOptional("  Tên mới: ", cat.getName()));
            cat.setDescription(ConsoleHelper.readStringOptional("  Mô tả mới: ", cat.getDescription()));
            categoryService.updateCategory(cat);
            ConsoleHelper.printSuccess("Đã cập nhật danh mục " + id);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue();
    }

    private void deleteCategory() {
        String id = ConsoleHelper.readString("  Nhập mã danh mục cần xóa: ");
        if (ConsoleHelper.confirm("  Xác nhận xóa?")) {
            if (categoryService.deleteCategory(id)) {
                ConsoleHelper.printSuccess("Đã xóa danh mục " + id);
            } else {
                ConsoleHelper.printError("Không tìm thấy danh mục " + id);
            }
        }
        ConsoleHelper.pressEnterToContinue();
    }
}
