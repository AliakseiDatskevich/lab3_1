package pl.com.bottega.ecommerce.sales.domain.reservation;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import java.util.Date;

/**
 * Created by Justyna on 31.01.2018.
 */
public class ReservationBuilder {

    private Reservation.ReservationStatus status;
    private Id id;
    private ClientData clientData;
    private Date createDate;

    private ReservationBuilder() {
    }

    public static ReservationBuilder getInstance() {
        return new ReservationBuilder();
    }

    public ReservationBuilder withStatus(Reservation.ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder withId(Id id) {
        this.id = id;
        return this;
    }

    public ReservationBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }

    public ReservationBuilder withCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }

    public Reservation build() {
        return new Reservation(id, status, clientData, createDate);
    }
}
