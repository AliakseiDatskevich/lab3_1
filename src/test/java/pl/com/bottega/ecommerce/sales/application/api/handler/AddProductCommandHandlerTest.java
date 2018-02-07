package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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

import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommandHandler handler;

    @Before
    public void setUp() {
        handler = new AddProductCommandHandler();

        handler.setClientRepository(mock(ClientRepository.class));
        handler.setProductRepository(mock(ProductRepository.class));
        handler.setReservationRepository(mock(ReservationRepository.class));
        handler.setSuggestionService(mock(SuggestionService.class));
        handler.setSystemContext(mock(SystemContext.class));
    }

    @Test
    public void areMethodsCalledProperNumberOfTimes() {
        stubMethods();

        Id orderId = Id.generate();
        Id productId = Id.generate();

        handler.handle(new AddProductCommand(orderId, productId, 5));

        verify(handler.getReservationRepository(), times(1)).load(orderId);
        verify(handler.getProductRepository(), times(1)).load(productId);
        verify(handler.getSuggestionService(), times(0)).suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any());
        verify(handler.getReservationRepository(), times(1)).save(Mockito.<Reservation>any());
    }

    @Test
    public void isSuggestEquivalentCalledForUnavailableProduct() {
        stubMethods2();

        Id orderId = Id.generate();
        Id productId = Id.generate();

        handler.handle(new AddProductCommand(orderId, productId, 5));

        verify(handler.getSuggestionService(), times(1)).suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any());
    }

    private void stubMethods() {
        Reservation reservation = new Reservation(null, Reservation.ReservationStatus.OPENED, null, null);
        when(handler.getReservationRepository().load(Mockito.<Id>any())).thenReturn(reservation);

        Product product = new Product(null, null, null, null);
        when(handler.getProductRepository().load(Mockito.<Id>any())).thenReturn(product);

        when(handler.getSuggestionService().suggestEquivalent(product, null)).thenReturn(product);
    }

    private void stubMethods2() {
        Reservation reservation = new Reservation(null, Reservation.ReservationStatus.OPENED, null, null);
        when(handler.getReservationRepository().load(Mockito.<Id>any())).thenReturn(reservation);

        Product product = new Product(null, null, null, null);
        product.markAsRemoved();
        when(handler.getProductRepository().load(Mockito.<Id>any())).thenReturn(product);

        Product suggestedProduct = new Product(null, null, null, null);
        when(handler.getSuggestionService().suggestEquivalent(product, null)).thenReturn(suggestedProduct);

        SystemUser systemUser = new SystemUser(Id.generate());
        when(handler.getSystemContext().getSystemUser()).thenReturn(systemUser);
    }
}