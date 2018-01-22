package lab3_1;

import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Before;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class Zad3_1AddProductCommandHandlerTest {

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand addProductCommand;
    private Reservation reservation;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private SuggestionService suggestionService;
    private Product product;
    private ReservationRepository reservationRepository;
    private SystemContext systemContext;
    private Money money;
    private Id orderID;
    private Id productID;
    private Id reservationID;
    private Id clientID;
    private String name = "imie";
    private Date date;
    private ClientData clientData;
    private String productName;

    @Before
    public void setUp() {
        productName = "nazwa produktu";
        date = new Date();
        orderID = new Id("1");
        productID = new Id("12");
        reservationID = new Id("33");
        clientID = new Id("99");
        money = new Money(12);
        clientData = new ClientData(clientID, name);
        addProductCommand = new AddProductCommand(orderID, productID, 22);
        reservation = new Reservation(reservationID, Reservation.ReservationStatus.OPENED, clientData, date);
        suggestionService = mock(SuggestionService.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        systemContext = mock(SystemContext.class);
        product = new Product(productID, money, productName, ProductType.FOOD);
        addProductCommandHandler = new AddProductCommandHandler();

    }
}
