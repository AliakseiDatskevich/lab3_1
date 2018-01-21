package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Before;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

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
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "John"), new Date());
        product = new Product(Id.generate(), new Money(100), "Item", ProductType.DRUG);
    }
}
