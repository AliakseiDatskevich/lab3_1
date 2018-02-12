package pl.com.bottega.ecommerce.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

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
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerTests {

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand command;
    private Reservation reservation;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private SuggestionService suggestionService;
    private Product product;
    private ReservationRepository reservationRepository;
    private SystemContext systemContext;
    private Client client;
    private Id id;
    private Product equivalentProduct;

    @Before
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        reservation = spy(new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "Jan"), new Date()));
        suggestionService = mock(SuggestionService.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        product = spy(new Product(Id.generate(), new Money(10), "Product", ProductType.FOOD));
        addProductCommandHandler = new AddProductCommandHandler();
        systemContext = new SystemContext();
        client = new Client();
        id = new Id("1");
        command = new AddProductCommand(id, id, 1);
        equivalentProduct = new Product(Id.generate(), new Money(100), "EqProduct", ProductType.FOOD);
        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionService);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(addProductCommandHandler, "systemContext", systemContext);
    }

    @Test
    public void isReservationRepositorySaveMethodCalledOnce() {
        when(productRepository.load(any(Id.class))).thenReturn(product);
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        addProductCommandHandler.handle(command);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
