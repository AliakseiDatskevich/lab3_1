package handler.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler = new AddProductCommandHandler();
    private Id id = new Id("1");
    private Product productMock = mock(Product.class);
    private Reservation reservationMock = mock(Reservation.class);
    private AddProductCommand addProductCommandMock = mock(AddProductCommand.class);
    private Client clientMock = mock(Client.class);
    private SystemUser systemUserMock = mock(SystemUser.class);
    private ReservationRepository reservationRepositorymock = mock(ReservationRepository.class);
    private ProductRepository productRepositoryMock = mock(ProductRepository.class);
    private ClientRepository clientRepositoryMock = mock(ClientRepository.class);
    private SuggestionService suggestionServiceMock = mock(SuggestionService.class);
    private SystemContext systemContextMock = mock(SystemContext.class);

    @Before
    public void setUp() {
        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepositorymock);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionServiceMock);
        Whitebox.setInternalState(addProductCommandHandler, "systemContext", systemContextMock);

        when(addProductCommandMock.getOrderId()).thenReturn(id);
        when(reservationRepositorymock.load(any(Id.class))).thenReturn(reservationMock);
        when(productRepositoryMock.load(any(Id.class))).thenReturn(productMock);
        when(systemContextMock.getSystemUser()).thenReturn(systemUserMock);
        when(systemUserMock.getClientId()).thenReturn(id);
        when(clientRepositoryMock.load(any(Id.class))).thenReturn(clientMock);
    }

    @Test
    public void checkIfAddMethodFromReservationClassIsCalledOnce() {
        when(productMock.isAvailable()).thenReturn(true);

        addProductCommandHandler.handle(addProductCommandMock);

        verify(reservationMock, Mockito.times(1)).add(any(Product.class), any(int.class));
    }

    @Test
    public void productIsNotAvailableCheckIfSuggestionServiceReturnCorrectValue() {
        Money money = new Money(1);
        String name = "TEST";
        Product expectedProduct = new Product(id, money, name, ProductType.STANDARD);
        ArgumentCaptor<Product> actualProduct = ArgumentCaptor.forClass(Product.class);
        when(productMock.isAvailable()).thenReturn(false);
        when(suggestionServiceMock.suggestEquivalent(any(Product.class), any(Client.class)))
                .thenReturn(expectedProduct);

        addProductCommandHandler.handle(addProductCommandMock);

        verify(reservationMock, Mockito.times(1)).add(actualProduct.capture(), any(int.class));
        assertThat(actualProduct.getValue(), is(expectedProduct));
    }

}
