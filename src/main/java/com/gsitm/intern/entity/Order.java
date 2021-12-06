package com.gsitm.intern.entity;


import com.gsitm.intern.constant.OrderStatus;
import com.gsitm.intern.constant.ReturnStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    //반품 요청일
    @Column(name = "return_req_date", nullable = true)
    private LocalDateTime returnReqDate;

    //반품 확정일
    @Column(name = "return_confirm_date", nullable = true)
    private LocalDateTime returnConfirmDate;

    //반품 여부
    @Enumerated(EnumType.STRING)
    @Column(name = "return_status")
    private ReturnStatus returnStatus;

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);

        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
}
