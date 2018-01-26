package lab3_1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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
import pl.com.bottega.ecommerce.system.application.SystemUser;

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
    private String name;
    private Date date;
    private ClientData clientData;
    private String productName;
    private Client client;
    private Product equivalentProduct;
    private int expected;
    SystemUser systemUser;

    @Before
    public void setUp() {
        name = "imie";
        productName = "nazwa produktu";
        date = new Date();
        orderID = new Id("1");
        productID = new Id("12");
        reservationID = new Id("33");
        clientID = new Id("99");
        money = new Money(12);
        clientData = new ClientData(clientID, name);
        addProductCommand = new AddProductCommand(orderID, productID, 22);
        // reservation = new Reservation(reservationID, Reservation.ReservationStatus.OPENED, clientData, date);
        suggestionService = mock(SuggestionService.class);
        productRepository = mock(ProductRepository.class);
        clientRepository = mock(ClientRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        systemContext = mock(SystemContext.class);
        product = new Product(productID, money, productName, ProductType.FOOD);
        addProductCommandHandler = new AddProductCommandHandler();
        client = new Client();
        systemUser = new SystemUser(clientID);
        reservation = Mockito
                .spy(new Reservation(reservationID, Reservation.ReservationStatus.OPENED, clientData, date));

        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepository);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepository);
        Whitebox.setInternalState(addProductCommandHandler, "systemContext", systemContext);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionService);

        when(reservationRepository.load(orderID)).thenReturn(reservation);
        when(productRepository.load(productID)).thenReturn(product);
    }

    @Test
    public void doesOneAddProductsToReservationCallReservationOnceTest() {
        expected = 1;
        addProductCommandHandler.handle(addProductCommand);
        verify(reservation, Mockito.times(expected)).add(product, 22);
    }

    @Test
    public void doesThreeAddProductsToReservationCallReservationThreeTimesTest() {
        expected = 3;

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservation, Mockito.times(expected)).add(product, 22);
    }

    @Test
    public void suggestEquivalentWhenRequestedProductNotAvailableTest() {
        expected = 1;
        Id equivalentProductID = new Id("88");
        Money equivalentProductMoney = new Money(999);
        String equivalentProductName = "Nazwa Zamiennika";
        equivalentProduct = new Product(equivalentProductID, equivalentProductMoney, equivalentProductName,
                ProductType.FOOD);

        product.markAsRemoved();

        when(clientRepository.load(clientID)).thenReturn(client);
        when(systemContext.getSystemUser()).thenReturn(systemUser);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(equivalentProduct);

        addProductCommandHandler.handle(addProductCommand);

        verify(suggestionService, Mockito.times(expected)).suggestEquivalent(product, client);
        assertTrue(suggestionService.suggestEquivalent(product, client).getName().equals(equivalentProduct.getName()));
        assertEquals(suggestionService.suggestEquivalent(product, client).getPrice(), equivalentProduct.getPrice());
        assertTrue(suggestionService.suggestEquivalent(product, client).getId().equals(equivalentProduct.getId()));
        assertTrue(suggestionService.suggestEquivalent(product, client).getProductType() == equivalentProduct
                .getProductType());
    }

}
