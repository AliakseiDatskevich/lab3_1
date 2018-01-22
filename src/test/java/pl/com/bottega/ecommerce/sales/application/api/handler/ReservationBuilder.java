package pl.com.bottega.ecommerce.sales.application.api.handler;

import static pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus;

import java.util.Date;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;

public class ReservationBuilder {

    private Id idReservation = new Id("1");
    private Id idClient = new Id("10");
    private String name = "John";
    private Date date = new Date();
    private ReservationStatus reservationStatus = ReservationStatus.OPENED;
    private ClientData clientData = new ClientData(idClient, name);

    public Reservation build() {
        return new Reservation(idReservation, reservationStatus, clientData, date);
    }

    public ReservationBuilder withIdReservation(Id id) {
        this.idReservation = id;
        return this;
    }

    public ReservationBuilder withIdClient(Id id) {
        this.idClient = id;
        return this;
    }

    public ReservationBuilder withNameClient(String name) {
        this.name = name;
        return this;
    }

    public ReservationBuilder withDate(Date date) {
        this.date = date;
        return this;
    }

    public ReservationBuilder withReservationStatus(ReservationStatus status) {
        this.reservationStatus = status;
        return this;
    }

    public ReservationBuilder withClientData(ClientData clientData) {
        this.clientData = clientData;
        return this;
    }
}
