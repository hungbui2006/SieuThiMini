package com.supermarket.datastore;

import com.supermarket.factory.ProductFactory;
import com.supermarket.model.*;
import com.supermarket.model.enums.LoyaltyTier;
import com.supermarket.model.enums.PaymentMethod;
import com.supermarket.model.enums.ProductType;
import com.supermarket.util.IdGenerator;

import java.time.LocalDate;

/**
 * Khởi tạo dữ liệu mẫu cho hệ thống (danh mục, sản phẩm, nhân viên, khách hàng...).
 * Gọi 1 lần khi khởi động app.
 */
public class DataSeeder {

    /** Tạo dữ liệu mẫu ban đầu. */
    public static void seed() {
        DataStore ds = DataStore.getInstance();

        // ===== CATEGORIES =====
        String[] catIds = {"DM001", "DM002", "DM003", "DM004", "DM005", "DM006", "DM007"};
        String[] catNames = {
                "Thực phẩm", "Đồ uống", "Gia vị", "Đồ dùng gia đình",
                "Bánh kẹo", "Sản phẩm sữa", "Rau củ quả"
        };
        String[] catDescs = {
                "Thực phẩm chế biến và đóng gói",
                "Nước giải khát, nước ngọt, cà phê",
                "Gia vị nấu ăn các loại",
                "Đồ dùng sinh hoạt gia đình",
                "Bánh, kẹo, snack các loại",
                "Sữa tươi, sữa chua, phô mai",
                "Rau xanh, củ, quả tươi"
        };
        for (int i = 0; i < catIds.length; i++) {
            ds.getCategoryRepository().save(new Category(catIds[i], catNames[i], catDescs[i]));
        }
        IdGenerator.resetCounter(IdGenerator.CATEGORY_PREFIX, catIds.length);

        // ===== SUPPLIERS =====
        ds.getSupplierRepository().save(new Supplier("NCC001", "Công ty TNHH Vinamilk",
                "028-54-155-555", "Số 10 Tân Trào, Q.7, TP.HCM", "contact@vinamilk.com.vn"));
        ds.getSupplierRepository().save(new Supplier("NCC002", "Công ty CP Masan Consumer",
                "028-62-556-600", "Tầng 12, MPlaza, 39 Lê Duẩn, Q.1, TP.HCM", "info@masan.com.vn"));
        ds.getSupplierRepository().save(new Supplier("NCC003", "Công ty Acecook Việt Nam",
                "028-38-160-840", "Lô II-3 CN, KCN Tân Bình, TP.HCM", "info@acecook.com.vn"));
        ds.getSupplierRepository().save(new Supplier("NCC004", "Công ty CP Unilever Việt Nam",
                "028-54-133-100", "156 Nguyễn Lương Bằng, Q.7, TP.HCM", "info@unilever.com.vn"));
        ds.getSupplierRepository().save(new Supplier("NCC005", "Đại lý Rau sạch Đà Lạt",
                "0263-382-1234", "42 Phan Đình Phùng, TP. Đà Lạt", "rausachdalat@gmail.com"));
        IdGenerator.resetCounter(IdGenerator.SUPPLIER_PREFIX, 5);

        // ===== PRODUCTS =====
        // Regular products
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP001", "Mì Hảo Hảo tôm chua cay", 4500, 200, "gói", "DM001", "NCC003"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP002", "Nước mắm Nam Ngư 500ml", 32000, 150, "chai", "DM003", "NCC002"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP003", "Dầu ăn Tường An 1L", 45000, 100, "chai", "DM003", "NCC002"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP004", "Nước suối Aquafina 500ml", 5000, 500, "chai", "DM002", "NCC004"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP005", "Bột giặt OMO 3kg", 125000, 80, "túi", "DM004", "NCC004"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP006", "Cà phê G7 hộp 18 gói", 52000, 120, "hộp", "DM002", "NCC002"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP007", "Bánh Oreo hộp 133g", 28000, 90, "hộp", "DM005", "NCC002"));
        ds.getProductRepository().save(ProductFactory.createProduct(
                ProductType.REGULAR, "SP008", "Giấy vệ sinh Pulppy 12 cuộn", 65000, 60, "bịch", "DM004", "NCC004"));

        // Promotion products
        ds.getProductRepository().save(ProductFactory.createPromotionProduct(
                "SP009", "Coca-Cola 1.5L", 18000, 300, "chai", "DM002", "NCC002",
                0.20, LocalDate.now().minusDays(5), LocalDate.now().plusDays(25)));
        ds.getProductRepository().save(ProductFactory.createPromotionProduct(
                "SP010", "Snack Poca khoai tây 54g", 12000, 180, "gói", "DM005", "NCC002",
                0.15, LocalDate.now(), LocalDate.now().plusDays(14)));

        // Perishable products
        ds.getProductRepository().save(ProductFactory.createPerishableProduct(
                "SP011", "Sữa tươi Vinamilk 1L", 32000, 100, "hộp", "DM006", "NCC001",
                LocalDate.now().minusDays(3), LocalDate.now().plusDays(12)));
        ds.getProductRepository().save(ProductFactory.createPerishableProduct(
                "SP012", "Sữa chua Vinamilk có đường (lốc 4)", 25000, 80, "lốc", "DM006", "NCC001",
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(20)));
        ds.getProductRepository().save(ProductFactory.createPerishableProduct(
                "SP013", "Rau cải ngọt Đà Lạt 500g", 15000, 50, "bó", "DM007", "NCC005",
                LocalDate.now(), LocalDate.now().plusDays(5)));
        ds.getProductRepository().save(ProductFactory.createPerishableProduct(
                "SP014", "Thịt heo xay 500g", 65000, 30, "khay", "DM001", "NCC005",
                LocalDate.now(), LocalDate.now().plusDays(3)));
        ds.getProductRepository().save(ProductFactory.createPerishableProduct(
                "SP015", "Đậu hũ non Ichiban 300g", 12000, 8, "hộp", "DM001", "NCC002",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2)));

