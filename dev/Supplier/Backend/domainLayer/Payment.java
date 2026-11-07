package Supplier.Backend.domainLayer;

import Supplier.Backend.serviceLayer.Message;

public class Payment {

    public enum PaymentMethod{
        CASH,
        CHECK,
        BANK_TRANSFER
    }

    public enum PaymentTerm {
        IN_ADVANCE,
        UPON_DELIVERY,
        BEGINNING_OF_MONTH,
        END_OF_MONTH
    }

    private PaymentMethod paymentMethod;
    private PaymentTerm paymentTerm;

    public Payment(PaymentMethod paymentMethod, PaymentTerm paymentTerm) {
        this.paymentMethod = paymentMethod;
        this.paymentTerm = paymentTerm;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public Message updatePaymentMethod(String paymentMethod) {
        try {
            this.paymentMethod = PaymentMethod.valueOf(paymentMethod);
            return new Message(true, "Payment method set to: " + paymentMethod);
        } catch (IllegalArgumentException e) {
            return new Message(false, "Invalid payment method: " + paymentMethod);
        }
    }
    public PaymentTerm getPaymentTerm() {
        return paymentTerm;
    }
    public Message updatePaymentTerm(String paymentTerm) {
        try {
            this.paymentTerm = PaymentTerm.valueOf(paymentTerm);
            return new Message(true, "Payment term set to: " + paymentTerm);
        } catch (IllegalArgumentException e) {
            return new Message(false, "Invalid payment term: " + paymentTerm);
        }
    }

    public String toString() {
        return "Payment{" +
                "payment method=" + paymentMethod +
                ", payment term=" + paymentTerm +
                '}';
    }




}
