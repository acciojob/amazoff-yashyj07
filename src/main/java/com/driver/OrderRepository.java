package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {
    Map<String, Order> orderDetails = new HashMap<>();
    Map<String, DeliveryPartner> partnerDetails = new HashMap<>();
    Map<String, String> orderToPartner = new HashMap<>();
    Map<String, List<String>> partnerToOrder = new HashMap<>();
    public void addOrder(Order order) {
        orderDetails.put(order.getId(), order);
    }
    public void addPartner(String partnerId) {
        partnerDetails.put(partnerId, new DeliveryPartner(partnerId));
    }
    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(!partnerToOrder.containsKey(partnerId)){
            partnerToOrder.put(partnerId, new ArrayList<>());
        }
        partnerToOrder.get(partnerId).add(orderId);
        partnerDetails.get(partnerId).setNumberOfOrders(partnerDetails.get(partnerId).getNumberOfOrders()+1);
        orderToPartner.put(orderId, partnerId);
    }

    public Order getOrderById(String orderId) {
        return orderDetails.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerDetails.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return partnerDetails.get(partnerId).getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerToOrder.get(partnerId);
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderDetails.keySet());
    }


    public Integer getCountOfUnassignedOrders() {
        return orderDetails.size()-orderToPartner.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int count = 0;
        int intTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3));
        List<String> ordersToCheck = partnerToOrder.get(partnerId);
        for(String orderID: ordersToCheck){
            if(orderDetails.get(orderID).getDeliveryTime()>intTime){
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int lastOrderTime = 0;

        List<String> ordersOfPartner = partnerToOrder.get(partnerId);
        for(String orderID: ordersOfPartner){
            if(orderDetails.get(orderID).getDeliveryTime()>lastOrderTime){
                lastOrderTime = orderDetails.get(orderID).getDeliveryTime();
            }
        }

        int hours = lastOrderTime/60;
        int minutes = lastOrderTime%60;
        String HH = ""+hours;
        String MM = ""+minutes;

        if(HH.length()==1){
            HH = '0'+HH;
        }
        if(MM.length()==1){
            MM = '0'+MM;
        }
        return HH+':'+MM;
    }

    public void deletePartnerById(String partnerId) {
        List<String> ordersOfPartner = partnerToOrder.get(partnerId);
        for(String orderId: ordersOfPartner){
            orderToPartner.remove(orderId);
        }
        partnerToOrder.remove(partnerId);
        partnerDetails.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        String partnerID = orderToPartner.get(orderId);
        partnerToOrder.get(partnerID).remove(orderId);
        partnerDetails.get(partnerID).setNumberOfOrders(partnerDetails.get(partnerID).getNumberOfOrders()-1);
        orderToPartner.remove(orderId);
        orderDetails.remove(orderId);
    }
}