        IdGenerator.resetCounter(IdGenerator.PRODUCT_PREFIX, 15);

        // ===== USERS =====
        ds.getUserRepository().save(new Admin("TK001", "admin", "admin123", "Nguyễn Viết Hiệp", "0901234567"));
        ds.getUserRepository().save(new Employee("TK002", "nv01", "nv123", "Trần Thị Bích",
                "0912345678", 8000000, 0.02));
        ds.getUserRepository().save(new Employee("TK003", "nv02", "nv123", "Lê Hoàng Cường",
                "0923456789", 7500000, 0.02));
        IdGenerator.resetCounter(IdGenerator.USER_PREFIX, 3);

        // ===== CUSTOMERS =====
        Customer c1 = new Customer("KH001", "Phạm Minh Đức", "0934567890", "duc.pham@gmail.com");
        c1.setRewardPoints(1800);
        c1.setTier(LoyaltyTier.fromPoints(1800));
        ds.getCustomerRepository().save(c1);

        Customer c2 = new Customer("KH002", "Hoàng Thị Lan", "0945678901", "lan.hoang@gmail.com");
        c2.setRewardPoints(600);
        c2.setTier(LoyaltyTier.fromPoints(600));
        ds.getCustomerRepository().save(c2);

        Customer c3 = new Customer("KH003", "Võ Thanh Tùng", "0956789012", "tung.vo@gmail.com");
        ds.getCustomerRepository().save(c3);

        IdGenerator.resetCounter(IdGenerator.CUSTOMER_PREFIX, 3);

        // ===== SAMPLE ORDERS (Đơn hàng mẫu đã thanh toán) =====

        // Đơn 1: Khách Phạm Minh Đức mua 3 món — thanh toán tiền mặt
        Order o1 = new Order("HD001", "TK002", "Trần Thị Bích");
        o1.setCustomerId("KH001"); o1.setCustomerName("Phạm Minh Đức");
        o1.setOrderDate(java.time.LocalDateTime.now().minusDays(2).withHour(9).withMinute(30));
        o1.addItem(new OrderItem("SP001", "Mì Hảo Hảo tôm chua cay", 10, 4500, 4500));
        o1.addItem(new OrderItem("SP004", "Nước suối Aquafina 500ml", 5, 5000, 5000));
        o1.addItem(new OrderItem("SP006", "Cà phê G7 hộp 18 gói", 2, 52000, 52000));
        o1.setPaymentMethod(PaymentMethod.CASH);
        o1.setCompleted(true);
        ds.getOrderRepository().save(o1);

