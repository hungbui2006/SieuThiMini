package com.supermarket.service;

import com.supermarket.datastore.DataStore;
import com.supermarket.exception.EntityNotFoundException;
import com.supermarket.exception.OutOfStockException;
import com.supermarket.model.*;
import com.supermarket.model.enums.PaymentMethod;
import com.supermarket.repository.OrderRepository;
import com.supermarket.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * Service xử lý đơn hàng: giỏ hàng, thanh toán, lịch sử bán hàng.
 */
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductService productService;
    private final CustomerService customerService;

    public OrderService(ProductService productService, CustomerService customerService) {
        this.orderRepo = DataStore.getInstance().getOrderRepository();
        this.productService = productService;
        this.customerService = customerService;
    }

    /** Tạo đơn hàng mới cho nhân viên thu ngân. */
    public Order createNewOrder(Employee employee) {
        String orderId = IdGenerator.nextId(IdGenerator.ORDER_PREFIX);
        return new Order(orderId, employee.getId(), employee.getFullName());
    }

    /** Thêm sản phẩm vào giỏ hàng (kiểm tra tồn kho + hạn sử dụng). */
    public void addToCart(Order order, String productId, int quantity) {
        Product product = productService.getProduct(productId);

        // Kiểm tra hết hạn
        if (product instanceof PerishableProduct) {
            PerishableProduct pp = (PerishableProduct) product;
            if (pp.isExpired()) {
                throw new IllegalStateException(
                        "Sản phẩm " + product.getName() + " đã hết hạn sử dụng, không thể bán!");
            }
        }

        // Kiểm tra tồn kho
        if (product.getStockQuantity() < quantity) {
            throw new OutOfStockException(productId, quantity, product.getStockQuantity());
        }

        OrderItem item = new OrderItem(
                product.getId(),
                product.getName(),
                quantity,
                product.calculateFinalPrice(),   // Đa hình — mỗi loại SP tính giá khác nhau
                product.getPrice()
        );
        order.addItem(item);
    }

    /** Gắn khách hàng thành viên vào đơn để áp dụng giảm giá. */
    public void attachCustomer(Order order, String customerId) {
        Customer customer = customerService.getCustomer(customerId);
        order.setCustomerId(customer.getId());
        order.setCustomerName(customer.getName());
        order.setCustomerDiscountRate(customer.getDiscountRate());
    }

    /** Hoàn tất đơn hàng: trừ kho, ghi nhận, tích điểm. */
    public void completeOrder(Order order, PaymentMethod paymentMethod, String paymentDetails) {
        // Trừ kho
        for (OrderItem item : order.getItems()) {
            Product product = productService.getProduct(item.getProductId());
            product.updateStock(-item.getQuantity()); // Trigger Observer
            productService.updateProduct(product);
        }

        // Thanh toán
        order.setPaymentMethod(paymentMethod);
        order.setPaymentDetails(paymentDetails);
        order.setCompleted(true);

        // Lưu đơn hàng
        orderRepo.save(order);

        // Tích điểm cho khách thành viên
        if (order.getCustomerId() != null) {
            int points = order.calculateRewardPoints();
            if (points > 0) {
                customerService.addRewardPoints(order.getCustomerId(), points);
            }
        }
    }

    /**
     * Returns all completed orders.
     */
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    /**
     * Returns an order by ID.
     */
    public Order getOrder(String id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy hóa đơn với mã: " + id));
    }

    /**
     * Returns orders placed on a specific date.
     */
    public List<Order> getOrdersByDate(LocalDate date) {
        return orderRepo.findByDate(date);
    }

    /**
     * Returns orders within a date range.
     */
    public List<Order> getOrdersByDateRange(LocalDate from, LocalDate to) {
        return orderRepo.findByDateRange(from, to);
    }

    /**
     * Returns all orders processed by a specific employee.
     */
    public List<Order> getOrdersByEmployee(String employeeId) {
        return orderRepo.findByEmployeeId(employeeId);
    }

    /**
     * Returns all orders for a specific customer.
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepo.findByCustomerId(customerId);
    }
}
