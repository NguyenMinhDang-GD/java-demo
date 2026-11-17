package vetcare.model;

import java.io.Serializable;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int appointmentId;
    private double consultationFee;
    private double medicineFee;
    private double vaccineFee;
    private double totalAmount;
    private String paymentMethod;
    private String status; // Unpaid, Paid

    public Invoice(int id, int appointmentId, double consultationFee, double medicineFee,
                   double vaccineFee, double totalAmount, String paymentMethod, String status) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.consultationFee = consultationFee;
        this.medicineFee = medicineFee;
        this.vaccineFee = vaccineFee;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getId() { return id; }
    public int getAppointmentId() { return appointmentId; }
    public double getConsultationFee() { return consultationFee; }
    public double getMedicineFee() { return medicineFee; }
    public double getVaccineFee() { return vaccineFee; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
