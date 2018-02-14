package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;

import static org.mockito.Matchers.any;

public class AddProductCommandHandlerTest {

    @Test
    public void handleCheckIfAddMethodFromReservationClassCalledOnce() {
        int expectedInvocations = 1;
        AddProductCommandHandler addProductCommandHandler = new AddProductCommandHandler();
        Id orderId = new Id("3");
        Id productId = new Id("34");
        int quantity = 3;
        AddProductCommand addProductCommand = new AddProductCommand(orderId, productId, quantity);
        Reservation mockedReservation = Mockito.mock(Reservation.class);
        ReservationRepository mockedReservationRepository = Mockito.mock(ReservationRepository.class);
        Product mockedProduct = Mockito.mock(Product.class);
        ProductRepository mockedProductRepository = Mockito.mock(ProductRepository.class);

        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", mockedReservationRepository);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", mockedProductRepository);

        Mockito.when(mockedReservationRepository.load(any(Id.class))).thenReturn(mockedReservation);
        Mockito.when(mockedProductRepository.load(any(Id.class))).thenReturn(mockedProduct);
        Mockito.when(mockedProduct.isAvailable()).thenReturn(true);

        addProductCommandHandler.handle(addProductCommand);

        Mockito.verify(mockedReservation, Mockito.times(expectedInvocations)).add(any(Product.class), any(int.class));
    }
}
