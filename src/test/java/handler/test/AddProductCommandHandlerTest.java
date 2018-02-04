package handler.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler = new AddProductCommandHandler();
    private Id id = new Id("1");
    private Product productMock = mock(Product.class);
    private Reservation reservationMock = mock(Reservation.class);
    private AddProductCommand addProductCommandMock = mock(AddProductCommand.class);
    private ReservationRepository reservationRepositorymock = mock(ReservationRepository.class);
    private ProductRepository productRepositoryMock = mock(ProductRepository.class);
    private ClientRepository clientRepositoryMock = mock(ClientRepository.class);

    @Before
    public void setUp() {
        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepositorymock);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepositoryMock);
    }

    @Test
    public void checkIfAddMethodFromReservationClassIsCalledOnce() {
        when(addProductCommandMock.getOrderId()).thenReturn(id);
        when(reservationRepositorymock.load(any(Id.class))).thenReturn(reservationMock);
        when(productRepositoryMock.load(any(Id.class))).thenReturn(productMock);
        when(productMock.isAvailable()).thenReturn(true);

        addProductCommandHandler.handle(addProductCommandMock);
        verify(reservationMock, Mockito.times(1)).add(any(Product.class), any(int.class));
    }

}
