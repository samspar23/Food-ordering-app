package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    /**
     * A controller method for saving order details in the database.
     *
     * @param saveOrderRequest - This argument contains all the attributes required to store order details in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<SaveOrderResponse> type object along with Http status CREATED.
     * @throws AuthorizationFailedException
     * @throws RestaurantNotFoundException
     * @throws CouponNotFoundException
     * @throws PaymentMethodNotFoundException
     * @throws ItemNotFoundException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST, path = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestBody(required = false) final SaveOrderRequest saveOrderRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, RestaurantNotFoundException, CouponNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException, AddressNotFoundException {
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setBill(saveOrderRequest.getBill().doubleValue());
        orderEntity.setCustomer(customerEntity);
        if(saveOrderRequest.getCouponId()!=null) orderEntity.setCoupon(orderService.getCouponByCouponId(String.valueOf(saveOrderRequest.getCouponId())));
        orderEntity.setPayment(paymentService.getPaymentByUUID(String.valueOf(saveOrderRequest.getPaymentId())));
        if(saveOrderRequest.getDiscount()!=null) orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        orderEntity.setAddress(addressService.getAddressByUUID(saveOrderRequest.getAddressId(),customerEntity));
        orderEntity.setUuid(UUID.randomUUID().toString());
        orderEntity.setRestaurant(restaurantService.restaurantByUUID(String.valueOf(saveOrderRequest.getRestaurantId())));
        Date date = new Date();
        orderEntity.setDate(date);
        orderEntity = orderService.saveOrder(orderEntity);
        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for(ItemQuantity itemQuantity : itemQuantities) {
            final OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setItem(itemService.getItemByUUID(String.valueOf(itemQuantity.getItemId())));
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderItemEntity.setQuantity(itemQuantity.getQuantity());
            orderItemEntity.setOrder(orderEntity);
            orderService.saveOrderItem(orderItemEntity);
        }

        SaveOrderResponse saveOrderResponse = new SaveOrderResponse().id(String.valueOf(orderEntity.getUuid())).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);
    }

    /**
     * A controller method fetch all placed order details from the database.
     *
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<CustomerOrderResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getAllPlacedOrders(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String access_token = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        List<OrderEntity> orderEntityList = orderService.getOrdersByCustomers(customerEntity.getUuid());
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        for(OrderEntity orderEntity : orderEntityList) {
            List<OrderItemEntity> orderItemEntityList = orderEntity.getOrderItemEntities();
            OrderListPayment orderListPayment = new OrderListPayment().id(UUID.fromString(orderEntity.getPayment().getUuid())).paymentName(orderEntity.getPayment().getPaymentName());
            OrderListCustomer orderListCustomer = new OrderListCustomer().id(UUID.fromString(orderEntity.getCustomer().getUuid())).contactNumber(orderEntity.getCustomer().getContactNumber()).
                    emailAddress(orderEntity.getCustomer().getEmail()).firstName(orderEntity.getCustomer().getFirstName()).lastName(orderEntity.getCustomer().getLastName());
            OrderListAddressState orderListAddressState = new OrderListAddressState().id(UUID.fromString(orderEntity.getAddress().getState().getUuid())).stateName(orderEntity.getAddress().getState().getStateName());
            OrderListAddress orderListAddress = new OrderListAddress().id(UUID.fromString(orderEntity.getAddress().getUuid())).flatBuildingName(orderEntity.getAddress().getFlatBuilNo()).
                    city(orderEntity.getAddress().getCity()).locality(orderEntity.getAddress().getLocality()).pincode(orderEntity.getAddress().getPincode()).state(orderListAddressState);
            OrderList orderList = new OrderList();
            if(orderEntity.getCoupon()!=null){
                OrderListCoupon orderListCoupon = new OrderListCoupon().id(UUID.fromString(orderEntity.getCoupon().getUuid())).couponName(orderEntity.getCoupon().getCouponName()).percent(orderEntity.getCoupon().getPercent());
                orderList.id(UUID.fromString(orderEntity.getUuid())).customer(orderListCustomer).payment(orderListPayment).coupon(orderListCoupon).address(orderListAddress).bill(BigDecimal.valueOf(orderEntity.getBill())).
                        discount(BigDecimal.valueOf(orderEntity.getDiscount())).date(orderEntity.getDate().toString());
            }
            else {
                orderList.id(UUID.fromString(orderEntity.getUuid())).customer(orderListCustomer).payment(orderListPayment).address(orderListAddress).bill(BigDecimal.valueOf(orderEntity.getBill())).date(orderEntity.getDate().toString());
            }
            for (OrderItemEntity orderItemEntity : orderItemEntityList) {
                final ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem().id(UUID.fromString(orderItemEntity.getItem().getUuid())).itemName(orderItemEntity.getItem().getItemName()).
                        itemPrice(orderItemEntity.getItem().getPrice()).type(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().name()));
                final ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse().item(itemQuantityResponseItem).quantity(orderItemEntity.getQuantity()).price(orderItemEntity.getPrice());
                orderList.addItemQuantitiesItem(itemQuantityResponse);
            }
            customerOrderResponse.addOrdersItem(orderList);
        }
        return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
    }

    /**
     * A controller method to get coupon details from the database.
     *
     * @param couponName    - The name of the coupon to be fetched from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<CouponDetailsResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws CouponNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name") String couponName, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, CouponNotFoundException {
        String access_token = authorization.split("Bearer ")[1];
        customerService.getCustomer(access_token);
        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse().id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

}