        // Đơn 2: Khách Hoàng Thị Lan mua — thanh toán MoMo
        Order o2 = new Order("HD002", "TK003", "Lê Hoàng Cường");
        o2.setCustomerId("KH002"); o2.setCustomerName("Hoàng Thị Lan");
        o2.setOrderDate(java.time.LocalDateTime.now().minusDays(1).withHour(14).withMinute(15));
        o2.addItem(new OrderItem("SP011", "Sữa tươi Vinamilk 1L", 3, 32000, 32000));
        o2.addItem(new OrderItem("SP002", "Nước mắm Nam Ngư 500ml", 2, 32000, 32000));
        o2.addItem(new OrderItem("SP005", "Bột giặt OMO 3kg", 1, 125000, 125000));
        o2.setPaymentMethod(PaymentMethod.MOMO);
        o2.setCompleted(true);
        ds.getOrderRepository().save(o2);

        // Đơn 3: Khách vãng lai (không có thẻ thành viên) — thanh toán thẻ
        Order o3 = new Order("HD003", "TK002", "Trần Thị Bích");
        o3.setOrderDate(java.time.LocalDateTime.now().minusDays(1).withHour(17).withMinute(45));
        o3.addItem(new OrderItem("SP009", "Coca-Cola 1.5L", 4, 14400, 18000));
        o3.addItem(new OrderItem("SP007", "Bánh Oreo hộp 133g", 3, 28000, 28000));
        o3.addItem(new OrderItem("SP008", "Giấy vệ sinh Pulppy 12 cuộn", 2, 65000, 65000));
        o3.setPaymentMethod(PaymentMethod.CARD);
        o3.setCompleted(true);
        ds.getOrderRepository().save(o3);

        // Đơn 4: Khách Võ Thanh Tùng — thanh toán VietQR — hôm nay
        Order o4 = new Order("HD004", "TK003", "Lê Hoàng Cường");
        o4.setCustomerId("KH003"); o4.setCustomerName("Võ Thanh Tùng");
        o4.setOrderDate(java.time.LocalDateTime.now().withHour(10).withMinute(0));
        o4.addItem(new OrderItem("SP003", "Dầu ăn Tường An 1L", 2, 45000, 45000));
        o4.addItem(new OrderItem("SP013", "Rau cải ngọt Đà Lạt 500g", 3, 15000, 15000));
        o4.addItem(new OrderItem("SP014", "Thịt heo xay 500g", 2, 65000, 65000));
        o4.setPaymentMethod(PaymentMethod.VIETQR);
        o4.setCompleted(true);
        ds.getOrderRepository().save(o4);

        // Đơn 5: Khách Phạm Minh Đức quay lại mua tiếp — hôm nay — ZaloPay
        Order o5 = new Order("HD005", "TK002", "Trần Thị Bích");
        o5.setCustomerId("KH001"); o5.setCustomerName("Phạm Minh Đức");
        o5.setOrderDate(java.time.LocalDateTime.now().withHour(15).withMinute(30));
        o5.addItem(new OrderItem("SP010", "Snack Poca khoai tây 54g", 5, 10200, 12000));
        o5.addItem(new OrderItem("SP012", "Sữa chua Vinamilk có đường (lốc 4)", 4, 25000, 25000));
        o5.addItem(new OrderItem("SP004", "Nước suối Aquafina 500ml", 10, 5000, 5000));
        o5.setPaymentMethod(PaymentMethod.ZALOPAY);
        o5.setCompleted(true);
        ds.getOrderRepository().save(o5);

        IdGenerator.resetCounter(IdGenerator.ORDER_PREFIX, 5);
        IdGenerator.resetCounter(IdGenerator.IMPORT_PREFIX, 0);

        System.out.println("✓ Đã khởi tạo dữ liệu mẫu thành công!");
    }
}
