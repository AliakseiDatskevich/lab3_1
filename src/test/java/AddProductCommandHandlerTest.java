import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
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
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationBuilder;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand command;
    private Reservation reservation;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private SuggestionService suggestionService;
    private Product product;
    private ReservationRepository reservationRepository;
    private SystemContext systemContext;
    private Product equivalentProduct;
    private Client client = new Client();
    private Id id = new Id("1");

    @Before public void setUp() throws Exception {
        command = new AddProductCommand(id, id, 1);
        reservation = spy(ReservationBuilder.reservation().withId(Id.generate())
                .withClientData(new ClientData(Id.generate(), "name")).
                        withCreateDate(new Date()).withStatus(Reservation.ReservationStatus.OPENED).build());
        suggestionService = mock(SuggestionService.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        systemContext = new SystemContext();
        product = new Product(Id.generate(), new Money(10), "Test name", ProductType.FOOD);
        equivalentProduct = new Product(Id.generate(), new Money(100), "eqproduct", ProductType.FOOD);
        addProductCommandHandler = new AddProductCommandHandler();
        reservationRepository = mock(ReservationRepository.class);
        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionService);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(addProductCommandHandler, "systemContext", systemContext);
    }

    @Test public void testHandleSaveCallOne() throws Exception {
        when(productRepository.load(any(Id.class))).thenReturn(product);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        addProductCommandHandler.handle(command);

        verify(reservationRepository, times(1)).save(any(Reservation.class));

    }

    @Test public void testHandleSuggestionEquivalentWhenProductNotAvailable() {
        when(clientRepository.load(id)).thenReturn(client);
        when(reservationRepository.load(command.getOrderId())).thenReturn(reservation);
        when(productRepository.load(command.getProductId())).thenReturn(product);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(equivalentProduct);
        product.markAsRemoved();
        assertThat(product.isAvailable(), is(equalTo(false)));
        addProductCommandHandler.handle(command);
        verify(reservation).add(equivalentProduct, 1);
    }

    @Test public void testHandleProductAddMethodHasBeenCalled() {
        reservation = spy(new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "name"), new Date()));
        when(productRepository.load(any(Id.class))).thenReturn(product);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);
        addProductCommandHandler.handle(command);
        verify(reservation, times(1)).add(product, 1);
    }
}

