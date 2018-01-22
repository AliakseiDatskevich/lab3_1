package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

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

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler handler;
    private AddProductCommand productCommand;
    private SuggestionService suggestionService;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private ReservationRepository reservationRepository;
    private SystemContext systemContext;
    private Reservation reservation;
    private Product product;
    private Client client;
    private Id idOrder;
    private Id idProduct;

    @Before
    public void setUp() {
        idOrder = new Id("12");
        idProduct = new Id("2232");
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        suggestionService = mock(SuggestionService.class);
        client = new Client();
        handler = new AddProductCommandHandler();
        productCommand = new AddProductCommand(idOrder, idProduct, 2);
        reservation = spy(new ReservationBuilder().build());
        product = spy(new ProductBuilder().build());
        systemContext = spy(new SystemContext());
        Product suggestionProduct = new ProductBuilder().build();

        Whitebox.setInternalState(handler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(handler, "productRepository", productRepository);
        Whitebox.setInternalState(handler, "systemContext", systemContext);
        Whitebox.setInternalState(handler, "clientRepository", clientRepository);
        Whitebox.setInternalState(handler, "suggestionService", suggestionService);
        when(reservationRepository.load(idOrder)).thenReturn(reservation);
        when(productRepository.load(idProduct)).thenReturn(product);
        when(clientRepository.load(systemContext.getSystemUser().getClientId())).thenReturn(client);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(suggestionProduct);
    }

    @Test
    public void testIsAvailableProductDefaultTrue() {
        handler.handle(productCommand);
        assertThat(product.isAvailable(), is(true));
    }

    @Test
    public void testLoadReservationWithReservationRepositoryCallOnce() {
        int sizeCall = 1;
        handler.handle(productCommand);
        verify(reservationRepository, times(sizeCall)).load(idOrder);
    }

    @Test
    public void testLoadProductWithProductRepositoryCallOnce() {
        int sizeCall = 1;
        handler.handle(productCommand);
        verify(productRepository, times(sizeCall)).load(idProduct);
    }

    @Test
    public void testAddProductsToReservationCallOnce() {
        int sizeCall = 1;
        handler.handle(productCommand);
        verify(reservation, times(sizeCall)).add(product, 2);
    }

    @Test
    public void testSaveReservationToReservationRepositoryCallOnce() {
        int sizeCall = 1;
        handler.handle(productCommand);
        verify(reservationRepository, times(sizeCall)).save(reservation);
    }

    @Test
    public void testLoadClientWithClientRepositoryCallOnce() {
        int sizeCall = 1;
        when(product.isAvailable()).thenReturn(false);
        handler.handle(productCommand);
        verify(clientRepository, times(sizeCall)).load(systemContext.getSystemUser().getClientId());
    }

    @Test
    public void testLoadClientWithClientRepositoryCallZero() {
        int sizeCall = 0;
        when(product.isAvailable()).thenReturn(true);
        handler.handle(productCommand);
        verify(clientRepository, times(sizeCall)).load(systemContext.getSystemUser().getClientId());
    }

    @Test
    public void testSuggestEquivalentProductCallOnce() {
        int sizeCall = 1;
        when(product.isAvailable()).thenReturn(false);
        handler.handle(productCommand);
        verify(suggestionService, times(sizeCall)).suggestEquivalent(product, client);
    }

    @Test
    public void testSuggestEquivalentProductCallZero() {
        int sizeCall = 0;
        when(product.isAvailable()).thenReturn(true);
        handler.handle(productCommand);
        verify(suggestionService, times(sizeCall)).suggestEquivalent(product, client);
    }
}
