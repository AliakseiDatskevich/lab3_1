package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler handler;
    private AddProductCommand productCommand;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private SuggestionService suggestionService;
    private SystemContext systemContext;
    private Reservation reservation;
    private Product product;
    private Id idOrder;
    private Id idProduct;

    @Before
    public void setUp() {
        idOrder = new Id("12");
        idProduct = new Id("2232");
        handler = new AddProductCommandHandler();
        productCommand = new AddProductCommand(idOrder, idProduct, 2);
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        suggestionService = mock(SuggestionService.class);
        systemContext = mock(SystemContext.class);
        reservation = spy(new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "John"), new Date()));
        product = spy(new ProductBuilder().build());

        Whitebox.setInternalState(handler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(handler, "productRepository", productRepository);
        Whitebox.setInternalState(handler, "systemContext", systemContext);
        Whitebox.setInternalState(handler, "clientRepository", clientRepository);
        Whitebox.setInternalState(handler, "suggestionService", suggestionService);
        when(reservationRepository.load(idOrder)).thenReturn(reservation);
        when(productRepository.load(idProduct)).thenReturn(product);
    }

    @Test
    public void testAddProductsToReservationCallOnce() {
        int sizeCall = 1;
        handler.handle(productCommand);
        verify(reservation, times(sizeCall)).add(product, 2);
    }

    @Test
    public void testSuggestEquivalentProductCallOnce() {
        int sizeCall = 1;
        Client client = new Client();
        Product suggestionProduct = new ProductBuilder().build();
        when(product.isAvailable()).thenReturn(false);
        when(clientRepository.load(new Id("1"))).thenReturn(client);
        when(systemContext.getSystemUser()).thenReturn(new SystemUser(new Id("1")));
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(suggestionProduct);
        handler.handle(productCommand);
        verify(suggestionService, times(sizeCall)).suggestEquivalent(product, client);
    }
}
