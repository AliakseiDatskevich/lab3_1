package pl.com.bottega.ecommerce.tests;

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
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class AddProductCommandHandlerTests {

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand command;
    private Reservation reservation;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private SuggestionService suggestionService;
    private Product product;

    @Before
    public void setUp() {
        command = new AddProductCommand(Id.generate(), Id.generate(), 1);
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                new ClientData(Id.generate(), "Jan"), new Date());
        suggestionService = mock(SuggestionService.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        product = new Product(Id.generate(), new Money(10), "Product", ProductType.FOOD);
        addProductCommandHandler = new AddProductCommandHandler();
    }

}
